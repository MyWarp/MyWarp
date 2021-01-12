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

package io.github.mywarp.mywarp.command.util;

import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;

/**
 * An Exception that includes a message to be displayed to users.
 *
 * <p>This interface should only be implemented by classes that also extend {@link Exception}.</p>
 *
 * @see io.github.mywarp.mywarp.platform.Actor#sendError(UserViewableException)
 */
public interface UserViewableException {

  DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  /**
   * Gets a localised string to be send to users informing them about the error.
   *
   * @return a user-readable information about the exception
   */
  String getUserMessage();

}
