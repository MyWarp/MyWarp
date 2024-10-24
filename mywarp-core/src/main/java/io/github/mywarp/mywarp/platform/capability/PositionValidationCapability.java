/*
 * Copyright (C) 2011 - 2022, MyWarp team and contributors
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

package io.github.mywarp.mywarp.platform.capability;

import com.flowpowered.math.vector.Vector3d;
import io.github.mywarp.mywarp.platform.LocalWorld;

import java.util.Optional;

/**
 * The capability of a platform to validate a given position and suggests alternative ones.
 */
public interface PositionValidationCapability {

  /**
   * Returns an Optional containing the first valid position starting from the given {@code originalPosition} within the
   * given {@code world} or {@code Optional.absent()} if no such position exists.
   *
   * @param originalPosition the original position
   * @param world            the world that contains the position
   * @return the first valid position
   */
  Optional<Vector3d> getValidPosition(Vector3d originalPosition, LocalWorld world);
}
