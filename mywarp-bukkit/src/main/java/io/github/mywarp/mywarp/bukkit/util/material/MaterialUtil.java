/*
 * Copyright (C) 2011 - 2019, MyWarp team and contributors
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

package io.github.mywarp.mywarp.bukkit.util.material;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.primitives.Ints;

import io.github.mywarp.mywarp.bukkit.BukkitAdapter;
import io.github.mywarp.mywarp.platform.LocalWorld;

import org.bukkit.Material;

import java.util.function.Predicate;

/**
 * Utilities for working with Materials.
 */
public class MaterialUtil {

  private MaterialUtil() {
  }

  /**
   * Gets the {@code Material} of the block at the given position within the given world.
   *
   * @param world    the world
   * @param position the position
   * @return the Material of the block at the given position
   */
  public static Material getMaterial(LocalWorld world, Vector3i position) {
    return BukkitAdapter.adapt(world).getBlockAt(Ints.checkedCast(position.getX()), Ints.checkedCast(position.getY()),
                                                 Ints.checkedCast(position.getZ())).getType();
  }

  /**
   * Tests whether the {@code Material} at the block at the given position within the given world fulfills the given
   * {@code Predicate}.
   *
   * @param world     the world
   * @param position  the position
   * @param predicate the predicate
   * @return true if the Material at the given position fulfills the given Predicate
   */
  public static boolean test(LocalWorld world, Vector3i position, Predicate<Material> predicate) {
    return predicate.test(getMaterial(world, position));
  }
}
