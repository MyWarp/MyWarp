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

package io.github.mywarp.mywarp.warp.event;

import io.github.mywarp.mywarp.util.playermatcher.PlayerMatcher;
import io.github.mywarp.mywarp.warp.Warp;

/**
 * Indicates the invites for a Warp have changed in some way.
 */
public class WarpInvitesEvent extends WarpEvent {

  private final InvitationStatus invitationStatus;
  private final PlayerMatcher invitation;

  /**
   * Constructs this event for the given warp, indicating that the particular playermatcher now has the given
   * InvitationStatus.
   *
   * @param warp             the warp
   * @param invitationStatus the invitationStatus
   * @param invitation          the PlayerMatcher
   */
  public WarpInvitesEvent(Warp warp, InvitationStatus invitationStatus, PlayerMatcher invitation) {
    super(warp);
    this.invitationStatus = invitationStatus;
    this.invitation = invitation;
  }

  /**
   * Gets the InvitationStatus of the particular playermatcher.
   *
   * @return the InvitationStatus
   */
  public InvitationStatus getInvitationStatus() {
    return invitationStatus;
  }

  public PlayerMatcher getInvitation() {
    return invitation;
  }

  /**
   * Represents the status of an PlayerMatcher that is indicated by an WarpInvitesEvent.
   */
  public enum InvitationStatus {
    ADDITION, REMOVAL
  }

}
