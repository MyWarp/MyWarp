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

package io.github.mywarp.mywarp.command.util;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.warp.Warp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Utilities for writing commands.
 */
public class CommandUtil {

  private CommandUtil() {
  }

  /**
   * Returns the name of the player identified by the given unique identifier or, if the name is not available, the
   * identifier as String.
   *
   * @param uniqueId the player's unique identifer
   * @param resolver the resolver used to resolve the player's name
   * @return a readable name
   */
  public static String toName(UUID uniqueId, PlayerNameResolver resolver) {
    return resolver.getByUniqueId(uniqueId).or(uniqueId.toString());
  }

  /**
   * Returns a alphabetically sorted List with the name of each  player identified by the given unique identifier or,
   * if the name is not available, the identifier as String.
   *
   * @param uniqueIds the unique identifiers
   * @param resolver  the resolver used to resolve the player's name
   * @return a sorted list of readable names
   */
  public static List<String> toName(Iterable<UUID> uniqueIds, PlayerNameResolver resolver) {
    List<String> ret = new ArrayList<String>();

    ImmutableMap<UUID, String> map = resolver.getByUniqueId(uniqueIds);

    for (UUID uniqueId : uniqueIds) {
      @Nullable String name = map.get(uniqueId);
      if (name != null) {
        ret.add(name);
      } else {
        ret.add(uniqueId.toString());
      }
    }
    Collections.sort(ret);
    return ret;
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
   * Returns the loaded world identified the given identifier or raises an Exception if the
   * world is not loaded.
   *
   * @param worldIdentifier the identifier
   * @param game            the Game to acquire the world from
   * @return the loaded world with the given identifier
   * @throws NoSuchWorldException if the warp's world cannot be acquired from the Game
   * @see Game#getWorld(UUID)
   */
  public static LocalWorld toWorld(UUID worldIdentifier, Game game) throws NoSuchWorldException {
    Optional<LocalWorld> worldOptional = game.getWorld(worldIdentifier);

    if (!worldOptional.isPresent()) {
      throw new NoSuchWorldException(worldIdentifier);
    }
    return worldOptional.get();
  }

  /**
   * Returns the name of the world identified by the given identifier or, if such a world is not loaded, the
   * identifier as string.
   *
   * @param worldIdentifier the identifier
   * @param game            the Game to acquire the world from
   * @return the world's name
   */
  public static String toWorldName(UUID worldIdentifier, Game game) {
    Optional<LocalWorld> worldOptional = game.getWorld(worldIdentifier);
    if (worldOptional.isPresent()) {
      return worldOptional.get().getName();
    }
    return worldIdentifier.toString();
  }
}
