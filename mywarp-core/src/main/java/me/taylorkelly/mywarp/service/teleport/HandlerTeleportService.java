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

package me.taylorkelly.mywarp.service.teleport;

import me.taylorkelly.mywarp.platform.Game;
import me.taylorkelly.mywarp.platform.LocalEntity;
import me.taylorkelly.mywarp.platform.PlayerNameResolver;
import me.taylorkelly.mywarp.util.teleport.TeleportHandler;
import me.taylorkelly.mywarp.warp.PlaceholderResolver;
import me.taylorkelly.mywarp.warp.Warp;

/**
 * Delegates teleport requests to a {@link TeleportHandler}.
 */
public class HandlerTeleportService implements TeleportService {

  private final TeleportHandler handler;
  private final Game game;
  private final PlaceholderResolver resolver;

  /**
   * Creates an instance that delegates calls to the given {@code handler}. Warp positions are pared against the given
   * {@code game}, player names in messageys are resolved with the given {@code playerNameResolver}.
   *
   * @param handler            the handler
   * @param game               the game
   * @param playerNameResolver the playerNameResolver
   */
  public HandlerTeleportService(TeleportHandler handler, Game game, PlayerNameResolver playerNameResolver) {
    this.handler = handler;
    this.game = game;
    this.resolver = new PlaceholderResolver(playerNameResolver);
  }

  @Override
  public TeleportHandler.TeleportStatus teleport(LocalEntity entity, Warp warp) {
    return warp.visit(entity, game, handler, resolver);
  }
}
