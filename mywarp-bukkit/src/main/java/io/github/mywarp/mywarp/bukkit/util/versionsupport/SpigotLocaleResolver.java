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

package io.github.mywarp.mywarp.bukkit.util.versionsupport;

import io.github.mywarp.mywarp.util.MyWarpLogger;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

/**
 * Tries to resolve a player's locale by using methods from the Spigot API (using reflection).
 */
class SpigotLocaleResolver extends FallbackLocaleResolver {

  private static final Logger log = MyWarpLogger.getLogger(SpigotLocaleResolver.class);

  private static final String METHOD_NAME = "getLocale";

  private final Method method;

  private SpigotLocaleResolver(Method method) {
    this.method = method;
  }

  static SpigotLocaleResolver create() throws NoSuchMethodException {
    return new SpigotLocaleResolver(Player.class.getMethod(METHOD_NAME));
  }

  @Override
  public Optional<Locale> resolve(Player player) {
    String rawLocale;
    try {
      rawLocale = method.invoke(player).toString();
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.debug("Failed to resolve the locale.", e);
      return Optional.empty();
    }
    return MinecraftLocaleParser.parseLocale(rawLocale);
  }
}
