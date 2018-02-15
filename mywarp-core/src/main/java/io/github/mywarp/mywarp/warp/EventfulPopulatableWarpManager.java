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

package io.github.mywarp.mywarp.warp;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.eventbus.EventBus;

import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.util.playermatcher.PlayerMatcher;
import io.github.mywarp.mywarp.util.teleport.TeleportHandler;
import io.github.mywarp.mywarp.warp.event.WarpAdditionEvent;
import io.github.mywarp.mywarp.warp.event.WarpDeletionEvent;
import io.github.mywarp.mywarp.warp.event.WarpEvent;
import io.github.mywarp.mywarp.warp.event.WarpInvitesEvent;
import io.github.mywarp.mywarp.warp.event.WarpUpdateEvent;

import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Fires events for all warps managed by it. Functional calls are all delegated to an underling PopulatableWarpManager
 * as required by the decorator pattern, events are implemented on top.
 *
 * <p>Events are dispatched in the {@link EventBus} given when initializing this PopulatableWarpManager. Individual
 * warps fire {@link WarpEvent}s and the manager itself fires {@link WarpAdditionEvent}s and {@link
 * WarpDeletionEvent}s when Warps are added to or removed from it. Handlers that want
 * to listen to such events need to register themselves on the EventBus.</p>
 */
public class EventfulPopulatableWarpManager extends ForwardingPopulatableWarpManager {

  private final PopulatableWarpManager delegate;
  private final EventBus eventBus;

  /**
   * Creates an instance that posts events on the given {@code eventBus}. Further management is delegated to the given
   * PopulatableWarpManager.
   *
   * @param delegate the PopulatableWarpManager to delegate calls to
   * @param eventBus the EventBus on which this manager will post events
   */
  public EventfulPopulatableWarpManager(PopulatableWarpManager delegate, EventBus eventBus) {
    this.delegate = delegate;
    this.eventBus = eventBus;
  }

  @Override
  protected PopulatableWarpManager delegate() {
    return delegate;
  }

  @Override
  public void add(Warp warp) {
    warp = new EventfulWarp(warp);
    delegate().add(warp);
    eventBus.post(new WarpAdditionEvent(warp));
  }

  @Override
  public void populate(Iterable<Warp> warps) {
    delegate()
        .populate(StreamSupport.stream(warps.spliterator(), false).map(EventfulWarp::new).collect(Collectors.toList()));
  }

  @Override
  public void remove(Warp warp) {
    delegate().remove(warp);
    eventBus.post(new WarpDeletionEvent(warp));
  }

  /**
   * Forwards method calls to an existing Warp and fires {@link WarpEvent}s to the parent's EventBus.
   */
  private class EventfulWarp extends ForwardingWarp {

    private final Warp delegate;

    private EventfulWarp(Warp delegate) {
      this.delegate = delegate;
    }

    @Override
    protected Warp delegate() {
      return delegate;
    }

    @Override
    public TeleportHandler.TeleportStatus visit(LocalEntity entity, TeleportHandler handler) {
      TeleportHandler.TeleportStatus status = delegate().visit(entity, handler);

      if (status.isPositionModified()) {
        eventBus.post(new WarpUpdateEvent(this, WarpUpdateEvent.UpdateType.VISITS));
      }
      return status;
    }

    @Override
    public void addInvitation(PlayerMatcher invitation) {
      super.addInvitation(invitation);
      eventBus.post(new WarpInvitesEvent(this, WarpInvitesEvent.InvitationStatus.ADDITION, invitation));
    }

    @Override
    public void removeInvitation(PlayerMatcher invitation) {
      super.removeInvitation(invitation);
      eventBus.post(new WarpInvitesEvent(this, WarpInvitesEvent.InvitationStatus.REMOVAL, invitation));
    }

    @Override
    public void setCreator(UUID uniqueId) {
      super.setCreator(uniqueId);
      eventBus.post(new WarpUpdateEvent(this, WarpUpdateEvent.UpdateType.CREATOR));

    }

    @Override
    public void setLocation(LocalWorld world, Vector3d position, Vector2f rotation) {
      super.setLocation(world, position, rotation);
      eventBus.post(new WarpUpdateEvent(this, WarpUpdateEvent.UpdateType.LOCATION));

    }

    @Override
    public void setType(Type type) {
      super.setType(type);
      eventBus.post(new WarpUpdateEvent(this, WarpUpdateEvent.UpdateType.TYPE));
    }

    @Override
    public void setWelcomeMessage(String welcomeMessage) {
      super.setWelcomeMessage(welcomeMessage);
      eventBus.post(new WarpUpdateEvent(this, WarpUpdateEvent.UpdateType.WELCOME_MESSAGE));
    }
  }
}
