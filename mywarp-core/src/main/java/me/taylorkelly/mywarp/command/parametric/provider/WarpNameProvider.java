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

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.ProvisionException;

import me.taylorkelly.mywarp.command.CommandHandler;
import me.taylorkelly.mywarp.command.parametric.provider.exception.InvalidWarpNameException;
import me.taylorkelly.mywarp.command.parametric.provider.exception.InvalidWarpNameException.Reason;
import me.taylorkelly.mywarp.platform.Settings;
import me.taylorkelly.mywarp.util.WarpUtils;
import me.taylorkelly.mywarp.warp.Warp;
import me.taylorkelly.mywarp.warp.WarpManager;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Provides {@link String} instances that are valid to be used as a name for a {@link Warp}.
 */
class WarpNameProvider extends NonSuggestiveProvider<String> {

  private final WarpManager warpManager;
  private final CommandHandler commandHandler;
  private final Settings settings;

  WarpNameProvider(WarpManager warpManager, CommandHandler commandHandler, Settings settings) {
    this.warpManager = warpManager;
    this.commandHandler = commandHandler;
    this.settings = settings;
  }

  @Override
  public String get(CommandArgs arguments, List<? extends Annotation> modifiers)
      throws ArgumentException, ProvisionException {
    final String name = arguments.next();

    if (existsSameNameWarp(name, settings.isCaseSensitiveWarpNames())) {
      throw new InvalidWarpNameException(name, Reason.ALREADY_EXISTS);
    }
    if (name.length() > WarpUtils.MAX_NAME_LENGTH) {
      throw new InvalidWarpNameException(name, Reason.TOO_LONG);
    }
    if (commandHandler.isSubCommand(name)) {
      throw new InvalidWarpNameException(name, Reason.IS_CMD);
    }

    return name;
  }

  private boolean existsSameNameWarp(final String nameToCheck, boolean checkCaseSensitively) {
    if (checkCaseSensitively) {
      return warpManager.containsByName(nameToCheck);
    }

    return !Iterables.isEmpty(warpManager.getAll(new Predicate<Warp>() {
      @Override
      public boolean apply(Warp input) {
        return input.getName().equalsIgnoreCase(nameToCheck);
      }
    }));
  }
}
