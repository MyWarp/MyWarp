/*
 * Copyright (C) 2011 - 2021, MyWarp team and contributors
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

package io.github.mywarp.mywarp.platform;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;

/**
 * An entity currently existing on the server.
 */
public interface LocalEntity {

  /**
   * Gets the world this entity is currently positioned in.
   *
   * @return the current world
   */
  LocalWorld getWorld();

  /**
   * Gets the current position of this entity.
   *
   * @return the current position
   */
  Vector3d getPosition();

  /**
   * Gets the current rotation of this entity.
   *
   * @return the current rotation
   */
  Vector2f getRotation();

  /**
   * Teleports this entity to the given position on the given world, and sets his rotation to the given one.
   *
   * @param world              the world
   * @param position           the position vector
   * @param rotation           the rotation
   * @param teleportTamedHorse if set and this entity currently sits on a tamed horse, the horse will be teleported too
   */
  void teleport(LocalWorld world, Vector3d position, Vector2f rotation, boolean teleportTamedHorse);

}
