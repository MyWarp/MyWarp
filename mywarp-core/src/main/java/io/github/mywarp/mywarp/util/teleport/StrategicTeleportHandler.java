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

package io.github.mywarp.mywarp.util.teleport;

import com.flowpowered.math.vector.Vector2f;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;

import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.Settings;
import io.github.mywarp.mywarp.platform.capability.PositionValidationCapability;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.Warp;

import java.util.Optional;
import java.util.UUID;

/**
 * Parses teleport positions against a {@link PositionValidationCapability}. If a valid position exists, the entity is
 * teleported there. If no valid position exists, the teleport is canceled.
 */
public class StrategicTeleportHandler implements TeleportHandler {

  private static final DynamicMessages msg = new DynamicMessages(Warp.RESOURCE_BUNDLE_NAME);

  private final Iterable<PositionValidationCapability> strategies;
  private final Settings settings;
  private final Game game;


  /**
   * Creates an instance that uses the given strategies to validate teleport positions.
   *
   * <p>The strategies are evaluated in the order of the given elements until either a strategy returns no valid
   * position or all strategies have evaluated the position. If a strategy returns an alternate position, following
   * strategies will check this position opposed to the original one. </p>
   *
   * @param settings   the settings instance to use
   * @param game       the game instance to use
   * @param strategies the strategies to use
   */
  public StrategicTeleportHandler(Settings settings, Game game, PositionValidationCapability... strategies) {
    this(settings, game, Lists.newArrayList(strategies));
  }

  /**
   * Creates an instance that uses the given strategies to validate teleport positions.
   *
   * <p>The strategies are evaluated in the order of the elements in the given Iterable until either a strategy returns
   * no valid position or all strategies have evaluated the position. If a strategy returns an alternate position,
   * following strategies will check this position opposed to the original one. </p>
   *
   * @param settings   the settings instance to use
   * @param game       the game instance to use
   * @param strategies the strategies to use
   */
  public StrategicTeleportHandler(Settings settings, Game game, Iterable<PositionValidationCapability> strategies) {
    this.strategies = strategies;
    this.settings = settings;
    this.game = game;
  }

  @Override
  public TeleportStatus teleport(LocalEntity entity, UUID worldIdentifier, Vector3d position, Vector2f rotation) {
    Optional<LocalWorld> worldOptional = game.getWorld(worldIdentifier);

    if (!worldOptional.isPresent()) {
      return TeleportStatus.NO_SUCH_WORLD;
    }
    LocalWorld world = worldOptional.get();

    Optional<Vector3d> optional = getValidPosition(world, position);

    if (!optional.isPresent()) {
      return TeleportStatus.NONE;
    }

    Vector3d validPosition = optional.get();

    if (settings.isShowTeleportEffect()) {
      world.playTeleportEffect(entity.getPosition());
    }
    entity.teleport(world, validPosition, rotation, settings.isTeleportTamedHorses());

    if (!validPosition.equals(position)) {
      return TeleportStatus.MODIFIED;
    }
    return TeleportStatus.ORIGINAL;
  }

  private Optional<Vector3d> getValidPosition(LocalWorld world, Vector3d originalPosition) {
    Optional<Vector3d> ret = Optional.of(originalPosition);
    for (PositionValidationCapability strategy : strategies) {
      if (!ret.isPresent()) {
        return ret;
      }
      ret = strategy.getValidPosition(ret.get(), world);
    }
    return ret;
  }
}
