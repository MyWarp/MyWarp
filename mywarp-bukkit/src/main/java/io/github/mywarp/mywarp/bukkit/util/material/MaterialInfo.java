/*
 * Copyright (C) 2011 - 2020, MyWarp team and contributors
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

import org.bukkit.Material;

/**
 * Provides information about Materials.
 */
public interface MaterialInfo {

  /**
   * Returns whether an entity <b>can stand <u>on</u></b> a block of the given Material without taking damage or
   * falling.
   *
   * <p>For example, for {@link Material#DIRT}, this method will return {@code true}. For {@link Material#LAVA}, this
   * method will return {@code false}.</p>
   *
   * @param toTest the Material to test
   * @return true if the given Material is safe to stand on
   */
  boolean safeToStandOn(Material toTest);

  /**
   * Returns whether an entity standing <b><u>within</u></b> the the given Material without taking damage or falling.
   *
   * <p>For example, for {@link Material#DIRT}, this method will return {@code true}. For {@link Material#AIR}, this
   * method will return {@code false}.</p>
   *
   * @param toTest the Material to test
   * @return true if the given Material is dangerous to stand within
   */
  boolean dangerousToStandWithin(Material toTest);

  /**
   * Returns whether an entity can toggle a block of the given by clicking on it.
   *
   * <p>For example, for {@link Material#STONE_BUTTON}, this method will return {@code true}.</p>
   *
   * @param toTest the Material to test
   * @return true if the given Material is clickable
   */
  boolean isClickable(Material toTest);

  /**
   * Returns whether an entity can trigger a block of the given by stepping on it.
   *
   * <p>For example, for {@link Material#STONE_PRESSURE_PLATE}, this method will return {@code true}.</p>
   *
   * @param toTest the Material to test
   * @return true if the given Material is clickable
   */
  boolean isTriggerable(Material toTest);
}
