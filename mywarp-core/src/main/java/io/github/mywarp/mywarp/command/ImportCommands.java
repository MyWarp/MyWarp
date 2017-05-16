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

package io.github.mywarp.mywarp.command;

import com.sk89q.intake.Command;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Require;

import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.Platform;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;
import io.github.mywarp.mywarp.warp.storage.ConnectionConfiguration;
import io.github.mywarp.mywarp.warp.storage.LegacyWarpSource;
import io.github.mywarp.mywarp.warp.storage.RelationalDataService;
import io.github.mywarp.mywarp.warp.storage.StorageInitializationException;
import io.github.mywarp.mywarp.warp.storage.WarpSource;
import io.github.mywarp.mywarp.warp.storage.WarpStorageFactory;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

/**
 * Bundles commands used to import Warps from an external source.
 */
public final class ImportCommands {

  private static final String IMPORT_PERMISSION = "mywarp.cmd.import";
  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final Platform platform;
  private final PlayerNameResolver playerNameResolver;
  private final WarpManager warpManager;
  private final Game game;

  /**
   * Creates an instance.
   *
   * @param warpManager        the WarpManager used by commands
   * @param platform           the Platform used by commands
   * @param playerNameResolver the PlayerNameResolver used by commands
   * @param game               the Game used by commands
   */
  ImportCommands(WarpManager warpManager, Platform platform, PlayerNameResolver playerNameResolver, Game game) {
    this.platform = platform;
    this.playerNameResolver = playerNameResolver;
    this.warpManager = warpManager;
    this.game = game;
  }

  @Command(aliases = {"current", "curr"}, desc = "import.current.description", help = "import.current.help")
  @Require(IMPORT_PERMISSION)
  public void current(Actor actor, ConnectionConfiguration configuration) throws CommandException {
    RelationalDataService dataService = platform.createDataService(configuration);
    try {
      start(actor, dataService, WarpStorageFactory.create(dataService.getDataSource(), configuration));
    } catch (StorageInitializationException | SQLException e) {
      throw new CommandException(msg.getString("import.no-connection", e.getMessage()));
    }
  }

  @Command(aliases = {"pre3-sqlite"}, desc = "import.pre3-sqlite.description", help = "import.pre3-sqlite.help")
  @Require(IMPORT_PERMISSION)
  public void pre3Sqlite(Actor actor, File database) throws CommandException {
    ConnectionConfiguration configuration = new ConnectionConfiguration("jdbc:sqlite:" + database.getAbsolutePath());
    try {
      RelationalDataService dataService = platform.createDataService(configuration);
      start(actor, dataService,
            new LegacyWarpSource(dataService.getDataSource(), configuration, "warpTable", playerNameResolver,
                                 getWorldSnapshot()));
    } catch (SQLException e) {
      throw new CommandException(msg.getString("import.no-connection", e.getMessage()));
    }
  }

  @Command(aliases = {"pre3-mysql"}, desc = "import.pre3-mysql.description", help = "import.pre3-mysql.help")
  @Require(IMPORT_PERMISSION)
  public void pre3Mysql(Actor actor, String dsn, String schema, String user, String password, String tableName)
      throws CommandException {
    ConnectionConfiguration
        config =
        new ConnectionConfiguration(dsn).setSchema(schema).setUser(user).setPassword(password);
    try {
      RelationalDataService dataService = platform.createDataService(config);
      start(actor, dataService, new LegacyWarpSource(dataService.getDataSource(), config, tableName, playerNameResolver,
                                                     getWorldSnapshot()));
    } catch (SQLException e) {
      throw new CommandException(msg.getString("import.no-connection", e.getMessage()));
    }
  }

  /**
   * Starts the import from the given {@code WarpSource}.
   *
   * @param initiator   the {@code Actor} who initated the import
   * @param dataService the data service that sources the {@code warpSource}
   * @param warpSource  the {@code WarpSource} to import from
   */
  private void start(final Actor initiator, final RelationalDataService dataService, final WarpSource warpSource) {
    initiator.sendMessage(msg.getString("import.started"));

    ExecutorService executorService = dataService.getExecutorService();

    CompletableFuture.supplyAsync(warpSource::getWarps, executorService).whenCompleteAsync((warps, ex) -> {
      if (ex != null) {
        initiator.sendError(msg.getString("import.no-connection", ex.getMessage()));
      } else {
        Set<Warp> notImportedWarps = new HashSet<Warp>();

        for (Warp warp : warps) {
          if (warpManager.containsByName(warp.getName())) {
            // skip the warp
            notImportedWarps.add(warp);
            continue;
          }
          warpManager.add(warp);
        }

        if (notImportedWarps.isEmpty()) {
          initiator.sendMessage(msg.getString("import.import-successful", warps.size()));
        } else {
          int successfullyImported = warps.size() - notImportedWarps.size();

          Message.Builder builder = Message.builder();
          builder.append(Message.Style.ERROR);
          builder.append(msg.getString("import.import-with-skips", successfullyImported, notImportedWarps.size()));
          builder.appendWithSeparators(notImportedWarps);

          initiator.sendMessage(builder.build());
        }
        dataService.close();
      }
    }, game.getExecutor());
  }

  /**
   * Gets a mapping of the names to uniqueIds from all worlds currently existing.
   *
   * @return a mapping of the names to uniqueIds from all worlds
   */
  private Map<String, UUID> getWorldSnapshot() {
    Map<String, UUID> snapshot = new HashMap<String, UUID>();
    for (LocalWorld world : game.getWorlds()) {
      snapshot.put(world.getName(), world.getUniqueId());
    }
    return snapshot;
  }

}
