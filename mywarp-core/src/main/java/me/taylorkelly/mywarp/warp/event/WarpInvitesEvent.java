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
 * Indicates the invites for a Warp have changed in some way.
 */
public abstract class WarpInvitesEvent extends WarpEvent {

  private final InvitationStatus invitationStatus;

  /**
   * Constructs this event for the given warp, indicating that the particular invitee now has the given
   * InvitationStatus.
   *
   * @param warp             the warp
   * @param invitationStatus the invitationStatus
   */
  public WarpInvitesEvent(Warp warp, InvitationStatus invitationStatus) {
    super(warp);
    this.invitationStatus = invitationStatus;
  }

  /**
   * Gets the InvitationStatus of the particular invitee.
   *
   * @return the InvitationStatus
   */
  public InvitationStatus getInvitationStatus() {
    return invitationStatus;
  }

  /**
   * Represents the status of an Invitation that is indicated by an WarpInvitesEvent.
   */
  public enum InvitationStatus {
    INVITE, UNINVITE
  }

}
