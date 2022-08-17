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

package io.github.mywarp.mywarp.bukkit.util.permission.group;

import org.bukkit.entity.Player;

/**
 * Resolves permission-groups when running on Bukkit.
 */
public interface GroupResolver {

  /**
   * Returns whether the given player is in the group identified by the given group-id.
   *
   * @param player  the player
   * @param groupId the name of the group
   * @return true if the player is in the group
   */
  boolean hasGroup(Player player, String groupId);
}
