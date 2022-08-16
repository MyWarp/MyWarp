/*
 * Copyright (C) 2011 - 2022, MyWarp team and contributors
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
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.Settings;
import io.github.mywarp.mywarp.platform.capability.PositionValidationCapability;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

/**
 * Parses teleport positions against a {@link PositionValidationCapability}. If a valid position exists, the entity is
 * teleported there. If no valid position exists, the teleport is canceled.
 */
public class StrategicTeleportHandler implements TeleportHandler {

  @Nullable
  private final PositionValidationCapability strategy;
  private final Settings settings;
  private final Game game;

  /**
   * Creates an instance that uses the given strategy to validate teleport positions.
   *
   * @param settings the settings instance to use
   * @param game     the game instance to use
   * @param strategy the strategy to use. May be null if no strategy should be used.
   */
  public StrategicTeleportHandler(Settings settings, Game game, @Nullable PositionValidationCapability strategy) {
    this.strategy = strategy;
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

    Vector3d validPosition = position;
    if (strategy != null) {
      Optional<Vector3d> optional = strategy.getValidPosition(position, world);

      if (!optional.isPresent()) {
        return TeleportStatus.NONE;
      }
      validPosition = optional.get();
    }

    if (settings.isShowTeleportEffect()) {
      world.playTeleportEffect(entity.getPosition());
    }
    entity.teleport(world, validPosition, rotation, settings.isTeleportTamedHorses());

    if (!validPosition.equals(position)) {
      return TeleportStatus.MODIFIED;
    }
    return TeleportStatus.ORIGINAL;
  }
}
