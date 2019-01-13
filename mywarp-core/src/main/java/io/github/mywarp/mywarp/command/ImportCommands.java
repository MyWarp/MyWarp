/*
 * Copyright (C) 2011 - 2019, MyWarp team and contributors
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
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.OptArg;

import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;
import io.github.mywarp.mywarp.warp.storage.LegacyWarpSource;
import io.github.mywarp.mywarp.warp.storage.SqlDataService;
import io.github.mywarp.mywarp.warp.storage.StorageInitializationException;
import io.github.mywarp.mywarp.warp.storage.WarpSource;
import io.github.mywarp.mywarp.warp.storage.WarpStorageFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

/**
 * Bundles commands used to import Warps from an external source.
 */
public final class ImportCommands {

  private static final String IMPORT_PERMISSION = "mywarp.cmd.import";
  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final PlayerNameResolver playerNameResolver;
  private final WarpManager warpManager;
  private final Game game;

  /**
   * Creates an instance.
   *
   * @param warpManager        the WarpManager used by commands
   * @param playerNameResolver the PlayerNameResolver used by commands
   * @param game               the Game used by commands
   */
  ImportCommands(WarpManager warpManager, PlayerNameResolver playerNameResolver, Game game) {
    this.playerNameResolver = playerNameResolver;
    this.warpManager = warpManager;
    this.game = game;
  }

  @Command(aliases = {"current", "curr"}, desc = "import.current.description", help = "import.current.help")
  @Require(IMPORT_PERMISSION)
  public void current(Actor actor, SqlDataService dataService) throws StorageInitializationException {
    WarpSource source = WarpStorageFactory.create(dataService);
    start(actor, dataService, source);
  }

  @Command(aliases = {"legacy"}, desc = "import.legacy.description", help = "import.legacy.help")
  @Require(IMPORT_PERMISSION)
  public void legacy(Actor actor, SqlDataService dataService, @OptArg("warpTable") String tableName)
      throws StorageInitializationException {
    WarpSource
        source =
        LegacyWarpSource.from(dataService.getDataSource(), tableName, dataService.getDatabase().orElse(null))
            .using(playerNameResolver, getWorldSnapshot());

    start(actor, dataService, source);
  }

  private void start(Actor initiator, SqlDataService dataService, WarpSource warpSource) {
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

  private Map<String, UUID> getWorldSnapshot() {
    return game.getWorlds().stream().collect(Collectors.toMap(LocalWorld::getName, LocalWorld::getUniqueId));
  }

}
