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

import com.google.common.collect.ImmutableSortedSet;

import io.github.mywarp.mywarp.platform.LocalPlayer;

import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import java.util.SortedSet;

/**
 * Provides a managed access to {@link ValueBundle}s.
 *
 * <p>A provider instance is created with a default and multiple custom bundles. When a bundle for a player is requested
 * from the provider, it iterates over the custom bundles in the order defined for ValueBundles and returns the first
 * bundle for which the player has the permission. If he does not have a permission for any bundle, the default one is
 * returned.</p>
 *
 * @param <B> the type of ValueBundle this provider provides
 */
public class BundleProvider<B extends ValueBundle> {

  private SortedSet<B> configuredBundles;
  private B defaultBundle;

  /**
   * Creates an instance.
   *
   * @param configuredBundles the configured bundles checked via permission
   * @param defaultBundle     the default bundle returned when none of the configured bundles is applicable
   */
  public BundleProvider(Iterable<B> configuredBundles, B defaultBundle) {
    this.configuredBundles = ImmutableSortedSet.copyOf(configuredBundles);
    this.defaultBundle = defaultBundle;

    for (B bundle : configuredBundles) {
      BukkitPermissionsRegistration.INSTANCE.register(new Permission(bundle.getPermission(), PermissionDefault.FALSE));
    }
  }

  /**
   * Gets the bundle applicable for the given {@code player}.
   *
   * @param player the player for whom the bundle is requested
   * @return the applicable bundle
   */
  public B getBundle(LocalPlayer player) {
    for (B bundle : configuredBundles) {
      if (player.hasPermission(bundle.getPermission())) {
        return bundle;
      }
    }
    return defaultBundle;
  }

}
