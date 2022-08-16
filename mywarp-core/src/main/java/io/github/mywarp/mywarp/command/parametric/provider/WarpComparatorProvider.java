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

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;
import io.github.mywarp.mywarp.util.WarpUtils;
import io.github.mywarp.mywarp.warp.Warp;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.Comparator;
import java.util.List;

/**
 * Provides Comparators for Warps.
 */
class WarpComparatorProvider extends AbstractProvider<Comparator<Warp>> implements Provider<Comparator<Warp>> {

  @Nullable
  @Override
  public Comparator<Warp> get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException {
    return WarpUtils.getComparator(arguments.next());
  }
}
