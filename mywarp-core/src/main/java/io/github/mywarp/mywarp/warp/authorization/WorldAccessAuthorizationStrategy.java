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
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.Settings;
import io.github.mywarp.mywarp.warp.Warp;

import java.util.Optional;

/**
 * Resolves a user's authentication to use a warp bases on the users permission to access the world that contains the
 * warp.
 */
public class WorldAccessAuthorizationStrategy extends ForwardingAuthorizationStrategy {

  private final AuthorizationStrategy delegate;
  private final Game game;
  private final Settings settings;

  /**
   * Creates an instance using the given {@code Settings}. If the tested user may visit the world of the tested warp
   * in question, further tests are delegated to the given {@code AuthorizationStrategy}.
   *
   * @param delegate the strategy to delegate further tests to
   * @param game     the configured Game instance
   * @param settings the configured Settings instance
   */
  public WorldAccessAuthorizationStrategy(AuthorizationStrategy delegate, Game game, Settings settings) {
    this.delegate = delegate;
    this.game = game;
    this.settings = settings;
  }

  @Override
  protected AuthorizationStrategy delegate() {
    return delegate;
  }

  @Override
  public boolean isUsable(Warp warp, LocalEntity entity) {
    if (entity instanceof Actor && cannotAccessWorld((Actor) entity, warp)) {
      return false;
    }
    return delegate().isUsable(warp, entity);
  }

  @Override
  public boolean isViewable(Warp warp, Actor actor) {
    if (cannotAccessWorld(actor, warp)) {
      return false;
    }
    return delegate().isViewable(warp, actor);
  }

  /**
   * Returns whether the given actor can access the world of the given warp.
   *
   * @param actor the Actor
   * @param warp  the Warp
   * @return {@code true} if the warp's world may not be accessed
   */
  private boolean cannotAccessWorld(Actor actor, Warp warp) {
    if (!settings.isControlWorldAccess()) {
      return false;
    }
    //if the Warp's world does not exist, the Actor cannot access it
    Optional<LocalWorld> world = game.getWorld(warp.getWorldIdentifier());
    return world.isPresent() && !actor.hasPermission("mywarp.world-access." + world.get().getName());
  }
}
