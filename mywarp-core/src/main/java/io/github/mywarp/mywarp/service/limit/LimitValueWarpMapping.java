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

package io.github.mywarp.mywarp.service.limit;

import io.github.mywarp.mywarp.service.limit.Limit.Value;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;

import java.util.Collection;
import java.util.function.Predicate;

/**
 * A mapping of limit values to warps on a certain WarpManager.
 *
 * <p>Methods of this class provide a live view on the underling WarpManager. Changes in the warpManager are directly
 * reflected.</p>
 */
public class LimitValueWarpMapping {

  private final WarpManager manager;
  private final Predicate<Warp> filter;


  /**
   * Creates an instance that operates on the given {@code warpManager} using only those warps that fulfill the given
   * {@code filter}.
   *
   * @param manager the WarpManager to operate on
   * @param filter  the filter
   */
  public LimitValueWarpMapping(WarpManager manager, Predicate<Warp> filter) {
    this.manager = manager;
    this.filter = filter;
  }

  /**
   * Gets a live view of warps that are counted under the given {@code value}.
   *
   * @param value the value
   * @return a Collection with all warps to be counted under the value
   */
  public Collection<Warp> get(Value value) {
    return manager.getAll(filter.and(value.getCondition()));
  }

  /**
   * Returns whether there exist at least the given number of warps to be counted under the given {@code value}.
   *
   * @param value the value
   * @param count the number of Warps that should exist at least
   * @return {@code true} if there are at least the given number of Warps
   */
  boolean atLeast(Value value, int count) {
    return get(value).size() >= count;
  }
}
