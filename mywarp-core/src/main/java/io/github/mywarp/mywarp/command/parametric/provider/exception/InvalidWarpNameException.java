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

package io.github.mywarp.mywarp.command.parametric.provider.exception;

import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.util.WarpUtils;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.Warp;

/**
 * Thrown when a given input is not a valid name for a {@link Warp}.
 */
public class InvalidWarpNameException extends NonMatchingInputException {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  /**
   * The reason why a String is not valid as a name for a {@link Warp}.
   */
  public enum Reason {
    ALREADY_EXISTS, TOO_LONG, IS_CMD
  }

  private final Reason reason;

  /**
   * Creates an instance.
   *
   * @param input  the input
   * @param reason the reason why the input is not a valid name
   */
  public InvalidWarpNameException(String input, Reason reason) {
    super(input);
    this.reason = reason;
  }

  @Override
  public String getLocalizedMessage() {
    switch (reason) {
      case ALREADY_EXISTS:
        return msg.getString("create.warp-exists", getInput());
      case TOO_LONG:
        return msg.getString("create.name-too-long", WarpUtils.MAX_NAME_LENGTH);
      case IS_CMD:
        return msg.getString("create.name-is-cmd", getInput());
      default:
        return getMessage();
    }
  }
}
