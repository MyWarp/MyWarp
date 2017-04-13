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

package io.github.mywarp.mywarp.bukkit.util.permission.group;

import org.anjocaido.groupmanager.GroupManager;
import org.bukkit.entity.Player;

/**
 * Resolves groups using <a href="https://github.com/essentials/Essentials">Essentials' GroupManager</a>.
 */
class GroupManagerResolver implements GroupResolver {

  private final GroupManager groupManager;

  GroupManagerResolver(GroupManager groupManager) {
    this.groupManager = groupManager;
  }

  @Override
  public boolean hasGroup(Player player, String group) {
    return groupManager.getWorldsHolder().getWorldPermissions(player).inGroup(player.getName(), group);
  }
}
