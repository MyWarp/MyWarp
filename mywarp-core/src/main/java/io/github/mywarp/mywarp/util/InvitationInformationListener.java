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

package io.github.mywarp.mywarp.util;

import com.google.common.eventbus.Subscribe;

import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.event.WarpInvitesEvent;

/**
 * Listens for (un)invitations and informs affected players.
 */
public class InvitationInformationListener {

  private static final DynamicMessages msg = new DynamicMessages("io.github.mywarp.mywarp.lang.Invitations");

  private final Game game;

  /**
   * Creates an instance that uses the given {@code game}.
   *
   * @param game the running game
   */
  public InvitationInformationListener(Game game) {
    this.game = game;
  }

  /**
   * Called whenever players are criteria to or uninvited from warps.
   *
   * @param event the event
   * @deprecated will be privatized once support for old Guava versions is removed
   */
  @Deprecated
  @Subscribe
  public void onInvitationAddition(WarpInvitesEvent event) {
    Message.Builder builder = Message.builder().append(Message.Style.INFO);

    WarpInvitesEvent.InvitationStatus status = event.getInvitationStatus();
    String warpName = event.getWarp().getName();

    switch (status) {
      case ADDITION:
        builder.append(msg.getString("player.invited", warpName));
        break;
      case REMOVAL:
        builder.append(msg.getString("player.uninvited", warpName));
        break;
      default:
        assert false : status;
    }

    event.getInvitation().allMatches(game).forEach((LocalPlayer p) -> p.sendMessage(builder.build()));
  }
}
