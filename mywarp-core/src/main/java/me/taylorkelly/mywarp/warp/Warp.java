/*
 * Copyright (C) 2011 - 2016, MyWarp team and contributors
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

package me.taylorkelly.mywarp.warp;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ComparisonChain;

import me.taylorkelly.mywarp.platform.Game;
import me.taylorkelly.mywarp.platform.LocalEntity;
import me.taylorkelly.mywarp.platform.LocalWorld;
import me.taylorkelly.mywarp.util.teleport.TeleportHandler;

import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.UUID;

/**
 * A named location with additional meta-data. Two Warps are equal if and only if their names are equal.
 *
 * <p>Use a {@link WarpBuilder} to create instances.</p>
 */
public interface Warp extends Comparable<Warp> {

  String RESOURCE_BUNDLE_NAME = "me.taylorkelly.mywarp.lang.Warp";

  /**
   * Teleports the given {@code entity} to this Warp with the given TeleportHandler.
   *
   * @param entity   the entity that attempted the teleport
   * @param game     the game within which the teleport occurs
   * @param handler  the TeleportHandler that handles the teleport
   * @param resolver the resolver to use for values in the warp's welcome message
   * @return the status of the teleport
   */
  TeleportHandler.TeleportStatus visit(LocalEntity entity, Game game, TeleportHandler handler,
                                       PlaceholderResolver resolver);

  /**
   * Returns whether the unique identifier is equal to the identifier of the player who created this Warp.
   *
   * @param uniqueId the unique identifier to check
   * @return true if the identifiers are equal
   */
  boolean isCreator(UUID uniqueId);

  /**
   * Returns whether the Warp has the same type as the given type.
   *
   * @param type the type
   * @return true if the given type is the same as this Warp's type
   */
  boolean isType(Warp.Type type);

  /**
   * Returns whether the given unique identifier identifies a player who is invited to this Warp.
   *
   * @param uniqueId the unique identifier to check
   * @return true if the identified player is invited to this Warp
   */
  boolean isPlayerInvited(UUID uniqueId);

  /**
   * Returns whether the permission-group identified by the given ID is invited to this Warp.
   *
   * @param groupId the ID of the group
   * @return true if group identified by the given ID is invited to this Warp
   */
  boolean isGroupInvited(String groupId);

  /**
   * Invites the permission-group identified by the given ID to this Warp.
   *
   * @param groupId the ID of the group who should be invited
   */
  void inviteGroup(String groupId);

  /**
   * Invites the player identified by the given unique identifier to this Warp.
   *
   * @param uniqueId the unique identifier of the player who should be invited
   */
  void invitePlayer(UUID uniqueId);

  /**
   * Uninvites the permission-group identified by the given ID from this Warp.
   *
   * @param groupId the ID of the group who should be uninvited
   */
  void uninviteGroup(String groupId);

  /**
   * Uninvites the player identified by the given unique identifier from this Warp.
   *
   * @param uniqueId the unique identifier of the player who should be uninvited
   */
  void uninvitePlayer(UUID uniqueId);

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
   * Gets an unmodifiable set containing the IDs of all permission-groups invited to this Warp.
   *
   * @return a set with all IDs of groups invited to this Warp
   */
  Set<String> getInvitedGroups();

  /**
   * Gets an unmodifiable set containing the unique identifiers of all players invited to this Warp.
   *
   * @return a set with all unique identifers of players who are invited to this Warp
   */
  Set<UUID> getInvitedPlayers();

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
  Date getCreationDate();

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
          .compare(w2.getCreationDate().getTime(), w1.getCreationDate().getTime()).compare(w1.getName(), w2.getName())
          .result();
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
      double daysExisting = (System.currentTimeMillis() - warp.getCreationDate().getTime()) / (1000 * 60 * 60 * 24L);
      return warp.getVisits() / Math.pow(daysExisting, GRAVITY_CONSTANT);
    }
  }

}
