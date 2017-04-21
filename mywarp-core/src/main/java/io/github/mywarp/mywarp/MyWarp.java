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

package io.github.mywarp.mywarp;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;

import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.Platform;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.platform.Settings;
import io.github.mywarp.mywarp.platform.capability.EconomyCapability;
import io.github.mywarp.mywarp.platform.capability.PositionValidationCapability;
import io.github.mywarp.mywarp.sign.WarpSignHandler;
import io.github.mywarp.mywarp.util.InvitationInformationListener;
import io.github.mywarp.mywarp.util.MyWarpLogger;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.teleport.LegacyPositionCorrectionCapability;
import io.github.mywarp.mywarp.util.teleport.StrategicTeleportHandler;
import io.github.mywarp.mywarp.util.teleport.TeleportHandler;
import io.github.mywarp.mywarp.warp.EventfulPopulatableWarpManager;
import io.github.mywarp.mywarp.warp.MemoryPopulatableWarpManager;
import io.github.mywarp.mywarp.warp.PopulatableWarpManager;
import io.github.mywarp.mywarp.warp.StoragePopulatableWarpManager;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;
import io.github.mywarp.mywarp.warp.authorization.AuthorizationResolver;
import io.github.mywarp.mywarp.warp.authorization.PermissionAuthorizationStrategy;
import io.github.mywarp.mywarp.warp.authorization.WarpPropertiesAuthorizationStrategy;
import io.github.mywarp.mywarp.warp.authorization.WorldAccessAuthorizationStrategy;
import io.github.mywarp.mywarp.warp.storage.AsyncWritingWarpStorage;
import io.github.mywarp.mywarp.warp.storage.ConnectionConfiguration;
import io.github.mywarp.mywarp.warp.storage.RelationalDataService;
import io.github.mywarp.mywarp.warp.storage.StorageInitializationException;
import io.github.mywarp.mywarp.warp.storage.WarpStorage;
import io.github.mywarp.mywarp.warp.storage.WarpStorageFactory;

import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Entry point and container for a working MyWarp implementation.
 *
 * <p>An instance of this class holds and manages MyWarp's internal logic. It is initialized with a {@link Platform}
 * which provides MyWarp with the connection to the game.</p>
 */
public final class MyWarp {

  private static final Logger log = MyWarpLogger.getLogger(MyWarp.class);

  private final Platform platform;
  private final RelationalDataService dataService;
  private final WarpStorage warpStorage;
  private final PopulatableWarpManager warpManager;
  private final EventBus eventBus;
  private final AuthorizationResolver authorizationResolver;

  private CommandHandler commandHandler;
  private TeleportHandler teleportHandler;

  @Nullable
  private InvitationInformationListener invitationInformationListener;

  /**
   * Creates a MyWarp instance that runs on the given {@code platform}.
   *
   * <p>If this method returns the instance without raising any exceptions, MyWarp's internal logic has been
   * successfully initialized and MyWarp is fully operational. Any additional service implemented by the client should
   * thus be ready to operate.</p>
   *
   * <p>Warps might no yet be available, but are scheduled to be loaded from the storage system. Once they are
   * available, {@link Platform#onWarpsLoaded()} will be called on {@code platform}.</p>
   *
   * @param platform the platform MyWarp will run on
   * @return a fully operational instance of MyWarp that runs on {@code platform}
   * @throws StorageInitializationException if the Storage system could not be initialized
   * @throws SQLException                   if the {@code DataService} offered by {@code platform} does not provide a
   *                                        valid {@code DataSource}
   */
  public static MyWarp initialize(Platform platform) throws StorageInitializationException, SQLException {

    ConnectionConfiguration connectionConfiguration = platform.getSettings().getRelationalStorageConfiguration();
    RelationalDataService dataService = platform.createDataService(connectionConfiguration);
    WarpStorage warpStorage;

    warpStorage =
        new AsyncWritingWarpStorage(
            WarpStorageFactory.createInitialized(dataService.getDataSource(), connectionConfiguration),
            dataService.getExecutorService());

    EventBus eventBus = new EventBus();

    PopulatableWarpManager
        warpManager =
        new EventfulPopulatableWarpManager(
            new StoragePopulatableWarpManager(new MemoryPopulatableWarpManager(), warpStorage), eventBus);

    AuthorizationResolver
        authorizationResolver =
        new AuthorizationResolver(new PermissionAuthorizationStrategy(
            new WorldAccessAuthorizationStrategy(new WarpPropertiesAuthorizationStrategy(), platform.getGame(),
                                                 platform.getSettings())));

    MyWarp myWarp = new MyWarp(platform, dataService, warpStorage, warpManager, eventBus, authorizationResolver);
    myWarp.initializeMutableFields();
    myWarp.loadWarps();

    return myWarp;
  }

  private MyWarp(Platform platform, RelationalDataService dataService, WarpStorage warpStorage,
                 PopulatableWarpManager warpManager, EventBus eventBus, AuthorizationResolver authorizationResolver) {
    this.platform = platform;
    this.dataService = dataService;
    this.warpStorage = warpStorage;
    this.warpManager = warpManager;
    this.eventBus = eventBus;
    this.authorizationResolver = authorizationResolver;
  }

  /**
   * Reloads MyWarp.
   *
   * <p>Reloading will remove all loaded warps from the active PopulatableWarpManager and reload them from the
   * configured storage. Interaction models (commands, signs...) are newly created. The platform running MyWarp may
   * reload the user configuration from disk.</p>
   */
  public void reload() {
    // cleanup
    warpManager.depopulate();
    DynamicMessages.clearCache();
    if (invitationInformationListener != null) {
      eventBus.unregister(invitationInformationListener);
    }

    //notify platform
    platform.onCoreReload();

    // setup new stuff
    initializeMutableFields();
    loadWarps();
  }

  /**
   * Gets the CommandHandler that holds and executes all of MyWarp's commands.
   *
   * @return the CommandHandler
   */
  public CommandHandler getCommandHandler() {
    return commandHandler;
  }

  /**
   * Gets the internal EventBus that keeps track of internal events thrown by MyWarp.
   *
   * @return the EventBus
   */
  public EventBus getEventBus() {
    return eventBus;
  }

  /**
   * Gets the Game instance of this MyWarp instance.
   *
   * @return the Game
   */
  public Game getGame() {
    return platform.getGame();
  }

  /**
   * Gets the Settings instance of this MyWarp instance.
   *
   * @return the Settings
   */
  public Settings getSettings() {
    return platform.getSettings();
  }

  /**
   * Gets the WarpManager of this MyWarp instance.
   *
   * @return the WarpManager
   */
  public WarpManager getWarpManager() {
    return warpManager;
  }

  /**
   * Gets the AuthorizationResolver instance of this MyWarp instance.
   *
   * @return the AuthorizationResolver
   */
  public AuthorizationResolver getAuthorizationResolver() {
    return authorizationResolver;
  }

  /**
   * Gets the PlayerNameResolver instance of this MyWarp instance.
   *
   * @return the PlayerNameResolver
   */
  public PlayerNameResolver getPlayerNameResolver() {
    return platform.getPlayerNameResolver();
  }

  /**
   * Gets the TeleportHandler instance of this MyWarp instance.
   *
   * @return the teleportHandler
   */
  public TeleportHandler getTeleportHandler() {
    return teleportHandler;
  }

  /**
   * Creates a new WarpSignHandler that hooks into the PopulatableWarpManager configured for this MyWarp instance.
   *
   * @return a new WarpSign instance
   */
  public WarpSignHandler createWarpSignHandler() {
    return new WarpSignHandler(getSettings().getWarpSignsIdentifiers(), this,
                               platform.getCapability(EconomyCapability.class).orElse(null));
  }

  private void initializeMutableFields() {
    List<PositionValidationCapability> validationStrategies = new ArrayList<PositionValidationCapability>();
    validationStrategies.add(new LegacyPositionCorrectionCapability());
    platform.getCapability(PositionValidationCapability.class).ifPresent(validationStrategies::add);

    teleportHandler = new StrategicTeleportHandler(getSettings(), getGame(), validationStrategies);

    commandHandler = new CommandHandler(this, platform);

    if (getSettings().isInformPlayerOnInvitation()) {
      invitationInformationListener = new InvitationInformationListener(getGame());
      eventBus.register(invitationInformationListener);
    }
  }

  private void loadWarps() {
    //FIXME
    ListenableFuture<List<Warp>> futureWarps = dataService.getExecutorService().submit(warpStorage::getWarps);

    Futures.addCallback(futureWarps, new FutureCallback<Collection<Warp>>() {

      @Override
      public void onSuccess(Collection<Warp> result) {
        warpManager.populate(result);

        //notify platform
        platform.onWarpsLoaded();

        log.info("{} warps loaded.", warpManager.getNumberOfAllWarps());
      }

      @Override
      public void onFailure(Throwable throwable) {
        log.error("Failed to load warps from the database.", throwable);
      }

    }, platform.getGame().getExecutor());
  }
}
