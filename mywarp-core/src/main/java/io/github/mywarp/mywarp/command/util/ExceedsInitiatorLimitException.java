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

package io.github.mywarp.mywarp.command.util;

import com.sk89q.intake.CommandException;

import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.service.limit.Limit;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;

import org.apache.commons.lang.text.StrBuilder;

/**
 * Indicates that an action exceeds or would exceed the limit of the initiator himself.
 *
 * @see ExceedsLimitException for an Exception thrown when the limit is unknown by the initiator
 */
public class ExceedsInitiatorLimitException extends CommandException {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final Limit.Value exceededLimit;
  private final int limitMaximum;

  /**
   * Constructs an instance.
   *
   * @param exceededLimit the exceeded Limit.Value
   * @param limitMaximum  the maximum number of warps a user can create under the exceeded limit
   */
  public ExceedsInitiatorLimitException(Limit.Value exceededLimit, int limitMaximum) {
    this.exceededLimit = exceededLimit;
    this.limitMaximum = limitMaximum;
  }

  /**
   * Gets the exceeded Limit.Value.
   *
   * @return the exceeded Limit.Value
   */
  public Limit.Value getExceededLimit() {
    return exceededLimit;
  }

  /**
   * Gets the maximum number of warps a user can create under the exceeded limit.
   *
   * @return the maximum number of warps of the exceeded limit
   */
  public int getLimitMaximum() {
    return limitMaximum;
  }

  @Override
  public String getLocalizedMessage() {
    StrBuilder builder = new StrBuilder();

    switch (exceededLimit) {
      case TOTAL:
        builder.append(msg.getString("exception.exceeds-initiator-limit.total", limitMaximum));
        break;
      case PRIVATE:
        builder.append(msg.getString("exception.exceeds-initiator-limit.private", limitMaximum));
        break;
      case PUBLIC:
        builder.append(msg.getString("exception.exceeds-initiator-limit.public", limitMaximum));
        break;
      default:
        assert false : exceededLimit;
    }
    builder.appendNewLine();
    builder.append(msg.getString("exception.exceeds-initiator-limit.delete-warps"));
    return builder.toString();
  }
}
