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

package io.github.mywarp.mywarp.bukkit.util.versionsupport;

import io.github.mywarp.mywarp.util.MyWarpLogger;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.util.function.Predicate;

import javax.annotation.Nullable;

/**
 * Handles functionality that requires different implementations for different versions if Minecraft or Bukkit.
 */
public final class VersionSupport {

  private static final Logger log = MyWarpLogger.getLogger(VersionSupport.class);

  @Nullable
  private static LocaleResolver localeResolver;
  @Nullable
  private static Predicate<Entity> horseChecker;

  /**
   * Gets a {@link LocaleResolver} implementation.
   *
   * @param implClass the class that implements {@link Player} at runtime
   * @return a working locale resolver
   */
  public static LocaleResolver getLocaleResolver(Class<? extends Player> implClass) {
    if (localeResolver == null) {
      try {
        localeResolver = SpigotLocaleResolver.create();
        log.debug("Using SpigotLocaleResolver.");
      } catch (NoSuchMethodException e1) {
        try {
          localeResolver = CraftBukkitLocaleResolver.create(implClass);
          log.debug("Using CraftBukkitLocaleResolver.");
        } catch (ReflectiveOperationException e2) {
          localeResolver = new FallbackLocaleResolver();
          log.warn("Unable to create the LocaleResolver appropriate for this Bukkit implementation."
                   + "Player locales WILL NOT be resolved!");
        }
      }
    }
    return localeResolver;
  }

  /**
   * Gets a predicate to check if a horse is tamed.
   *
   * @return a working checker
   */
  public static Predicate<Entity> getTamedHorseChecker() {
    if (horseChecker == null) {
      try {
        horseChecker = TamedHorseChecker112.create();
        log.debug("Using TamedHorseChecker112.");
      } catch (ClassNotFoundException e) {
        horseChecker = new LegacyTamedHorseChecker();
        log.debug("Using LegacyTamedHorseChecker.");
      }
    }
    return horseChecker;
  }

}
