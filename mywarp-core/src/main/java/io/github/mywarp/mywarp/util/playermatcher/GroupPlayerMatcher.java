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

package io.github.mywarp.mywarp.util.playermatcher;

import io.github.mywarp.mywarp.platform.LocalPlayer;

/**
 * Matches players based on their permission groups.
 *
 * @see LocalPlayer#hasGroup(String)
 */
public class GroupPlayerMatcher extends AbstractPlayerMatcher<String> {

  /**
   * Creates a new instance that matches players with the given {@code groupId}.
   *
   * @param groupId the group identifier
   */
  public GroupPlayerMatcher(String groupId) {
    super(groupId);
  }

  @Override
  public boolean test(LocalPlayer player) {
    return player.hasGroup(criteria);
  }

}
