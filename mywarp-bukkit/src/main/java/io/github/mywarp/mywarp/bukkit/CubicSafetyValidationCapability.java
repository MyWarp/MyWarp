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

package io.github.mywarp.mywarp.bukkit;

import static io.github.mywarp.mywarp.bukkit.MyWarpPlugin.getMaterial;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;

import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.capability.PositionValidationCapability;

/**
 * Searches for positions that are safe for a normal entity within a cube surrounding a given center position.
 */
public class CubicSafetyValidationCapability implements PositionValidationCapability {

  private int searchRadius;

  /**
   * Creates an instance that searches for safe positions within the given radius.
   *
   * @param searchRadius the radius within safe positions are searched
   */
  CubicSafetyValidationCapability(int searchRadius) {
    this.searchRadius = searchRadius;
  }

  @Override
  public Optional<Vector3d> getValidPosition(Vector3d originalPosition, LocalWorld world) {
    if (isSafe(world, originalPosition)) {
      return Optional.of(originalPosition);
    }
    Optional<Vector3d> safePosition; // never modify the given location!

    for (int i = 2; i <= searchRadius; i++) {
      safePosition = checkCubeSurface(world, originalPosition, i);
      if (safePosition.isPresent()) {
        return safePosition;
      }
    }
    return Optional.absent();
  }

  /**
   * Gets an Optional containing the first safe position from the cube surface of the given half-edge-length centered at
   * the given position in the given world, if such a position exits.
   *
   * @param world          the world where the position is placed it
   * @param center         the central position vector
   * @param halfEdgeLength half of the effective edge length, including the block in the center
   * @return the first safe location found, or {@code Optional#absent()} if none could be found
   */
  private Optional<Vector3d> checkCubeSurface(LocalWorld world, Vector3d center, int halfEdgeLength) {
    Optional<Vector3d> safePosition;

    int diameter = getEdgeLength(halfEdgeLength);
    for (int i = 0; i < diameter; i++) {
      // makes the location 'swing' up/down (+1, -2, +3, -4...)
      center = center.add(0, i % 2 == 0 ? -i : i, 0);
      if (i < diameter - 2) {
        // if we are more than 2 steps away from the ending, we are in
        // the "middle" of the cube and only need to check the outline
        safePosition = checkHorizontalSquareOutline(world, center, halfEdgeLength);
      } else {
        // check bottom and top areas
        safePosition = checkHorizontalSquare(world, center, halfEdgeLength);
      }
      if (safePosition.isPresent()) {
        return safePosition;
      }
    }
    return Optional.absent();
  }

  /**
   * Gets an Optional with the first safe position from a horizontal square with the given half-edge-length centered at
   * the given position in the given world, if such a position exits.
   *
   * @param world          the world where the position is placed it
   * @param center         the central position vector
   * @param halfEdgeLength half of the effective edge length, including the block in the center
   * @return the first safe position, or {@code Optional#absent()} if none could be found
   */
  private Optional<Vector3d> checkHorizontalSquare(LocalWorld world, Vector3d center, int halfEdgeLength) {
    if (isSafe(world, center)) {
      return Optional.of(center);
    }
    Optional<Vector3d> checkPosition;

    // loop through surrounding blocks, starting with a half-edge-length of
    // 2 (1 would just be the central block)
    for (int i = 2; i <= halfEdgeLength; i++) {
      checkPosition = checkHorizontalSquareOutline(world, center, i);
      if (checkPosition.isPresent()) {
        return checkPosition;
      }
    }
    return Optional.absent();
  }

  /**
   * Gets an Optional with the first safe position from the outline of horizontal square with the given half-edge-length
   * centered at the given position in the given world, if such a position exits.
   *
   * @param world          the world where the position is placed it
   * @param center         the central position vector
   * @param halfEdgeLength half of the effective edge length, including the block in the center
   * @return the first safe position, or {@code Optional#absent()} if none could be found
   */
  private Optional<Vector3d> checkHorizontalSquareOutline(LocalWorld world, Vector3d center, int halfEdgeLength) {
    int blockSteps = getEdgeLength(halfEdgeLength) - 1;
    Vector3d checkPosition = center.add(halfEdgeLength - 1, 0, halfEdgeLength - 1);

    for (int i = 0; i < blockSteps; i++) {
      checkPosition = checkPosition.add(-1, 0, 0);
      if (isSafe(world, checkPosition)) {
        return Optional.of(checkPosition);
      }
    }

    for (int i = 0; i < blockSteps; i++) {
      checkPosition = checkPosition.add(0, 0, -1);
      if (isSafe(world, checkPosition)) {
        return Optional.of(checkPosition);
      }
    }

    for (int i = 0; i < blockSteps; i++) {
      checkPosition = checkPosition.add(1, 0, 0);
      if (isSafe(world, checkPosition)) {
        return Optional.of(checkPosition);
      }
    }
    for (int i = 0; i < blockSteps; i++) {
      checkPosition = checkPosition.add(0, 0, 1);
      if (isSafe(world, checkPosition)) {
        return Optional.of(checkPosition);
      }
    }
    return Optional.absent();
  }

  /**
   * Returns whether the given {@code position} on the given {@code world} is safe for a regular entity to be teleported
   * to.
   *
   * @param world    the world to check
   * @param position the position to check
   * @return {@code true} is the position is safe
   */
  private boolean isSafe(LocalWorld world, Vector3d position) {
    if (!MaterialInfo.canEntitySafelyStandWithin(getMaterial(world, position.add(0, 1, 0).toInt()))) {
      return false;
    }
    if (!MaterialInfo.canEntitySafelyStandWithin(getMaterial(world, position.toInt()))) {
      return false;
    }
    return MaterialInfo.canEntitySafelyStandOn(getMaterial(world, position.sub(0, 1, 0).toInt()));
  }

  /**
   * Gets the edge length of a square with the given half-edge-length. The later is expected to include the block at the
   * center, e.g. the half-edge-length '2' would result in a edge-length of '3'.
   *
   * @param halfEdgeLength half of the effective edge length, including the block in the center
   * @return the edge length
   */
  private int getEdgeLength(int halfEdgeLength) {
    return (halfEdgeLength - 1) * 2 + 1;
  }

}
