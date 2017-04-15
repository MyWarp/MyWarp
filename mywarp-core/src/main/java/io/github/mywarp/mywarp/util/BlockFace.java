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

import com.flowpowered.math.vector.Vector3i;

/**
 * The individual faces of a block.
 *
 * <p>Since blocks are regular cubes, there are only 6 individual block faces, one for each side.</p>
 */
public enum BlockFace {

  /**
   * The norther side of a block (-z).
   */
  NORTH(new Vector3i(0, 0, -1)), /**
   * The eastern side of a block (+x).
   */
  EAST(new Vector3i(1, 0, 0)), /**
   * The souther side of a block (+z).
   */
  SOUTH(new Vector3i(0, 0, 1)), /**
   * The western side of a block (-x).
   */
  WEST(new Vector3i(-1, 0, 0)), /**
   * The top side of a block (+y).
   */
  UP(new Vector3i(0, 1, 0)), /**
   * The bottom side of a block (-y).
   */
  DOWN(new Vector3i(0, -1, 0));

  private final Vector3i vector;
  private BlockFace opposite;

  BlockFace(Vector3i vector) {
    this.vector = vector;
  }

  static {
    NORTH.opposite = SOUTH;
    EAST.opposite = WEST;
    SOUTH.opposite = NORTH;
    WEST.opposite = EAST;
    UP.opposite = DOWN;
    DOWN.opposite = UP;
  }

  /**
   * Gets a vector representation of this block face.
   *
   * @return a vector representing this block face
   */
  public Vector3i getVector() {
    return vector;
  }

  /**
   * Gets the block face that is the opposite of this one.
   *
   * @return the opposite block face
   */
  public BlockFace getOpposite() {
    return opposite;
  }
}
