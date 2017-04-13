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

package io.github.mywarp.mywarp.command.parametric.namespace;

import com.sk89q.intake.parametric.ProvisionException;

import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;

/**
 * Thrown when an sub-instance of Actor is required, but the Actor who called the command is not an instance of the
 * required type.
 */
public class IllegalCommandSenderException extends ProvisionException {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final Actor actor;

  /**
   * Creates an instance.
   *
   * @param actor the Actor who called the command
   */
  public IllegalCommandSenderException(Actor actor) {
    super("Expected a different instance than " + actor.getClass().getCanonicalName());
    this.actor = actor;
  }

  /**
   * Gets the actor who called the command.
   *
   * @return the actor
   */
  public Actor getActor() {
    return actor;
  }

  @Override
  public String getLocalizedMessage() {
    return msg.getString("exception.illegal-command-sender");
  }
}
