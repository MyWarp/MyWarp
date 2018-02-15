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

package io.github.mywarp.mywarp.warp;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableSet;

import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.util.playermatcher.PlayerMatcher;
import io.github.mywarp.mywarp.util.teleport.TeleportHandler;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;
import java.util.UUID;

/**
 * A named location with additional meta-data. Two Warps are equal if and only if their names are equal.
 *
 * <p>Use a {@link WarpBuilder} to create instances.</p>
 */
public interface Warp extends Comparable<Warp> {

  String RESOURCE_BUNDLE_NAME = "io.github.mywarp.mywarp.lang.Warp";

  /**
   * Teleports the given {@code entity} to this Warp with the given TeleportHandler.
   *
   * @param entity  the entity to teleport
   * @param handler the TeleportHandler that handles the teleport
   * @return the status of the teleport
   */
  TeleportHandler.TeleportStatus visit(LocalEntity entity, TeleportHandler handler);

  /**
   * Returns whether the unique identifier is equal to the identifier of the player who created this Warp.
   *
   * @param uniqueId the unique identifier to check
   * @return true if the identifiers are equal
   */
  default boolean isCreator(UUID uniqueId) {
    return getCreator().equals(uniqueId);
  }

  /**
   * Returns whether the Warp has the same type as the given type.
   *
   * @param type the type
   * @return true if the given type is the same as this Warp's type
   */
  default boolean isType(Warp.Type type) {
    return getType().equals(type);
  }

  /**
   * Returns whether the given player is criteria to this Warp.
   *
   * @param player the player to check
   * @return true if the identified player is criteria to this Warp
   */
  default boolean isInvited(LocalPlayer player) {
    return getInvitations().stream().anyMatch(p -> p.test(player));
  }

  /**
   * Returns whether this warp already has the given PlayerMatcher.
   *
   * @param invitation the playermatcher to check
   * @return true if this warp already has the given playermatcher
   */
  default boolean hasInvitation(PlayerMatcher invitation) {
    return getInvitations().contains(invitation);
  }

  /**
   * Adds the given playermatcher to this Warp.
   *
   * @param invitation the playermatcher to addInvitation
   */
  void addInvitation(PlayerMatcher invitation);

  /**
   * Removes the given playermatcher from this Warp.
   *
   * @param invitation the playermatcher to removeInvitation
   */
  void removeInvitation(PlayerMatcher invitation);

  /**
   * Gets the unique identifier of this Warp's creator.
   *
   * @return the unique identifier of the creator of this Warp
   */
  UUID getCreator();

  /**
   * Sets the creator of this Warp to the one identified by the given unique identifier.
   *
   * @param uniqueId the unique identifier of the new creator
   */
  void setCreator(UUID uniqueId);

  /**
   * Gets an unmodifiable set containing all Invitees criteria to this Warp.
   *
   * @return a set with all Invitees criteria to this Warp
   */
  ImmutableSet<PlayerMatcher> getInvitations();

  /**
   * Gets this Warp's name.
   *
   * @return the name of this Warp
   */
  String getName();

  /**
   * Gets the unique identifier of the world this warp is positioned in.
   *
   * <p>The loaded world identified by the identifier can be acquired from the Game by calling
   * {@link Game#getWorld(UUID)}. There is however no guarantee, that a warp's world is actually loaded,  calling
   * {@code Game.getWorld(UUID} with the unique identifier returned by this methid m retun an empty Optional.</p>
   *
   * @return the world's unique identifier
   */
  UUID getWorldIdentifier();

  /**
   * Gets this Warp's position.
   *
   * @return the position
   */
  Vector3d getPosition();

  /**
   * Gets this Warp's rotation.
   *
   * <p>The format of the rotation is represented by:</p>
   *
   * <ul><code>x -> pitch</code>, <code>y -> yaw</code></ul>
   *
   * @return the rotation
   */
  Vector2f getRotation();

  /**
   * Gets this Warp's type.
   *
   * @return the type of this Warp
   */
  Warp.Type getType();

  /**
   * Sets the type of this Warp to the given one.
   *
   * @param type the new type
   */
  void setType(Warp.Type type);

  /**
   * Gets this Warp's creation-date.
   *
   * @return the creation-date of this Warp
   */
  Instant getCreationDate();

  /**
   * Gets this Warp's visits number.
   *
   * @return the number of times this Warp has been visited
   */
  int getVisits();

  /**
   * Gets this Warp's welcome message.
   *
   * <p>The returned message may still contain warp variables that can be replaced using a
   * {@link PlaceholderResolver}</p>
   *
   * @return the raw welcome message of this Warp
   */
  String getWelcomeMessage();

  /**
   * Sets the welcome-message of this Warp to the given one.
   *
   * @param welcomeMessage the new welcome-message
   */
  void setWelcomeMessage(String welcomeMessage);

  /**
   * Sets the location of this Warp.
   *
   * @param world    the world
   * @param position the position
   * @param rotation the rotation
   */
  void setLocation(LocalWorld world, Vector3d position, Vector2f rotation);

  /**
   * The type of a Warp.
   */
  enum Type {
    /**
     * A private Warp.
     */
    PRIVATE, /**
     * A public Warp.
     */
    PUBLIC
  }

  /**
   * Orders Warps by popularity: popular Warps come first, unpopular last.
   *
   * <p>Warps with a higher popularity score are preferred over Warps with lower score. If the score is equal, newer
   * Warps are preferred over older Warps. If both Warps were created at the same millisecond, the alphabetically
   * first is preferred.</p>
   */
  class PopularityComparator implements Comparator<Warp> {

    private static final double GRAVITY_CONSTANT = 0.8;

    @Override
    public int compare(Warp w1, Warp w2) {
      return ComparisonChain.start().compare(popularityScore(w2), popularityScore(w1))
          .compare(w2.getCreationDate(), w1.getCreationDate()).compare(w1.getName(), w2.getName()).result();
    }

    /**
     * Computes the popularity score of the given {@code warp}. The score depends on the number of visits of the
     * Warp as well as the warp's age.
     *
     * @return the popularity score of this Warp
     */
    private double popularityScore(Warp warp) {
      // a basic implementation of the hacker news ranking algorithm detailed
      // at http://amix.dk/blog/post/19574: Older warps receive lower scores
      // due to the influence of the gravity constant.
      double daysExisting = Duration.between(warp.getCreationDate(), Instant.now()).toMillis() / (1000 * 60 * 60 * 24L);
      return warp.getVisits() / Math.pow(daysExisting, GRAVITY_CONSTANT);
    }
  }

}
