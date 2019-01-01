/*
 * Copyright (C) 2011 - 2019, MyWarp team and contributors
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

package io.github.mywarp.mywarp.platform.capability;

import static com.google.common.base.Preconditions.checkState;

import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.service.teleport.timer.TimerAction;

import java.time.Duration;

import javax.annotation.Nullable;

/**
 * The capability of a platform to run timers.
 */
public interface TimerCapability {

  /**
   * Gets the duration appropriate for a timer of the given {@code timerClass} for the given {@code player}.
   *
   * @param player     the player
   * @param timerClass the class of the timer
   * @return the duration
   */
  Duration getDuration(LocalPlayer player, Class<? extends TimerAction<?>> timerClass);

  /**
   * Starts the given {@code timer} with the given {@code duration} on the given subject.
   *
   * @param timedSubject the subject to start the timer on
   * @param duration     the duration to use
   * @param timer        the timer to start
   * @param <T>          the type of the subject
   */
  <T> void start(T timedSubject, Duration duration, TimerAction<T> timer);

  /**
   * Returns whether the given subject has a running timer of the given class.
   *
   * @param timedSubject the subject to check
   * @param timerClass   the class of the timer
   * @return the result of the evaluation
   */
  EvaluationResult has(Object timedSubject, Class<? extends TimerAction<?>> timerClass);

  /**
   * Returns whether users should be notified after there cooldown ended.
   *
   * @return {@code true} if users should be notified
   */
  boolean notifyOnCooldownFinish();

  /**
   * Returns whether users should be notified once there warmup starts.
   *
   * @return {@code true} if users should be notified
   */
  boolean notifyOnWarmupStart();

  /**
   * Returns whether a running warmup should be aborted if the user takes any damage.
   *
   * @return {@code true} if the warmup should be aborted on damage
   */
  boolean abortWarmupOnDamage();

  /**
   * Returns whether a running warmup should be aborted if the user moves.
   *
   * @return {@code true} if the warmup should be aborted on move
   */
  boolean abortWarmupOnMove();

  /**
   * The result of an evaluation that checked whether a certain subject has a running timer.
   *
   * @see #has(Object, Class)
   */
  class EvaluationResult {

    private final boolean timerRunning;
    @Nullable
    private final Duration durationLeft;

    /**
     * Creates an instance.
     *
     * @param durationLeft the Duration left on the running Timer
     */
    private EvaluationResult(boolean timerRunning, @Nullable Duration durationLeft) {
      this.timerRunning = timerRunning;
      this.durationLeft = durationLeft;
    }

    /**
     * Returns whether a timer is running.
     *
     * @return {@code true} if a timer is running
     */
    public boolean isTimerRunning() {
      return timerRunning;
    }

    /**
     * Gets the duration left on the running timer.
     *
     * @return the duration left
     * @throws IllegalStateException if no timer is running and thus {@link #isTimerRunning()} returns {@code true}
     */
    public Duration getDurationLeft() {
      checkState(timerRunning);
      return durationLeft;
    }

    /**
     * Creates an instance that indicates no running timers.
     *
     * @return an instance
     */
    public static EvaluationResult noRunningTimer() {
      return new EvaluationResult(false, null);
    }

    /**
     * Creates an instance that indicates a timer is running and the given {@code duration} is left until it finishes.
     *
     * @param duration the duration left
     * @return an instance
     */
    public static EvaluationResult runningTimer(Duration duration) {
      return new EvaluationResult(true, duration);
    }
  }
}
