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

package io.github.mywarp.mywarp.bukkit.settings;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableMap;

import io.github.mywarp.mywarp.bukkit.util.permission.ValueBundle;
import io.github.mywarp.mywarp.service.teleport.timer.TimerAction;
import io.github.mywarp.mywarp.service.teleport.timer.WarpCooldown;
import io.github.mywarp.mywarp.service.teleport.timer.WarpWarmup;

import org.bukkit.configuration.ConfigurationSection;

import java.time.Duration;
import java.util.Map;

/**
 * A ValueBundle that bundles Durations.
 */
public class DurationBundle extends ValueBundle {

  private final Map<Class<? extends TimerAction<?>>, Duration> durations;

  /**
   * Creates a new bundle with the given {@code identifier} and the given {@code values}.
   *
   * <p>Individual fees are read from {@code values}. Non existing entries are read as {@code 0}.</p>
   *
   * @param identifier the bundle's identifier
   * @param values     the bundle's values
   */
  static DurationBundle create(String identifier, ConfigurationSection values) {
    checkNotNull(identifier);
    checkNotNull(values);

    return new DurationBundle(identifier, createDuration(values.getLong("warpCooldown")),
                              createDuration(values.getLong("warpWarmup")));
  }

  private DurationBundle(String identifier, Duration warpCooldown, Duration warpWarmup) {
    super(identifier, "mywarp.timer");

    durations =
        ImmutableMap.<Class<? extends TimerAction<?>>, Duration>builder().put(WarpCooldown.class, warpCooldown)
            .put(WarpWarmup.class, warpWarmup).build();
  }

  /**
   * Gets the Duration referenced by the given type.
   *
   * @param clazz the TimerAction class the Duration is requested for
   * @return the referenced duration
   */
  public Duration get(Class<? extends TimerAction<?>> clazz) {
    return durations.get(clazz);
  }

  private static Duration createDuration(long seconds) {
    return Duration.ofSeconds(seconds);
  }

}
