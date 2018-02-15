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

import com.google.common.collect.ImmutableSet;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Executor;

/**
 * A connection with the logic of the running game.
 */
public interface Game {

  /**
   * Gets an Executor that executes submitted tasks within the Thread that handles the game's logic. This is normally
   * called 'synchronous' execution.
   *
   * @return the Executor
   */
  Executor getExecutor();

  /**
   * Gets an Optional containing the player of the given name, if such a player exists.
   *
   * @param name the name of the player
   * @return an Optional containing the player
   */
  Optional<LocalPlayer> getPlayer(String name);

  /**
   * Gets an Optional containing the player of the given identifier, if such a player exists.
   *
   * @param identifier the unique identifier of the player
   * @return an Optional containing the player
   */
  Optional<LocalPlayer> getPlayer(UUID identifier);

  /**
   * Gets an ImmutableSet with all players currently on the server.
   *
   * @return an ImmutableSet with all players
   */
  ImmutableSet<LocalPlayer> getPlayers();

  /**
   * Gets an Optional containing the world of the given name, if such a world exists.
   *
   * @param name the name of the world
   * @return an Optional containing the world
   */
  Optional<LocalWorld> getWorld(String name);

  /**
   * Gets an Optional containing the world of the given unique identifier, if such a world exists.
   *
   * @param uniqueId the unique identifier of the world
   * @return an Optional containing the world
   */
  Optional<LocalWorld> getWorld(UUID uniqueId);

  /**
   * Gets an ImmutableSet with all worlds currently loaded on the server.
   *
   * @return an ImmutableSet with all loaded worlds
   */
  ImmutableSet<LocalWorld> getWorlds();
}
