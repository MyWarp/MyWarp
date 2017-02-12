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
 * Resolves a user's authentication based on permissions.
 */
public class PermissionAuthorizationStrategy extends ForwardingAuthorizationStrategy {

  private final AuthorizationStrategy delegate;

  /**
   * Creates an instance. If the tested user does not have the required permission, further tests are delegated to the
   * given {@code AuthorizationStrategy}.
   *
   * @param delegate the strategy to delegate further tests to
   */
  public PermissionAuthorizationStrategy(AuthorizationStrategy delegate) {
    this.delegate = delegate;
  }

  @Override
  protected AuthorizationStrategy delegate() {
    return delegate;
  }

  @Override
  public boolean isModifiable(Warp warp, Actor actor) {
    return actor.hasPermission("mywarp.override.modify") || delegate.isModifiable(warp, actor);
  }

  @Override
  public boolean isUsable(Warp warp, LocalEntity entity) {
    return (entity instanceof Actor && ((Actor) entity).hasPermission("mywarp.override.use")) || delegate
            .isUsable(warp, entity);
  }

  @Override
  public boolean isViewable(Warp warp, Actor actor) {
    return actor.hasPermission("mywarp.override.view") || delegate.isViewable(warp, actor);
  }
}
