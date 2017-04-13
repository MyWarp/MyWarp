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

package io.github.mywarp.mywarp.warp;

import java.util.UUID;

/**
 * Implements methods that can be resolved using other existing methods.
 */
abstract class AbstractWarp implements Warp {

  @Override
  public boolean isCreator(UUID uniqueId) {
    return getCreator().equals(uniqueId);
  }

  @Override
  public boolean isType(Type type) {
    return getType() == type;
  }

  @Override
  public boolean isPlayerInvited(UUID uniqueId) {
    return getInvitedPlayers().contains(uniqueId);
  }

  @Override
  public boolean isGroupInvited(String groupId) {
    return getInvitedGroups().contains(groupId);
  }

  @Override
  public int compareTo(Warp that) {
    return getName().compareTo(that.getName());
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (getName() == null ? 0 : getName().hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    SimpleWarp other = (SimpleWarp) obj;
    if (getName() == null) {
      if (other.getName() != null) {
        return false;
      }
    } else if (!getName().equals(other.getName())) {
      return false;
    }
    return true;
  }
}
