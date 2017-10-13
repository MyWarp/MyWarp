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

import static com.google.common.base.Preconditions.checkNotNull;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Parses Strings in Minecraft's locale format into {@link Locale} objects.
 */
class MinecraftLocaleParser {

  //XXX use Guava's LoadingCache once we updated to Guava 11
  private static final Map<String, Locale> cache = new HashMap<>();

  /**
   * Parses the given String into a Locale.
   *
   * <p>Returns an Optional with the resulting Locale or Optional.absent() if the String cannot be parsed into a
   * Locale.</p>
   *
   * @param rawLocale the String to convert
   * @return an Optional with the parsed Locale
   */
  static Optional<Locale> parseLocale(String rawLocale) {
    Locale locale = cache.get(rawLocale);
    if (locale == null) {
      try {
        locale = toLocaleCaseInsensitive(rawLocale);
      } catch (RuntimeException e) {
        return Optional.empty();
      }
      cache.put(rawLocale, locale);
    }
    return Optional.of(locale);
  }

  /**
   * Converts a String to a Locale.
   *
   * <p>This method takes the string format of a locale and creates the locale object from it.</p>
   *
   * <pre>
   *   toLocaleCaseInsensitive("")           = new Locale("", "")
   *   toLocaleCaseInsensitive("en")         = new Locale("en", "")
   *   toLocaleCaseInsensitive("en_GB")      = new Locale("en", "GB")
   *   toLocaleCaseInsensitive("en_GB_xxx")  = new Locale("en", "GB", "xyz")   (#)
   * </pre>
   *
   * <p>(#) The behaviour of the JDK variant constructor changed between JDK1.3 and JDK1.4. In JDK1.3, the constructor
   * upper cases the variant, in JDK1.4, it doesn't. Thus, the result from getVariant() may vary depending on your
   * JDK.</p>
   *
   * <p>This method validates the input: The length must be correct. The separator must be an underscore. </p>
   *
   * <p>It does <b>not</b> validate whether language and country code are lower or uppercase.</p>
   *
   * @param str the locale String to convert
   * @return a Locale
   * @throws IllegalArgumentException if the string is an invalid format
   * @see Locale#forLanguageTag(String)
   */
  //Note: this implementation is adapted from Apache Commons 2.6
  private static Locale toLocaleCaseInsensitive(final String str) {
    checkNotNull(str);

    if (str.isEmpty()) { //JDK 8 introduced an empty locale where all fields are blank which we do not support
      throw new IllegalArgumentException("Invalid locale format: " + str);
    }
    if (str.contains("#")) { //Cannot handle Java 7 script & extensions
      throw new IllegalArgumentException("Invalid locale format: " + str);
    }
    final int len = str.length();
    if (len < 2) {
      throw new IllegalArgumentException("Invalid locale format: " + str);
    }

    //String starting with '_'
    final char ch0 = str.charAt(0);
    if (ch0 == '_') {
      if (len < 3) {
        throw new IllegalArgumentException("Invalid locale format: " + str);
      }
      final char ch1 = str.charAt(1);
      final char ch2 = str.charAt(2);
      if (!Character.isUpperCase(ch1) || !Character.isUpperCase(ch2)) {
        throw new IllegalArgumentException("Invalid locale format: " + str);
      }
      if (len == 3) {
        return new Locale(StringUtils.EMPTY, str.substring(1, 3));
      }
      if (len < 5) {
        throw new IllegalArgumentException("Invalid locale format: " + str);
      }
      if (str.charAt(3) != '_') {
        throw new IllegalArgumentException("Invalid locale format: " + str);
      }
      return new Locale(StringUtils.EMPTY, str.substring(1, 3), str.substring(4));
    }

    //String string with characters, e.g. 'en_US'
    final String[] split = str.split("_", -1);
    final int occurrences = split.length - 1;
    switch (occurrences) {
      case 0:
        if ((len == 2 || len == 3)) {
          return new Locale(str);
        }
        throw new IllegalArgumentException("Invalid locale format: " + str);

      case 1:
        if ((split[0].length() == 2 || split[0].length() == 3) && split[1].length() == 2) {
          return new Locale(split[0], split[1]);
        }
        throw new IllegalArgumentException("Invalid locale format: " + str);

      case 2:
        if ((split[0].length() == 2 || split[0].length() == 3) && (split[1].length() == 0 || split[1].length() == 2)
            && split[2].length() > 0) {
          return new Locale(split[0], split[1], split[2]);
        }

        //fallthrough
      default:
        throw new IllegalArgumentException("Invalid locale format: " + str);
    }
  }
}
