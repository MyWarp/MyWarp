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

package io.github.mywarp.mywarp.util.teleport;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;

import io.github.mywarp.mywarp.platform.LocalEntity;

import java.util.UUID;

/**
 * Handles entity teleports.
 */
public interface TeleportHandler {

  /**
   * Teleports the given {@code entity} to the given {@code position} on the world identified by the given {@code
   * worldIdentifier} with the given {@code rotation} and returns an appropriate status.
   *
   * @param entity          the entity to teleport
   * @param worldIdentifier the identifier of the world to teleport to
   * @param position        the position to teleport to
   * @param rotation        the rotation
   * @return the status of the teleport
   */
  TeleportStatus teleport(LocalEntity entity, UUID worldIdentifier, Vector3d position, Vector2f rotation);

  /**
   * The status of a finished teleport.
   */
  enum TeleportStatus {
    /**
     * The teleport was not executed as the requested world does not exist.
     */
    NO_SUCH_WORLD(false),
    /**
     * The entity has not been teleported.
     */
    NONE(false),
    /**
     * The entity has been teleported to the desired position.
     */
    ORIGINAL(true),
    /**
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
