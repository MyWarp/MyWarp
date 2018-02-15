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

import org.bukkit.Bukkit;

import java.util.concurrent.Executor;

/**
 * An Executor for the Bukkit plattform. Given Runnables will be called by the main server thread at the next tick.
 */
public class BukkitExecutor implements Executor {

  private final MyWarpPlugin plugin;

  /**
   * Creates an instance.
   *
   * @param plugin the running plugin instance
   */
  BukkitExecutor(MyWarpPlugin plugin) {
    this.plugin = plugin;
  }

  @Override
  public void execute(Runnable command) {
    Bukkit.getScheduler().runTask(plugin, command);
  }

}
