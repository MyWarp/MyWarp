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

package me.taylorkelly.mywarp.command.parametric.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;

import me.taylorkelly.mywarp.command.parametric.provider.exception.NoSuchFileException;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

/**
 * Provides {@link File} instances.
 */
class FileProvider implements Provider<File> {

  private final File base;

  FileProvider(File base) {
    this.base = base;
  }

  @Override
  public boolean isProvided() {
    return false;
  }

  @Nullable
  @Override
  public File get(CommandArgs arguments, List<? extends Annotation> modifiers)
          throws ArgumentException, ProvisionException {
    File ret = new File(base, arguments.next());
    if (!ret.exists() || !ret.canRead()) {
      throw new NoSuchFileException(ret);
    }
    return ret;
  }

  @Override
  public List<String> getSuggestions(String prefix, Namespace namespace) {
    //TODO this does not work well for folders
    final File specified = new File(base, prefix);

    File searchFolder = specified.getParentFile();
    if (searchFolder == null || !searchFolder.exists()) {
      return Collections.emptyList();
    }

    String[] list = searchFolder.list(new FilenameFilter() {
      @Override
      public boolean accept(File file, String name) {
        return name.startsWith(specified.getName());
      }
    });
    return list != null ? Arrays.asList(list) : Collections.<String>emptyList();
  }
}
