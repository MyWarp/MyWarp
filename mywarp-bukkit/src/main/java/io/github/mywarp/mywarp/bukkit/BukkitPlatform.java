/*
 * Copyright (C) 2011 - 2018, MyWarp team and contributors
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

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

import io.github.mywarp.mywarp.bukkit.settings.BukkitSettings;
import io.github.mywarp.mywarp.bukkit.settings.DurationBundle;
import io.github.mywarp.mywarp.bukkit.settings.FeeBundle;
import io.github.mywarp.mywarp.bukkit.util.jdbc.JdbcConfiguration;
import io.github.mywarp.mywarp.bukkit.util.permission.BundleProvider;
import io.github.mywarp.mywarp.platform.InvalidFormatException;
import io.github.mywarp.mywarp.platform.Platform;
import io.github.mywarp.mywarp.platform.capability.EconomyCapability;
import io.github.mywarp.mywarp.platform.capability.LimitCapability;
import io.github.mywarp.mywarp.platform.capability.PositionValidationCapability;
import io.github.mywarp.mywarp.platform.capability.TimerCapability;
import io.github.mywarp.mywarp.util.MyWarpLogger;
import io.github.mywarp.mywarp.warp.storage.SqlDataService;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.slf4j.Logger;

import java.io.File;
import java.util.Optional;

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
    plugin.registerClosable(profileCache);
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
          new BukkitLimitCapability(settings.getLimitsConfiguredLimitBundles(), settings.getLimitsDefaultLimitBundle());
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
              new BundleProvider<>(settings.getEconomyConfiguredFeeBundles(), settings.getEconomyDefaultFeeBundle());
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
          new BundleProvider<>(settings.getTimersConfiguredDurationBundles(), settings

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

    return Optional.ofNullable(registered);
  }

  @Override
  public SqlDataService createDataService(String config) throws InvalidFormatException {
    return plugin.createDataService(JdbcConfiguration.fromString(config));
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
