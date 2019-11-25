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

package io.github.mywarp.mywarp.bukkit.settings;

import com.google.common.collect.ImmutableSet;

import io.github.mywarp.mywarp.bukkit.util.jdbc.JdbcConfiguration;
import io.github.mywarp.mywarp.platform.InvalidFormatException;
import io.github.mywarp.mywarp.platform.Settings;
import io.github.mywarp.mywarp.util.MyWarpLogger;
import io.github.mywarp.mywarp.util.WarpUtils;
import io.github.mywarp.mywarp.warp.Warp;

import org.apache.commons.lang.LocaleUtils;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

/**
 * The settings when running on Bukkit. This implementation relies on Bukkit's configuration API to manage the actual
 * configuration file.
 */
public class BukkitSettings implements Settings {

  private static final Logger log = MyWarpLogger.getLogger(BukkitSettings.class);

  private final File configFile;
  private final Configuration defaultConfiguration;

  private Configuration config;
  private Locale defaultLocale;

  /**
   * Initializes this instance.
   *
   * @param configFile           the file that holds the configuration
   * @param defaultConfiguration the default configuration that acts as a fallback
   */
  public BukkitSettings(File configFile, Configuration defaultConfiguration) {
    this.configFile = configFile;
    this.defaultConfiguration = defaultConfiguration;

    reload();
  }

  /**
   * Reloads the configuration.
   */
  public void reload() {
    config = createConfiguration();

    // add defaults
    config.options().copyDefaults(true);

    config.setDefaults(defaultConfiguration);
    config.addDefault("storage.url", "jdbc:h2:" + configFile.getParentFile().getAbsolutePath() + "/warps");

    //save defaults if we are reading from a file
    if (config instanceof FileConfiguration) {
      try {
        ((FileConfiguration) config).save(configFile);
      } catch (IOException e) {
        log.error(String.format("Failed to save configuration to '%1$s', using build-in defaults for missing values.",
                                configFile.getAbsolutePath()), e);
      }
    }

    // Bukkit's config does not support Locale objects and this call is quit
    // expensive so we cache the Locale
    String configuredLocale = config.getString("localization.defaultLocale");
    defaultLocale = LocaleUtils.toLocale(configuredLocale);
  }

  private Configuration createConfiguration() {
    if (!configFile.exists()) {
      try {
        if (!configFile.createNewFile()) {
          throw new IOException("The file already exists.");
        }
        log.info("Default '{}' created successfully.", configFile.getName());
      } catch (IOException e) {
        log.error(String.format(
            "Failed to create the configuration file ('%1$s'), using build-in defaults for all values.",
            configFile.getAbsolutePath()), e);
        return defaultConfiguration;
      }
    }
    return YamlConfiguration.loadConfiguration(configFile);
  }

  @Override
  public boolean isControlWorldAccess() {
    return config.getBoolean("settings.controlWorldAccess");
  }

  @Override
  public boolean isShowTeleportEffect() {
    return config.getBoolean("settings.showTeleportEffect");
  }

  @Override
  public boolean isCaseSensitiveWarpNames() {
    return config.getBoolean("settings.caseSensitiveWarpNames");
  }

  @Override
  public boolean isTeleportTamedHorses() {
    return config.getBoolean("settings.teleportHorses");
  }

  @Override
  public Locale getLocalizationDefaultLocale() {
    return defaultLocale;
  }

  @Override
  public boolean isLocalizationPerPlayer() {
    return config.getBoolean("localization.perPlayer");
  }

  /**
   * Returns whether safety checks for teleports are enabled.
   *
   * @return {@code true} if the location's safety should be checked before teleporting an entity
   */
  public boolean isSafetyEnabled() {
    return config.getBoolean("teleportSafety.enabled");
  }

  /**
   * Gets the radius that is used to search a safe location.
   *
   * @return the search radius
   */
  public int getSafetySearchRadius() {
    return config.getInt("teleportSafety.searchRadius");
  }

  @Override
  public boolean isWarpSignsEnabled() {
    return config.getBoolean("warpSigns.enabled");
  }

  @Override
  public ImmutableSet<String> getWarpSignsIdentifiers() {
    return ImmutableSet.copyOf(config.getStringList("warpSigns.identifiers"));
  }

  /**
   * Returns whether warp creation limit are enabled.
   *
   * @return {@code true} if limit are enabled
   */
  public boolean isLimitsEnabled() {
    return config.getBoolean("limits.enabled");
  }

  /**
   * Gets the default LimitBundle.
   *
   * @return the default LimitBundle.
   */
  public LimitBundle getLimitsDefaultLimitBundle() {
    return LimitBundle.createGlobal("default", config.getConfigurationSection("limits.defaultLimit"));
  }

  /**
   * Gets a list of additional configured LimitBundles.
   *
   * @return all configured LimitBundle
   */
  public List<LimitBundle> getLimitsConfiguredLimitBundles() {
    List<LimitBundle> ret = new ArrayList<LimitBundle>();

    @Nullable ConfigurationSection configuredLimits = config.getConfigurationSection("limits.configuredLimits");
    if (configuredLimits == null) {
      return ret;
    }

    for (String key : configuredLimits.getKeys(false)) {
      @Nullable ConfigurationSection section = configuredLimits.getConfigurationSection(key);
      if (section == null) {
        log.warn("The configuration section with the key '{}' does not contain any readable values and will be ignored."
                 + " Is your configuration file correctly formatted?", key);
        continue;
      }
      ret.add(LimitBundle.create(key, section));
    }
    return ret;
  }

  /**
   * Returns whether timers are enabled.
   *
   * @return {@code true} if timers are enabled
   */
  public boolean isTimersEnabled() {
    return config.getBoolean("timers.enabled");
  }

  /**
   * Returns whether timers should be used for warp signs, too.
   *
   * @return {@code true} if timers should be used for signs
   */
  public boolean isTimersEnabledForSigns() {
    return config.getBoolean("timers.forSigns");
  }

  /**
   * Returns whether the warp-cooldown should notify users when they have cooled down.
   *
   * @return {@code true} if users should be notified
   */
  public boolean isTimersCooldownNotifyOnFinish() {
    return config.getBoolean("timers.warpCooldown.notifyOnFinish");
  }

  /**
   * Returns whether the warp-warmup should be aborted if the users takes any damage while warming up.
   *
   * @return {@code true} if the warp-warmuo should be aborted
   */
  public boolean isTimersWarmupAbortOnDamage() {
    return config.getBoolean("timers.warpWarmup.abortOnDamage");
  }

  /**
   * Returns whether the warp-warmup should be aborted if the user moves while warming up.
   *
   * @return {@code true} if the warp-warmup should be aborted
   */
  public boolean isTimersWarmupAbortOnMove() {
    return config.getBoolean("timers.warpWarmup.abortOnMove");
  }

  /**
   * Returns whether the warp-warmup should notify users when the warmup starts.
   *
   * @return {@code true} if users should be notified
   */
  public boolean isTimersWarmupNotifyOnStart() {
    return config.getBoolean("timers.warpWarmup.notifyOnStart");
  }

  /**
   * Gets the default DurationBundle.
   *
   * @return the default DurationBundle
   */
  public DurationBundle getTimersDefaultDurationBundle() {
    return DurationBundle.create("default", config.getConfigurationSection("timers.defaultTimer"));

  }

  /**
   * Gets a list of additional configured DurationBundles.
   *
   * @return all configured DurationBundles
   */
  public List<DurationBundle> getTimersConfiguredDurationBundles() {
    List<DurationBundle> ret = new ArrayList<DurationBundle>();

    @Nullable ConfigurationSection configuredTimers = config.getConfigurationSection("timers.configuredTimers");
    if (configuredTimers == null) {
      return ret;
    }

    for (String key : configuredTimers.getKeys(false)) {
      @Nullable ConfigurationSection section = configuredTimers.getConfigurationSection(key);
      if (section == null) {
        log.warn("The configuration section with the key '{}' does not contain any readable values and will be ignored."
                 + " Is your configuration file correctly formatted?", key);
        continue;
      }
      ret.add(DurationBundle.create(key, configuredTimers.getConfigurationSection(key)));
    }
    return ret;
  }

  /**
   * Returns whether economy support is enabled.
   *
   * @return {@code true} if economy support is enabled
   */
  public boolean isEconomyEnabled() {
    return config.getBoolean("economy.enabled");
  }

  /**
   * Returns whether the economy link should inform users after successful transactions.
   *
   * @return {@code {@code true}} if users should be informed after a transaction
   */
  public boolean isEconomyInformAfterTransaction() {
    return config.getBoolean("economy.informAfterTransaction");
  }

  /**
   * Gets the configuration for JDBC access to the database that stores warps.
   *
   * @return the configuration
   * @throws InvalidFormatException if the configuration has an invalid format
   */
  public JdbcConfiguration getJdbcStorageConfiguration() throws InvalidFormatException {
    return JdbcConfiguration.fromConfig(config.getConfigurationSection("storage"));
  }

  @Override
  public boolean isInformPlayerOnInvitation() {
    return config.getBoolean("settings.informPlayersOnInvitation");
  }

  @Override
  public Comparator<Warp> getDefaultListComparator() {
    return WarpUtils.getComparator(config.getString("settings.defaultListComparator"));
  }

  /**
   * Gets the default FeeBundle.
   *
   * @return the default FeeBundle
   */
  public FeeBundle getEconomyDefaultFeeBundle() {
    return FeeBundle.create("default", config.getConfigurationSection("economy.defaultFee"));

  }

  /**
   * Gets a list of additional configured FeeBundles.
   *
   * @return all configured FeeBundles
   */
  public List<FeeBundle> getEconomyConfiguredFeeBundles() {
    List<FeeBundle> ret = new ArrayList<FeeBundle>();
    ConfigurationSection configuredFees = config.getConfigurationSection("economy.configuredFees");

    if (configuredFees == null) {
      return ret;
    }
    for (String key : configuredFees.getKeys(false)) {
      @Nullable ConfigurationSection section = configuredFees.getConfigurationSection(key);
      if (section == null) {
        log.warn("The configuration section with the key '{}' does not contain any readable values and will be ignored."
                 + " Is your configuration file correctly formatted?", key);
        continue;
      }
      ret.add(FeeBundle.create(key, configuredFees.getConfigurationSection(key)));
    }
    return ret;
  }

  /**
   * Returns whether Dynmap should be used as marker-service.
   *
   * @return true if Dynmap should be used
   */
  public boolean isDynmapEnabled() {
    return config.getBoolean("dynmap.enabled");
  }

  /**
   * Gets the display name of the layer that stores MyWarp's markers.
   *
   * @return the layer's display name
   */
  public String getDynmapLayerDisplayName() {
    return config.getString("dynmap.layer.displayName");
  }

  /**
   * Returns whether the layer that stores MyWarp's markers is hidden by default.
   *
   * @return true if the layer is hidden by default
   */
  public boolean isDynmapLayerHiddenByDefault() {
    return config.getBoolean("dynmap.layer.hiddenByDefault");
  }

  /**
   * Gets the priority of the layer that stores MyWarp's markers.
   *
   * @return the layer's priority
   */
  public int getDynmapLayerPriority() {
    return config.getInt("dynmap.layer.priority");
  }

  /**
   * Gets the Dynmap identifier of the icon that is used for MyWarp's markers.
   *
   * @return the icon's identifier
   */
  public String getDynmapMarkerIconId() {
    return config.getString("dynmap.marker.iconID");
  }

  /**
   * Gets the minimal zoom level that must be meat to display MyWarp's markers.
   *
   * @return the minimal zoom level
   */
  public int getDynmapMarkerMinZoom() {
    return config.getInt("dynmap.marker.minZoom");
  }

  /**
   * Returns whether MyWarp's markers should show a label by default.
   *
   * @return true if the label is visible by default
   */
  public boolean isDynmapMarkerShowLable() {
    return config.getBoolean("dynmap.marker.showLabel");
  }

}
