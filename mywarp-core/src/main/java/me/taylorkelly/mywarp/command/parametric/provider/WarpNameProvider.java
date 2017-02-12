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
import com.sk89q.intake.parametric.ProvisionException;

import me.taylorkelly.mywarp.command.CommandHandler;
import me.taylorkelly.mywarp.command.parametric.provider.exception.InvalidWarpNameException;
import me.taylorkelly.mywarp.command.parametric.provider.exception.InvalidWarpNameException.Reason;
import me.taylorkelly.mywarp.util.WarpUtils;
import me.taylorkelly.mywarp.warp.Warp;
import me.taylorkelly.mywarp.warp.WarpManager;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Provides {@link String} instances that are valid to be used as a name for a {@link Warp}.
 */
class WarpNameProvider extends NonSuggestiveProvider<String> {

  private WarpManager warpManager;
  private CommandHandler commandHandler;

  WarpNameProvider(WarpManager warpManager, CommandHandler commandHandler) {
    this.warpManager = warpManager;
    this.commandHandler = commandHandler;
  }

  @Override
  public String get(CommandArgs arguments, List<? extends Annotation> modifiers)
          throws ArgumentException, ProvisionException {
    String name = arguments.next();

    if (warpManager.containsByName(name)) {
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
}
