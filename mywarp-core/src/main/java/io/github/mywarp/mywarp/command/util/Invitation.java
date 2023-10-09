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

package io.github.mywarp.mywarp.command.util;

import io.github.mywarp.mywarp.util.playermatcher.PlayerMatcher;
import io.github.mywarp.mywarp.util.playermatcher.UuidPlayerMatcher;

/**
 * Matches players using a Profile's unique identifier.
 *
 * <p>Unless a Profile is already available, {@link UuidPlayerMatcher} should be used instead.</p>
 */
public class Invitation {

  private final PlayerMatcher matcher;
  private final String identifier;


  /**
   * Creates a new invitation
   *
   * @param matcher    the invitation's PlayerMatcher
   * @param identifier the invitation's given name as entered by the player
   */
  public Invitation(PlayerMatcher matcher, String identifier) {
    this.matcher = matcher;
    this.identifier = identifier;
  }

  /**
   * Gets the matcher.
   *
   * @return the matcher
   */
  public PlayerMatcher getMatcher() {
    return matcher;
  }

  /**
   * Gets the given name.
   *
   * @return the given name
   */
  public String getIdentifier() {
    return identifier;
  }
}
