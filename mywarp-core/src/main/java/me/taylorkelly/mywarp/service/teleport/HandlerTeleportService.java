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

import me.taylorkelly.mywarp.platform.Actor;
import me.taylorkelly.mywarp.platform.LocalEntity;
import me.taylorkelly.mywarp.platform.PlayerNameResolver;
import me.taylorkelly.mywarp.util.i18n.DynamicMessages;
import me.taylorkelly.mywarp.util.teleport.TeleportHandler;
import me.taylorkelly.mywarp.warp.PlaceholderResolver;
import me.taylorkelly.mywarp.warp.Warp;

/**
 * Delegates teleport requests to a {@link TeleportHandler}.
 *
 * <p>The TeleportHandler executes the teleport and returns a {@link TeleportHandler.TeleportStatus} with the status of
 * the teleport. This service will then send a message corresponding with the teleport's status to the teleported entity
 * if this entity is an Actor.</p>
 */
public class HandlerTeleportService implements TeleportService {

  private static final DynamicMessages msg = new DynamicMessages("me.taylorkelly.mywarp.lang.Teleports");

  private final TeleportHandler handler;
  private final PlaceholderResolver resolver;

  /**
   * Creates an instance that delegates calls to the given {@code handler}.
   *
   * @param handler            the handler to handle teleports
   * @param playerNameResolver the playerNameResolver used to resolve player names in messages send by this service
   */
  public HandlerTeleportService(TeleportHandler handler, PlayerNameResolver playerNameResolver) {
    this.handler = handler;
    this.resolver = new PlaceholderResolver(playerNameResolver);
  }

  @Override
  public TeleportHandler.TeleportStatus teleport(LocalEntity entity, Warp warp) {
    TeleportHandler.TeleportStatus status = warp.visit(entity, handler);

    if (entity instanceof Actor) {
      Actor actor = (Actor) entity;
      switch (status) {
        case ORIGINAL:
          String welcomeMsg = warp.getWelcomeMessage();
          if (!welcomeMsg.isEmpty()) {
            actor.sendMessage(resolver.values(warp, actor).resolvePlaceholders(welcomeMsg));
          }
          break;
        case MODIFIED:
          actor.sendError(msg.getString("unsafe-location.closest", warp.getName()));
          break;
        case NONE:
          actor.sendError(msg.getString("unsafe-location.no-teleport", warp.getName()));
          break;
        case NO_SUCH_WORLD:
          actor.sendError(msg.getString("no-such-world", warp.getName(), warp.getWorldIdentifier()));
          break;
        default:
          assert false : status;
      }
    }
    return status;
  }
}
