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

package io.github.mywarp.mywarp.bukkit.util;

import io.github.mywarp.mywarp.util.MyWarpLogger;

import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * Resolves the Locale of {@link Player}s using the Locale of the player's client.
 *
 * <p>The resolver uses reflection to load Minecraft's player-object through CraftBukkit's {@code getHandle()} method,
 * and then accesses it's {@code locale} field. The process may fail if future Minecraft or CraftBukkit change this
 * structure. It is recommended to supply a fallback for such cases.</p>
 *
 * <p>This class is not threadsafe.</p>
 */
public enum ReflectiveLocaleResolver {

  /**
   * The singleton instance.
   */
  INSTANCE;

  private static final Logger log = MyWarpLogger.getLogger(ReflectiveLocaleResolver.class);

  @Nullable
  private Method handleMethod;
  @Nullable
  private Field localeField;

  /**
   * Resolves the locale of the given Player.
   *
   * @param player the Player
   * @return the locale of this Player
   * @throws UnresolvableLocaleException if the locale cannot be resolved
   */
  public Locale resolve(Player player) throws UnresolvableLocaleException {
    if (handleMethod == null) {
      try {
        //CraftBukkit implements Player in CraftPlayer with has the 'getHandle()' method
        handleMethod = player.getClass().getMethod("getHandle");
      } catch (NoSuchMethodException e) {
        log.debug("Failed to resolve the locale because the 'getHandle()' method does not exist.", e);
        throw new UnresolvableLocaleException(e);
      }
      handleMethod.setAccessible(true);
    }
    if (localeField == null) {
      try {
        localeField = handleMethod.getReturnType().getDeclaredField("locale");
      } catch (NoSuchFieldException e) {
        log.debug("Failed to resolve the locale because the 'locale' field does not exist.", e);
        throw new UnresolvableLocaleException(e);
      }
      localeField.setAccessible(true);
    }

    String rawLocale;
    try {
      rawLocale = (String) localeField.get(handleMethod.invoke(player));
    } catch (IllegalAccessException e) {
      log.debug("Failed to resolve the locale.", e);
      throw new UnresolvableLocaleException(e);
    } catch (InvocationTargetException e) {
      log.debug("Failed to resolve the locale because of an unhandled exception.", e);
      throw new UnresolvableLocaleException(e);
    }

    Optional<Locale> parsedLocale = MinecraftLocaleParser.parseLocale(rawLocale);
    if (parsedLocale.isPresent()) {
      return parsedLocale.get();
    }
    log.debug("Failed to parse the locale from String due to an invalid format: {}", rawLocale);
    throw new UnresolvableLocaleException();
  }

  /**
   * Indicates that a Locale cannot be resolved.
   */
  public class UnresolvableLocaleException extends Exception {

    private UnresolvableLocaleException() {
      super();
    }

    private UnresolvableLocaleException(Exception e) {
      super(e);
    }
  }

}
