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

package io.github.mywarp.mywarp.command.util;

import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.warp.Warp;

import java.util.Objects;
import java.util.UUID;

/**
 * Utilities for writing commands.
 */
public class CommandUtil {

  private CommandUtil() {
  }

  /**
   * Returns the loaded world the given {@code warp} is positioned within or raises an Exception if the world is not
   * loaded.
   *
   * @param warp the warp
   * @param game the Game to acquire the world from
   * @return the loaded world of the warp
   * @throws NoSuchWorldException if the warp's world cannot be acquired from the Game
   * @see Game#getWorld(UUID)
   */
  public static LocalWorld toWorld(Warp warp, Game game) throws NoSuchWorldException {
    return toWorld(warp.getWorldIdentifier(), game);
  }

  /**
   * Returns the loaded world identified the given identifier or raises an Exception if the world is not loaded.
   *
   * @param worldIdentifier the identifier
   * @param game            the Game to acquire the world from
   * @return the loaded world with the given identifier
   * @throws NoSuchWorldException if the warp's world cannot be acquired from the Game
   * @see Game#getWorld(UUID)
   */
  public static LocalWorld toWorld(UUID worldIdentifier, Game game) throws NoSuchWorldException {
    return game.getWorld(worldIdentifier).orElseThrow(() -> new NoSuchWorldException(worldIdentifier));
  }

  /**
   * Returns the name of the world identified by the given identifier or, if such a world is not loaded, the identifier
   * as string.
   *
   * @param worldIdentifier the identifier
   * @param game            the Game to acquire the world from
   * @return the world's name
   */
  public static String toWorldName(UUID worldIdentifier, Game game) {
    return game.getWorld(worldIdentifier).map(LocalWorld::getName).orElse(worldIdentifier.toString());
  }

  /**
   * Returns true if and only if {@code searchStr} is contained within {@code str} while ignoring the cases of both
   * strings.
   *
   * <p>Note that this method may produce false results for some edge cases such as the German 'ß' and 'SS' which
   * should be equivalent, but are not accepted as equivalent by this method.</p>
   *
   * @param str       the string to compare to.
   * @param searchStr the string to search.
   * @return true if and only if {@code str} contains {@code searchStr} regardeless of the case of both
   */
  public static boolean containsIgnoreCase(String str, String searchStr) {
    //this is a lot faster than using str.toLowercase().contains(seachStr.toLowercase())
    //see https://stackoverflow.com/a/25379180.
    Objects.requireNonNull(str);
    Objects.requireNonNull(searchStr);

    final int length = searchStr.length();
    if (length == 0) {
      return true;
    }

    for (int i = str.length() - length; i >= 0; i--) {
      if (str.regionMatches(true, i, searchStr, 0, length)) {
        return true;
      }
    }
    return false;
  }
}
