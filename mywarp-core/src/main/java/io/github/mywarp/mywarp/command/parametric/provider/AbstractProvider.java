/*
 * Copyright (C) 2011 - 2022, MyWarp team and contributors
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

import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.parametric.Provider;

import java.util.Collections;
import java.util.List;

/**
 * An abstract Provider that by default consumes arguments from the command stack to provide values and does not suggest
 * any commands.
 */
abstract class AbstractProvider<T> implements Provider<T> {

  @Override
  public boolean isProvided() {
    return false;
  }

  @Override
  public List<String> getSuggestions(String prefix, Namespace locals) {
    return Collections.emptyList();
  }
}
