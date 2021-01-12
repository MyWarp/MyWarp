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

package io.github.mywarp.mywarp.platform;

import io.github.mywarp.mywarp.command.util.UserViewableException;
import java.util.UUID;

/**
 * Thrown when the given input does not match an online {@link LocalPlayer}.
 */
public class NoSuchProfileException extends Exception implements UserViewableException {

  private final String input;

  /**
   * Creates an instance.
   *
   * @param uniqueId the unique identifier of the non existing player
   */
  public NoSuchProfileException(UUID uniqueId) {
    this(uniqueId.toString());
  }

  /**
   * Creates an instance.
   *
   * @param input the input
   */
  public NoSuchProfileException(String input) {
    this.input = input;
  }

  /**
   * Gets a string representation of the input that caused this Exception.
   *
   * <p>This may be an invalid player-name or a non-existing unique identifier.</p>
   *
   * @return the input causing this Exception
   */
  public String getInput() {
    return input;
  }

  @Override
  public String getUserMessage() {
    return msg.getString("exception.no-such-profile", getInput());
  }
}
