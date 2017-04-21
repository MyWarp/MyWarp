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

package io.github.mywarp.mywarp.util.teleport;

import com.flowpowered.math.vector.Vector3d;

import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.capability.PositionValidationCapability;

import java.util.Optional;

/**
 * Corrects positions saved with MyWarp versions prior to version 3 if necessary.
 *
 * <p>Before version 3 the warp height was equivalent with the Y coordinate of the block. If the warp was located on top
 * of a block that was smaller than a full block (e.g. a half step, a stair, a pressured plate), the height needs to be
 * adjusted or the entity ends up <i>within</i> this block.</p>
 */
public class LegacyPositionCorrectionCapability implements PositionValidationCapability {

  @Override
  public Optional<Vector3d> getValidPosition(Vector3d originalPosition, LocalWorld world) {
    if (world.isNotFullHeight(originalPosition.toInt())) {
      originalPosition = originalPosition.add(0, 1, 0);
    }
    return Optional.of(originalPosition);
  }
}
