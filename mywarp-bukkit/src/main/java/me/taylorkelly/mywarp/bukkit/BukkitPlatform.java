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

package me.taylorkelly.mywarp.bukkit;

import com.google.common.base.Optional;
import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import me.taylorkelly.mywarp.bukkit.settings.BukkitSettings;
import me.taylorkelly.mywarp.bukkit.settings.DurationBundle;
import me.taylorkelly.mywarp.bukkit.settings.FeeBundle;
import me.taylorkelly.mywarp.bukkit.util.permission.BundleProvider;
import me.taylorkelly.mywarp.platform.Platform;
import me.taylorkelly.mywarp.platform.capability.EconomyCapability;
import me.taylorkelly.mywarp.platform.capability.LimitCapability;
import me.taylorkelly.mywarp.platform.capability.PositionValidationCapability;
import me.taylorkelly.mywarp.platform.capability.TimerCapability;
import me.taylorkelly.mywarp.util.MyWarpLogger;
import me.taylorkelly.mywarp.warp.storage.ConnectionConfiguration;
import me.taylorkelly.mywarp.warp.storage.RelationalDataService;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.slf4j.Logger;

import java.io.File;

/**
 * The platform implementation for Bukkit.
 */
public class BukkitPlatform implements Platform {

  private static final Logger log = MyWarpLogger.getLogger(BukkitPlatform.class);

  private final MyWarpPlugin plugin;

  private final File dataFolder;
  private final BukkitSettings settings;
  private final BukkitGame game;
  private final SquirrelIdPlayerNameResolver profileCache;

  private final ClassToInstanceMap<Object> registeredCapabilities = MutableClassToInstanceMap.create();

  BukkitPlatform(MyWarpPlugin plugin, File dataFolder, FileConfiguration defaultConfig) {
    this.plugin = plugin;
    this.dataFolder = dataFolder;

    //initialize platform support
    this.settings = new BukkitSettings(new File(dataFolder, "config.yml"), defaultConfig);
    this.game = new BukkitGame(plugin, new BukkitExecutor(plugin));
    this.profileCache = new SquirrelIdPlayerNameResolver(new File(dataFolder, "profiles.db"));
  }

  @Override
  public File getDataFolder() {
    return dataFolder;
  }

  @Override
  public BukkitSettings getSettings() {
    return settings;
  }

  @Override
  public BukkitGame getGame() {
    return game;
  }

  @Override
  public SquirrelIdPlayerNameResolver getPlayerNameResolver() {
    return profileCache;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <C> Optional<C> getCapability(Class<C> capabilityClass) {
    C registered = registeredCapabilities.getInstance(capabilityClass);

    if (registered != null) {
      return Optional.of(registered);
    }

    //LimitCapability
    if (capabilityClass.isAssignableFrom(LimitCapability.class) && settings.isLimitsEnabled()) {
      LimitCapability
              limitCapability =
              new BukkitLimitCapability(settings.getLimitsConfiguredLimitBundles(),
                      settings.getLimitsDefaultLimitBundle());
      registeredCapabilities.putInstance(LimitCapability.class, limitCapability);
      registered = (C) limitCapability;
    }

    //EconomyCapability
    if (capabilityClass.isAssignableFrom(EconomyCapability.class) && settings.isEconomyEnabled()) {
      EconomyCapability economyCapability = null;
      try {
        RegisteredServiceProvider<Economy> serviceProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (serviceProvider != null) {
          BundleProvider<FeeBundle>
                  feeProvider =
                  new BundleProvider<FeeBundle>(settings.getEconomyConfiguredFeeBundles(),
                          settings.getEconomyDefaultFeeBundle());
          economyCapability = new BukkitEconomyCapability(serviceProvider.getProvider(), feeProvider, settings);
        } else {
          log.error("Failed to hook into Vault (Economy is null). Economy support will not be available.");
        }
      } catch (NoClassDefFoundError e) {
        log.error("Failed to hook into Vault (Economy Class not available). Economy support will not be available.");
      }

      if (economyCapability != null) {
        registeredCapabilities.putInstance(EconomyCapability.class, economyCapability);
        registered = (C) economyCapability;
      }
    }

    //TimerCapability
    if (capabilityClass.isAssignableFrom(TimerCapability.class) && settings.isTimersEnabled()) {
      BundleProvider<DurationBundle>
              durationProvider =
              new BundleProvider<DurationBundle>(settings.getTimersConfiguredDurationBundles(), settings

                      .getTimersDefaultDurationBundle());
      TimerCapability timerCapability = new BukkitTimerCapability(plugin, durationProvider, settings);
      registeredCapabilities.putInstance(TimerCapability.class, timerCapability);
      registered = (C) timerCapability;

    }

    //PositionSafetyCapability
    if (capabilityClass.isAssignableFrom(PositionValidationCapability.class) && settings.isSafetyEnabled()) {
      PositionValidationCapability
              positionValidationCapability =
              new CubicSafetyValidationCapability(settings.getSafetySearchRadius());
      registeredCapabilities.putInstance(PositionValidationCapability.class, positionValidationCapability);
      registered = (C) positionValidationCapability;
    }

    return Optional.fromNullable(registered);
  }

  @Override
  public RelationalDataService createDataService(ConnectionConfiguration configuration) {
    RelationalDataService ret = new SingleConnectionDataService(configuration);

    //add weak reference so it can be closed on shutdown if not done by the caller
    plugin.registerClosable(ret);

    return ret;
  }

  @Override
  public void onCoreReload() {
    // cleanup old stuff
    plugin.unregister();
    registeredCapabilities.clear();

    // load new stuff
    settings.reload();
    plugin.notifyCoreInitialized();
  }

  @Override
  public void onWarpsLoaded() {
    plugin.notifyWarpAvailability();
  }

}
