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

import io.github.mywarp.mywarp.bukkit.settings.BukkitSettings;
import io.github.mywarp.mywarp.bukkit.settings.DurationBundle;
import io.github.mywarp.mywarp.bukkit.util.BukkitTimerHandler;
import io.github.mywarp.mywarp.bukkit.util.permission.BundleProvider;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.capability.TimerCapability;
import io.github.mywarp.mywarp.service.teleport.timer.TimerAction;
import java.time.Duration;

/**
 * Timer compatibility for the Bukkit platform.
 */
public class BukkitTimerCapability implements TimerCapability {

  private final BukkitTimerHandler timerHandler;
  private final BundleProvider<DurationBundle> durationProvider;
  private final BukkitSettings settings;

  BukkitTimerCapability(MyWarpPlugin plugin, BundleProvider<DurationBundle> durationProvider, BukkitSettings settings) {
    this.timerHandler = new BukkitTimerHandler(plugin);
    this.durationProvider = durationProvider;
    this.settings = settings;
  }

  @Override
  public <T> void start(T timedSubject, Duration duration, TimerAction<T> timer) {
    timerHandler.start(timedSubject, duration, timer);
  }

  @Override
  public TimerCapability.EvaluationResult has(Object timedSubject, Class<? extends TimerAction<?>> timerClass) {
    return timerHandler.has(timedSubject, timerClass);
  }

  @Override
  public Duration getDuration(LocalPlayer player, Class<? extends TimerAction<?>> timerClass) {
    return durationProvider.getBundle(player).get(timerClass);
  }

  @Override
  public boolean notifyOnCooldownFinish() {
    return settings.isTimersCooldownNotifyOnFinish();
  }

  @Override
  public boolean abortWarmupOnDamage() {
    return settings.isTimersWarmupAbortOnDamage();
  }

  @Override
  public boolean abortWarmupOnMove() {
    return settings.isTimersWarmupAbortOnMove();
  }

  @Override
  public boolean notifyOnWarmupStart() {
    return settings.isTimersWarmupNotifyOnStart();
  }
}
