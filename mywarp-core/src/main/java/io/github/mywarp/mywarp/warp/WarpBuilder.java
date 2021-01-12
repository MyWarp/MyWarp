/*
 * Copyright (C) 2011 - 2021, MyWarp team and contributors
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

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Iterables;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.playermatcher.PlayerMatcher;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Builds {@link Warp}s.
 */
public class WarpBuilder {

  private static final DynamicMessages msg = new DynamicMessages(Warp.RESOURCE_BUNDLE_NAME);

  private final String name;
  private final Set<PlayerMatcher> invitations;
  private final UUID creator;
  private final UUID worldIdentifier;
  private final Vector3d position;
  private final Vector2f rotation;

  private Instant creationDate = Instant.now();
  private Warp.Type type = Warp.Type.PUBLIC;
  private int visits = 0;
  private String welcomeMessage = msg.getString("default-welcome-message");

  /**
   * Creates an instance that builds Warp's using the given values.
   *
   * @param name            the Warp's name
   * @param creator         the Warp's creator unique identifier
   * @param worldIdentifier the identifier of the world the Warp is located in
   * @param position        the Warp's position
   * @param rotation        the Warp's rotation
   */
  public WarpBuilder(String name, UUID creator, UUID worldIdentifier, Vector3d position, Vector2f rotation) {
    this.invitations = new HashSet<>();
    this.name = name;
    this.creator = creator;
    this.worldIdentifier = worldIdentifier;
    this.position = position;
    this.rotation = rotation;
  }

  /**
   * Sets the creation date of the Warp.
   *
   * @param creationDate the creation date
   * @return this {@code WarpBuilder}
   */
  public WarpBuilder setCreationDate(Instant creationDate) {
    this.creationDate = creationDate;
    return this;
  }

  /**
   * Adds the given playermatcher to the Warp.
   *
   * @param invitations the invitations to add
   * @return this {@code WarpBuilder}
   */
  public WarpBuilder addInvitation(PlayerMatcher... invitations) {
    return addInvitations(Arrays.asList(invitations));
  }

  /**
   * Adds each playermatcher to the Warp.
   *
   * @param invitations the invitations to add
   * @return this {@code WarpBuilder}
   */
  public WarpBuilder addInvitations(Iterable<PlayerMatcher> invitations) {
    Iterables.addAll(this.invitations, invitations);
    return this;
  }

  /**
   * Sets the type of the Warp.
   *
   * @param type the type
   * @return this {@code WarpBuilder}
   */
  public WarpBuilder setType(Warp.Type type) {
    this.type = type;
    return this;
  }

  /**
   * Sets the amount of visits of the Warp.
   *
   * @param visits the amount of visits
   * @return this {@code WarpBuilder}
   */
  public WarpBuilder setVisits(int visits) {
    this.visits = visits;
    return this;
  }

  /**
   * Sets the welcome message of the Warp.
   *
   * @param welcomeMessage the welcome message
   * @return this {@code WarpBuilder}
   */
  public WarpBuilder setWelcomeMessage(String welcomeMessage) {
    this.welcomeMessage = welcomeMessage;
    return this;
  }

  /**
   * Builds the Warp based on the contents of this Builder.
   *
   * @return the created Warp
   */
  public Warp build() {
    return new SimpleWarp(name, creationDate, invitations, creator, type, worldIdentifier, position, rotation, visits,
        welcomeMessage);
  }
}
