/*
 * Copyright (C) 2011 - 2020, MyWarp team and contributors
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

package io.github.mywarp.mywarp.service.economy;

import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.Platform;
import io.github.mywarp.mywarp.platform.capability.EconomyCapability;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import java.math.BigDecimal;

/**
 * Handles economic tasks.
 *
 * <p>The economy is supplied by the {@link Platform} via an implemented {@link
 * EconomyCapability}.</p>
 */
public class EconomyService {

  private static final DynamicMessages msg = new DynamicMessages("io.github.mywarp.mywarp.lang.Economy");

  private final EconomyCapability capability;

  /**
   * Creates an instance that uses the given {@code capability} to resolve requests.
   *
   * @param capability the capability
   */
  public EconomyService(EconomyCapability capability) {
    this.capability = capability;
  }

  /**
   * Returns whether the given {@code player} has at least the amount identified by the given {@code fee}.
   *
   * @param player the player
   * @param fee    the fee
   * @return {@code true} if the player has at lest the amount
   */
  public boolean hasAtLeast(LocalPlayer player, FeeType fee) {
    if (canDisobeyFees(player)) {
      return true;
    }
    BigDecimal amount = capability.getFee(player, fee);
    if (amount.signum() != 1) {
      return true;
    }
    boolean has = capability.hasAtLeast(player, amount);

    if (!has) {
      player.sendError(msg.getString("transaction.not-affordable", amount));
    }
    return has;
  }

  /**
   * Withdraws the amount identified by the given {@code fee} from the given {@code player} and returns the amount that
   * was actually withdrawn.
   *
   * @param player the player
   * @param fee    the fee
   * @return the amount that was actually withdrawn
   */
  public BigDecimal withdraw(LocalPlayer player, FeeType fee) {
    if (canDisobeyFees(player)) {
      return BigDecimal.ZERO;
    }
    BigDecimal amount = capability.getFee(player, fee);
    if (amount.signum() != 1) {
      return BigDecimal.ZERO;
    }
    amount = capability.withdraw(player, capability.getFee(player, fee));

    if (capability.informAfterTransaction()) {
      player.sendMessage(msg.getString("transaction.complete", amount));
    }
    return amount;
  }

  private boolean canDisobeyFees(LocalPlayer player) {
    return player.hasPermission("mywarp.economy.disobey");
  }
}
