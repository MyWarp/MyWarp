/*
 * Copyright (C) 2011 - 2020, MyWarp team and contributors
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

package io.github.mywarp.mywarp.bukkit;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSortedSet;
import io.github.mywarp.mywarp.bukkit.settings.LimitBundle;
import io.github.mywarp.mywarp.bukkit.util.permission.BukkitPermissionsRegistration;
import io.github.mywarp.mywarp.bukkit.util.permission.ValueBundle;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.capability.LimitCapability;
import io.github.mywarp.mywarp.service.limit.Limit;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

/**
 * Limit compatibility for the Bukkit platform. The actual Limits are stored in {@link LimitBundle}s managed by this
 * provider.
 *
 * <p> Players either need to have a specific permission of a certain bundle that covers the world in question or they
 * fall under a global default bundle. If a player has the permission for more than one bundle, the alphabetically first
 * bundle will be used. </p>
 */
public class BukkitLimitCapability implements LimitCapability {

  private final SortedSet<LimitBundle> configuredLimits;
  private final LimitBundle defaultLimit;

  /**
   * Initializes this provider.
   *
   * @param configuredLimits the configured FeeBundles that are assigned to a player via a specific permission
   * @param defaultLimit     the default FeeBundle that acts as a fallback if a player has none of the specific
   *                         permissions
   */
  BukkitLimitCapability(Iterable<LimitBundle> configuredLimits, LimitBundle defaultLimit) {
    this.configuredLimits = ImmutableSortedSet.copyOf(configuredLimits);
    this.defaultLimit = defaultLimit;

    for (ValueBundle bundle : configuredLimits) {
      BukkitPermissionsRegistration.INSTANCE.register(new Permission(bundle.getPermission(), PermissionDefault.FALSE));
    }

    // register per-world overrides
    String base = "mywarp.limit.disobey";

    for (World world : Bukkit.getWorlds()) {
      // mywarp.limit.disobey.[WORLDNAME].*
      Permission worldPerm = new Permission(base + "." + world.getName() + ".*");
      worldPerm.addParent(base + ".*", true);
      BukkitPermissionsRegistration.INSTANCE.register(worldPerm);

      // mywarp.limit.disobey.[WORLDNAME].total etc.
      for (String type : Arrays.asList("total", "private", "public")) {
        Permission perm = new Permission(base + "." + world.getName() + "." + type);
        perm.addParent(worldPerm, true);
        BukkitPermissionsRegistration.INSTANCE.register(perm);
      }
    }
  }

  @Override
  public Limit getLimit(LocalPlayer player, LocalWorld world) {
    for (LimitBundle bundle : configuredLimits) {
      if (!player.hasPermission(bundle.getPermission())) {
        continue;
      }
      if (!bundle.isAffectedWorld(world.getUniqueId())) {
        continue;
      }
      return bundle;
    }
    return defaultLimit;
  }

  @Override
  public List<Limit> getEffectiveLimits(LocalPlayer player) {
    Builder<Limit> ret = ImmutableList.builder();
    Set<LocalWorld> worlds = new HashSet<>();
    for (LimitBundle bundle : configuredLimits) {
      if (!player.hasPermission(bundle.getPermission())) {
        continue;
      }
      if (worlds.containsAll(bundle.getAffectedWorlds())) {
        // the affective bundles already cover all worlds that this
        // bundle covers, so it is effectively overwritten.
        continue;
      }
      ret.add(bundle);
      worlds.addAll(bundle.getAffectedWorlds());
    }
    // if there is a world that is not covered by all bundles, the default
    // bundle (always global) is needed
    if (!worlds.containsAll(defaultLimit.getAffectedWorlds())) {
      ret.add(defaultLimit);
    }
    return ret.build();
  }

}
