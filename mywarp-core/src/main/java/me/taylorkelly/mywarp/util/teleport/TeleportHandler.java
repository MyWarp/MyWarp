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

package me.taylorkelly.mywarp.util.teleport;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;

import me.taylorkelly.mywarp.platform.LocalEntity;
import me.taylorkelly.mywarp.platform.LocalWorld;

/**
 * Handles entity teleports.
 */
public interface TeleportHandler {

  /**
   * Teleports the given {@code entity} to the given {@code position} on the given {@code world} with the given {@code
   * rotation} and returns an appropriate status.
   *
   * @param entity   the entity to teleport
   * @param world    the world to teleport to
   * @param position the position to teleport to
   * @param rotation the rotation
   * @return the status of the teleport
   */
  TeleportStatus teleport(LocalEntity entity, LocalWorld world, Vector3d position, Vector2f rotation);

  /**
   * The status of a finished teleport.
   */
  enum TeleportStatus {
    /**
     * The entity has not been teleported.
     */
    NONE(false), /**
     * The entity has been teleported to the desired position.
     */
    ORIGINAL(true), /**
     * The entity has been teleported, but the position is not equal to the desired one.
     */
    MODIFIED(true);

    private final boolean positionModified;

    TeleportStatus(boolean positionModified) {
      this.positionModified = positionModified;
    }

    /**
     * Returns whether this status implies that the position has been modified.
     *
     * @return {@code true} if a position change is implied
     */
    public boolean isPositionModified() {
      return positionModified;
    }
  }
}
