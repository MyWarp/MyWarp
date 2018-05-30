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

package io.github.mywarp.mywarp.bukkit.util.stats;

import io.github.mywarp.mywarp.bukkit.MyWarpPlugin;
import io.github.mywarp.mywarp.bukkit.settings.BukkitSettings;
import io.github.mywarp.mywarp.bukkit.util.jdbc.JdbcConfiguration;
import io.github.mywarp.mywarp.warp.WarpManager;

/**
 * Handles the collection of statistics using external service provider.
 */
public interface StatisticService {

  /**
   * Creates an instance for an appropriate external service provider.
   *
   * @param plugin the plugin instance
   * @return a new statistic service
   */
  static StatisticService create(MyWarpPlugin plugin) {
    return new BStatsService(plugin);
  }

  /**
   * Gets the human readable name of the external service provider and, if appropriate, its URL.
   *
   * @return the name of the service provider
   */
  String getServiceName();

  /**
   * Adds a chart that tracks whether MyWarp's feature are enabled or disabled.
   *
   * <p>Implementations should track if the following features are enable:
   * <ul>
   * <li>TeleportSafety</li>
   * <li>WarpSigns</li>
   * <li>Limits</li>
   * <li>Economy Support</li>
   * <li>Timers</li>
   * <li>Dynmap Integration"</li>
   * </ul></p>
   *
   * @param settings the settings used
   */
  void addFeatureChart(BukkitSettings settings);

  /**
   * Adds a chart that tracks which DBMS is used.
   *
   * @param configuration the database configuration used
   */
  void addDbmsChart(JdbcConfiguration configuration);

  /**
   * Adds a chart that tracks how many warps are handled by MyWarp.
   *
   * @param manager the WarpManager used
   */
  void addWarpChart(WarpManager manager);
}
