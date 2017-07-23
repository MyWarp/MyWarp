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

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;

import io.github.mywarp.mywarp.bukkit.util.ReflectiveLocaleResolver;
import io.github.mywarp.mywarp.bukkit.util.conversation.AcceptancePromptFactory;
import io.github.mywarp.mywarp.bukkit.util.conversation.WelcomeEditorFactory;
import io.github.mywarp.mywarp.bukkit.util.permission.group.GroupResolver;
import io.github.mywarp.mywarp.bukkit.util.versionsupport.TamedHorseChecker;
import io.github.mywarp.mywarp.bukkit.util.versionsupport.VersionSupport;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.Settings;
import io.github.mywarp.mywarp.util.MyWarpLogger;
import io.github.mywarp.mywarp.warp.Warp;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.slf4j.Logger;

import java.util.Locale;
import java.util.UUID;

/**
 * A reference to a Player in Bukkit.
 */
public class BukkitPlayer extends BukkitActor implements LocalPlayer {

  private static final Logger log = MyWarpLogger.getLogger(BukkitPlayer.class);

  private final AcceptancePromptFactory acceptancePromptFactory;
  private final WelcomeEditorFactory welcomeEditorFactory;
  private final GroupResolver groupResolver;

  /**
   * Creates an instance that references the given {@code player}.
   *
   * @param player                  the player
   * @param acceptancePromptFactory the factory to create warp acceptance conversations
   * @param welcomeEditorFactory    the factory to create welcome message editor conversations
   * @param groupResolver           the group resolver
   * @param settings                the configured settings
   */
  BukkitPlayer(Player player, AcceptancePromptFactory acceptancePromptFactory,
               WelcomeEditorFactory welcomeEditorFactory, GroupResolver groupResolver, Settings settings) {
    super(player, settings);
    this.acceptancePromptFactory = acceptancePromptFactory;
    this.welcomeEditorFactory = welcomeEditorFactory;
    this.groupResolver = groupResolver;
  }

  @Override
  public Player getWrapped() {
    return (Player) super.getWrapped();
  }

  @Override
  public UUID getUniqueId() {
    return getWrapped().getUniqueId();
  }

  @Override
  public Locale getLocale() {
    Locale locale = super.getLocale();
    if (settings.isLocalizationPerPlayer()) {
      try {
        locale = ReflectiveLocaleResolver.INSTANCE.resolve(getWrapped());
      } catch (ReflectiveLocaleResolver.UnresolvableLocaleException e) {
        log.warn(String.format("Failed to resolve the Locale for %s, defaulting to %s.", getName(), locale), e);
      }
    }
    return locale;
  }

  @Override
  public void initiateAcceptanceConversation(Actor initiator, Warp warp) {
    acceptancePromptFactory.create(this, warp, initiator);
  }

  @Override
  public void initiateWelcomeChangeConversation(Warp warp) {
    welcomeEditorFactory.create(this, warp);
  }

  @Override
  public boolean hasGroup(String groupId) {
    return groupResolver.hasGroup(getWrapped(), groupId);
  }

  @Override
  public double getHealth() {
    return getWrapped().getHealth();
  }

  @Override
  public void setCompassTarget(LocalWorld world, Vector3d position) {
    Location bukkitLoc = new Location(BukkitAdapter.adapt(world), position.getX(), position.getY(), position.getZ());
    getWrapped().setCompassTarget(bukkitLoc);
  }

  @Override
  public void resetCompass() {
    getWrapped().setCompassTarget(getWrapped().getWorld().getSpawnLocation());
  }

  @Override
  public LocalWorld getWorld() {
    return BukkitAdapter.adapt(getWrapped().getWorld());
  }

  @Override
  public Vector3d getPosition() {
    Location bukkitLoc = getWrapped().getLocation();
    return new Vector3d(bukkitLoc.getX(), bukkitLoc.getY(), bukkitLoc.getZ());
  }

  @Override
  public Vector2f getRotation() {
    Location bukkitLoc = getWrapped().getLocation();
    return new Vector2f(bukkitLoc.getPitch(), bukkitLoc.getYaw());
  }

  @Override
  public void teleport(LocalWorld world, Vector3d position, Vector2f rotation, boolean teleportTamedHorse) {
    Location
        bukkitLoc =
        new Location(BukkitAdapter.adapt(world), position.getX(), position.getY(), position.getZ(), rotation.getY(),
                     rotation.getX());
    teleportRecursive(getWrapped(), bukkitLoc, teleportTamedHorse);
  }

  private void teleportRecursive(Entity toTeleport, Location bukkitLoc, boolean teleportTamedHorses) {
    Entity vehicle = null;

    // handle vehicles
    if (teleportTamedHorses && VersionSupport.get(TamedHorseChecker.class).test(toTeleport.getVehicle())) {
      vehicle = toTeleport.getVehicle();
    }
    toTeleport.leaveVehicle();

    // load the chunk if needed
    int blockX = bukkitLoc.getBlockX();
    int blockZ = bukkitLoc.getBlockZ();
    if (!bukkitLoc.getWorld().isChunkLoaded(blockX, blockZ)) {
      bukkitLoc.getWorld().refreshChunk(blockX, blockZ);
    }

    // teleport the entity
    toTeleport.teleport(bukkitLoc);

    // teleport the vehicle
    if (vehicle != null) {
      teleportRecursive(vehicle, bukkitLoc, true);
      vehicle.setPassenger(toTeleport);
    }
  }
}
