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

package me.taylorkelly.mywarp.command.parametric.provider.exception;

import me.taylorkelly.mywarp.command.CommandHandler;
import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.platform.PlayerNameResolver;
import me.taylorkelly.mywarp.util.i18n.DynamicMessages;

import java.util.UUID;

/**
 * Thrown when the given input does not match an online {@link LocalPlayer}.
 */
public class NoSuchPlayerException extends NonMatchingInputException {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  /**
   * Creates an instance.
   *
   * @param uniqueId the unique identifier of the Player
   * @param resolver the PlayerNameResolver to convert the identifier into a human readable name
   */
  public NoSuchPlayerException(UUID uniqueId, PlayerNameResolver resolver) {
    this(resolver.getByUniqueId(uniqueId).or(uniqueId.toString()));
  }

  /**
   * Creates an instance.
   *
   * @param input the input
   */
  public NoSuchPlayerException(String input) {
    super(input);
  }

  @Override
  public String getLocalizedMessage() {
    return msg.getString("exception.no-such-player", getInput());
  }
}
