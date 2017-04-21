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

package io.github.mywarp.mywarp.service.teleport.timer;

import java.util.concurrent.TimeUnit;

/**
 * An immutable representation of a duration.
 */
//FIXME can java APi be used instead?
public class Duration {

  private static final double TICKS_PER_MILLISSECOND = 20 * 0.001;

  private final long durationMillis;

  /**
   * Creates an instance.
   *
   * @param duration the duration
   * @param unit     the TimeUnit of the former duration
   */
  public Duration(long duration, TimeUnit unit) {
    this.durationMillis = unit.toMillis(duration);
  }

  /**
   * Gets the duration in the given TimeUnit.
   *
   * @param unit the TimeUnit
   * @return the duration in the given TimeUnit
   */
  public long get(TimeUnit unit) {
    return unit.convert(durationMillis, TimeUnit.MILLISECONDS);
  }

  /**
   * Gets the duration in Minecraft-Ticks.
   *
   * @return the duration in ticks
   */
  public long getTicks() {
    return (long) (durationMillis * TICKS_PER_MILLISSECOND);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (int) (durationMillis ^ (durationMillis >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Duration other = (Duration) obj;
    if (durationMillis != other.durationMillis) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "Duration [durationMillis=" + durationMillis + "]";
  }

}
