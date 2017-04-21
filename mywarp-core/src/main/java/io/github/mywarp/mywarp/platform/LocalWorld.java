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

package io.github.mywarp.mywarp.platform;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;

import io.github.mywarp.mywarp.util.BlockFace;

import java.util.Optional;
import java.util.UUID;

/**
 * A world or dimension currently existing on the server.
 */
public interface LocalWorld {

  /**
   * Gets the name of this world.
   *
   * @return this world's name
   */
  String getName();

  /**
   * Gets the unique ID of this world.
   *
   * @return this world's unique identifier
   */
  UUID getUniqueId();

  /**
   * Gets an Optional with the Sign that exists at the given {@code position} within this world, if such a Sign exists.
   *
   * @param position the position to check
   * @return an Optional containing the Sign
   */
  Optional<Sign> getSign(Vector3i position);

  /**
   * Gets an Optional with the Sign that exists at the given {@code position} within this world and which is attached to
   * the given {@code blockFace}, if such a Sign exists.
   *
   * @param position  the position to check
   * @param blockFace the blockFace the sign is attached to
   * @return an Optional containing the Sign
   */
  Optional<Sign> getAttachedSign(Vector3i position, BlockFace blockFace);

  /**
   * Plays the teleport effect at the given {@code position} within this world.
   *
   * <p>The teleport effect is a normal smoke effect applied four times.</p>
   *
   * @param position the position where the effect should be played
   */
  void playTeleportEffect(Vector3d position);

  /**
   * Returns whether the block at the given {@code position} within this world is smaller than a single full block.
   *
   * @param position the position to check
   * @return {@code true} if this particular block is smaller than a normal block
   * @deprecated This method exists only to support warps created in legacy versions and may be removed at any time.
   */
  @Deprecated
  boolean isNotFullHeight(Vector3i position);

}
