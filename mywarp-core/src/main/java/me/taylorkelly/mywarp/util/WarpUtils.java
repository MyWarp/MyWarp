/*
 * Copyright (C) 2011 - 2016, MyWarp team and contributors
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

package me.taylorkelly.mywarp.util;

import com.google.common.base.Predicate;

import me.taylorkelly.mywarp.warp.Warp;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    // this method might not be 100% exact (considering leap seconds), but
    // within the current Java API there are no alternatives
    long
        daysSinceCreation =
        TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - warp.getCreationDate().getTime());
    if (daysSinceCreation <= 0) {
      daysSinceCreation = 1;
    }
    return warp.getVisits() / daysSinceCreation;
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the warp being tested is created by player identified by the
   * given profile.
   *
   * @param uniqueId the Profile
   * @return a predicate that checks if the given warp is created by the given player
   * @see Warp#isCreator(java.util.UUID)
   */
  public static Predicate<Warp> isCreator(final UUID uniqueId) {
    return new Predicate<Warp>() {

      @Override
      public boolean apply(Warp warp) {
        return warp.isCreator(uniqueId);
      }

    };
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the warp being tested is of the given type.
   *
   * @param type the type
   * @return a predicate that checks if the given warp is of the given type
   * @see Warp#isType(Warp.Type)
   */
  public static Predicate<Warp> isType(final Warp.Type type) {
    return new Predicate<Warp>() {

      @Override
      public boolean apply(Warp warp) {
        return warp.isType(type);
      }

    };
  }

}
