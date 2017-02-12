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

package me.taylorkelly.mywarp.platform.capability;

import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.service.economy.FeeType;

import java.math.BigDecimal;

/**
 * The capability of a platform to provide economical functionality.
 */
public interface EconomyCapability {

  /**
   * Returns whether users should be informed after successful transaction.
   *
   * @return {@code true} is users should be informed
   */
  boolean informAfterTransaction();

  /**
   * Gets the amount applicable for the given {@code player} and the given {@code feeType}.
   *
   * @param player  the player
   * @param feeType the fee type
   * @return the amount
   */
  BigDecimal getFee(LocalPlayer player, FeeType feeType);

  /**
   * Returns whether the given {@code player} has at least the given {@code amount}.
   *
   * @param player the player
   * @param amount the amount
   * @return {@code true} if the player has at least the given amount
   */
  boolean hasAtLeast(LocalPlayer player, BigDecimal amount);

  /**
   * Withdraws the given {@code player} with the given {@code amount}.
   *
   * @param player the player
   * @param amount the amount
   * @return the withdrawn amount
   */
  BigDecimal withdraw(LocalPlayer player, BigDecimal amount);

}
