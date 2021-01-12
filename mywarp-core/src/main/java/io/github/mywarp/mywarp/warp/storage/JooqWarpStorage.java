/*
 * Copyright (C) 2011 - 2021, MyWarp team and contributors
 *
 * This file is part of MyWarp.
 *
 * MyWarp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyWarp is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MyWarp. If not, see <http://www.gnu.org/licenses/>.
 */

package io.github.mywarp.mywarp.warp.storage;

import static io.github.mywarp.mywarp.warp.storage.generated.Tables.GROUP;
import static io.github.mywarp.mywarp.warp.storage.generated.Tables.PLAYER;
import static io.github.mywarp.mywarp.warp.storage.generated.Tables.WARP;
import static io.github.mywarp.mywarp.warp.storage.generated.Tables.WARP_GROUP_MAP;
import static io.github.mywarp.mywarp.warp.storage.generated.Tables.WARP_PLAYER_MAP;
import static io.github.mywarp.mywarp.warp.storage.generated.Tables.WORLD;
import static org.jooq.impl.DSL.select;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;
import io.github.mywarp.mywarp.util.playermatcher.GroupPlayerMatcher;
import io.github.mywarp.mywarp.util.playermatcher.PlayerMatcher;
import io.github.mywarp.mywarp.util.playermatcher.UuidPlayerMatcher;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.Warp.Type;
import io.github.mywarp.mywarp.warp.WarpBuilder;
import io.github.mywarp.mywarp.warp.storage.generated.tables.Player;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.jooq.Allow;
import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Insert;
import org.jooq.InsertOnDuplicateStep;
import org.jooq.InsertSetMoreStep;
import org.jooq.Record;
import org.jooq.Record14;
import org.jooq.Require;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.types.UInteger;

/**
 * A storage implementation that stores warps in a relational database.
 */
@SuppressWarnings("checkstyle:indentation")
@Allow({SQLDialect.SQLITE, SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.MARIADB})
@Require({SQLDialect.SQLITE, SQLDialect.H2, SQLDialect.MYSQL, SQLDialect.MARIADB})
class JooqWarpStorage implements WarpStorage {

  private final Configuration configuration;

  /**
   * Creates an instance that uses the given {@code Configuration}.
   *
   * @param configuration the Configuration
   */
  JooqWarpStorage(Configuration configuration) {
    this.configuration = configuration;
  }

  private DSLContext create(Configuration configuration) {
    return DSL.using(configuration);
  }

  @Override
  public void addWarp(final Warp warp) {
    final Vector3d position = warp.getPosition();
    final Vector2f rotation = warp.getRotation();

    final Set<UUID> invitedPlayerIds = new HashSet<>();
    final Set<String> invitedGroupIds = new HashSet<>();

    warp.getInvitations().forEach(i -> {
      if (i instanceof UuidPlayerMatcher) {
        invitedPlayerIds.add(((UuidPlayerMatcher) i).getCriteria());
      } else if (i instanceof GroupPlayerMatcher) {
        invitedGroupIds.add(((GroupPlayerMatcher) i).getCriteria());
      } else {
        assert false;
      }
    });

    //all required playerIds
    final List<UUID> playerIds = new ArrayList<>();
    playerIds.add(warp.getCreator());
    playerIds.addAll(invitedPlayerIds);

    // @formatter:off
    create(configuration).transaction((Configuration configuration) -> {

      //Insert all players
      List<Insert<Record>> playerInserts = new ArrayList<>();
      for (UUID playerId : playerIds) {
        playerInserts.add(insertOrIgnore(configuration, PLAYER, PLAYER.UUID, playerId));
      }
      create(configuration).batch(playerInserts).execute();

      //Insert the world
      insertOrIgnore(configuration, WORLD, WORLD.UUID, warp.getWorldIdentifier()).execute();

      //Insert the warp
      create(configuration)
          .insertInto(WARP)
          .set(WARP.NAME, warp.getName())
          .set(WARP.PLAYER_ID,
              select(PLAYER.PLAYER_ID)
                  .from(PLAYER)
                  .where(PLAYER.UUID.eq(warp.getCreator()))
                  .limit(1)
          )
          .set(WARP.TYPE, warp.getType())
          .set(WARP.X, position.getX())
          .set(WARP.Y, position.getY())
          .set(WARP.Z, position.getZ())
          .set(WARP.PITCH, rotation.getX())
          .set(WARP.YAW, rotation.getY())
          .set(WARP.WORLD_ID,
              select(WORLD.WORLD_ID)
                  .from(WORLD)
                  .where(WORLD.UUID.eq(warp.getWorldIdentifier()))
                  .limit(1))
          .set(WARP.CREATION_DATE, warp.getCreationDate())
          .set(WARP.VISITS, UInteger.valueOf(warp.getVisits()))
          .set(WARP.WELCOME_MESSAGE, warp.getWelcomeMessage())
          .execute();

      //Insert all groups
      List<Insert<Record>> groupInserts = new ArrayList<>();
      for (String groupName : invitedGroupIds) {
        groupInserts.add(insertOrIgnore(configuration, GROUP, GROUP.NAME, groupName));
      }
      create(configuration).batch(groupInserts).execute();

      //insert all player-invitations
      List<InsertSetMoreStep<Record>> warpPlayerInserts = new ArrayList<>();
      invitedPlayerIds.forEach(playerId -> {
        warpPlayerInserts.add(create(configuration)
            .insertInto(WARP_PLAYER_MAP)
            .set(WARP_PLAYER_MAP.WARP_ID,
                select(WARP.WARP_ID)
                    .from(WARP)
                    .where(WARP.NAME.eq(warp.getName()))
                    .limit(1)
            )
            .set(WARP_PLAYER_MAP.PLAYER_ID,
                select(PLAYER.PLAYER_ID)
                    .from(PLAYER)
                    .where(PLAYER.UUID.eq(playerId))
                    .limit(1)
            )
        );
      });
      create(configuration).batch(warpPlayerInserts).execute();

      //insert all group-invitations
      List<InsertSetMoreStep<Record>> warpGroupInserts = new ArrayList<>();
      invitedGroupIds.forEach(groupName -> {
        warpGroupInserts.add(create(configuration)
            .insertInto(WARP_GROUP_MAP)
            .set(WARP_GROUP_MAP.WARP_ID,
                select(WARP.WARP_ID)
                    .from(WARP)
                    .where(WARP.NAME.eq(warp.getName()))
                    .limit(1)
            )
            .set(WARP_GROUP_MAP.GROUP_ID,
                select(GROUP.GROUP_ID)
                    .from(GROUP)
                    .where(GROUP.NAME.eq(groupName))
                    .limit(1)
            )
        );
      });
      create(configuration).batch(warpGroupInserts).execute();
    });
    // @formatter:on
  }

  @Override
  public void removeWarp(final Warp warp) {
    // @formatter:off
    create(configuration)
        .delete(WARP)
        .where(WARP.NAME.eq(warp.getName()))
        .execute();
    // @formatter:on
  }

  @Override
  public List<Warp> getWarps() {
    // Alias for the player-table to represent the warp-creator
    Player creatorTable = PLAYER.as("c");

    // query the database and group results by name - each map-entry
    // contains all values for one single warp
    // @formatter:off
    Map<String, Result<Record14<String, UUID, Type, Double, Double, Double, Float, Float, UUID, Instant,
        UInteger, String, UUID, String>>> groupedResults = create(configuration)
        .select(WARP.NAME, creatorTable.UUID, WARP.TYPE, WARP.X, WARP.Y, WARP.Z, WARP.YAW,
            WARP.PITCH, WORLD.UUID, WARP.CREATION_DATE, WARP.VISITS,
            WARP.WELCOME_MESSAGE, PLAYER.UUID, GROUP.NAME)
        .from(WARP
            .join(WORLD)
            .on(WARP.WORLD_ID.eq(WORLD.WORLD_ID))
            .join(creatorTable)
            .on(WARP.PLAYER_ID.eq(creatorTable.PLAYER_ID))
            .leftOuterJoin(WARP_PLAYER_MAP)
            .on(WARP_PLAYER_MAP.WARP_ID.eq(WARP.WARP_ID))
            .leftOuterJoin(PLAYER)
            .on(WARP_PLAYER_MAP.PLAYER_ID.eq(PLAYER.PLAYER_ID)))
        .leftOuterJoin(WARP_GROUP_MAP)
        .on(WARP_GROUP_MAP.WARP_ID.eq(WARP.WARP_ID))
        .leftOuterJoin(GROUP)
        .on(WARP_GROUP_MAP.GROUP_ID.eq(GROUP.GROUP_ID))
        .fetch().intoGroups(WARP.NAME);
    // @formatter:on

    // create warp-instances from the results
    return groupedResults.values().stream().map(r -> {
      Vector3d position = new Vector3d(r.getValue(0, WARP.X), r.getValue(0, WARP.Y), r.getValue(0, WARP.Z));
      Vector2f rotation = new Vector2f(r.getValue(0, WARP.PITCH), r.getValue(0, WARP.YAW));

      WarpBuilder
          builder =
          new WarpBuilder(r.getValue(0, WARP.NAME), r.getValue(0, creatorTable.UUID), r.getValue(0, WORLD.UUID),
              position, rotation);

      // optional values
      builder.setType(r.getValue(0, WARP.TYPE));
      builder.setCreationDate(r.getValue(0, WARP.CREATION_DATE));
      builder.setVisits(r.getValue(0, WARP.VISITS).intValue());
      builder.setWelcomeMessage(r.getValue(0, WARP.WELCOME_MESSAGE));

      for (@Nullable String groupName : r.getValues(GROUP.NAME)) {
        if (groupName != null) {
          builder.addInvitation(new GroupPlayerMatcher(groupName));
        }
      }

      for (@Nullable UUID inviteeUniqueId : r.getValues(PLAYER.UUID)) {
        if (inviteeUniqueId != null) {
          builder.addInvitation(new UuidPlayerMatcher(inviteeUniqueId));
        }
      }

      return builder.build();
    }).collect(Collectors.toList());
  }

  @Override
  public void addInvitation(Warp warp, PlayerMatcher invitation) {
    if (invitation instanceof UuidPlayerMatcher) {
      addPlayerInvitation(warp, (UuidPlayerMatcher) invitation);
    } else if (invitation instanceof GroupPlayerMatcher) {
      addGroupInvitation(warp, (GroupPlayerMatcher) invitation);
    } else {
      assert false;
    }
  }

  @Override
  public void removeInvitation(Warp warp, PlayerMatcher invitation) {
    if (invitation instanceof UuidPlayerMatcher) {
      removePlayerInvitation(warp, (UuidPlayerMatcher) invitation);
    } else if (invitation instanceof GroupPlayerMatcher) {
      removeGroupInvitation(warp, (GroupPlayerMatcher) invitation);
    } else {
      assert false;
    }
  }

  private void addPlayerInvitation(final Warp warp, final UuidPlayerMatcher invitation) {
    create(configuration).transaction(configuration -> {
      // @formatter:off
      insertOrIgnore(configuration, PLAYER, PLAYER.UUID, invitation.getCriteria()).execute();

      create(configuration)
          .insertInto(WARP_PLAYER_MAP)
          .set(WARP_PLAYER_MAP.WARP_ID,
              select(WARP.WARP_ID)
                  .from(WARP)
                  .where(WARP.NAME.eq(warp.getName()))
                  .limit(1)
          )
          .set(WARP_PLAYER_MAP.PLAYER_ID,
              select(PLAYER.PLAYER_ID)
                  .from(PLAYER)
                  .where(PLAYER.UUID.eq(invitation.getCriteria()))
                  .limit(1)
          )
          .execute();
      // @formatter:on
    });
  }

  private void removePlayerInvitation(final Warp warp, final UuidPlayerMatcher invitation) {
    // @formatter:off
    create(configuration)
        .delete(WARP_PLAYER_MAP)
        .where(
            WARP_PLAYER_MAP.WARP_ID.eq(
                select(WARP.WARP_ID)
                    .from(WARP)
                    .where(WARP.NAME.eq(warp.getName()))
                    .limit(1))
                .and(WARP_PLAYER_MAP.PLAYER_ID.eq(
                    select(PLAYER.PLAYER_ID)
                        .from(PLAYER)
                        .where(PLAYER.UUID.eq(invitation.getCriteria()))
                        .limit(1))
                )
        )
        .execute();
    // @formatter:on
  }

  private void addGroupInvitation(final Warp warp, final GroupPlayerMatcher invitation) {
    create(configuration).transaction(configuration -> {
      // @formatter:off
      insertOrIgnore(configuration, GROUP, GROUP.NAME, invitation.getCriteria()).execute();

      create(configuration)
          .insertInto(WARP_GROUP_MAP)
          .set(WARP_GROUP_MAP.WARP_ID,
              select(WARP.WARP_ID)
                  .from(WARP)
                  .where(WARP.NAME.eq(warp.getName()))
                  .limit(1)
          )
          .set(WARP_GROUP_MAP.GROUP_ID,
              select(GROUP.GROUP_ID)
                  .from(GROUP)
                  .where(GROUP.NAME.eq(invitation.getCriteria()))
                  .limit(1)
          )
          .execute();
      // @formatter:on
    });
  }

  private void removeGroupInvitation(final Warp warp, final GroupPlayerMatcher invitation) {
    // @formatter:off
    create(configuration)
        .delete(WARP_GROUP_MAP)
        .where(
            WARP_GROUP_MAP.WARP_ID.eq(
                select(WARP.WARP_ID)
                    .from(WARP)
                    .where(WARP.NAME.eq(warp.getName()))
                    .limit(1))
                .and(WARP_GROUP_MAP.GROUP_ID.eq(
                    select(GROUP.GROUP_ID)
                        .from(GROUP)
                        .where(GROUP.NAME.eq(invitation.getCriteria()))
                        .limit(1))
                )
        )
        .execute();
    // @formatter:on
  }

  @Override
  public void updateCreator(final Warp warp) {
    create(configuration).transaction(configuration -> {
      // @formatter:off
      insertOrIgnore(configuration, PLAYER, PLAYER.UUID, warp.getCreator()).execute();

      create(configuration)
          .update(WARP)
          .set(WARP.PLAYER_ID,
              select(PLAYER.PLAYER_ID)
                  .from(PLAYER)
                  .where(PLAYER.UUID.eq(warp.getCreator()))
                  .limit(1)
          )
          .where(WARP.NAME.eq(warp.getName()))
          .execute();
      // @formatter:on
    });
  }

  @Override
  public void updateLocation(final Warp warp) {
    final Vector3d position = warp.getPosition();
    final Vector2f rotation = warp.getRotation();

    create(configuration).transaction(configuration -> {
      // @formatter:off
      insertOrIgnore(configuration, WORLD, WORLD.UUID, warp.getWorldIdentifier()).execute();

      create(configuration)
          .update(WARP)
          .set(WARP.X, position.getX())
          .set(WARP.Y, position.getY())
          .set(WARP.Z, position.getZ())
          .set(WARP.PITCH, rotation.getX())
          .set(WARP.YAW, rotation.getY())
          .set(WARP.WORLD_ID,
              select(WORLD.WORLD_ID)
                  .from(WORLD)
                  .where(WORLD.UUID.eq(warp.getWorldIdentifier()))
                  .limit(1))
          .where(WARP.NAME.eq(warp.getName()))
          .execute();
      // @formatter:on
    });
  }

  @Override
  public void updateType(final Warp warp) {
    // @formatter:off
    create(configuration)
        .update(WARP)
        .set(WARP.TYPE, warp.getType())
        .where(WARP.NAME.eq(warp.getName()))
        .execute();
    // @formatter:on
  }

  @Override
  public void updateVisits(final Warp warp) {
    // @formatter:off
    create(configuration)
        .update(WARP)
        .set(WARP.VISITS, UInteger.valueOf(warp.getVisits()))
        .where(WARP.NAME.eq(warp.getName()))
        .execute();
    // @formatter:on
  }

  @Override
  public void updateWelcomeMessage(final Warp warp) {
    // @formatter:off
    create(configuration)
        .update(WARP)
        .set(WARP.WELCOME_MESSAGE, warp.getWelcomeMessage())
        .where(WARP.NAME.eq(warp.getName()))
        .execute();
    // @formatter:on
  }

  /**
   * Creates an {@code INSERT ... ON DUPLICATE IGNORE} query that insert the given {@code value} into the given {@code
   * uniqueField} in the given {@code table}, assuming that the given {@code value} should be unique.
   *
   * @param configuration the {@code Configuration} used to generate the query
   * @param table         the {@code Table} to insert in
   * @param uniqueField   the {@code TableField}  to insert - must be unique!
   * @param value         the value to insert
   * @return a corresponding {@code Insert} query
   * @see InsertOnDuplicateStep#onDuplicateKeyIgnore()
   */
  private <R extends Record, T> Insert<R> insertOrIgnore(Configuration configuration, Table<R> table,
      TableField<R, T> uniqueField, T value) {
    // @formatter:off
    return create(configuration)
        .insertInto(table)
        .columns(uniqueField)
        .values(value)
        .onDuplicateKeyIgnore();
    // @formatter:on
  }
}
