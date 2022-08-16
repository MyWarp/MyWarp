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

package io.github.mywarp.mywarp.platform;

import io.github.mywarp.mywarp.command.util.UserViewableException;
import io.github.mywarp.mywarp.util.Message;

import java.util.Locale;

/**
 * Someone who can interact with MyWarp.
 */
public interface Actor {

  /**
   * Gets the name of this Actor.
   *
   * @return the name
   */
  String getName();

  /**
   * Returns whether this Actor has the given permission.
   *
   * @param node the permission-node
   * @return {@code true} if the Actor has the permission
   */
  boolean hasPermission(String node);

  /**
   * Sends a message to this Actor.
   *
   * <p>The message is formatted according to the configuration of the given {@code msg} instance.</p>
   *
   * @param msg the message
   * @see #sendMessage(String)
   * @see #sendError(String)
   */
  void sendMessage(Message msg);

  /**
   * Sends a message to this Actor.
   *
   * <p>The message will be formatted according to MyWarp's defaults.</p>
   *
   * @param msg the message
   * @see #sendError(String)
   * @see #sendMessage(Message)
   */
  default void sendMessage(String msg) {
    sendMessage(Message.builder().append(Message.Style.DEFAULT).append(msg).build());
  }

  /**
   * Sends an error message to this Actor.
   *
   * <p>The message will be formatted to indicate an error.</p>
   *
   * @param msg the error-message
   * @see #sendMessage(String)
   * @see #sendMessage(Message)
   */
  default void sendError(String msg) {
    sendMessage(Message.builder().append(Message.Style.ERROR).append(msg).build());
  }

  default void sendError(UserViewableException ex) {
    sendError(ex.getUserMessage());
  }

  /**
   * Gets the current Locale of this Actor.
   *
   * @return the Actor's Locale
   */
  Locale getLocale();

}
