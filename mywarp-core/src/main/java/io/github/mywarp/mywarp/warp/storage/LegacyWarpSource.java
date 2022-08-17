/*
 * Copyright (C) 2011 - 2022, MyWarp team and contributors
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

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.platform.Profile;
import io.github.mywarp.mywarp.util.MyWarpLogger;
import io.github.mywarp.mywarp.util.playermatcher.GroupPlayerMatcher;
import io.github.mywarp.mywarp.util.playermatcher.UuidPlayerMatcher;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpBuilder;
import org.jooq.*;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.tools.jdbc.JDBCUtils;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutionException;

import static org.jooq.SQLDialect.*;
import static org.jooq.impl.DSL.*;

/**
 * A {@link WarpSource} for databases with a legacy scheme (pre 3.0).
 *
 * <p>The legacy database stores player and world names as strings, instead of using unique IDs. Calling {@link
 * #getWarps()} will convert both. Player names are acquired by calling the configured {@link PlayerNameResolver}, witch
 * may result in a blocking call.</p>
 *
 * <p>Call {@link #using(DataSource, String, String)} to create instances.</p>
 */
@SuppressWarnings("checkstyle:indentation")
@Allow({SQLITE, MYSQL, MARIADB})
@Require({SQLITE, MYSQL, MARIADB})
public final class LegacyWarpSource implements WarpSource {

  private static final Logger log = MyWarpLogger.getLogger(LegacyWarpSource.class);
  private static final ImmutableSet<SQLDialect> SUPPORTED_DIALECTS = ImmutableSet.of(MYSQL, MARIADB, SQLITE);

  private final Splitter splitter = Splitter.on(',').omitEmptyStrings().trimResults();
  private final Configuration configuration;
  private final Name tableName;
  private final PlayerNameResolver playerResolver;
  private final ImmutableMap<String, UUID> worldMap;

  private LegacyWarpSource(Configuration configuration, Name name, PlayerNameResolver resolver,
      Map<String, UUID> worldMap) {
    this.configuration = configuration;
    this.tableName = name;
    this.playerResolver = resolver;
    this.worldMap = ImmutableMap.copyOf(worldMap);
  }

  /**
   * Creates a builder for creating a new LegacyWarpSource instance.
   *
   * <p>This instance will import warps from the given {@code dataSource}, using the given {@code tableName} and, if
   * not null, {@code databaseName}.</p>
   *
   * @param dataSource   the DataSource that provides a connection to the DBMS
   * @param tableName    the name of the DB table that contains the warps
   * @param databaseName the name of the database (sometimes also called schema that contains the table. May be {@code
   *                     null} if the DBMS does not support multiple databases.
   * @return a builder step
   * @throws SQLException                if the connection to the DBMS fails
   * @throws UnsupportedDialectException if the dialect if the {@code dataSource} is not supported
   */
  public static LegacyWarpImporterBuilder using(DataSource dataSource, String tableName, @Nullable String databaseName)
      throws UnsupportedDialectException, SQLException {
    return using(dataSource, databaseName != null ? name(databaseName, tableName) : name(tableName));
  }

  private static LegacyWarpImporterBuilder using(DataSource source, Name tableName)
      throws UnsupportedDialectException, SQLException {
    SQLDialect dialect;
    try (Connection conn = source.getConnection()) {
      dialect = JDBCUtils.dialect(conn);

      if (SUPPORTED_DIALECTS.stream().noneMatch(dialect::supports)) {
        throw new UnsupportedDialectException(dialect);
      }

    }
    return new LegacyWarpImporterBuilder(new DefaultConfiguration().set(dialect).set(new Settings()).set(source),
        tableName);
  }

  @Override
  public List<Warp> getWarps() {

    // @formatter:off
    Result<Record13<String, String, Boolean, Double, Double, Double, Float, Float, String, Integer, String, String,
        String>>
        results =
        DSL.using(configuration).select(field(name("name"), String.class), //1
            field(name("creator"), String.class), //2
            field(name("publicAll"), Boolean.class), //3
            field(name("x"), Double.class), //4
            field(name("y"), Double.class), //5
            field(name("z"), Double.class), //6
            field(name("yaw"), Float.class), //7
            field(name("pitch"), Float.class), //8
            field(name("world"), String.class), //9
            field(name("visits"), Integer.class), //10
            field(name("welcomeMessage"), String.class), //11
            field(name("permissions"), String.class), //12
            field(name("groupPermissions"), String.class)) //13
            .from(table(tableName)).fetch();
    // @formatter:on
    log.info("{} entries found.", results.size());

    Set<String> playerNames = new HashSet<>(results.getValues("creator", String.class));
    for (String invitedPlayers : results.getValues("permissions", String.class)) {
      Iterables.addAll(playerNames, splitter.split(invitedPlayers));
    }
    log.info("Looking up unique IDs for {} unique players.", playerNames.size());

    Set<Profile> cache;
    try {
      //the method getWarps() should never be called on the main thread, so we simply block execution until the
      // profiles are available.
      cache = playerResolver.getByName(playerNames).get();
    } catch (InterruptedException | ExecutionException e) {
      log.error("Failed to resolve player names.", e);
      return Collections.emptyList();
    }
    log.info("{} unique IDs found.", cache.size());

    // the legacy database may contain player-names with a wrong case, so the lookup must be case insensitive
    TreeMap<String, UUID> profileLookup = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    for (Profile profile : cache) {
      if (!profile.getName().isPresent()) {
        //this should not happen as we generate this list using names
        continue;
      }
      profileLookup.put(profile.getName().get(), profile.getUuid());
    }

    List<Warp> ret = new ArrayList<>(results.size());

    for (Record13<String, String, Boolean, Double, Double, Double, Float, Float, String, Integer, String, String,
        String> r : results) {
      String warpName = r.value1();

      String creatorName = r.value2();
      UUID creator = profileLookup.get(creatorName);
      if (creator == null) {
        log.warn("For the creator of '{}' ({}) no unique ID could be found. The warp will be ignored.", warpName,
            creatorName);
        continue;
      }

      Warp.Type type = r.value3() ? Warp.Type.PUBLIC : Warp.Type.PRIVATE;

      Vector3d position = new Vector3d(r.value4(), r.value5(), r.value6());
      Vector2f rotation = new Vector2f(r.value8(), r.value7());

      String worldName = r.value9();
      UUID worldId = worldMap.get(worldName);
      if (worldId == null) {
        log.warn("For the world of '{}' ({}) no unique ID could be found. The warp will be ignored.", warpName,
            worldName);
        continue;
      }

      WarpBuilder builder = new WarpBuilder(warpName, creator, worldId, position, rotation);

      // optional values
      builder.setType(type);
      builder.setVisits(r.value10());
      builder.setWelcomeMessage(r.value11());

      splitter.split(r.value13()).forEach(g -> builder.addInvitation(new GroupPlayerMatcher(g)));

      for (String playerName : splitter.split(r.value12())) {
        UUID invitee = profileLookup.get(playerName);
        if (invitee == null) {
          log.warn("{}, who is criteria to '{}' does not have a unique ID. The playermatcher will be ignored.",
              playerName, warpName);
          continue;
        }
        builder.addInvitation(new UuidPlayerMatcher(invitee));
      }

      ret.add(builder.build());
      log.debug("Warp '{}' exported.", warpName);
    }

    log.info("{} warps exported from source.", ret.size());
    return ret;
  }

  /**
   * Builder class for {@link LegacyWarpSource}s.
   *
   * @see LegacyWarpSource#using(DataSource, String, String)
   */
  public static class LegacyWarpImporterBuilder {

    private final Configuration configuration;
    private final Name tableName;

    private LegacyWarpImporterBuilder(Configuration configuration, Name tableName) {
      this.configuration = configuration;
      this.tableName = tableName;
    }

    /**
     * Creates a WarpSource that converts warps using the given {@code resolver} and {@code worldMap}.
     *
     * @param resolver the PlayerNameResolver to resolve player names found in legacy warps
     * @param worldMap the map of existing worlds to resolve world names found in legacy warps
     * @return a WarpSource of the old database
     */
    public WarpSource using(PlayerNameResolver resolver, Map<String, UUID> worldMap) {
      return new LegacyWarpSource(configuration, tableName, resolver, worldMap);
    }
  }
}
