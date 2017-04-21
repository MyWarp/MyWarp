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

package io.github.mywarp.mywarp.util;

import com.google.common.eventbus.Subscribe;

import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.event.WarpGroupInvitesEvent;
import io.github.mywarp.mywarp.warp.event.WarpInvitesEvent;
import io.github.mywarp.mywarp.warp.event.WarpPlayerInvitesEvent;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

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
   * Called whenever players are invited to or uninvited from warps.
   *
   * @param event the event
   * @deprecated will be privatized once support for old Guava versions is removed
   */
  @Deprecated
  @Subscribe
  public void onPlayerInvite(WarpPlayerInvitesEvent event) {
    Optional<LocalPlayer> playerOptional = game.getPlayer(event.getUniqueId());
    if (!playerOptional.isPresent()) {
      return;
    }
    inform(event.getInvitationStatus(), event.getWarp().getName(), playerOptional.get());
  }

  /**
   * Called whenever groups are invited to or uninvited from warps.
   *
   * @param event the event
   * @deprecated will be privatized once support for old Guava versions is removed
   */
  @Deprecated
  @Subscribe
  public void onGroupInvite(final WarpGroupInvitesEvent event) {
    inform(event.getInvitationStatus(), event.getWarp().getName(),
           game.getPlayers().stream().filter(p -> p.hasGroup(event.getGroupId())).collect(Collectors.toList()));
  }

  private void inform(WarpInvitesEvent.InvitationStatus status, String warpName, LocalPlayer... players) {
    inform(status, warpName, Arrays.asList(players));
  }

  private void inform(WarpInvitesEvent.InvitationStatus status, String warpName, Iterable<LocalPlayer> players) {
    Message.Builder builder = Message.builder().append(Message.Style.INFO);

    switch (status) {
      case INVITE:
        builder.append(msg.getString("player.invited", warpName));
        break;
      case UNINVITE:
        builder.append(msg.getString("player.uninvited", warpName));
        break;
      default:
        assert false : status;
    }

    for (LocalPlayer player : players) {
      player.sendMessage(builder.build());
    }
  }

}
