/*
 * Copyright (C) 2011 - 2019, MyWarp team and contributors
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

package io.github.mywarp.mywarp.platform.capability;

import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.service.limit.Limit;

import java.util.List;

/**
 * The capability of a platform to provide limit functionality.
 */
public interface LimitCapability {

  /**
   * Gets the limit that affects the given {@code player} on the given {@code world}.
   *
   * @param player the player
   * @param world  the world
   * @return the Limit
   */
  Limit getLimit(LocalPlayer player, LocalWorld world);

  /**
   * Gets all limits that can possible affect the given {@code player}.
   *
   * @param player the player
   * @return a list of limits that could affect the player
   */
  List<Limit> getEffectiveLimits(LocalPlayer player);

}
