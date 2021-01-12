/*
 * Copyright (C) 2011 - 2021, MyWarp team and contributors
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

import com.sk89q.intake.argument.ArgumentParseException;
import io.github.mywarp.mywarp.command.util.UserViewableException;

/**
 * Thrown when a given input does not match the requirements.
 */
public abstract class NonMatchingInputException extends ArgumentParseException implements UserViewableException {

  private final String input;

  /**
   * Creates an instance.
   *
   * @param input the invalid input
   */
  NonMatchingInputException(String input) {
    super("Invalid input: '" + input + "'");
    this.input = input;
  }

  /**
   * Gets the input that was invalid.
   *
   * @return the invalid input
   */
  public String getInput() {
    return input;
  }

  @Override
  public String getLocalizedMessage() {
    return getUserMessage();
  }
}
