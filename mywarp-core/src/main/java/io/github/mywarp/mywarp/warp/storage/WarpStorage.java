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

package io.github.mywarp.mywarp.warp.storage;

import io.github.mywarp.mywarp.util.playermatcher.PlayerMatcher;
import io.github.mywarp.mywarp.warp.Warp;

/**
 * A connection to a data storage, e.g. a rational database.
 */
public interface WarpStorage extends WarpSource {

  /**
   * Adds the given {@code Warp} to the underlying data storage.
   *
   * @param warp the {@code Warp} to add
   */
  void addWarp(Warp warp);

  /**
   * Removes the given {@code Warp} from the underlying data storage.
   *
   * @param warp the {@code Warp} to remove
   */
  void removeWarp(Warp warp);

  /**
   * Adds the given playermatcher to the given {@code Warp}.
   *
   * @param warp       the {@code Warp}
   * @param invitation the playermatcher to add
   */
  void addInvitation(Warp warp, PlayerMatcher invitation);

  /**
   * Removes the given playermatcher from the given {@code Warp}.
   *
   * @param warp       the {@code Warp}
   * @param invitation the playermatcher to remove
   */
  void removeInvitation(Warp warp, PlayerMatcher invitation);

  /**
   * Updates the creator of the given {@code Warp}.
   *
   * @param warp the {@code Warp} to update
   */
  void updateCreator(Warp warp);

  /**
   * Updates the location of the given {@code Warp}.
   *
   * @param warp the {@code Warp} to update
   */
  void updateLocation(Warp warp);

  /**
   * Updates the type of the given {@code Warp}.
   *
   * @param warp the {@code Warp} to update
   */
  void updateType(Warp warp);

  /**
   * Updates the visits of the given {@code Warp}.
   *
   * @param warp the {@code Warp} to update
   */
  void updateVisits(Warp warp);

  /**
   * Update the welcome-message of the given {@code Warp}.
   *
   * @param warp the {@code Warp} to update
   */
  void updateWelcomeMessage(Warp warp);

}
