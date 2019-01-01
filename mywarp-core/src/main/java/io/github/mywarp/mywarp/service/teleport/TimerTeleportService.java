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

package io.github.mywarp.mywarp.service.teleport;

import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.capability.TimerCapability;
import io.github.mywarp.mywarp.service.teleport.timer.WarpCooldown;
import io.github.mywarp.mywarp.service.teleport.timer.WarpWarmup;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.teleport.TeleportHandler;
import io.github.mywarp.mywarp.warp.Warp;

import java.time.Duration;

/**
 * Sets timers for users who want to teleport.
 *
 * <p>Only {@link LocalPlayer}s without the disobey permission need to warmup when teleporting or cooldown after
 * teleporting (or both). The teleport itself is delegated to another teleportService.</p>
 */
public class TimerTeleportService extends ForwardingTeleportService {

  public static final String RESOURCE_BUNDLE_NAME = "io.github.mywarp.mywarp.lang.Timers";

  private static final DynamicMessages msg = new DynamicMessages(RESOURCE_BUNDLE_NAME);

  private final TeleportService delegate;
  private final Game game;
  private final TimerCapability capability;

  /**
   * Creates an instance that uses the given TimerService and the given DurationProvider to create and resolve timers.
   * Teleports are delegated to the given TeleportService.
   *
   * @param delegate   the TeleportService to delegate teleports to
   * @param game       the current Game instance
   * @param capability the TimerService
   */
  public TimerTeleportService(TeleportService delegate, Game game, TimerCapability capability) {
    this.delegate = delegate;
    this.game = game;
    this.capability = capability;
  }

  @Override
  public TeleportHandler.TeleportStatus teleport(LocalEntity entity, Warp warp) {

    if (canDisobeyTimers(entity)) {
      return delegate().teleport(entity, warp);
    }

    LocalPlayer player = (LocalPlayer) entity;

    // check for already running timers
    TimerCapability.EvaluationResult cooldownResult = capability.has(player.getUniqueId(), WarpCooldown.class);
    if (cooldownResult.isTimerRunning()) {
      player.sendError(msg.getString("timer-already-running", cooldownResult.getDurationLeft().getSeconds()));
      return TeleportHandler.TeleportStatus.NONE;
    }
    TimerCapability.EvaluationResult warmupResult = capability.has(player.getUniqueId(), WarpWarmup.class);
    if (warmupResult.isTimerRunning()) {
      player.sendError(msg.getString("timer-already-running", warmupResult.getDurationLeft().getSeconds()));
      return TeleportHandler.TeleportStatus.NONE;
    }

    // start warmup
    Duration duration = capability.getDuration(player, WarpWarmup.class);
    capability.start(player.getUniqueId(), duration, new WarpWarmup(player, warp, game, delegate(), capability));
    if (capability.notifyOnWarmupStart()) {
      player.sendMessage(msg.getString("warp-warmup.started", warp.getName(), duration.getSeconds()));
    }

    // teleport will be scheduled by WarpWarmup once the warmup ended
    return TeleportHandler.TeleportStatus.NONE;
  }

  @Override
  protected TeleportService delegate() {
    return delegate;
  }

  private boolean canDisobeyTimers(LocalEntity entity) {
    if (!(entity instanceof LocalPlayer)) {
      return true;
    }
    return ((LocalPlayer) entity).hasPermission("mywarp.timer.disobey");
  }
}
