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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.List;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;
import javax.annotation.Nullable;

/**
 * Creates {@link PropertyResourceBundle}s as described by the default implementation, but initializes them with a
 * custom encoding.
 */
class EncodedControl extends ResourceBundle.Control {

  private static final Charset DEFAULT_ENCODING = StandardCharsets.UTF_8;

  private final Charset encoding;

  /**
   * Creates an instance that resolves bundles from the classpath and reads them with a <b>UTF-8</b> encoding.
   */
  public EncodedControl() {
    this(DEFAULT_ENCODING);
  }

  /**
   * Creates an instance that resolves bundles from the classpath and reads them with the given {@code encoding}.
   *
   * @param encoding the encoding to use
   */
  public EncodedControl(Charset encoding) {
    this.encoding = encoding;
  }

  @Override
  public List<String> getFormats(String baseName) {
    return ResourceBundle.Control.FORMAT_PROPERTIES;
  }

  @Override
  @Nullable
  public ResourceBundle newBundle(final String baseName, final Locale locale, final String format,
      final ClassLoader loader, final boolean reload) throws IOException {
    checkArgument(ResourceBundle.Control.FORMAT_PROPERTIES.contains(format), "unknown format: " + format);

    final String bundleName = toBundleName(baseName, locale);
    final String resourceName = toResourceName(bundleName, "properties");

    ResourceBundle bundle = null;
    InputStream stream;

    try {
      stream =
          AccessController
              .doPrivileged((PrivilegedExceptionAction<InputStream>) () -> readResource(resourceName, loader, reload));
    } catch (PrivilegedActionException e) {
      throw (IOException) e.getException();
    }

    if (stream != null) {
      bundle = createBundle(stream);
    }

    return bundle;
  }

  /**
   * Creates an {@code InputStream} for the resource of the given {@code resourceName} using the given {@code loader} to
   * load it. May return {@code null} if the resource could not be loaded.
   *
   * @param resourceName the name of the resource
   * @param loader       the loader
   * @param reload       whether the resource is expired and should be reloaded from disk
   * @return an {@code InputStream} for the resource
   * @throws IOException if a I/O error occurs
   */
  @Nullable
  protected InputStream readResource(String resourceName, ClassLoader loader, boolean reload) throws IOException {
    InputStream is = null;

    if (reload) {
      URL url = loader.getResource(resourceName);
      if (url != null) {
        URLConnection connection = url.openConnection();
        if (connection != null) {
          // Disable caches to get fresh data for reloading.
          connection.setUseCaches(false);
          is = connection.getInputStream();
        }
      }
    }
    if (is == null) {
      is = loader.getResourceAsStream(resourceName);
    }
    return is;
  }

  /**
   * Creates a new ResourceBundle from the given {@code stream}, using this instances encodind.
   *
   * @param stream the stream to read from
   * @return a matching ResourceBundle
   * @throws IOException if a I/O error occurs
   */
  protected ResourceBundle createBundle(InputStream stream) throws IOException {
    try (Reader reader = new InputStreamReader(stream, encoding)) {
      return new PropertyResourceBundle(reader);
    }

  }
}
