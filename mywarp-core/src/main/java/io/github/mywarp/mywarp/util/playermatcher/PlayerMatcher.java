/*
 * Copyright (C) 2011 - 2018, MyWarp team and contributors
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

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A predicate that matches one or more players.
 */
public interface PlayerMatcher extends Predicate<LocalPlayer> {

  @Override
  boolean test(LocalPlayer player);

  /**
   * Returns a Set of all players that match this Predicate.
   *
   * @param game the running game
   * @return a Set with all matching players
   */
  default Set<LocalPlayer> allMatches(Game game) {
    return game.getPlayers().stream().filter(this).collect(Collectors.toSet());
  }

}
