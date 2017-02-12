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

package me.taylorkelly.mywarp.warp.storage;

import com.google.common.collect.ForwardingObject;

import me.taylorkelly.mywarp.warp.Warp;

import java.util.List;
import java.util.UUID;

/**
 * A WarpStorage that forwards all its method calls to another WarpStorage. Subclasses should override one or more
 * methods to modify the behavior of the backing WarpStorage as desired per the <a
 * href="http://en.wikipedia.org/wiki/Decorator_pattern">decorator pattern</a>.
 */
abstract class ForwardingWarpStorage extends ForwardingObject implements WarpStorage {

  @Override
  protected abstract WarpStorage delegate();

  @Override
  public void addWarp(Warp warp) {
    delegate().addWarp(warp);
  }

  @Override
  public void removeWarp(Warp warp) {
    delegate().removeWarp(warp);
  }

  @Override
  public List<Warp> getWarps() {
    return delegate().getWarps();
  }

  @Override
  public void inviteGroup(Warp warp, String groupId) {
    delegate().inviteGroup(warp, groupId);
  }

  @Override
  public void invitePlayer(Warp warp, UUID uniqueId) {
    delegate().invitePlayer(warp, uniqueId);
  }

  @Override
  public void uninviteGroup(Warp warp, String groupId) {
    delegate().uninviteGroup(warp, groupId);
  }

  @Override
  public void uninvitePlayer(Warp warp, UUID uniqueId) {
    delegate().uninvitePlayer(warp, uniqueId);
  }

  @Override
  public void updateCreator(Warp warp) {
    delegate().updateCreator(warp);
  }

  @Override
  public void updateLocation(Warp warp) {
    delegate().updateLocation(warp);
  }

  @Override
  public void updateType(Warp warp) {
    delegate().updateType(warp);
  }

  @Override
  public void updateVisits(Warp warp) {
    delegate().updateVisits(warp);
  }

  @Override
  public void updateWelcomeMessage(Warp warp) {
    delegate().updateWelcomeMessage(warp);
  }
}
