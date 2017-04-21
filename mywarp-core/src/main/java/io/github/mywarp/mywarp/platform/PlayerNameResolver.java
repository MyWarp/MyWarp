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

package io.github.mywarp.mywarp.platform;

import com.google.common.collect.ImmutableMap;

import java.util.Optional;
import java.util.UUID;

/**
 * Resolve player names form unique identifiers and vice-versa.
 */
public interface PlayerNameResolver {

  /**
   * Gets an Optional with the name of the player with the given unique identifier, if available in this cache.
   *
   * @param uniqueId the unique identifier
   * @return an Optional with the corresponding name
   */
  Optional<String> getByUniqueId(UUID uniqueId);

  /**
   * Gets all names of players with the given unique identifiers, if available in this cache. If none of the given
   * unique identifiers has a cached name, an empty Map will be returned.
   *
   * @param uniqueIds an Iterable of unique identifiers
   * @return a Map with the unique identifier and the corresponding name
   */
  ImmutableMap<UUID, String> getByUniqueId(Iterable<UUID> uniqueIds);

  /**
   * Gets an Optional containing the unique identifier of a player of the given name, if such a player exists.
   *
   * <p>Since Minecraft usernames are case-insensitive, calling {@link #getByUniqueId(UUID)} with the value returned by
   * this method may return a String with a different case than the String given to this method.</p>
   *
   * <p>Calling this method might result in a blocking call to a remote server to get the Profiles.</p>
   *
   * @param name the name
   * @return an Optional containing the unique identifier
   */
  Optional<UUID> getByName(String name);

  /**
   * Gets the unique identifiers for all players of the given names, if such a player exists. If none of the given names
   * has a unique identifier, an empty Map will be returned.
   *
   * <p>Minecraft usernames are case-insensitive; the map returned by this method must make sure that each player's
   * identifier is returned regardless of the case of the requested String. However the Map returned by this method
   * contains the name with the case that was originally given to this method.</p>
   *
   * <p>Calling this method might result in a blocking call to a remote server to get the Profiles.</p>
   *
   * @param names an Iterable of names
   * @return a Map with the name originally given and the corresponding unique identifier
   */
  ImmutableMap<String, UUID> getByName(Iterable<String> names);

}
