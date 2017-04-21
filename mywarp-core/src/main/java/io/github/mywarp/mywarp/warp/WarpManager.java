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

package io.github.mywarp.mywarp.warp;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Holds and manages Warps.
 *
 * <p>Implementations must guarantee that at any given time, the name of each Warp that an instance contains, is
 * unique. This guarantees that getting a Warp by name works as expected.</p>
 */
public interface WarpManager {

  /**
   * Adds the given {@code warp} to this manager.
   *
   * @param warp the Warp to add
   * @throws IllegalArgumentException if this manager already contains a warp with the name of the given one
   */
  void add(Warp warp);

  /**
   * Removes the given {@code warp} from this manager.
   *
   * @param warp the Warp to remove
   */
  void remove(Warp warp);

  /**
   * Checks whether this manager contains the given {@code warp}.
   *
   * @param warp the warp to check
   * @return {@code true} if this manager contains the given warp
   */
  boolean contains(Warp warp);

  /**
   * Checks whether this manager contains a warp with the given {@code name}.
   *
   * @param name the name to check
   * @return {@code true} if this manager contains a warp with the given name
   */
  boolean containsByName(String name);

  /**
   * Gets an Optional containing the Warp of the given {@code name}, if this manager contains such a Warp.
   *
   * @param name the name of the Warp
   * @return an Optional containing the Warp with the given name
   */
  Optional<Warp> getByName(String name);

  /**
   * Gets a Collection with all Warps on this manager that fulfill the given {@code predicate}.
   *
   * @param predicate the predicate to fulfill
   * @return all Warps that fulfill the Predicate
   */
  Collection<Warp> getAll(Predicate<Warp> predicate);

  /**
   * Gets the number of Warps managed by this manager that fulfill the given predicate.
   *
   * @param predicate to fulfill
   * @return the number of Warps that fulfill the Predicate
   */
  int getNumberOfWarps(Predicate<Warp> predicate);

  /**
   * Gets the number of all Warps managed by this manger.
   *
   * @return the number of all Warps on this manager
   */
  int getNumberOfAllWarps();
}
