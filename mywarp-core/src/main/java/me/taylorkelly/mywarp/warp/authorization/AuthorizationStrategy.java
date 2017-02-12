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

package me.taylorkelly.mywarp.warp.authorization;

import me.taylorkelly.mywarp.platform.Actor;
import me.taylorkelly.mywarp.platform.LocalEntity;
import me.taylorkelly.mywarp.warp.Warp;

/**
 * A strategy to resolve a user's authentication for a certain Warp.
 *
 * @see AuthorizationResolver
 */
public interface AuthorizationStrategy {

  /**
   * Returns whether the given {@code Warp} is modifiable by the given {@code Actor}.
   *
   * @param warp  the warp to check
   * @param actor the Actor to check
   * @return true if the given Actor can modify this Warp
   */
  boolean isModifiable(Warp warp, Actor actor);

  /**
   * Returns whether the given {@code Warp} is usable by the given entity.
   *
   * @param warp   the warp to check
   * @param entity the entity to check
   * @return true if the given entity can use this Warp
   */
  boolean isUsable(Warp warp, LocalEntity entity);

  /**
   * Returns whether the given {@code Warp} is viewable by the given {@code Actor}.
   *
   * @param warp  the warp to check
   * @param actor the Actor to check
   * @return true if the Actor can view this Warp
   */
  boolean isViewable(Warp warp, Actor actor);

}
