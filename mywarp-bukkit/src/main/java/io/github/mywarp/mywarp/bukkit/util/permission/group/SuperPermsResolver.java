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

package io.github.mywarp.mywarp.bukkit.util.permission.group;

import org.bukkit.entity.Player;

/**
 * Resolves groups using Bukkit's native SuperPerms.
 *
 * <p>Since SuperPerms have no inherent concept of groups, this resolver checks if the subject has the {@code
 * group.[GROUPNAME]} permission to resolve group membership.</p>
 */
class SuperPermsResolver implements GroupResolver {

  private static final String groupPrefix = "group.";

  @Override
  public boolean hasGroup(Player player, String group) {
    return player.hasPermission(groupPrefix + group);
  }
}
