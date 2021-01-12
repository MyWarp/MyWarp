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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

/**
 * Tries to resolve a player's Locale by using reflection to access MC's internals through CraftBukkit.
 **/
class CraftBukkitLocaleResolver implements LocaleResolver {

  private static final Logger log = MyWarpLogger.getLogger(CraftBukkitLocaleResolver.class);

  private static final String METHOD_NAME = "getHandle";
  private static final String FIELD_NAME = "locale";

  private final Method handleMethod;
  private final Field localeField;

  private CraftBukkitLocaleResolver(Method handleMethod, Field localeField) {
    this.handleMethod = handleMethod;
    this.localeField = localeField;
  }

  static LocaleResolver create(Class<? extends Player> p) throws NoSuchFieldException, NoSuchMethodException {
    Method handleMethod = p.getMethod(METHOD_NAME);
    handleMethod.setAccessible(true);

    Field localeField = handleMethod.getReturnType().getDeclaredField(FIELD_NAME);
    localeField.setAccessible(true);

    return new CraftBukkitLocaleResolver(handleMethod, localeField);
  }

  @Override
  public Optional<Locale> resolve(Player player) {
    String rawLocale;
    try {
      rawLocale = localeField.get(handleMethod.invoke(player)).toString();
    } catch (IllegalAccessException | InvocationTargetException e) {
      log.debug("Failed to resolve the locale.", e);
      return Optional.empty();
    }

    return MinecraftLocaleParser.parseLocale(rawLocale);
  }

}
