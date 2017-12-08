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

import java.io.File;

/**
 * Thrown when the given input does not match an existing {@link File}.
 */
public class NoSuchFileException extends NonMatchingInputException {

  /**
   * Creates an instance for the given File.
   *
   * @param nonExisting the non existing file
   */
  public NoSuchFileException(File nonExisting) {
    super(nonExisting.getAbsolutePath());
  }

  @Override
  public String getLocalizedMessage() {
    return msg.getString("exception.file-not-found", getInput());
  }
}
