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

/**
 * Thrown when a given input is not a valid data service configuration.
 *
 * @see io.github.mywarp.mywarp.platform.Platform#createDataService(String)
 * @see io.github.mywarp.mywarp.platform.InvalidFormatException
 */
public class InvalidDataServiceConfigException extends NonMatchingInputException {

  private final String expectedFormat;

  /**
   * Creates an instance.
   *
   * @param input          the invalid input
   * @param expectedFormat the expected format
   */
  public InvalidDataServiceConfigException(String input, String expectedFormat) {
    super(input);
    this.expectedFormat = expectedFormat;
  }

  @Override
  public String getLocalizedMessage() {
    return getUserMessage();
  }

  @Override
  public String getUserMessage() {
    return msg.getString("exception.invalid-data-service-config", expectedFormat, getInput());
  }
}
