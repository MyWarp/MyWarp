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

package me.taylorkelly.mywarp.bukkit;

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Optional;

import me.taylorkelly.mywarp.command.util.NoSuchWorldException;
import me.taylorkelly.mywarp.platform.LocalWorld;
import me.taylorkelly.mywarp.platform.Sign;
import me.taylorkelly.mywarp.util.BlockFace;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.UUID;

import javax.annotation.Nullable;

/**
 * A reference to a World in Bukkit.
 */
public class BukkitWorld implements LocalWorld {

  private final UUID worldIdentifier;

  /**
   * Creates an instance that references the given World.
   *
   * @param world the World.
   */
  BukkitWorld(World world) {
    this.worldIdentifier = world.getUID();
  }

  @Override
  public String getName() {
    return getLoadedWorld().getName();
  }

  @Override
  public UUID getUniqueId() {
    return worldIdentifier;
  }

  @Override
  public boolean isNotFullHeight(Vector3i position) {
    return MaterialInfo.isNotFullHeight(MyWarpPlugin.getMaterial(this, position));
  }

  @Override
  public void playTeleportEffect(Vector3d position) {
    Location loc = new Location(getLoadedWorld(), position.getX(), position.getY(), position.getZ());

    // play the smoke effect
    for (int i = 0; i < 4; i++) {
      getLoadedWorld().playEffect(loc, Effect.SMOKE, 4);
    }
  }

  @Override
  public Optional<Sign> getSign(Vector3i position) {
    //Generics are stupid and throw an error when not casting
    return Optional.fromNullable((Sign) getBukkitSign(position));
  }

  @Override
  public Optional<Sign> getAttachedSign(Vector3i position, BlockFace blockFace) {
    BukkitSign sign = getBukkitSign(position);

    if (sign != null && sign.isAttached(blockFace)) {
      //Generics are stupid and throw an error when not casting
      return Optional.of((Sign) sign);
    }
    return Optional.absent();
  }

  @Nullable
  private BukkitSign getBukkitSign(Vector3i position) {
    Block block = getLoadedWorld().getBlockAt(position.getX(), position.getY(), position.getZ());

    if (block.getState() instanceof org.bukkit.block.Sign) {
      return new BukkitSign((org.bukkit.block.Sign) block.getState());
    }
    return null;
  }

  /**
   * Gets the loaded World that is referenced by this BukkitWorld.
   *
   * @return the loaded World
   * @throws NoSuchWorldException if the World is no longer loaded
   */
  World getLoadedWorld() {
    World ret = Bukkit.getWorld(worldIdentifier);
    if (ret == null) {
      throw new IllegalStateException(
              "The world with the identifier " + worldIdentifier + "is not available in Bukkit.");
    }
    return ret;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    BukkitWorld that = (BukkitWorld) o;

    if (!worldIdentifier.equals(that.worldIdentifier)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    return worldIdentifier.hashCode();
  }

  @Override
  public String toString() {
    return "BukkitWorld{" + "worldIdentifier=" + worldIdentifier + '}';
  }
}
