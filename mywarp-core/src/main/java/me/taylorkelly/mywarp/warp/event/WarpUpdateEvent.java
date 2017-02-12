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
 * Indicates that a Warp was updated in some way.
 */
public class WarpUpdateEvent extends WarpEvent {

  private final UpdateType type;

  /**
   * Constructs this event for the given Warp with the given UpdateType.
   *
   * @param warp the warp
   * @param type the type
   */
  public WarpUpdateEvent(Warp warp, UpdateType type) {
    super(warp);
    this.type = type;
  }

  /**
   * Gets the type, indicating how exactly the Warp was updated.
   *
   * @return the type
   */
  public UpdateType getType() {
    return type;
  }

  /**
   * Represents the way a Warp was updated.
   */
  public enum UpdateType {
    /**
     * The warp's creator was updated.
     */
    CREATOR, /**
     * The warp's location was updated.
     */
    LOCATION, /**
     * The warp's type was updated.
     */
    TYPE, /**
     * The warp's visit-counter was updated.
     */
    VISITS, /**
     * The warp's welcome-message was updated.
     */
    WELCOME_MESSAGE
  }

}
