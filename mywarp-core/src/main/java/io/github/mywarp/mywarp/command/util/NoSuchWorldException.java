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
import java.util.UUID;

/**
 * Thrown when an attempt is made to access a world that does not exist on the server at the moment, the attempt was
 * made.
 */
public class NoSuchWorldException extends CommandException implements UserViewableException {

  private final UUID worldIdentifier;

  /**
   * Creates an instance.
   *
   * @param worldIdentifier the world's unique identifier
   */
  NoSuchWorldException(UUID worldIdentifier) {
    this.worldIdentifier = worldIdentifier;
  }

  /**
   * Gets the unique identifier of the world that is unavailable.
   *
   * @return the identifier of the unavailable world
   */
  public UUID getWorldIdentifier() {
    return worldIdentifier;
  }

  @Override
  public String getUserMessage() {
    return msg.getString("exception.no-such-world", worldIdentifier);
  }
}
