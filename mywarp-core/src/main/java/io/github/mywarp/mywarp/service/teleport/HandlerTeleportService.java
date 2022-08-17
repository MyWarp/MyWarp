/*
 * Copyright (C) 2011 - 2022, MyWarp team and contributors
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

import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.teleport.TeleportHandler;
import io.github.mywarp.mywarp.warp.PlaceholderResolver;
import io.github.mywarp.mywarp.warp.Warp;

/**
 * Delegates teleport requests to a {@link TeleportHandler}.
 *
 * <p>The TeleportHandler executes the teleport and returns a {@link TeleportHandler.TeleportStatus} with the status of
 * the teleport. This service will then send a message corresponding with the teleport's status to the teleported entity
 * if this entity is an Actor.</p>
 */
public class HandlerTeleportService implements TeleportService {

  private static final DynamicMessages msg = new DynamicMessages("io.github.mywarp.mywarp.lang.Teleports");

  private final TeleportHandler handler;

  /**
   * Creates an instance that delegates calls to the given {@code handler}.
   *
   * @param handler the handler to handle teleports
   */
  public HandlerTeleportService(TeleportHandler handler) {
    this.handler = handler;
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
            actor.sendMessage(PlaceholderResolver.from(warp, actor).resolvePlaceholders(welcomeMsg));
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
