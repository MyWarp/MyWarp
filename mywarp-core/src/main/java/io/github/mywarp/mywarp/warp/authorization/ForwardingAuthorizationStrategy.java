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

import com.google.common.collect.ForwardingObject;

import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.warp.Warp;

/**
 * Forwards all method calls to another AuthorizationStrategy. Subclasses should override one or more methods to modify
 * the behavior of the backing AuthorizationStrategy as desired per the <a href="http://en.wikipedia
 * .org/wiki/Decorator_pattern">decorator pattern</a>.
 */
abstract class ForwardingAuthorizationStrategy extends ForwardingObject implements AuthorizationStrategy {

  @Override
  public boolean isModifiable(Warp warp, Actor actor) {
    return delegate().isModifiable(warp, actor);
  }

  @Override
  public boolean isUsable(Warp warp, LocalEntity entity) {
    return delegate().isUsable(warp, entity);
  }

  @Override
  public boolean isViewable(Warp warp, Actor actor) {
    return delegate().isViewable(warp, actor);
  }

  @Override
  protected abstract AuthorizationStrategy delegate();
}
