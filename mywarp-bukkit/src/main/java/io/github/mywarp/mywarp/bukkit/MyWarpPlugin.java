/*
 * Copyright (C) 2011 - 2020, MyWarp team and contributors
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

package io.github.mywarp.mywarp.bukkit;

import static com.google.common.base.Preconditions.checkState;

import io.github.mywarp.mywarp.MyWarp;
import io.github.mywarp.mywarp.bukkit.settings.BukkitSettings;
import io.github.mywarp.mywarp.bukkit.util.conversation.AcceptancePromptFactory;
import io.github.mywarp.mywarp.bukkit.util.conversation.WelcomeEditorFactory;
import io.github.mywarp.mywarp.bukkit.util.jdbc.JdbcConfiguration;
import io.github.mywarp.mywarp.bukkit.util.material.ConfigurableMaterialInfo;
import io.github.mywarp.mywarp.bukkit.util.material.MaterialInfo;
import io.github.mywarp.mywarp.bukkit.util.permission.BukkitPermissionsRegistration;
import io.github.mywarp.mywarp.bukkit.util.permission.group.GroupResolver;
import io.github.mywarp.mywarp.bukkit.util.permission.group.GroupResolverFactory;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.InvalidFormatException;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.capability.TimerCapability;
import io.github.mywarp.mywarp.util.MyWarpLogger;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.i18n.FolderSourcedControl;
import io.github.mywarp.mywarp.util.i18n.LocaleManager;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.storage.SqlDataService;
import io.github.mywarp.mywarp.warp.storage.TableInitializationException;
import io.github.mywarp.mywarp.warp.storage.UnsupportedDialectException;
import java.io.Closeable;
import java.io.File;
import java.lang.ref.WeakReference;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.WeakHashMap;
import javax.annotation.Nullable;
import org.apache.commons.lang.text.StrBuilder;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.dynmap.DynmapCommonAPI;
import org.slf4j.Logger;

/**
 * The MyWarp plugin singleton when running on Bukkit.
 *
 * <p>This class is loaded by Bukkit when the plugin is initialized.</p>
 */
public final class MyWarpPlugin extends JavaPlugin {

  public static final String CONVERSATION_RESOURCE_BUNDLE_NAME = "io.github.mywarp.mywarp.lang.Conversations";
  public static final int CONVERSATION_TIMEOUT = 60;

  private static final Logger log = MyWarpLogger.getLogger(MyWarpPlugin.class);

  private final ResourceBundle.Control control = new FolderSourcedControl(new File(getDataFolder(), "lang"));
  private final Set<AutoCloseable> closeables = Collections.newSetFromMap(new WeakHashMap<AutoCloseable, Boolean>());

  private BukkitPlatform platform;
  private MyWarp myWarp;
  private GroupResolver groupResolver;
  private AcceptancePromptFactory acceptancePromptFactory;
  private WelcomeEditorFactory welcomeEditorFactory;

  @Nullable
  private DynmapMarker marker;

  @Override
  public void onEnable() {

    // initialize platform
    DynamicMessages.setControl(control);
    File dataFolder = getDataFolder();
    if (!dataFolder.exists()) {
      if (!dataFolder.mkdirs()) {
        log.error("Failed to create MyWarp's data-folder: " + dataFolder);
        log.error("MyWarp is unable to continue and will be disabled.");
        Bukkit.getPluginManager().disablePlugin(this);
        return;
      }
    }
    platform = new BukkitPlatform(this, dataFolder, YamlConfiguration.loadConfiguration(getTextResource("config.yml")));

    // setup the core
    try {
      myWarp = MyWarp.initialize(platform, createDataService(getSettings().getJdbcStorageConfiguration()));
    } catch (InvalidFormatException | UnsupportedDialectException | SQLException | TableInitializationException e) {
      log.error("Failed to initialize warp storage.");
      log.error(e.getLocalizedMessage(), e);
      log.error("MyWarp is unable to continue and will be disabled.");
      Bukkit.getPluginManager().disablePlugin(this);
      return;
    }

    // further platform-specific objects
    groupResolver = GroupResolverFactory.createResolver();
    acceptancePromptFactory =
        new AcceptancePromptFactory(createConversationFactory(), myWarp.getAuthorizationResolver(), platform.getGame(),
            platform.getPlayerNameResolver(), this);
    welcomeEditorFactory = new WelcomeEditorFactory(createConversationFactory());

    notifyCoreInitialized();
  }

  @Override
  public void onDisable() {
    unregister();

    //close any registered Closables
    for (AutoCloseable closeable : closeables) {
      try {
        closeable.close();
      } catch (Exception e) {
        log.warn("Failed to close " + closeable.getClass().getCanonicalName(), e);
      }
    }
  }

  /**
   * Notifies the MyWarpPlugin instance that the core is fully initialized.
   */
  void notifyCoreInitialized() {

    //register profile service listener
    getProfileCache().registerEvents(this);

    //register warp sign listener
    if (getSettings().isWarpSignsEnabled()) {
      TimerCapability capability = null;
      if (getSettings().isTimersEnabledForSigns()) {
        capability = platform.getCapability(TimerCapability.class).orElse(null);
      }
      platform.getCapability(TimerCapability.class);
      new WarpSignListener(this, myWarp.createWarpSignHandler(capability), createMaterialInformation())
          .registerEvents(this);
    }

    // register world access permissions
    for (World loadedWorld : Bukkit.getWorlds()) {
      Permission perm = new Permission("mywarp.world-access." + loadedWorld.getName());
      perm.addParent("mywarp.world-access.*", true);
      BukkitPermissionsRegistration.INSTANCE.register(perm);
    }
  }

  /**
   * Notifies the MyWarpPlugin instance about the availability of warps, so that it can execute additional callback (if
   * any).
   */
  void notifyWarpAvailability() {
    if (getSettings().isDynmapEnabled()) {
      Plugin dynmap = getServer().getPluginManager().getPlugin("dynmap");
      if (dynmap != null && dynmap.isEnabled() && dynmap instanceof DynmapCommonAPI) {
        marker = new DynmapMarker((DynmapCommonAPI) dynmap, this, platform, w -> w.isType(Warp.Type.PUBLIC));
        marker.addMarker(myWarp.getWarpManager().getAll(warp -> true));
        myWarp.getEventBus().register(marker);
      } else {
        log.error("Failed to hook into Dynmap. Disabling Dynmap support.");
      }
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    Actor actor = wrap(sender);

    // set the locale for this command session
    LocaleManager.setLocale(actor.getLocale());

    // create the command string
    StrBuilder builder = new StrBuilder();
    builder.append(label);
    for (String argument : args) {
      builder.appendSeparator(' ');
      builder.append(argument);
    }
    myWarp.getCommandHandler().callCommand(builder.toString(), actor);
    return true;
  }

  @Override
  public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
    Actor actor = wrap(sender);

    // set the locale for this command session
    LocaleManager.setLocale(actor.getLocale());

    // create the command string
    StrBuilder builder = new StrBuilder();
    builder.append(alias);
    for (String argument : args) {
      builder.appendSeparator(' ');
      builder.append(argument);
    }
    return myWarp.getCommandHandler().getSuggestions(builder.toString(), actor);
  }

  /**
   * Creates a new Actor instance by wrapping the given Bukkit {@code commandSender}.
   *
   * @param commandSender the CommandSender to wrap
   * @return a new Actor referencing the {@code commandSender}
   */
  public Actor wrap(CommandSender commandSender) {
    if (commandSender instanceof Player) {
      return wrap((Player) commandSender);
    }
    return new BukkitActor(commandSender, getSettings());
  }

  /**
   * Creates a new LocalPlayer instance by wrapping the given Bukkit {@code player}.
   *
   * @param player the Player to wrap
   * @return a new LocalPlayer referencing the {@code player}
   */
  public LocalPlayer wrap(Player player) {
    return new BukkitPlayer(player, getAcceptancePromptFactory(), getWelcomeEditorFactory(), getGroupResolver(),
        getSettings());
  }

  /**
   * Gets the GroupResolver that resolve's a player's group.
   *
   * @return the configured GroupResolver
   */
  GroupResolver getGroupResolver() {
    checkState(groupResolver != null, "'groupResolver' is not yet initialized");
    return groupResolver;
  }

  /**
   * Gets the conversation factory for welcome editor conversations.
   *
   * @return the configured welcome editor factory
   */
  WelcomeEditorFactory getWelcomeEditorFactory() {
    checkState(welcomeEditorFactory != null, "'welcomeEditorFactory' is not yet initialized");
    return welcomeEditorFactory;
  }

  /**
   * Gets the conversation factory for warp acceptance conversations.
   *
   * @return the configured acceptance prompt factory
   */
  AcceptancePromptFactory getAcceptancePromptFactory() {
    checkState(acceptancePromptFactory != null, "'acceptancePromptFactory' is not yet initialized");
    return acceptancePromptFactory;
  }

  /**
   * Gets the BukkitSettings instance that provides access to MyWarp's settings.
   *
   * @return the configured settings
   */
  protected BukkitSettings getSettings() {
    checkState(platform != null, "'platform' is not yet initialized");
    return platform.getSettings();
  }

  /**
   * Gets the PlayerNameResolver that stored Profiles for known players.
   *
   * @return the configured PlayerNameResolver
   */
  SquirrelIdPlayerNameResolver getProfileCache() {
    checkState(platform != null, "'platform' is not yet initialized");
    return platform.getPlayerNameResolver();
  }

  /**
   * Registers the given {@code closable} for closure when the plugin is disabled.
   *
   * <p>Registered Closables will be stored within a {@link WeakReference}. If MyWarp is disabled by
   * Bukkit and the reference is still valid, {@link Closeable#close()} is invoked.</p>
   *
   * @param closeable the Closable to register
   */
  void registerClosable(AutoCloseable closeable) {
    closeables.add(closeable);
  }

  private ConversationFactory createConversationFactory() {
    return new ConversationFactory(this).withModality(true).withTimeout(CONVERSATION_TIMEOUT);
  }

  /**
   * Unregisters all permissions registered by MyWarp, all active event-listeners and all created markers (if any).
   */
  void unregister() {
    HandlerList.unregisterAll(this);
    BukkitPermissionsRegistration.INSTANCE.unregisterAll();

    if (marker != null) {
      marker.clear();
    }
  }

  SqlDataService createDataService(JdbcConfiguration configuration) {
    SqlDataService ret = new SingleConnectionDataService(configuration);

    //add weak reference so it can be closed on shutdown if not done by the caller
    registerClosable(ret);

    return ret;
  }

  MaterialInfo createMaterialInformation() {
    return new ConfigurableMaterialInfo(YamlConfiguration.loadConfiguration(getTextResource("material-info.yml")));
  }

}
