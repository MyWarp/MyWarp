/*
 * Copyright (C) 2011 - 2022, MyWarp team and contributors
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

package io.github.mywarp.mywarp.service.teleport.timer;

import com.flowpowered.math.vector.Vector3d;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.capability.TimerCapability;
import io.github.mywarp.mywarp.service.teleport.TeleportService;
import io.github.mywarp.mywarp.service.teleport.TimerTeleportService;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.i18n.LocaleManager;
import io.github.mywarp.mywarp.warp.Warp;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

/**
 * A warmup that teleports a player to a warp when done.
 */
public class WarpWarmup extends AbortableTimerAction<UUID> {

  private static final int ALLOWED_DISTANCE = 2;

  private static final DynamicMessages msg = new DynamicMessages(TimerTeleportService.RESOURCE_BUNDLE_NAME);

  private final Warp warp;
  private final Vector3d initialPosition;
  private final double initialHealth;
  private final Game game;
  private final TeleportService teleportService;
  private final TimerCapability capability;

  /**
   * Creates an instance for the given {@code player} and {@code warp}.
   *
   * @param player          the player who is cooling down
   * @param warp            the warp that the player wants to use
   * @param game            the game the teleport happens within
   * @param teleportService the teleportService to use
   * @param capability      platform's capability to run timers
   */
  public WarpWarmup(LocalPlayer player, Warp warp, Game game, TeleportService teleportService,
      TimerCapability capability) {
    super(player.getUniqueId());
    this.warp = warp;
    this.game = game;
    this.teleportService = teleportService;
    this.capability = capability;
    this.initialPosition = player.getPosition();
    this.initialHealth = player.getHealth();
  }

  @Override
  public void run() {
    Optional<LocalPlayer> optionalPlayer = game.getPlayer(getTimedSuject());
    if (!optionalPlayer.isPresent()) {
      return;
    }
    LocalPlayer player = optionalPlayer.get();
    LocaleManager.setLocale(player.getLocale());

    if (teleportService.teleport(player, warp).isPositionModified()) {
      Duration duration = capability.getDuration(player, WarpCooldown.class);
      capability
          .start(player.getUniqueId(), duration, new WarpCooldown(player, game, capability.notifyOnCooldownFinish()));
    }
  }

  @Override
  public boolean abort() {
    Optional<LocalPlayer> player = game.getPlayer(getTimedSuject());
    // player is not online, but might re-login so the timer continues
    return player.isPresent() && (abortOnMove(player.get()) || abortOnDamage(player.get()));
  }

  /**
   * Returns whether the warmup should be aborted because the player has moved.
   *
   * @param player the LocalPlayer
   * @return true if the warmup should be aborted
   */
  private boolean abortOnMove(LocalPlayer player) {
    if (!capability.abortWarmupOnMove() || player.hasPermission("mywarp.timer.disobey.warmup-abort.move")) {
      return false;
    }
    if (player.getPosition().distanceSquared(initialPosition) <= Math.pow(ALLOWED_DISTANCE, 2)) {
      return false;
    }
    LocaleManager.setLocale(player.getLocale());
    player.sendError(msg.getString("warp-warmup.cancelled.move"));
    return true;
  }

  /**
   * Returns whether the warmup should be aborted because the player has taken damage.
   *
   * @param player the LocalPlayer
   * @return true if the warmup should be aborted
   */
  private boolean abortOnDamage(LocalPlayer player) {
    if (!capability.abortWarmupOnDamage() || player.hasPermission("mywarp.timer.disobey.warmup-abort.damage")) {
      return false;
    }
    if (player.getHealth() >= initialHealth) {
      return false;
    }
    LocaleManager.setLocale(player.getLocale());
    player.sendError(msg.getString("warp-warmup.cancelled.damage"));
    return true;
  }

}
