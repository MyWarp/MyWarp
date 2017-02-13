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

package me.taylorkelly.mywarp.warp;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import me.taylorkelly.mywarp.platform.LocalEntity;
import me.taylorkelly.mywarp.platform.LocalWorld;
import me.taylorkelly.mywarp.util.teleport.TeleportHandler;
import me.taylorkelly.mywarp.util.teleport.TeleportHandler.TeleportStatus;
import me.taylorkelly.mywarp.warp.storage.WarpStorage;

import java.util.UUID;

/**
 * Stores all warps managed in a {@link WarpStorage}. Calls are all delegated to an underling PopulatableWarpManager as
 * required by the decorator pattern, storage is implemented on top.
 */
public class StoragePopulatableWarpManager extends ForwardingPopulatableWarpManager {

  private final PopulatableWarpManager delegate;
  private final WarpStorage storage;

  /**
   * Creates an instance that stores warps in the given {@code storage}. Further management is delegated to the given
   * PopulatableWarpManager.
   *
   * @param delegate the PopulatableWarpManager to delegate calls to
   * @param storage  the WarpStorage that stores Warps managed by this manager
   */
  public StoragePopulatableWarpManager(PopulatableWarpManager delegate, WarpStorage storage) {
    this.delegate = delegate;
    this.storage = storage;
  }

  @Override
  protected PopulatableWarpManager delegate() {
    return delegate;
  }

  @Override
  public void add(Warp warp) {
    warp = new PersistentWarp(warp);
    delegate().add(warp);
    storage.addWarp(warp);
  }

  @Override
  public void remove(Warp warp) {
    delegate().remove(warp);
    storage.removeWarp(warp);
  }

  @Override
  public void populate(Iterable<Warp> warps) {
    delegate().populate(Iterables.transform(warps, new Function<Warp, Warp>() {

      @Override
      public PersistentWarp apply(Warp input) {
        return new PersistentWarp(input);
      }
    }));
  }

  /**
   * A Warp that persists its values using a {@link StoragePopulatableWarpManager}.
   */
  private class PersistentWarp extends ForwardingWarp {

    private final Warp delegate;

    private PersistentWarp(Warp delegate) {
      this.delegate = delegate;
    }

    @Override
    protected Warp delegate() {
      return delegate;
    }

    @Override
    public TeleportStatus visit(LocalEntity entity, TeleportHandler handler) {
      TeleportStatus status = delegate().visit(entity, handler);

      if (status.isPositionModified()) {
        storage.updateVisits(delegate());
      }
      return status;
    }

    @Override
    public void inviteGroup(String groupId) {
      super.inviteGroup(groupId);
      storage.inviteGroup(delegate(), groupId);

    }

    @Override
    public void invitePlayer(UUID uniqueId) {
      super.invitePlayer(uniqueId);
      storage.invitePlayer(delegate(), uniqueId);

    }

    @Override
    public void uninviteGroup(String groupId) {
      super.uninviteGroup(groupId);
      storage.uninviteGroup(delegate(), groupId);

    }

    @Override
    public void uninvitePlayer(UUID uniqueId) {
      super.uninvitePlayer(uniqueId);
      storage.uninvitePlayer(delegate(), uniqueId);

    }

    @Override
    public void setCreator(UUID uniqueId) {
      super.setCreator(uniqueId);
      storage.updateCreator(delegate());

    }

    @Override
    public void setLocation(LocalWorld world, Vector3d position, Vector2f rotation) {
      super.setLocation(world, position, rotation);
      storage.updateLocation(delegate());

    }

    @Override
    public void setType(Type type) {
      super.setType(type);
      storage.updateType(delegate());
    }

    @Override
    public void setWelcomeMessage(String welcomeMessage) {
      super.setWelcomeMessage(welcomeMessage);
      storage.updateWelcomeMessage(delegate());
    }
  }
}
