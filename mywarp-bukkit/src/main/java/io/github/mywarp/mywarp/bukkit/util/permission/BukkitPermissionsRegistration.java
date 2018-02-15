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

package io.github.mywarp.mywarp.bukkit.util.permission;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Controls registered permissions on Bukkit.
 */
public enum BukkitPermissionsRegistration {

  /**
   * The singleton instance.
   */
  INSTANCE;

  private final Set<Permission> registeredPermissions = new HashSet<Permission>();

  /**
   * Registers the given Permission on the server.
   *
   * @param perm the Permission
   */
  public void register(Permission perm) {
    Bukkit.getPluginManager().addPermission(perm);
    registeredPermissions.add(perm);
  }

  /**
   * Unregisters the given Permission from the server.
   *
   * @param perm the Permission
   */
  public void unregister(Permission perm) {
    Bukkit.getPluginManager().removePermission(perm);
    registeredPermissions.remove(perm);
  }

  /**
   * Unregisters all previously registered permissions from the server.
   */
  public void unregisterAll() {
    for (Iterator<Permission> iterator = registeredPermissions.iterator(); iterator.hasNext(); ) {
      Permission perm = iterator.next();

      Bukkit.getPluginManager().removePermission(perm);
      iterator.remove();
    }
  }

}
