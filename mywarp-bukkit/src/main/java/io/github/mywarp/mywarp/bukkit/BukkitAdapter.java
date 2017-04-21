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

package io.github.mywarp.mywarp.bukkit;

import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.util.BlockFace;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * Adapts between equivalent local and Bukkit objects.
 *
 * <p>Operations of this class are always stateless. Conversions that depend on a state are available in {@link
 * MyWarpPlugin}.</p>
 */
public final class BukkitAdapter {

  private BukkitAdapter() {
  }

  /**
   * Adapts between a LocalWorld and a World.
   *
   * @param world the LocalWorld
   * @return the World representing the given LocalWorld
   */
  public static World adapt(LocalWorld world) {
    if (world instanceof BukkitWorld) {
      return ((BukkitWorld) world).getLoadedWorld();
    }
    World loadedWorld = Bukkit.getWorld(world.getUniqueId());
    if (loadedWorld == null) {
      throw new IllegalArgumentException("Cannot find a loaded world for " + world + "in Bukkit.");
    }
    return loadedWorld;
  }

  /**
   * Adapts between a World and a LocalWorld.
   *
   * @param world the World
   * @return the LocalWorld representing the given World
   */
  public static LocalWorld adapt(World world) {
    return new BukkitWorld(world);
  }

  /**
   * Adapts between a LocalPlayer and a Player.
   *
   * @param player the LocalPlayer
   * @return the Player representing the given LocalPlayer
   */
  public static Player adapt(LocalPlayer player) {
    if (player instanceof BukkitPlayer) {
      return ((BukkitPlayer) player).getWrapped();
    }
    Player loadedPlayer = Bukkit.getPlayer(player.getUniqueId());
    if (loadedPlayer == null) {
      throw new IllegalArgumentException("Cannot find a loaded player for " + player + "in Bukkit.");
    }
    return loadedPlayer;
  }

  static Optional<BlockFace> adapt(org.bukkit.block.BlockFace blockFace) {
    switch (blockFace) {
      case NORTH:
        return Optional.of(BlockFace.NORTH);
      case EAST:
        return Optional.of(BlockFace.EAST);
      case SOUTH:
        return Optional.of(BlockFace.SOUTH);
      case WEST:
        return Optional.of(BlockFace.WEST);
      case UP:
        return Optional.of(BlockFace.UP);
      case DOWN:
        return Optional.of(BlockFace.DOWN);
      default:
        return Optional.empty();
    }
  }
}
