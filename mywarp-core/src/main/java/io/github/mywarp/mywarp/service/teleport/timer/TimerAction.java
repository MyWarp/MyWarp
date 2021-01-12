/*
 * Copyright (C) 2011 - 2021, MyWarp team and contributors
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

import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.service.teleport.TimerTeleportService;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import java.time.Duration;
import java.time.Instant;

/**
 * An action that is executed after a timer finishes.
 *
 * @param <T> the type of the subject the timer runs on
 */
public abstract class TimerAction<T> implements Runnable {

  private static final DynamicMessages msg = new DynamicMessages(TimerTeleportService.RESOURCE_BUNDLE_NAME);

  private Instant lastInformed = Instant.MIN;
  private final T timedSuject;

  /**
   * Creates an instance on the given subject.
   *
   * @param timedSubject the subject the timer runs on
   */
  public TimerAction(T timedSubject) {
    this.timedSuject = timedSubject;
  }

  /**
   * Gets the subject the timer runs on.
   *
   * @return the subject
   */
  public T getTimedSuject() {
    return timedSuject;
  }

  /**
   * Informs the given Actor that this action is still running.
   *
   * @param actor    the Actor to inform
   * @param duration the duration left until completed
   */
  public void informTimerRunning(Actor actor, Duration duration) {
    Instant now = Instant.now();
    if (now.minusSeconds(4).isAfter(lastInformed)) {
      actor.sendError(msg.getString("timer-already-running", duration.getSeconds()));
      lastInformed = now;
    }
  }

}
