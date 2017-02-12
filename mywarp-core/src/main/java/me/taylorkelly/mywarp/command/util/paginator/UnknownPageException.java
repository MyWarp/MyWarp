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

package me.taylorkelly.mywarp.command.util.paginator;

/**
 * Indicates that the requested page does not exist.
 */
public class UnknownPageException extends Exception {

  private final int highestPage;

  UnknownPageException(int highestPage) {
    this.highestPage = highestPage;
  }

  UnknownPageException(String message, int highestPage) {
    super(message);
    this.highestPage = highestPage;
  }

  UnknownPageException(String message, Throwable cause, int highestPage) {
    super(message, cause);
    this.highestPage = highestPage;
  }

  UnknownPageException(Throwable cause, int highestPage) {
    super(cause);
    this.highestPage = highestPage;
  }

  /**
   * Gets the number of the highest existing page.
   *
   * @return the highestPage
   */
  public int getHighestPage() {
    return highestPage;
  }
}
