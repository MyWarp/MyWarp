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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import javax.annotation.Nullable;

/**
 * Searches for {@link PropertyResourceBundle}s in a folder, before trying to resolve them from the classpath.
 */
public class FolderSourcedControl extends EncodedControl {

  private final File bundleFolder;

  /**
   * Creates an instance that tries to load bundles from the given {@code folder} before falling back to the classpath.
   * Bundles are read with an {@code UTF-8} encoding.
   *
   * @param folder the folder to load from
   */
  public FolderSourcedControl(File folder) {
    super();
    this.bundleFolder = folder;
  }

  /**
   * Creates an instance that tries to load bundles from the given {@code folder} before falling back to the classpath.
   * Bundles are read with the given {@code encoding}.
   *
   * @param folder   the folder to load from
   * @param encoding the encoding to use
   */
  public FolderSourcedControl(Charset encoding, File folder) {
    super(encoding);
    this.bundleFolder = folder;
  }

  @Override
  @Nullable
  public ResourceBundle newBundle(final String baseName, final Locale locale, final String format,
                                  final ClassLoader loader, final boolean reload) throws IOException {
    checkArgument(ResourceBundle.Control.FORMAT_PROPERTIES.contains(format), "unknown format: %s", format);

    final String bundleName = toBundleName(baseName, locale);
    final String resourceName = toResourceName(bundleName, "properties");

    ResourceBundle bundle = null;
    final InputStream stream;

    //this implementation mirrors Java's but initializes the ResourceBundle with a Reader and encoding
    try {
      stream = AccessController.doPrivileged(new PrivilegedExceptionAction<InputStream>() {
        public InputStream run() throws IOException {
          InputStream is = null;

          //never load the base locale from a file
          if (locale != Locale.ROOT) {
            File bundleFile = new File(bundleFolder, resourceName);
            if (bundleFile.isFile()) {
              is = new FileInputStream(bundleFile);
            }
          }
          if (is == null) {
            is = readResource(resourceName, loader, reload);
          }
          return is;
        }
      });
    } catch (PrivilegedActionException e) {
      throw (IOException) e.getException();
    }

    if (stream != null) {
      bundle = createBundle(stream);
    }

    return bundle;
  }
}
