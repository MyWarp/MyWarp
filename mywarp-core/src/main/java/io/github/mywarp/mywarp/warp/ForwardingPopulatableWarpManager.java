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
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingObject;

import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.util.teleport.TeleportHandler;

import java.util.Collection;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * Forwards all method calls to another PopulatableWarpManager. Subclasses should override one or more methods to modify
 * the behavior of the backing PopulatableWarpManager as desired per the
 * <a href="http://en.wikipedia.org/wiki/Decorator_pattern">
 * decorator pattern</a>.
 */
abstract class ForwardingPopulatableWarpManager extends ForwardingObject implements PopulatableWarpManager {

  @Override
  public void add(Warp warp) {
    delegate().add(warp);
  }

  @Override
  public void remove(Warp warp) {
    delegate().remove(warp);
  }

  @Override
  public boolean contains(Warp warp) {
    return delegate().contains(warp);
  }

  @Override
  public boolean containsByName(String name) {
    return delegate().containsByName(name);
  }

  @Override
  public Optional<Warp> getByName(String name) {
    return delegate().getByName(name);
  }

  @Override
  public Collection<Warp> getAll(Predicate<Warp> predicate) {
    return delegate().getAll(predicate);
  }

  @Override
  public int getNumberOfWarps(Predicate<Warp> predicate) {
    return delegate().getNumberOfWarps(predicate);
  }

  @Override
  public int getNumberOfAllWarps() {
    return delegate().getNumberOfAllWarps();
  }

  @Override
  public void depopulate() {
    delegate().depopulate();
  }

  @Override
  public void populate(Iterable<Warp> warps) {
    delegate().populate(warps);
  }

  @Override
  protected abstract PopulatableWarpManager delegate();

  /**
   * Forwards all method calls to another Warp. Subclasses should override one or more methods to modify the behavior
   * of the backing Warp as desired per the <a href="http://en.wikipedia .org/wiki/Decorator_pattern">decorator
   * pattern</a>.
   */
  abstract class ForwardingWarp extends ForwardingObject implements Warp {

    @Override
    public TeleportHandler.TeleportStatus visit(LocalEntity entity, TeleportHandler handler) {
      return delegate().visit(entity, handler);
    }

    @Override
    public boolean isCreator(UUID uniqueId) {
      return delegate().isCreator(uniqueId);
    }

    @Override
    public boolean isType(Type type) {
      return delegate().isType(type);
    }

    @Override
    public boolean isPlayerInvited(UUID uniqueId) {
      return delegate().isPlayerInvited(uniqueId);
    }

    @Override
    public boolean isGroupInvited(String groupId) {
      return delegate().isGroupInvited(groupId);
    }

    @Override
    public void inviteGroup(String groupId) {
      delegate().inviteGroup(groupId);
    }

    @Override
    public void invitePlayer(UUID uniqueId) {
      delegate().invitePlayer(uniqueId);
    }

    @Override
    public void uninviteGroup(String groupId) {
      delegate().uninviteGroup(groupId);
    }

    @Override
    public void uninvitePlayer(UUID uniqueId) {
      delegate().uninvitePlayer(uniqueId);
    }

    @Override
    public int compareTo(Warp that) {
      return delegate().compareTo(that);
    }

    @Override
    public UUID getCreator() {
      return delegate().getCreator();
    }

    @Override
    public void setCreator(UUID uniqueId) {
      delegate().setCreator(uniqueId);
    }

    @Override
    public Set<String> getInvitedGroups() {
      return delegate().getInvitedGroups();
    }

    @Override
    public Set<UUID> getInvitedPlayers() {
      return delegate().getInvitedPlayers();
    }

    @Override
    public String getName() {
      return delegate().getName();
    }

    @Override
    public UUID getWorldIdentifier() {
      return delegate().getWorldIdentifier();
    }

    @Override
    public Vector3d getPosition() {
      return delegate().getPosition();
    }

    @Override
    public Vector2f getRotation() {
      return delegate().getRotation();
    }

    @Override
    public Type getType() {
      return delegate().getType();
    }

    @Override
    public void setType(Type type) {
      delegate().setType(type);
    }

    @Override
    public Date getCreationDate() {
      return delegate().getCreationDate();
    }

    @Override
    public int getVisits() {
      return delegate().getVisits();
    }

    @Override
    public String getWelcomeMessage() {
      return delegate().getWelcomeMessage();
    }

    @Override
    public void setWelcomeMessage(String welcomeMessage) {
      delegate().setWelcomeMessage(welcomeMessage);
    }

    @Override
    public void setLocation(LocalWorld world, Vector3d position, Vector2f rotation) {
      delegate().setLocation(world, position, rotation);
    }

    @Override
    protected abstract Warp delegate();

  }
}
