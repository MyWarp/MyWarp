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

package io.github.mywarp.mywarp.bukkit.util.material;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.primitives.Ints;

import io.github.mywarp.mywarp.bukkit.BukkitAdapter;
import io.github.mywarp.mywarp.platform.LocalWorld;

import org.bukkit.Material;

/**
 * Provides information about Materials.
 */
public interface MaterialInfo {

  /**
   * Gets the {@code Material} of the block at the given position within the given world.
   *
   * @param world    the world
   * @param position the position
   * @return the Material of the block at the given position
   */
  static Material getMaterial(LocalWorld world, Vector3i position) {
    return BukkitAdapter.adapt(world).getBlockAt(Ints.checkedCast(position.getX()), Ints.checkedCast(position.getY()),
                                                 Ints.checkedCast(position.getZ())).getType();
  }

  /**
   * Returns whether an entity <b>can stand <u>on</u></b> the block at the given {@code position} of the given {@code
   * world} without taking damage or falling.
   *
   * @param world    the world
   * @param position the position
   * @return true if the given position is safe to stand on
   * @see #safeToStandOn(Material)
   */
  default boolean safeToStandOn(LocalWorld world, Vector3i position) {
    return safeToStandOn(getMaterial(world, position));
  }

  /**
   * Returns whether an entity <b>can stand <u>on</u></b> a block of the given {@code material} without taking damage or
   * falling.
   *
   * <p>For example, if {@code material} is {@link Material#DIRT}, this method will return {@code true}. If
   * {@code material} is {@link Material#LAVA}, this method will return {@code false}.</p>
   *
   * @param material the material
   * @return true if the given material is safe to stand on
   */
  boolean safeToStandOn(Material material);

  /**
   * Returns whether an entity standing <b><u>within</u></b> the the block at the given {@code position} of the given
   * {@code world} will take damage from doing so.
   *
   * @param world    the world
   * @param position the position
   * @return true if the given position is dangerous
   * @see #dangerousToStandWithin(Material)
   */
  default boolean dangerousToStandWithin(LocalWorld world, Vector3i position) {
    return dangerousToStandWithin(getMaterial(world, position));
  }

  /**
   * Returns whether an entity standing <b><u>within</u></b> the the block at the given {@code position} of the given
   * {@code world} will take damage from doing so.
   *
   * <p>For example, if {@code material} is {@link Material#DIRT}, this method will return {@code true}. If
   * {@code material} is {@link Material#AIR}, this method will return {@code false}.</p>
   *
   * @param material the Material to check
   * @return true if the given material is dangerous
   */
  boolean dangerousToStandWithin(Material material);
}
