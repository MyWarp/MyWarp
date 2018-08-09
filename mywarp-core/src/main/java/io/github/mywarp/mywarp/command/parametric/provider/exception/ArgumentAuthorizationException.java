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

package io.github.mywarp.mywarp.command.parametric.provider.exception;

import com.sk89q.intake.argument.ArgumentException;

import io.github.mywarp.mywarp.command.util.UserViewableException;

/**
 * Thrown if a given argument requires a permission, but the command sender does not have this permission.
 */
public class ArgumentAuthorizationException extends ArgumentException implements UserViewableException {

  @Override
  public String getUserMessage() {
    return msg.getString("exception.insufficient-permission");
  }
}
