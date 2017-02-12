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

package me.taylorkelly.mywarp.warp.event;

import me.taylorkelly.mywarp.warp.Warp;

import java.util.UUID;

/**
 * Indicates that a player was invited to or uninvited from a Warp.
 */
public class WarpPlayerInvitesEvent extends WarpInvitesEvent {

  private final UUID uniqueId;

  /**
   * Constructs this event for the given Warp, indicating that the player with the given unique identifier now has the
   * given InvitationStatus.
   *
   * @param warp             the warp
   * @param invitationStatus the InvitationStatus of the player
   * @param uniqueId         the unique identifier of the player
   */
  public WarpPlayerInvitesEvent(Warp warp, InvitationStatus invitationStatus, UUID uniqueId) {
    super(warp, invitationStatus);
    this.uniqueId = uniqueId;
  }

  /**
   * Gets the unique identifier of the player.
   *
   * @return the unique identifer
   */
  public UUID getUniqueId() {
    return uniqueId;
  }

}
