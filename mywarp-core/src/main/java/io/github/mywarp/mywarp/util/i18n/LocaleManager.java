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

package io.github.mywarp.mywarp.util.i18n;

import java.util.Locale;

/**
 * Manages the Thread's Locale.
 */
public class LocaleManager {

  private static final ThreadLocal<Locale> activeLocal = new ThreadLocal<Locale>() {

    @Override
    protected Locale initialValue() {
      return Locale.getDefault();
    }
  };

  private LocaleManager() {
  }

  /**
   * Gets the Locale for the Thread calling this method.
   *
   * @return the Locale
   */
  public static Locale getLocale() {
    return activeLocal.get();
  }

  /**
   * Sets the Locale for the Thread calling this method.
   *
   * @param locale the Locale
   */
  public static void setLocale(Locale locale) {
    activeLocal.set(locale);
  }
}
