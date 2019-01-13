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

package io.github.mywarp.mywarp.bukkit;

import com.flowpowered.math.vector.Vector3i;

import io.github.mywarp.mywarp.bukkit.util.AbstractListener;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.sign.WarpSignHandler;
import io.github.mywarp.mywarp.util.BlockFace;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.Attachable;
import org.bukkit.material.Button;
import org.bukkit.material.Lever;
import org.bukkit.material.MaterialData;
import org.bukkit.material.PressurePlate;

import java.util.Optional;

/**
 * Listens for events involving signs and feats them to a {@link WarpSignHandler}.
 */
class WarpSignListener extends AbstractListener {

  private final MyWarpPlugin plugin;
  private final WarpSignHandler warpSignHandler;

  /**
   * Initializes this listener.
   *
   * @param warpSignHandler the warpSignHandler that will be feat by this listener
   */
  WarpSignListener(MyWarpPlugin plugin, WarpSignHandler warpSignHandler) {
    this.plugin = plugin;
    this.warpSignHandler = warpSignHandler;
  }

  /**
   * Called whenever a sign is changed.
   *
   * @param event the event
   */
  @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
  public void onSignChange(SignChangeEvent event) {
    Optional<Boolean>
        isValidWarpSign =
        warpSignHandler.handleSignCreation(plugin.wrap(event.getPlayer()), new EventSign(event));

    if (!isValidWarpSign.isPresent()) {
      return;
    }

    if (!isValidWarpSign.get()) {
      event.getBlock().breakNaturally();
      event.setCancelled(true);
    }
  }

  /**
   * Called whenever a player interacts with a block.
   *
   * @param event the event
   */
  @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
  public void onPlayerInteract(PlayerInteractEvent event) {
    Block block = event.getClickedBlock();

    switch (event.getAction()) {
      case RIGHT_CLICK_BLOCK:
        //player clicked on a sign directly
        if (block.getState() instanceof Sign) {
          boolean cancel = warpSignHandler.handleInteraction(toPlayer(event), new BukkitSign((Sign) block.getState()));
          event.setCancelled(cancel);
          return;
        }

        //player clicked on something that might trigger a warp sign
        MaterialData materialData = block.getState().getData();

        if (materialData instanceof Button || materialData instanceof Lever) {
          Optional<BlockFace> blockFace = attachedBlockFace(block);

          if (blockFace.isPresent()) {
            warpSignHandler.handleInteraction(toPlayer(event), toVector(block), blockFace.get());
          }
        }
        break;
      case PHYSICAL:
        //player stepped on something that might trigger a warp sign
        if (block.getState().getData() instanceof PressurePlate) {
          warpSignHandler.handleInteraction(toPlayer(event), toVector(block), BlockFace.UP);
        }
        break;
      default: //do nothing
    }
  }

  private LocalPlayer toPlayer(PlayerInteractEvent event) {
    return plugin.wrap(event.getPlayer());
  }

  private static Vector3i toVector(Block block) {
    Location loc = block.getLocation();
    return new Vector3i(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
  }

  /**
   * A sign that is actually a wrapped event.
   */
  private class EventSign implements io.github.mywarp.mywarp.platform.Sign {

    private final SignChangeEvent event;

    private EventSign(SignChangeEvent event) {
      this.event = event;
    }

    @Override
    public String getLine(int line) {
      return event.getLine(line);
    }

    @Override
    public void setLine(int line, String text) {
      event.setLine(line, text);
    }
  }

  private Optional<BlockFace> attachedBlockFace(Block block) {
    MaterialData materialData = block.getState().getData();

    if (materialData instanceof Attachable) {
      return BukkitAdapter.adapt(((Attachable) materialData).getAttachedFace());
    }
    return Optional.empty();
  }

}
