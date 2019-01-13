/*
 * Copyright (C) 2011 - 2019, MyWarp team and contributors
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

package io.github.mywarp.mywarp.sign;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Iterables;

import io.github.mywarp.mywarp.MyWarp;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.Sign;
import io.github.mywarp.mywarp.platform.capability.EconomyCapability;
import io.github.mywarp.mywarp.service.economy.EconomyService;
import io.github.mywarp.mywarp.service.economy.FeeType;
import io.github.mywarp.mywarp.service.teleport.EconomyTeleportService;
import io.github.mywarp.mywarp.service.teleport.HandlerTeleportService;
import io.github.mywarp.mywarp.service.teleport.TeleportService;
import io.github.mywarp.mywarp.util.BlockFace;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.i18n.LocaleManager;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;
import io.github.mywarp.mywarp.warp.authorization.AuthorizationResolver;

import java.util.Collection;
import java.util.Optional;
import java.util.TreeSet;

import javax.annotation.Nullable;

/**
 * Handles interaction with warp signs.
 *
 * <p>A warp sign is a sign that has an identifier (e.g. 'MyWarp') enclosed by brackets in the second and the name of
 * an existing warp in the third line. If a player interacts with a sign by clicking it, he is teleported to the warp
 * specified on the sign, if he meets certain conditions.</p>
 *
 * <p>As of itself this class does nothing. It must be feat by a event system that tracks creation and clinking on
 * signs.</p>
 */
public class WarpSignHandler {

  private static final int WARPNAME_LINE = 2;
  private static final int IDENTIFIER_LINE = 1;

  private static final DynamicMessages msg = new DynamicMessages("io.github.mywarp.mywarp.lang.WarpSigns");

  private final TreeSet<String> identifiers = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
  private final boolean caseSensitiveWarpNames;
  private final AuthorizationResolver authorizationResolver;
  private final WarpManager warpManager;
  private final TeleportService teleportService;

  @Nullable
  private final EconomyService economyService;

  /**
   * Creates a new WarpSignHandler that identifies warp signs with the given iterable of identifiers.
   *
   * @param identifiers       the identifiers of warp signs
   * @param myWarp            the MyWarp instance
   * @param economyCapability the EconomyCapability used by this instance - can be null if no economy should be used
   */
  public WarpSignHandler(Iterable<String> identifiers, MyWarp myWarp, @Nullable EconomyCapability economyCapability) {
    this(identifiers, myWarp.getSettings().isCaseSensitiveWarpNames(), myWarp.getAuthorizationResolver(),
         createEconomyService(economyCapability), createTeleportService(myWarp, economyCapability),
         myWarp.getWarpManager());
  }

  private WarpSignHandler(Iterable<String> identifiers, boolean caseSensitiveWarpNames,
                          AuthorizationResolver authorizationResolver, @Nullable EconomyService economyService,
                          TeleportService teleportService, WarpManager warpManager) {
    Iterables.addAll(this.identifiers, identifiers);
    this.caseSensitiveWarpNames = caseSensitiveWarpNames;
    this.authorizationResolver = authorizationResolver;
    this.economyService = economyService;
    this.teleportService = teleportService;
    this.warpManager = warpManager;
  }

  /**
   * Handles the creation of the given {@code sign} by the given {@code player}. Returns an Optional with the result of
   * this creation: {@code true} if the sign is a warp sign and could be created, {@code false} if the sign is a warp
   * sign but could not be created, {@code Optional.absent()} if the sign is not a warp sign.
   *
   * <p>If the sign is a warp sign, the player has the permission to create warp signs, the warp given on the warp sign
   * exists and the player may create warp signs to it, the sign may be created. If any of this conditions is not met,
   * the sign may not be created and the player is informed (if appropriate).</p>
   *
   * @param player the player who created the sign
   * @param sign   the created sign
   * @return the result
   */
  public Optional<Boolean> handleSignCreation(LocalPlayer player, Sign sign) {
    if (!isWarpSign(sign)) {
      return Optional.empty();
    }

    //validate permission
    LocaleManager.setLocale(player.getLocale());
    if (!player.hasPermission("mywarp.sign.create.self")) {
      player.sendError(msg.getString("permission.create"));
      return Optional.of(false);
    }
    String name = sign.getLine(WARPNAME_LINE);
    Optional<Warp> optional = getWarp(name);

    //validate warp existence
    if (!optional.isPresent()) {
      player.sendError(msg.getString("warp-non-existent", name));
      return Optional.of(false);
    }
    Warp warp = optional.get();

    //validate authorization
    if (!authorizationResolver.isModifiable(warp, player) && !player.hasPermission("mywarp.sign.create")) {
      player.sendError(msg.getString("permission.create.to-warp", name));
      return Optional.of(false);
    }

    //validate economy
    if (economyService != null) {
      if (!economyService.hasAtLeast(player, FeeType.WARP_SIGN_CREATE)) {
        return Optional.of(false);
      }
      economyService.withdraw(player, FeeType.WARP_SIGN_CREATE);
    }

    // get the right spelling (case) out of the config
    String line = sign.getLine(IDENTIFIER_LINE);
    line = line.substring(1, line.length() - 1);
    sign.setLine(IDENTIFIER_LINE, "[" + identifiers.ceiling(line) + "]");
    sign.setLine(WARPNAME_LINE, warp.getName());

    player.sendMessage(msg.getString("created-successful"));
    return Optional.of(true);
  }

  @Nullable
  private static EconomyService createEconomyService(@Nullable EconomyCapability economyCapability) {
    if (economyCapability != null) {
      return new EconomyService(economyCapability);
    }
    return null;
  }

  private static TeleportService createTeleportService(MyWarp myWarp, @Nullable EconomyCapability economyCapability) {
    TeleportService ret = new HandlerTeleportService(myWarp.getTeleportHandler());

    if (economyCapability != null) {
      ret = new EconomyTeleportService(ret, createEconomyService(economyCapability), FeeType.WARP_SIGN_USE);
    }
    return ret;
  }

  /**
   * Handles the interaction of the given {@code player} with the given {@code sign}. Returns {@code true} if and only
   * if the sign is a valid warp sign.
   *
   * <p>If the sign is a warp sign, the player has the permission to use warp signs, the warp given on the warp sign
   * exists and is usable by the player, he is teleported there. If any of this conditions is not met, the handling is
   * aborted and the player is informed (if appropriate).</p>
   *
   * <p>Typically an interaction is a right click.</p>
   *
   * @param player the player who interacted with the the sign
   * @param sign   the sign interacted with
   * @return {@code true} if the sign is a warp sign
   */
  public boolean handleInteraction(LocalPlayer player, Sign sign) {
    if (!isWarpSign(sign)) {
      return false;
    }
    LocaleManager.setLocale(player.getLocale());
    if (!player.hasPermission("mywarp.sign.use")) {
      player.sendError(msg.getString("permission.use"));
      return true;
    }

    String warpName = sign.getLine(WARPNAME_LINE);

    Optional<Warp> optional = getWarp(warpName);
    if (!optional.isPresent()) {
      player.sendError(msg.getString("warp-non-existent", warpName));
      return true;
    }
    final Warp warp = optional.get();

    if (!authorizationResolver.isUsable(warp, player)) {
      player.sendError(msg.getString("permission.use.to-warp", warpName));
      return true;
    }

    teleportService.teleport(player, warp);
    return true;
  }

  /**
   * Handles the interaction of the given {@code player} with the given {@code blockFace} of the block at the given
   * {@code position}.
   *
   * <p>If position and block face can be traced back to a warp sign, the player has the permission to use warp signs,
   * the warp given on the warp sign exists and is usable by the player, he is teleported there. If any of this
   * conditions is not met, the handling is aborted and the player is informed (if appropriate).</p>
   *
   * <p>Typically an interaction is a right click.</p>
   *
   * @param player    the player who interacted
   * @param position  the position of the interaction
   * @param blockFace the blockFace of the interaction
   * @return {@code true} if the sign is a warp sign
   */
  public boolean handleInteraction(LocalPlayer player, Vector3i position, BlockFace blockFace) {
    LocalWorld world = player.getWorld();
    Optional<Sign> sign;

    switch (blockFace) {
      case NORTH:
      case EAST:
      case SOUTH:
      case WEST:
        sign = world.getAttachedSign(position.add(blockFace.getVector().mul(2)), blockFace.getOpposite());
        break;
      case UP:
      case DOWN:
        sign = world.getSign(position.sub(blockFace.getVector().mul(2)));
        break;
      default:
        sign = world.getSign(position);
    }

    return !sign.isPresent() || handleInteraction(player, sign.get());
  }

  private Optional<Warp> getWarp(String warpName) {
    if (caseSensitiveWarpNames) {
      return warpManager.getByName(warpName);
    }
    Collection<Warp> warps = warpManager.getAll(w -> w.getName().equalsIgnoreCase(warpName));
    if (warps.size() == 1) {
      return Optional.of(warps.iterator().next());
    }
    return Optional.empty();
  }

  private boolean isWarpSign(Sign sign) {
    String identifier = sign.getLine(IDENTIFIER_LINE);

    if (!(identifier.startsWith("[") && identifier.endsWith("]"))) {
      return false;
    }
    identifier = identifier.substring(1, identifier.length() - 1);
    return identifiers.contains(identifier);
  }
}
