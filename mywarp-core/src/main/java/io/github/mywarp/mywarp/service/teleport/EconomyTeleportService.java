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

package io.github.mywarp.mywarp.service.teleport;

import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.service.economy.EconomyService;
import io.github.mywarp.mywarp.service.economy.FeeType;
import io.github.mywarp.mywarp.util.teleport.TeleportHandler;
import io.github.mywarp.mywarp.warp.Warp;

/**
 * Bills users for teleporting and cancels the teleport if the entity cannot afford it.
 *
 * <p>Only {@link LocalPlayer}s are billed, other entitys are simply teleported. The teleport itself is delegated to
 * another TeleportService.</p>
 */
public class EconomyTeleportService extends ForwardingTeleportService {

  private final TeleportService delegate;
  private final EconomyService economyService;
  private final FeeType fee;

  /**
   * Creates an instance that uses the given EconomyService to withdraw users with the given {@code fee} on successful
   * teleport. The teleport itself is delegated to the given TeleportService.
   *
   * @param delegate       the TeleportService to delegate teleports to
   * @param economyService the EconomyService to use
   * @param fee            the fee to withdraw
   */
  public EconomyTeleportService(TeleportService delegate, EconomyService economyService, FeeType fee) {
    this.delegate = delegate;
    this.economyService = economyService;
    this.fee = fee;
  }

  @Override
  public TeleportHandler.TeleportStatus teleport(LocalEntity entity, Warp warp) {
    if (entity instanceof LocalPlayer && !economyService.hasAtLeast((LocalPlayer) entity, fee)) {
      return TeleportHandler.TeleportStatus.NONE;
    }
    TeleportHandler.TeleportStatus status = delegate().teleport(entity, warp);

    if (entity instanceof LocalPlayer && status.isPositionModified()) {
      economyService.withdraw((LocalPlayer) entity, fee);
    }

    return status;
  }

  @Override
  protected TeleportService delegate() {
    return delegate;
  }
}
