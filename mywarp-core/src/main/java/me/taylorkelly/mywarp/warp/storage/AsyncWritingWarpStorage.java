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

import me.taylorkelly.mywarp.warp.Warp;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * A {@code WarpStorage} that works on top of another {@code WarpStorage} and executes every <b>writing</b> task
 * asynchronous on a specified {@link Executor}.
 */
public class AsyncWritingWarpStorage extends ForwardingWarpStorage {

  private final WarpStorage warpStorage;
  private final Executor executor;

  /**
   * Creates an instance. Every call is delegated to the given {@code warpStorage}, writing methods are executed in the
   * given {@code executor}, reading methods still run in the thread that calls the method.
   *
   * @param warpStorage the {@code WarpStorage} whose writing methods should be executed asynchronous
   * @param executor    the {@code Executor} that executes writing methods
   */
  public AsyncWritingWarpStorage(WarpStorage warpStorage, Executor executor) {
    this.warpStorage = warpStorage;
    this.executor = executor;
  }

  @Override
  protected WarpStorage delegate() {
    return warpStorage;
  }

  @Override
  public void addWarp(final Warp warp) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().addWarp(warp);
      }
    });
  }

  @Override
  public void removeWarp(final Warp warp) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().removeWarp(warp);
      }
    });
  }

  @Override
  public List<Warp> getWarps() {
    return delegate().getWarps();
  }

  @Override
  public void inviteGroup(final Warp warp, final String groupId) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().inviteGroup(warp, groupId);
      }
    });
  }

  @Override
  public void invitePlayer(final Warp warp, final UUID uniqueId) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().invitePlayer(warp, uniqueId);
      }
    });
  }

  @Override
  public void uninviteGroup(final Warp warp, final String groupId) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().uninviteGroup(warp, groupId);
      }
    });
  }

  @Override
  public void uninvitePlayer(final Warp warp, final UUID uniqueId) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().uninvitePlayer(warp, uniqueId);
      }
    });
  }

  @Override
  public void updateCreator(final Warp warp) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().updateCreator(warp);
      }
    });
  }

  @Override
  public void updateLocation(final Warp warp) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().updateLocation(warp);
      }
    });
  }

  @Override
  public void updateType(final Warp warp) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().updateType(warp);
      }
    });
  }

  @Override
  public void updateVisits(final Warp warp) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().updateVisits(warp);
      }
    });
  }

  @Override
  public void updateWelcomeMessage(final Warp warp) {
    executor.execute(new Runnable() {
      @Override
      public void run() {
        delegate().updateWelcomeMessage(warp);
      }
    });
  }
}
