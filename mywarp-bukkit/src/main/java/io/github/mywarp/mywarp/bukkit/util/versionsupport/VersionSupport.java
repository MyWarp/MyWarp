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

package io.github.mywarp.mywarp.bukkit.util.versionsupport;

import com.google.common.collect.ClassToInstanceMap;
import com.google.common.collect.MutableClassToInstanceMap;

/**
 * Handles functionality that requires different implementations for different versions if Minecraft or Bukkit.
 */
public final class VersionSupport {

  private static final ClassToInstanceMap<Object> map = MutableClassToInstanceMap.create();

  /**
   * Gets an instance of the given class if this class is covered by version support. Throws an exception otherwise.
   *
   * <p>Multiple class for the same class will always return the same instance. On the first call, an instance of the
   * implementation appropriate for the Bukkit and MC version currently running is created and cached.</p>
   *
   * @param clazz the class of the requested instance
   * @param <S>   the type of the requested instance
   * @return an appropriate instance of {@code clazz}
   * @throws IllegalArgumentException if {@code clazz} is not covered by version support
   */
  public static <S extends VersionSupportable> S get(Class<S> clazz) {
    if (!map.containsKey(clazz)) {

      if (clazz.equals(TamedHorseChecker.class)) {
        TamedHorseChecker checker;
        try {
          checker = TamedHorseChecker112.create();
        } catch (ClassNotFoundException ex) {
          checker = new TamedHorseChecker();
        }
        map.putInstance(TamedHorseChecker.class, checker);

      } else {
        throw new IllegalArgumentException("'" + clazz + "' is not covered by VersionSupport.");
      }
    }
    return map.getInstance(clazz);
  }

}
