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

package me.taylorkelly.mywarp.bukkit.settings;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import me.taylorkelly.mywarp.bukkit.BukkitAdapter;
import me.taylorkelly.mywarp.bukkit.util.permission.ValueBundle;
import me.taylorkelly.mywarp.platform.LocalWorld;
import me.taylorkelly.mywarp.service.limit.Limit;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A ValueBundle that bundles warp creation limits.
 */
public class LimitBundle extends ValueBundle implements Limit {

  private static final String WORLD_KEY = "affectedWorlds";

  private final Map<Type, Integer> limitMap;
  private final WorldHolder worldHolder;

  /**
   * Creates a new bundle with the given {@code identifier} and the given {@code values}.
   *
   * <p>Individual limits are read from {@code values}. Non existing limits are read as {@code 0}; non-existing worlds
   * are ignored.</p>
   *
   * @param identifier the bundle's identifier
   * @param values     the bundle's values
   */
  static LimitBundle create(String identifier, ConfigurationSection values) {
    checkNotNull(identifier);
    checkNotNull(values);

    WorldHolder worldHolder;
    if (isGlobal(values)) {
      worldHolder = WorldHolder.INSTANCE;
    } else {

      Set<UUID> worlds = new HashSet<UUID>();
      for (String name : values.getStringList(WORLD_KEY)) {
        World world = Bukkit.getWorld(name);
        if (world == null) {
          //REVIEW warn?
          continue;
        }
        worlds.add(world.getUID());
      }
      worldHolder = new ConfiguredWorldHolder(worlds);
    }

    return new LimitBundle(identifier, createMap(values), worldHolder);
  }

  /**
   * Creates a new global bundle with the given {@code identifier} and the given {@code values}.
   *
   * <p>Individual limits are read from {@code values}. Non existing limits are read as {@code 0}; any affected worlds
   * configured in {@code values} are ignored.</p>
   *
   * @param identifier the bundle's identifier
   * @param values     the bundle's values
   */
  static LimitBundle createGlobal(String identifier, ConfigurationSection values) {
    checkNotNull(identifier);
    checkNotNull(values);

    return new LimitBundle(identifier, createMap(values), WorldHolder.INSTANCE);
  }

  private static boolean isGlobal(ConfigurationSection values) {
    return !values.contains(WORLD_KEY);
  }

  private static EnumMap<Limit.Type, Integer> createMap(ConfigurationSection values) {
    EnumMap<Type, Integer> ret = new EnumMap<Limit.Type, Integer>(Type.class);
    ret.put(Type.TOTAL, values.getInt("totalLimit"));
    ret.put(Type.PUBLIC, values.getInt("publicLimit"));
    ret.put(Type.PRIVATE, values.getInt("privateLimit"));
    return ret;
  }

  private LimitBundle(String identifier, EnumMap<Type, Integer> limitMap, WorldHolder worldHolder) {
    super(identifier, "mywarp.limit");
    this.limitMap = limitMap;
    this.worldHolder = worldHolder;
  }

  @Override
  public int getLimit(Type type) {
    return limitMap.get(type);
  }

  @Override
  public ImmutableSet<LocalWorld> getAffectedWorlds() {
    return worldHolder.getAffectedWorlds();
  }

  @Override
  public boolean isAffectedWorld(UUID worldIdentifier) {
    return worldHolder.isAffectedWorld(worldIdentifier);
  }

  /**
   * A managed reference to all worlds that exist on the server.
   */
  private static class WorldHolder {

    private static final WorldHolder INSTANCE = new WorldHolder();

    private WorldHolder() {
    }

    boolean isAffectedWorld(final UUID worldIdentifier) {
      for (LocalWorld world : getAffectedWorlds()) {
        if (world.getUniqueId().equals(worldIdentifier)) {
          return true;
        }
      }
      return false;
    }

    ImmutableSet<LocalWorld> getAffectedWorlds() {
      ImmutableSet.Builder<LocalWorld> builder = ImmutableSet.builder();
      for (World world : Bukkit.getWorlds()) {
        builder.add(BukkitAdapter.adapt(world));
      }
      return builder.build();
    }

  }

  /**
   * A managed reference to certain pre-configured worlds on the server.
   */
  private static class ConfiguredWorldHolder extends WorldHolder {

    private final Set<UUID> worldIdentifiers;

    private ConfiguredWorldHolder(Iterable<UUID> worldIdentifiers) {
      this.worldIdentifiers = Sets.newHashSet(worldIdentifiers);
    }

    @Override
    protected ImmutableSet<LocalWorld> getAffectedWorlds() {
      ImmutableSet.Builder<LocalWorld> builder = ImmutableSet.builder();
      for (World world : Bukkit.getWorlds()) {
        if (worldIdentifiers.contains(world.getUID())) {
          builder.add(BukkitAdapter.adapt(world));
        }
      }
      return builder.build();
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }

    LimitBundle that = (LimitBundle) o;

    if (limitMap != null ? !limitMap.equals(that.limitMap) : that.limitMap != null) {
      return false;
    }
    return worldHolder != null ? worldHolder.equals(that.worldHolder) : that.worldHolder == null;

  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (limitMap != null ? limitMap.hashCode() : 0);
    result = 31 * result + (worldHolder != null ? worldHolder.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "LimitBundle{" + "limitMap=" + limitMap + ", worldHolder=" + worldHolder + '}';
  }
}
