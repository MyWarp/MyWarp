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

package io.github.mywarp.mywarp.platform;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Resolve player names form unique identifiers and vice-versa.
 */
public interface PlayerNameResolver {

  /**
   * Gets an CompletableFuture with the Profile of the player with the given unique identifier.
   *
   * <p>If the cache does not contain the profile, the Future will throw a
   * {@link java.util.concurrent.CompletionException} caused by a {@link NoSuchProfileException}.</p>
   *
   * @param uniqueId the unique identifier
   * @return a Future with the corresponding Profile, if available
   */
  CompletableFuture<Profile> getByUniqueId(UUID uniqueId);

  /**
   * Gets an CompletableFuture with a Set of Profiles of the players with the given unique identifiers, if available on
   * this cache.
   *
   * <p>If the cache does not contain a Profile for a given unique identifier, it will not be present in the returned
   * Set. Alas, if none of the the given unique identifiers has a cached Profile, an empty Set will be returned.</p>
   *
   * @param uniqueIds an Iterable of unique identifiers
   * @return a Future with Set of corresponding Profiles, if available
   */
  CompletableFuture<Set<Profile>> getByUniqueId(Iterable<UUID> uniqueIds);

  /**
   * Gets an CompletableFuture with the Profile of the player of the given name, if such a player exists.
   *
   * <p>If the profile does not exist, the Future will throw a
   * {@link java.util.concurrent.CompletionException} caused by a {@link NoSuchProfileException}.</p>
   *
   * <p>This method may connect to a remote server in order to get the appropriate Profile.</p>
   *
   * @param name the name
   * @return a Future with the corresponding Profile, if available
   */
  CompletableFuture<Profile> getByName(String name);

  /**
   * Gets an CompletableFuture with a Set of Profiles of the players with the given unique identifiers, if available on
   * this cache.
   *
   * <p>If a Profile for a given name does not exist, it will not be present in the returned
   * Set. Alas, if none of the the given names has a Profile, an empty Set will be returned.</p>
   *
   * <p>This method may connect to a remote server in order to get the appropriate Profile.</p>
   *
   * @param names an Iterable of names
   * @return a Future with a Set of corresponding Profiles
   */
  CompletableFuture<Set<Profile>> getByName(Iterable<String> names);


}
