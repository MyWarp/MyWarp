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

package io.github.mywarp.mywarp.service.teleport;

import com.google.common.collect.ForwardingObject;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.util.teleport.TeleportHandler;
import io.github.mywarp.mywarp.warp.Warp;

/**
 * Forwards all method calls to another TeleportService. Subclasses should override one or more methods to modify the
 * behavior of the backing TeleportService as desired per the <a href="http://en.wikipedia
 * .org/wiki/Decorator_pattern">decorator pattern</a>.
 */
abstract class ForwardingTeleportService extends ForwardingObject implements TeleportService {

  @Override
  public TeleportHandler.TeleportStatus teleport(LocalEntity entity, Warp warp) {
    return delegate().teleport(entity, warp);
  }

  @Override
  protected abstract TeleportService delegate();
}
