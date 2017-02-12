/*
 * Copyright (C) 2011 - 2017, MyWarp team and contributors
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

package me.taylorkelly.mywarp.warp.storage;

import static com.google.common.base.Preconditions.checkArgument;
import static org.jooq.impl.DSL.field;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.table;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

import me.taylorkelly.mywarp.platform.PlayerNameResolver;
import me.taylorkelly.mywarp.util.MyWarpLogger;
import me.taylorkelly.mywarp.warp.Warp;
import me.taylorkelly.mywarp.warp.WarpBuilder;

import org.jooq.Configuration;
import org.jooq.Name;
import org.jooq.Record13;
import org.jooq.Result;
import org.jooq.SQLDialect;
import org.jooq.conf.Settings;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.sql.DataSource;

/**
 * A {@link WarpSource} for databases with a legacy scheme (pre 3.0).
 *
 * <p>The legacy database stores player and world names as strings, instead of using unique IDs. Calling {@link
 * #getWarps()} will convert both. Player names are acquired by calling the configured {@link PlayerNameResolver}, witch
 * may result in a blocking call.</p>
 */
@SuppressWarnings("checkstyle:indentation")
public class LegacyWarpSource implements WarpSource {

  private static final Logger log = MyWarpLogger.getLogger(LegacyWarpSource.class);
  private static final ImmutableSet<SQLDialect>
          SUPPORTED_DIALECTS =
          ImmutableSet.of(SQLDialect.MYSQL, SQLDialect.SQLITE);

  private final Splitter splitter = Splitter.on(',').omitEmptyStrings().trimResults();
  private final ImmutableMap<String, UUID> worldsSnapshot;
  private final Configuration configuration;
  private final Name tableName;
  private final PlayerNameResolver playerNameResolver;

  /**
   * Creates an instance from the given DataSource with the given configuration.
   *
   * @param dataSource         the DataSource that connects to the database to use
   * @param config             the configuration to use for connection
   * @param tableName          the name of the MySQL table to use
   * @param playerNameResolver the PlayerNameResolver to create Profiles on import
   * @param worldsSnapshot     a snapshot of existing worlds used to convert the positions of imported warps
   */
  public LegacyWarpSource(DataSource dataSource, ConnectionConfiguration config, String tableName,
                          PlayerNameResolver playerNameResolver, Map<String, UUID> worldsSnapshot) {
    checkArgument(SUPPORTED_DIALECTS.contains(config.getDialect()));

    this.configuration = new DefaultConfiguration().set(config.getDialect()).set(new Settings()).set(dataSource);
    this.tableName = config.supportsSchemas() ? name(config.getSchema(), tableName) : name(tableName);
    this.playerNameResolver = playerNameResolver;
    this.worldsSnapshot = ImmutableMap.copyOf(worldsSnapshot);
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

    Set<String> playerNames = new HashSet<String>(results.getValues("creator", String.class));
    for (String invitedPlayers : results.getValues("permissions", String.class)) {
      Iterables.addAll(playerNames, splitter.split(invitedPlayers));
    }
    log.info("Looking up unique IDs for {} unique players.", playerNames.size());

    ImmutableMap<String, UUID> cache = playerNameResolver.getByName(playerNames);
    log.info("{} unique IDs found.", cache.size());

    // the legacy database may contain player-names with a wrong case, so the lookup must be case insensitive
    TreeMap<String, UUID> profileLookup = new TreeMap<String, UUID>(String.CASE_INSENSITIVE_ORDER);
    profileLookup.putAll(cache);

    List<Warp> ret = new ArrayList<Warp>(results.size());

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
      UUID worldId = worldsSnapshot.get(worldName);
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

      builder.addInvitedGroups(splitter.split(r.value13()));

      for (String playerName : splitter.split(r.value12())) {
        UUID invitee = profileLookup.get(playerName);
        if (invitee == null) {
          log.warn("{}, who is invited to '{}' does not have a unique ID. The invitation will be ignored.", playerName,
                  warpName);
          continue;
        }
        builder.addInvitedPlayer(invitee);
      }

      ret.add(builder.build());
      log.debug("Warp '{}' exported.", warpName);
    }

    log.info("{} warps exported from source.", ret.size());
    return ret;
  }
}
