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

import com.sk89q.intake.CommandException;
import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;

/**
 * Indicates that an action exceeds a limit of somebody else than the initiator.
 *
 * @see ExceedsInitiatorLimitException for an Exception thrown when the limit of the initiator is exceeded
 */
public class ExceedsLimitException extends CommandException implements UserViewableException {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final LocalPlayer subject;

  /**
   * Constructs an instance.
   *
   * @param subject the subject whose limit are or would be exceeded
   */
  public ExceedsLimitException(LocalPlayer subject) {
    this.subject = subject;
  }

  @Override
  public String getUserMessage() {
    return msg.getString("exception.exceeds-limit", subject.getName());
  }
}
