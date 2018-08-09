/*
 * Copyright (C) 2011 - 2018, MyWarp team and contributors
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

import io.github.mywarp.mywarp.platform.Profile;
import io.github.mywarp.mywarp.util.playermatcher.UuidPlayerMatcher;

/**
 * Matches players using a Profile's unique identifier.
 *
 * <p>Unless a Profile is already available, {@link UuidPlayerMatcher} should be used instead.</p>
 */
public class ProfilePlayerMatcher extends UuidPlayerMatcher {

  private final Profile profile;

  /**
   * Creates an instance that will match the player who has the unique identifer of the given profile.
   *
   * @param profile the profile
   */
  public ProfilePlayerMatcher(Profile profile) {
    super(profile.getUuid());
    this.profile = profile;
  }

  /**
   * Gets the Profile of the player who this matcher matches.
   *
   * @return the profile
   */
  public Profile getProfile() {
    return profile;
  }
}
