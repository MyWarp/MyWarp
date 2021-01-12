/*
 * Copyright (C) 2011 - 2021, MyWarp team and contributors
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

package io.github.mywarp.mywarp.util.playermatcher;

import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Matches players based on their unique identifier.
 *
 * @see LocalPlayer#getUniqueId()
 */
public class UuidPlayerMatcher extends AbstractPlayerMatcher<UUID> {

  /**
   * Creates a new instance that matches players with the given {@code playerId}.
   *
   * @param playerId the player identifier
   */
  public UuidPlayerMatcher(UUID playerId) {
    super(playerId);
  }

  @Override
  public boolean test(LocalPlayer player) {
    return player.getUniqueId().equals(getCriteria());
  }

  @Override
  public Set<LocalPlayer> allMatches(Game game) {
    //there can be only a single match
    return game.getPlayer(getCriteria()).map(Collections::singleton).orElseGet(Collections::emptySet);
  }
}
