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

import io.github.mywarp.mywarp.warp.Warp;

import java.time.Duration;
import java.time.Instant;

/**
 * Utility methods to work with warps.
 */
public final class WarpUtils {

  public static final int MAX_NAME_LENGTH = 32;

  /**
   * Block initialization of this class.
   */
  private WarpUtils() {
  }

  /**
   * Gets the average number of visits per day, from the point the given {@code Warp} was created until this method is
   * called.
   *
   * @param warp the warp
   * @return the average number of visits per day
   */
  public static double visitsPerDay(Warp warp) {
    long days = Duration.between(warp.getCreationDate(), Instant.now()).toDays();
    if (days < 1) {
      days = 1;
    }
    return warp.getVisits() / days;
  }
}
