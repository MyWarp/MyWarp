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

/**
 * Indicates that a group was invited to or uninvited from a Warp.
 */
public class WarpGroupInvitesEvent extends WarpInvitesEvent {

  private final String groupId;

  /**
   * Constructs this event for the given Warp, indicating that the group of the given identifier now has the given
   * InvitationStatus.
   *
   * @param warp             the warp
   * @param invitationStatus the InvitationStatus of the player
   * @param groupId          the identifier if the group
   */
  public WarpGroupInvitesEvent(Warp warp, InvitationStatus invitationStatus, String groupId) {
    super(warp, invitationStatus);
    this.groupId = groupId;
  }

  /**
   * Gets the identifier of the group.
   *
   * @return the groupId
   */
  public String getGroupId() {
    return groupId;
  }
}
