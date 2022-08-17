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

package io.github.mywarp.mywarp.service.teleport.timer;

/**
 * An action that is executed when a timer finishes and can be internally aborted while the timer is running.
 *
 * @param <T> the type of the subject the timer runs on
 */
public abstract class AbortableTimerAction<T> extends TimerAction<T> {

  public static final long CHECK_INTERVAL_TICKS = 40;

  /**
   * Creates an instance on the given subject.
   *
   * @param timedSubject the subject the timer runs on
   */
  public AbortableTimerAction(T timedSubject) {
    super(timedSubject);
  }

  /**
   * Returns whether the timer should be aborted.
   *
   * <p> This method will be called periodically while the timer is running to check if the timer should be aborted
   * prematurely. </p>
   *
   * @return {@code true} if the timer should be aborted
   */
  public abstract boolean abort();

}
