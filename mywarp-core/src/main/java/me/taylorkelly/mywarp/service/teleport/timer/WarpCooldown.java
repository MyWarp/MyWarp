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

package me.taylorkelly.mywarp.service.teleport.timer;

import com.google.common.base.Optional;

import me.taylorkelly.mywarp.platform.Game;
import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.service.teleport.TimerTeleportService;
import me.taylorkelly.mywarp.util.i18n.DynamicMessages;
import me.taylorkelly.mywarp.util.i18n.LocaleManager;

import java.util.UUID;

/**
 * A cooldown that blocks a player from using warps.
 */
public class WarpCooldown extends TimerAction<UUID> {

  private static final DynamicMessages msg = new DynamicMessages(TimerTeleportService.RESOURCE_BUNDLE_NAME);

  private final Game game;
  private final boolean notifyOnFinish;

  /**
   * Creates an instance for the given {@code player}.
   *
   * @param player         the player who is cooling down
   * @param game           the active game
   * @param notifyOnFinish whether the {@code player} should be notified once the cooldown finishes
   */
  public WarpCooldown(LocalPlayer player, Game game, boolean notifyOnFinish) {
    super(player.getUniqueId());
    this.game = game;
    this.notifyOnFinish = notifyOnFinish;
  }

  @Override
  public void run() {
    if (notifyOnFinish) {
      Optional<LocalPlayer> optionalPlayer = game.getPlayer(getTimedSuject());

      if (optionalPlayer.isPresent()) {
        LocalPlayer player = optionalPlayer.get();
        LocaleManager.setLocale(player.getLocale());
        player.sendMessage(msg.getString("warp-cooldown.ended"));
      }
    }
  }

}
