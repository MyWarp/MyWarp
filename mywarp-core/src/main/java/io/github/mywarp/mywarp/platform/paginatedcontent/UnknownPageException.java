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

package io.github.mywarp.mywarp.platform.paginatedcontent;

import io.github.mywarp.mywarp.command.util.UserViewableException;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;

/**
 * Indicates that the requested page does not exist.
 */
public class UnknownPageException extends Exception implements UserViewableException {

  protected DynamicMessages msg = new DynamicMessages(SimplePaginatedContent.RESOURCE_BUNDLE_NAME);

  private final int highestPage;

  /**
   * Creats an instance.
   *
   * @param highestPage the number of the highest available page
   */
  public UnknownPageException(int highestPage) {
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

  @Override
  public String getUserMessage() {
    return msg.getString("unknown-page", getHighestPage());
  }
}
