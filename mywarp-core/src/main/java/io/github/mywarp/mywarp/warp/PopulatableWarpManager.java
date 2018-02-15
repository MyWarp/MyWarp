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

package io.github.mywarp.mywarp.warp;


/**
 * A WarpManager that can be populated and depopulated without invoking {@link #add(Warp)} or
 * {@link #remove(Warp)}.
 *
 * <p>Implementations can define additional behavior for both methods (e.g. saving the warp in a database) and
 * callers can choose to ignore this behavior if needed b using the methods provided by this interface.</p>
 */
public interface PopulatableWarpManager extends WarpManager {

  /**
   * Populates this manager with the given Warps.
   *
   * @param warps the Warps
   * @throws IllegalArgumentException if this manager already contains a Warp with a name equal to one of the given
   *                                  Warps
   */
  void populate(Iterable<Warp> warps);

  /**
   * Depopulates this manager, removing all Warps previously managed by it.
   */
  void depopulate();

}
