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

package io.github.mywarp.mywarp.util.i18n;

import io.github.mywarp.mywarp.util.MyWarpLogger;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.slf4j.Logger;

/**
 * Provides localized messages using Java ResourceBundles.
 *
 * <p>When created, instances will load MyWarp's built-in resource bundles. This process can be customized for all
 * existing instances by registering a custom {@link java.util.ResourceBundle.Control} via {@link
 * #setControl(ResourceBundle.Control)}.</p>
 */
public class DynamicMessages {

  private static final Logger log = MyWarpLogger.getLogger(DynamicMessages.class);

  private static ResourceBundle.Control control = new EncodedControl();

  private final String baseName;

  /**
   * Creates an instance. Strings will be looked up from a ResourceBundle of the given name.
   *
   * @param baseName the name of the ResourceBundle
   */
  public DynamicMessages(String baseName) {
    this.baseName = baseName;
  }

  /**
   * Clears the cache used by all DynamicMessages.
   */
  public static void clearCache() {
    ResourceBundle.clearCache();
  }

  /**
   * Sets the {@link java.util.ResourceBundle.Control} instance used by all DynamicMessages instances.
   *
   * @param control the ResourceBundle.Control
   */
  public static synchronized void setControl(ResourceBundle.Control control) {
    DynamicMessages.control = control;
  }

  /**
   * Gets a localized string.
   *
   * <p>If no localization exits, {@code $&#123;key&#125;} will be returned.</p>
   *
   * @param key the key
   * @return the localized string
   */
  public String getString(String key) {
    return getString(key, LocaleManager.getLocale());
  }

  /**
   * Gets a localized string.
   *
   * <p>If no localization exits, {@code $&#123;key&#125;} will be returned.</p>
   *
   * @param key    the key
   * @param locale the Locale
   * @return the localized string
   */
  public String getString(String key, Locale locale) {
    try {
      ResourceBundle bundle = getBundle(locale);
      return bundle.getString(key);
    } catch (MissingResourceException e) {
      log.warn("Failed to find message.", e);
    }
    return "${" + key + "}";
  }

  /**
   * Gets a localized and formatted string.
   *
   * <p> If no localization exits, {@code $&#123;key&#125;:args} will be returned. </p>
   *
   * @param key  the key
   * @param args the arguments
   * @return the localized string
   * @see java.text.MessageFormat
   */
  public String getString(String key, Object... args) {
    return getString(key, LocaleManager.getLocale(), args);
  }

  /**
   * Gets a localized and formatted string.
   *
   * <p> If no localization exits, {@code $&#123;key&#125;:args} will be returned.</p>
   *
   * @param key    the key
   * @param locale the Locale
   * @param args   the arguments
   * @return a localized string
   * @see java.text.MessageFormat
   */
  public String getString(String key, Locale locale, Object... args) {
    try {
      ResourceBundle bundle = getBundle(locale);
      MessageFormat format = new MessageFormat(bundle.getString(key), bundle.getLocale());
      return format.format(args);
    } catch (MissingResourceException e) {
      log.warn("Failed to find message.", e);
    }
    return "${" + key + "}:" + Arrays.toString(args);
  }

  /**
   * Gets the ResourceBundle applicable for the given key.
   *
   * @param locale the Locale
   * @return the applicable ResourceBundle
   */
  private ResourceBundle getBundle(Locale locale) {
    return ResourceBundle.getBundle(baseName, locale, control);
  }
}
