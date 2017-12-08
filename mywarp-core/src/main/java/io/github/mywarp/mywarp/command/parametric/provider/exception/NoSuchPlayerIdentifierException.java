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

/**
 * Thrown when a given input does not match a known player.
 * <p/>
 * Typically this is caused by a malformed query or unavailable UUID servers.
 */
public class NoSuchPlayerIdentifierException extends NonMatchingInputException {

  /**
   * Creates an instance.
   *
   * @param input the input
   */
  public NoSuchPlayerIdentifierException(String input) {
    super(input);
  }

  @Override
  public String getLocalizedMessage() {
    return msg.getString("exception.no-such-profile", getInput());
  }
}
