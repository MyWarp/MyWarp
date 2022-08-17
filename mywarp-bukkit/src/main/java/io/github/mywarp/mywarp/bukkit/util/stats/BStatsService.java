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

package io.github.mywarp.mywarp.bukkit.util.stats;

import io.github.mywarp.mywarp.bukkit.MyWarpPlugin;
import io.github.mywarp.mywarp.bukkit.settings.BukkitSettings;
import io.github.mywarp.mywarp.bukkit.util.jdbc.JdbcConfiguration;
import io.github.mywarp.mywarp.warp.WarpManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimpleBarChart;
import org.bstats.charts.SimplePie;
import org.bstats.charts.SingleLineChart;

import java.util.HashMap;
import java.util.Map;

/**
 * Collects statistics using bStats.
 *
 * @see <a href="http://bstats.org">http://bstats.org</a>
 */
class BStatsService implements StatisticService {

  private static final int PLUGIN_ID = 16163;

  private final Metrics metrics;

  BStatsService(MyWarpPlugin plugin) {
    this.metrics = new Metrics(plugin, PLUGIN_ID);
  }

  @Override
  public String getServiceName() {
    return "bStats (www.bstats.org)";
  }

  @Override
  public void addFeatureChart(BukkitSettings settings) {
    metrics.addCustomChart(new SimpleBarChart("usedFeatures", () -> {
      Map<String, Integer> map = new HashMap<>();
      map.put("TeleportSafety", parse(settings.isSafetyEnabled()));
      map.put("WarpSigns", parse(settings.isWarpSignsEnabled()));
      map.put("Limits", parse(settings.isLimitsEnabled()));
      map.put("Economy Support", parse(settings.isEconomyEnabled()));
      map.put("Timers", parse(settings.isTimersEnabled()));
      map.put("Dynmap Integration", parse(settings.isDynmapEnabled()));
      return map;
    }));
  }

  @Override
  public void addDbmsChart(JdbcConfiguration config) {
    metrics.addCustomChart(new SimplePie("sqlProtocol", config::getProtocol));
  }

  @Override
  public void addWarpChart(WarpManager manager) {
    metrics.addCustomChart(new SingleLineChart("numberOfWarps", manager::getNumberOfAllWarps));
  }

  private int parse(boolean enabled) {
    if (enabled) {
      return 1;
    } else {
      return 0;
    }
  }

}
