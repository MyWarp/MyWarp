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

package io.github.mywarp.mywarp.command.parametric.provider.exception;

import com.google.common.collect.ImmutableList;
import io.github.mywarp.mywarp.util.McUtil;
import io.github.mywarp.mywarp.warp.Warp;

/**
 * Thrown when a given input does not match an existing {@link Warp}.
 */
public class NoSuchWarpException extends NonMatchingInputException {

  private final ImmutableList<Warp> matches;

  /**
   * Creates an instance.
   *
   * @param input   the input
   * @param matches the possible matches of the input
   */
  public NoSuchWarpException(String input, ImmutableList<Warp> matches) {
    super(input);
    this.matches = matches;
  }

  @Override
  public String getLocalizedMessage() {
    return getUserMessage();
  }

  @Override
  public String getUserMessage() {
    StringBuilder builder = new StringBuilder();
    builder.append(msg.getString("exception.no-such-warp", getInput()));

    if (!matches.isEmpty()) {
      builder.append(McUtil.lineSeparator());
      builder.append(msg.getString("exception.no-such-warp.suggestion", matches.get(1).getName()));
    }
    return builder.toString();
  }
}
