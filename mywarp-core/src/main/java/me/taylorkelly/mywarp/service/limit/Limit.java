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

package me.taylorkelly.mywarp.service.limit;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;

import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.platform.LocalWorld;
import me.taylorkelly.mywarp.warp.Warp;

import java.util.EnumSet;
import java.util.UUID;

/**
 * A creation limit for warps. Implementations are expected to provide the limit for each {@link Value} and a way to
 * resolve these limit per world.
 */
public interface Limit {

  /**
   * Gets a list of all worlds that are affected by this Limit.
   *
   * @return a Set with all affected worlds
   */
  ImmutableSet<LocalWorld> getAffectedWorlds();

  /**
   * Returns whether the given LocalWorld is affected by this Limit.
   *
   * @param worldIdentifier the world to check
   * @return true if the given world is affected
   */
  boolean isAffectedWorld(UUID worldIdentifier);

  /**
   * Gets the maximum number of warps a user can create under the given Limit.Value.
   *
   * @param value the value of limit
   * @return the maximum number of warps
   */
  int get(Value value);

  /**
   * The different types limit.
   */
  enum Value {
    /**
     * The total limit (accounts all warps).
     */
    TOTAL(EnumSet.allOf(Warp.Type.class)), /**
     * The private limit (accounts only private warps).
     */
    PRIVATE(Warp.Type.PRIVATE), /**
     * The public limit (accounts only public warps).
     */
    PUBLIC(Warp.Type.PUBLIC);

    private final EnumSet<Warp.Type> warpTypes;

    Value(Warp.Type type) {
      this(EnumSet.of(type));
    }

    Value(EnumSet<Warp.Type> warpTypes) {
      this.warpTypes = warpTypes;
    }

    /**
     * Gets the condition of this Value.
     *
     * @return the condition
     */
    Predicate<Warp> getCondition() {
      return new Predicate<Warp>() {
        @Override
        public boolean apply(Warp input) {
          return warpTypes.contains(input.getType());
        }
      };
    }

    /**
     * Gets the name of this Value in lower case.
     *
     * @return this Value's name in lowercase
     * @see #name()
     */
    public String lowerCaseName() {
      return name().toLowerCase();
    }

    /**
     * Returns whether the given player can disobey any limit of this Value on the given world.
     *
     * @param player the player
     * @param world  the world
     * @return {@code true} if the player can disobey any limit of this Value
     */
    boolean canDisobey(LocalPlayer player, LocalWorld world) {
      String perm = "mywarp.limit.disobey." + world.getName() + "." + lowerCaseName();
      return player.hasPermission(perm);
    }

    static EnumSet<Value> getApplicableValues(Warp.Type warpType) {
      EnumSet<Value> ret = EnumSet.noneOf(Value.class);
      for (Value value : values()) {
        if (value.warpTypes.contains(warpType)) {
          ret.add(value);
        }
      }
      return ret;
    }

    EnumSet<Warp.Type> getWarpTypes() {
      return warpTypes;
    }
  }

}
