/*
 * Copyright (C) 2011 - 2022, MyWarp team and contributors
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

import com.google.common.collect.Ordering;
import io.github.mywarp.mywarp.warp.Warp;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;

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
    return warp.getVisits() / (double) (days);
  }

  /**
   * Gets the Comparator that matches the given {@code key}.
   *
   * @param key the key
   * @return the matching Comparator
   */
  public static Comparator<Warp> getComparator(String key) {
    switch (key) {
      case "alp":
      case "alphabetically":
        return Comparator.comparing(Warp::getName);
      case "dat":
      case "creationDate":
        return Comparator.comparing(Warp::getCreationDate);
      case "pop":
      case "popularity":
        return new Warp.PopularityComparator();
      case "ran":
      case "random":
        return random();
      case "vis":
      case "visits":
        return Comparator.comparing(Warp::getVisits);
      case "def":
      case "default":
      default:
        return Ordering.natural();
    }
  }

  private static <T> Comparator<T> random() {
    Map<Object, UUID> randomIds = new IdentityHashMap<>();
    Map<Object, Integer> uniqueIds = new IdentityHashMap<>();
    return Comparator.comparing((T t) -> randomIds.computeIfAbsent(t, k -> UUID.randomUUID()))
        .thenComparing(t -> uniqueIds.computeIfAbsent(t, k -> uniqueIds.size()));
  }
}
