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

package io.github.mywarp.mywarp.warp;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;

import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.util.teleport.TeleportHandler;

import java.time.Instant;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * A simple implementation that natively stores its properties.
 */
class SimpleWarp extends AbstractWarp {

  private final String name;
  private final Instant creationDate;
  private final Set<UUID> invitedPlayers;
  private final Set<String> invitedGroups;

  private volatile UUID creator;
  private volatile Warp.Type type;
  private volatile UUID worldIdentifier;
  private volatile Vector3d position;
  private volatile Vector2f rotation;
  private volatile int visits;
  private volatile String welcomeMessage;

  /**
   * Creates a instance with the given values.
   *
   * @param name            the warp's name
   * @param creationDate    the warp's creation date
   * @param invitedPlayers  a Set of player profiles invited to this warp
   * @param invitedGroups   a set of group identifiers invited to this warp
   * @param creator         the profile of the warp's creator
   * @param type            the warp's type
   * @param worldIdentifier the identifier of the world that holds the warp
   * @param position        the warp's position
   * @param rotation        the warp's rotation
   * @param visits          the number of times the warp has been visited
   * @param welcomeMessage  the warp's welcome message
   * @throws NullPointerException     if one of the given values is {@code null}
   * @throws IllegalArgumentException if {@code invitedPlayers} or {@code invitedGroups} contains {@code null}
   */
  SimpleWarp(String name, Instant creationDate, Set<UUID> invitedPlayers, Set<String> invitedGroups, UUID creator,
             Type type, UUID worldIdentifier, Vector3d position, Vector2f rotation, int visits, String welcomeMessage) {
    this.name = checkNotNull(name);
    this.creationDate = checkNotNull(creationDate);
    checkArgument(!checkNotNull(invitedPlayers).contains(null), "'invitedPlayers' must not contain null.");
    this.invitedPlayers = invitedPlayers;
    checkArgument(!checkNotNull(invitedGroups).contains(null), "'invitedGroups' must not contain null.");
    this.invitedGroups = invitedGroups;
    this.creator = checkNotNull(creator);
    this.type = checkNotNull(type);
    this.worldIdentifier = checkNotNull(worldIdentifier);
    this.position = checkNotNull(position);
    this.rotation = checkNotNull(rotation);
    this.visits = checkNotNull(visits);
    this.welcomeMessage = checkNotNull(welcomeMessage);
  }

  @Override
  public TeleportHandler.TeleportStatus visit(LocalEntity entity, TeleportHandler handler) {
    TeleportHandler.TeleportStatus status = handler.teleport(entity, worldIdentifier, getPosition(), getRotation());

    if (status.isPositionModified()) {
      visits++;
    }
    return status;
  }

  @Override
  public void inviteGroup(String groupId) {
    invitedGroups.add(groupId);
  }

  @Override
  public void invitePlayer(UUID uniqueId) {
    invitedPlayers.add(uniqueId);
  }

  @Override
  public void uninviteGroup(String groupId) {
    invitedGroups.remove(groupId);
  }

  @Override
  public void uninvitePlayer(UUID uniqueId) {
    invitedPlayers.remove(uniqueId);
  }

  @Override
  public UUID getCreator() {
    return creator;
  }

  @Override
  public void setCreator(UUID uniqueId) {
    this.creator = uniqueId;
  }

  @Override
  public Set<String> getInvitedGroups() {
    return Collections.unmodifiableSet(invitedGroups);
  }

  @Override
  public Set<UUID> getInvitedPlayers() {
    return Collections.unmodifiableSet(invitedPlayers);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Warp.Type getType() {
    return type;
  }

  @Override
  public void setType(Warp.Type type) {
    this.type = type;
  }

  @Override
  public Instant getCreationDate() {
    return creationDate;
  }

  @Override
  public int getVisits() {
    return visits;
  }

  @Override
  public String getWelcomeMessage() {
    return welcomeMessage;
  }

  @Override
  public void setWelcomeMessage(String welcomeMessage) {
    this.welcomeMessage = welcomeMessage;
  }

  @Override
  public void setLocation(LocalWorld world, Vector3d position, Vector2f rotation) {
    this.worldIdentifier = world.getUniqueId();
    this.position = position;
    this.rotation = rotation;
  }

  @Override
  public Vector3d getPosition() {
    return position;
  }

  @Override
  public Vector2f getRotation() {
    return rotation;
  }

  @Override
  public UUID getWorldIdentifier() {
    return worldIdentifier;
  }

  @Override
  public String toString() {
    return "SimpleWarp{" + "name='" + name + '\'' + ", creationDate=" + creationDate + ", invitedPlayers="
           + invitedPlayers + ", invitedGroups=" + invitedGroups + ", creator=" + creator + ", type=" + type
           + ", worldIdentifier=" + worldIdentifier + ", position=" + position + ", rotation=" + rotation + ", visits="
           + visits + ", welcomeMessage='" + welcomeMessage + '\'' + '}';
  }
}
