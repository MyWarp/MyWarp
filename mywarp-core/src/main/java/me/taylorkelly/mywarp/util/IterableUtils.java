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

package me.taylorkelly.mywarp.util;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;

/**
 * Utilities for working with Iterables.
 */
public final class IterableUtils {

  private IterableUtils() {
  }

  /**
   * Returns whether the given Iterable contains has least the given number of entries.
   *
   * @param <T>      the type of entries
   * @param iterable the iterable to check
   * @param count    the number of entries the iterable should have at least
   * @return {@code true} if the given Iterable has at least the given number of entries
   */
  public static <T> boolean atLeast(Iterable<T> iterable, int count) {
    return Iterables.size(Iterables.limit(iterable, count)) == count;
  }

  /**
   * Returns an Optional containing the first element in iterable or {@code Optional.absent()} if the iterable is
   * empty.
   *
   * @param <T>      the type of entries
   * @param iterable the iterable to check
   * @return the first element
   */
  public static <T> Optional<T> getFirst(Iterable<T> iterable) {
    T first = Iterables.getFirst(iterable, null);
    return Optional.fromNullable(first);
  }

}
