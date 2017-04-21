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

package io.github.mywarp.mywarp.warp.authorization;

import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.warp.Warp;

import java.util.function.Predicate;

/**
 * Resolves a user's authentication for a certain warp using a previously defined {@link AuthorizationStrategy}.
 *
 * <p>There are three different non exclusive types of authorizations. A warp can be: <ol> <li>modifiable,</li>
 * <li>usable,</li> <li>viewable.</li> </ol></p>
 */
public class AuthorizationResolver {

  private AuthorizationStrategy strategy;

  /**
   * Creates an instance that uses the given strategy to resolve authentications.
   *
   * @param strategy the strategy to be used
   */
  public AuthorizationResolver(AuthorizationStrategy strategy) {
    this.strategy = strategy;
  }

  /**
   * Returns whether the given {@code Warp} is modifiable by the given {@code Actor}.
   *
   * <p>If this method returns {@code true} it is guaranteed that {@link #isUsable(Warp, LocalEntity)} as well as {@link
   * #isViewable(Warp, Actor)} also return {@code true}.</p>
   *
   * @param warp  the warp to check
   * @param actor the Actor to check
   * @return {@code true} if the given Actor can modify this Warp
   */
  public boolean isModifiable(Warp warp, Actor actor) {
    return strategy.isModifiable(warp, actor);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the tested {@code Warp} is modifiable by the given Actor.
   *
   * @param actor the Actor
   * @return a predicate that checks if the given warp is modifiable by the given Actor
   * @see #isModifiable(Warp, Actor)
   */
  public Predicate<Warp> isModifiable(final Actor actor) {
    return input -> isModifiable(input, actor);
  }

  /**
   * Returns whether the given {@code Warp} is usable by the given {@code entity}.
   *
   * <p>If this method returns {@code true} it is guaranteed that {@link #isViewable(Warp, Actor)} also returns {@code
   * true}.</p>
   *
   * @param warp   the warp to check
   * @param entity the entity to check
   * @return {@code true} if the given entity can use this Warp
   */
  public boolean isUsable(Warp warp, LocalEntity entity) {
    return strategy.isUsable(warp, entity);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the tested {@code Warp} is usable by the given entity.
   *
   * @param entity the entity
   * @return a predicate that checks if the given warp is usable by the given entity
   * @see #isUsable(Warp, LocalEntity)
   */
  public Predicate<Warp> isUsable(final LocalEntity entity) {
    return input -> isUsable(input, entity);
  }

  /**
   * Returns whether the given {@code Warp} is viewable by the given {@code Actor}.
   *
   * @param warp  the warp to check
   * @param actor the Actor to check
   * @return {@code true} if the Actor can view this Warp
   */
  public boolean isViewable(Warp warp, Actor actor) {
    return strategy.isViewable(warp, actor);
  }

  /**
   * Returns a predicate that evaluates to {@code true} if the tested {@code Warp} is viewable by the given Actor.
   *
   * @param actor the Actor
   * @return a predicate that checks if the given warp is usable by the given Actor
   * @see #isViewable(Warp, Actor)
   */
  public Predicate<Warp> isViewable(final Actor actor) {
    return input -> isViewable(input, actor);
  }
}
