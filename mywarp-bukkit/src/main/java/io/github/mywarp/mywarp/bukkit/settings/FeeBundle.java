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

package io.github.mywarp.mywarp.bukkit.settings;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.math.BigDecimal.valueOf;

import io.github.mywarp.mywarp.bukkit.util.permission.ValueBundle;
import io.github.mywarp.mywarp.service.economy.FeeType;

import org.bukkit.configuration.ConfigurationSection;

import java.math.BigDecimal;
import java.util.EnumMap;

/**
 * A ValueBundle that bundles fees.
 */
public class FeeBundle extends ValueBundle {

  private EnumMap<FeeType, BigDecimal> fees;

  /**
   * Creates a new bundle with the given {@code identifier} and the given {@code values}.
   *
   * <p>Individual fees are read from {@code values}. Non existing entries are read as {@code 0}.</p>
   *
   * @param identifier the bundle's identifier
   * @param values     the bundle's values
   */
  static FeeBundle create(String identifier, ConfigurationSection values) {
    checkNotNull(identifier);
    checkNotNull(values);

    EnumMap<FeeType, BigDecimal> fees = new EnumMap<FeeType, BigDecimal>(FeeType.class);
    fees.put(FeeType.ASSETS, valueOf(values.getDouble("assets")));
    fees.put(FeeType.CREATE, valueOf(values.getDouble("create")));
    fees.put(FeeType.CREATE_PRIVATE, valueOf(values.getDouble("createPrivate")));
    fees.put(FeeType.DELETE, valueOf(values.getDouble("delete")));
    fees.put(FeeType.GIVE, valueOf(values.getDouble("give")));
    fees.put(FeeType.HELP, valueOf(values.getDouble("help")));
    fees.put(FeeType.INFO, valueOf(values.getDouble("info")));
    fees.put(FeeType.INVITE, valueOf(values.getDouble("invite")));
    fees.put(FeeType.LIST, valueOf(values.getDouble("list")));
    fees.put(FeeType.POINT, valueOf(values.getDouble("point")));
    fees.put(FeeType.PRIVATE, valueOf(values.getDouble("private")));
    fees.put(FeeType.PUBLIC, valueOf(values.getDouble("public")));
    fees.put(FeeType.UNINVITE, valueOf(values.getDouble("uninvite")));
    fees.put(FeeType.UPDATE, valueOf(values.getDouble("update")));
    fees.put(FeeType.WARP_PLAYER, valueOf(values.getDouble("warpPlayer")));
    fees.put(FeeType.WARP_SIGN_CREATE, valueOf(values.getDouble("warpSignCreate")));
    fees.put(FeeType.WARP_SIGN_USE, valueOf(values.getDouble("warpSignUse")));
    fees.put(FeeType.WARP_TO, valueOf(values.getDouble("warpTo")));
    fees.put(FeeType.WELCOME, valueOf(values.getDouble("welcome")));

    return new FeeBundle(identifier, fees);
  }

  private FeeBundle(String identifier, EnumMap<FeeType, BigDecimal> fees) {
    super(identifier, "mywarp.economy");
    this.fees = fees;
  }

  /**
   * Gets the fee referenced by the given FeeType.
   *
   * @param type the FeeType
   * @return the fee
   */
  public BigDecimal get(FeeType type) {
    return fees.get(type);
  }
}
