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

package io.github.mywarp.mywarp.command.parametric.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;

import io.github.mywarp.mywarp.command.parametric.provider.exception.InvalidDataServiceConfigException;
import io.github.mywarp.mywarp.platform.InvalidFormatException;
import io.github.mywarp.mywarp.platform.Platform;
import io.github.mywarp.mywarp.warp.storage.SqlDataService;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Provides {@link SqlDataService} instances.
 */
class DataServiceProvider extends AbstractProvider<SqlDataService> {

  private final Platform platform;

  DataServiceProvider(Platform platform) {
    this.platform = platform;
  }

  @Override
  public SqlDataService get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException {
    String config = arguments.next();
    try {
      return platform.createDataService(config);
    } catch (InvalidFormatException e) {
      throw new InvalidDataServiceConfigException(config, e.getExpectedFormat());
    }
  }
}
