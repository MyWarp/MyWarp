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

package io.github.mywarp.mywarp.bukkit.util;

import static com.google.common.base.Preconditions.checkArgument;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import io.github.mywarp.mywarp.bukkit.MyWarpPlugin;
import io.github.mywarp.mywarp.platform.capability.TimerCapability;
import io.github.mywarp.mywarp.service.teleport.timer.AbortableTimerAction;
import io.github.mywarp.mywarp.service.teleport.timer.Duration;
import io.github.mywarp.mywarp.service.teleport.timer.TimerAction;

import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

/**
 * Handles timers on Bukkit.
 */
public class BukkitTimerHandler {

  private final Table<Object, Class<? extends TimerAction>, SelfRunningRunnable>
      runningTimers =
      HashBasedTable.create();
  private final MyWarpPlugin plugin;

  /**
   * Creates an instance.
   *
   * @param plugin the running plugin instance
   */
  public BukkitTimerHandler(MyWarpPlugin plugin) {
    this.plugin = plugin;
  }

  /**
   * Starts the given {@code timer} with the given {@code duration} on the given subject.
   *
   * @param timedSubject the subject to start the timer on
   * @param duration     the duration to use
   * @param timer        the timer to start
   * @param <T>          the type of the subject
   */
  public <T> void start(T timedSubject, Duration duration, TimerAction<T> timer) {
    checkArgument(!has(timedSubject, timer.getClass()).isTimerRunning(),
                  "A timer of the type " + timer.getClass() + " is already running for " + timedSubject);

    runningTimers.put(timedSubject, timer.getClass(), new SelfRunningRunnable<T>(timer, duration));
  }

  /**
   * Returns whether the given subject has a running timer of the given class.
   *
   * @param timedSubject the subject to check
   * @param timerClass   the class of the timer
   * @return the result of the evaluation
   */
  public TimerCapability.EvaluationResult has(Object timedSubject, Class<? extends TimerAction> timerClass) {
    SelfRunningRunnable action = runningTimers.get(timedSubject, timerClass);
    if (action != null) {
      return TimerCapability.EvaluationResult.runningTimer(action.getRemainingTime());
    }
    return TimerCapability.EvaluationResult.noRunningTimer();
  }

  private void cancel(Object timedSubject, Class<? extends TimerAction> clazz) {
    BukkitRunnable runnable = runningTimers.remove(timedSubject, clazz);
    if (runnable != null) {
      runnable.cancel();
    }
  }

  /**
   * A BukkitRunnable that starts itself after creation.
   *
   * @param <T> the type of the instance the timer runs on
   */
  private class SelfRunningRunnable<T> extends BukkitRunnable {

    private final TimerAction<T> runnable;
    private final long startMillis;
    private final long durationMillis;

    @Nullable
    private final BukkitRunnable abortingCheck;

    /**
     * Creates an instance. The given Runnable will be submitted for execution after the given Duration.
     *
     * @param runnable the Runnable
     * @param duration the Duration
     */
    protected SelfRunningRunnable(TimerAction<T> runnable, Duration duration) {
      this.runnable = runnable;
      this.durationMillis = duration.get(TimeUnit.MILLISECONDS);

      this.startMillis = System.currentTimeMillis();
      runTaskLater(plugin, duration.getTicks());

      if (runnable instanceof AbortableTimerAction) {
        abortingCheck = new BukkitRunnable() {

          @Override
          public void run() {
            if (((AbortableTimerAction<T>) SelfRunningRunnable.this.runnable).abort()) {
              SelfRunningRunnable.this.cancel();
            }
          }

        };
        abortingCheck
            .runTaskTimer(plugin, AbortableTimerAction.CHECK_INTERVAL_TICKS, AbortableTimerAction.CHECK_INTERVAL_TICKS);
      } else {
        abortingCheck = null;
      }
    }

    /**
     * Gets the time remaining until this Runnable is executed.
     *
     * <p> The returned Duration might not be entirely exact, since Minecraft's internal clock may run faster or slower
     * than the real-world time. </p>
     *
     * @return the time remaining
     */
    protected Duration getRemainingTime() {
      return new Duration(durationMillis - (System.currentTimeMillis() - startMillis), TimeUnit.MILLISECONDS);
    }

    @Override
    public void run() {
      BukkitTimerHandler.this.cancel(runnable.getTimedSuject(), runnable.getClass());
      cancelAbortingCheck();
      runnable.run();
    }

    @Override
    public void cancel() {
      BukkitTimerHandler.this.cancel(runnable.getTimedSuject(), runnable.getClass());
      cancelAbortingCheck();
      super.cancel();
    }

    private void cancelAbortingCheck() {
      if (abortingCheck != null) {
        abortingCheck.cancel();
      }
    }

  }
}
