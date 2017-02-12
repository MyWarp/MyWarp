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
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;

import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.platform.LocalWorld;
import me.taylorkelly.mywarp.util.WarpUtils;
import me.taylorkelly.mywarp.warp.Warp;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * A creation limit for warps. Implementations are expected to provide the limit for each {@link Type} and a way to
 * resolve these limit per world.
 */
public interface Limit {

  /**
   * Gets the maximum number of warps a user can create under the given Limit.Type.
   *
   * @param type the type of limit
   * @return the maximum number of warps
   */
  int getLimit(Type type);

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
   * The different types limit.
   */
  enum Type {
    /**
     * The total limit (accounts all warps).
     */
    TOTAL(null, Predicates.<Warp>alwaysTrue()), /**
     * The private limit (accounts only private warps).
     */
    PRIVATE(TOTAL, WarpUtils.isType(Warp.Type.PRIVATE)), /**
     * The public limit (accounts only public warps).
     */
    PUBLIC(TOTAL, WarpUtils.isType(Warp.Type.PUBLIC));

    @Nullable
    private final Type parent;
    private final Predicate<Warp> condition;

    /**
     * Initializes this Limit.
     *
     * @param parent    the parent of this Type. The parent must count all warps that are counted by this limit. Can be
     *                  {@code null} if this Type has no parent.
     * @param condition the condition a warp must fulfill to be counted under this limit
     */
    Type(@Nullable Type parent, Predicate<Warp> condition) {
      this.parent = parent;
      this.condition = condition;
    }

    /**
     * Gets the condition of this Type.
     *
     * @return the condition
     */
    public Predicate<Warp> getCondition() {
      return condition;
    }

    /**
     * Gets the parent of this Type. May return {@code null} if this Type has no parent.
     *
     * @return the parent
     */
    @Nullable
    public Type getParent() {
      return parent;
    }

    /**
     * Gets the parents of this type recursively. The last entry in the returned Deque is the parent of this Type, the
     * second will be the parent of the parent of this Type and so one.
     *
     * @return the parents, recursively
     */
    public Deque<Type> getParentsRecusive() {
      Deque<Limit.Type> ret = new ArrayDeque<Limit.Type>();
      Limit.Type parent = this.getParent();
      do {
        ret.addFirst(parent);
        parent = parent.getParent();
      } while (parent != null);
      return ret;
    }

    /**
     * Gets the name of this Type in lower case.
     *
     * @return this Type's name in lowercase
     * @see #name()
     */
    public String lowerCaseName() {
      return name().toLowerCase();
    }

    /**
     * Returns whether the given player can disobey any limit of this Type on the given world.
     *
     * @param player the player
     * @param world  the world
     * @return {@code true} if the player can disobey any limit of this Type
     */
    public boolean canDisobey(LocalPlayer player, LocalWorld world) {
      String perm = "mywarp.limit.disobey." + world.getName() + "." + lowerCaseName();
      return player.hasPermission(perm);
    }

    public static Type valueOf(Warp.Type type) {
      switch (type) {
        case PRIVATE:
          return PRIVATE;
        case PUBLIC:
          return PUBLIC;
        default:
          return TOTAL;
      }
    }
  }

}
