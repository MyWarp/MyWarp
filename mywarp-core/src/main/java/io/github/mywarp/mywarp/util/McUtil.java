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

package io.github.mywarp.mywarp.util;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;

/**
 * Utilities for MC.
 */
public class McUtil {

  private static final int AVERAGE_TICKS_PER_SECOND = 20;
  private static final String LINE_SEPARATOR = "\n";

  private McUtil() {
  }

  /**
   * Returns the line separator string.
   *
   * <p>MC's line separator is NOT system-dependent; instead, even on Windows based systems, '\n' is used.
   * See issues #157 and #61.</p>
   *
   * @return the line separator string
   */
  public static String lineSeparator() {
    return LINE_SEPARATOR;
  }

  /**
   * Returns the number of server ticks that are (ideally) equivalent to the given {@code amount} of time.
   *
   * <p>The actual number of ticks occurring in a certain amount can very depending on the server load. Thus, the
   * number returned by this method should be interpreted as the number of ticks that are ideally equivalent to the
   * given amount.</p>
   *
   * @param amount the amount if time
   * @return the number of ticks equivalent to the given amount
   */
  public static long toTicks(TemporalAmount amount) {
    return amount.get(ChronoUnit.SECONDS) * AVERAGE_TICKS_PER_SECOND;
  }

  /**
   * Returns the Duration that is (ideally) equivalent to the given number of server ticks.
   *
   * <p>The actual number of ticks occurring in a certain amount can very depending on the server load. Thus,
   * the number returned by this method should be interpreted as the number of ticks that are ideally equivalent to the
   * given amount.</p>
   *
   * @param ticks the number of ticks
   * @return the duration equivalent to the given number of ticks
   */
  public static Duration fromTicks(long ticks) {
    return Duration.ofSeconds(ticks / AVERAGE_TICKS_PER_SECOND);
  }

}
