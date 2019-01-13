/*
 * Copyright (C) 2011 - 2019, MyWarp team and contributors
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

/**
 * Thrown if an input does not match the required format.
 */
public class InvalidFormatException extends Exception {

  private final String query;
  private final String expectedFormat;

  /**
   * Creates an instance.
   *
   * @param query          the given query
   * @param expectedFormat the expected format
   */
  public InvalidFormatException(String query, String expectedFormat) {
    this.query = query;
    this.expectedFormat = expectedFormat;
  }

  /**
   * Gets the original query.
   *
   * @return the query
   */
  public String getQuery() {
    return query;
  }

  /**
   * Gets a human readable form of the required format.
   *
   * @return the required format
   */
  public String getExpectedFormat() {
    return expectedFormat;
  }
}
