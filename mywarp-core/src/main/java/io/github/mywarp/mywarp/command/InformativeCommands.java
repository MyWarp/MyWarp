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

package io.github.mywarp.mywarp.command;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Ordering;
import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.OptArg;
import com.sk89q.intake.parametric.annotation.Range;
import com.sk89q.intake.parametric.annotation.Switch;
import com.sk89q.intake.util.auth.AuthorizationException;
import io.github.mywarp.mywarp.command.parametric.annotation.Billable;
import io.github.mywarp.mywarp.command.parametric.annotation.Viewable;
import io.github.mywarp.mywarp.command.parametric.namespace.IllegalCommandSenderException;
import io.github.mywarp.mywarp.command.util.CommandUtil;
import io.github.mywarp.mywarp.command.util.UnknownException;
import io.github.mywarp.mywarp.command.util.UserViewableException;
import io.github.mywarp.mywarp.command.util.printer.AssetsPrinter;
import io.github.mywarp.mywarp.command.util.printer.InfoPrinter;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.Platform;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.platform.Profile;
import io.github.mywarp.mywarp.service.economy.FeeType;
import io.github.mywarp.mywarp.service.limit.LimitService;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;
import io.github.mywarp.mywarp.warp.authorization.AuthorizationResolver;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

/**
 * Bundles commands that provide information about existing Warps.
 */
public final class InformativeCommands {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final AuthorizationResolver authorizationResolver;
  private final WarpManager warpManager;
  private final Game game;
  private final PlayerNameResolver playerNameResolver;
  private final Platform platform;
  @Nullable
  private final LimitService limitService;

  /**
   * Creates an instance.
   *
   * @param warpManager           the WarpManager used by commands
   * @param limitService          the LimitService used by commands - may be {@code null} if no limit service is used
   * @param authorizationResolver the AuthorizationResolver used by commands
   * @param platform              the Platform instance to be used in commands
   * @param playerNameResolver    the PlayerNameResolver used by commands
   * @param game                  the Game used by commands
   */
  InformativeCommands(WarpManager warpManager, @Nullable LimitService limitService,
      AuthorizationResolver authorizationResolver, Platform platform,
      PlayerNameResolver playerNameResolver, Game game) {
    this.authorizationResolver = authorizationResolver;
    this.warpManager = warpManager;
    this.platform = platform;
    this.game = game;
    this.limitService = limitService;
    this.playerNameResolver = playerNameResolver;
  }

  @Command(aliases = {"assets", "limits"}, desc = "assets.description", help = "assets.help")
  @Require("mywarp.cmd.assets.self")
  @Billable(FeeType.ASSETS)
  public void assets(Actor actor, @OptArg LocalPlayer creator)
      throws IllegalCommandSenderException, AuthorizationException {
    if (creator == null) {
      if (actor instanceof LocalPlayer) {
        creator = (LocalPlayer) actor;
      } else {
        throw new IllegalCommandSenderException(actor);
      }
    } else if (!actor.hasPermission("mywarp.cmd.assets")) {
      throw new AuthorizationException();
    }

    AssetsPrinter printer;
    if (limitService != null) {
      printer = AssetsPrinter.create(creator, limitService);
    } else {
      printer = AssetsPrinter.create(creator, game, warpManager);
    }
    printer.print(actor);
  }

  @Command(aliases = {"list", "alist"}, desc = "list.description", help = "list.help")
  @Require("mywarp.cmd.list")
  @Billable(FeeType.LIST)
  public void list(final Actor actor, @OptArg("1") int page, @Switch('c') CompletableFuture<Profile> creatorFuture,
      @Switch('n') final String name, @Switch('o') final Comparator<Warp> comparator,
      @Switch('r') @Range(min = 1, max = Integer.MAX_VALUE) final Integer radius,
      @Switch('w') final String world) {

    // build the filter Predicate
    if (creatorFuture == null) {
      creatorFuture = CompletableFuture.completedFuture(null);
    }
    CompletableFuture<Predicate<Warp>> filterFuture = creatorFuture.thenApplyAsync(creator -> {
      Predicate<Warp> filter = authorizationResolver.isViewable(actor);
      if (creator != null) {
        filter = filter.and(w -> w.isCreator(creator.getUuid()));
      }
      if (name != null) {
        filter = filter.and(input -> CommandUtil.containsIgnoreCase(input.getName(), name));
      }
      if (radius != null) {
        if (!(actor instanceof LocalEntity)) {
          actor.sendError(new IllegalCommandSenderException(actor));
          return warp -> false;
        }

        LocalEntity entity = (LocalEntity) actor;

        final UUID worldId = entity.getWorld().getUniqueId();

        final int squaredRadius = radius * radius;
        final Vector3d position = entity.getPosition();
        filter =
            filter.and(input -> input.getWorldIdentifier().equals(worldId)
                && input.getPosition().distanceSquared(position) <= squaredRadius);
      }

      if (world != null) {
        filter = filter.and(input -> {
          Optional<LocalWorld> worldOptional = game.getWorld(input.getWorldIdentifier());
          return worldOptional.isPresent() && CommandUtil.containsIgnoreCase(worldOptional.get().getName(), world);
        });
      }
      return filter;
    }, game.getExecutor());

    Ordering<Warp>
        ordering =
        Ordering.from(comparator != null ? comparator : platform.getSettings().getDefaultListComparator());

    // query all matching warps
    CompletableFuture<List<Warp>>
        warpsFuture = filterFuture.thenApply(filter -> ordering.sortedCopy(warpManager.getAll(filter)));

    // build the list of creator names
    CompletableFuture<Map<UUID, String>>
        creatorsFuture =
        warpsFuture.thenApply(warps -> warps.stream().map(Warp::getCreator).collect(Collectors.toSet()))
            .thenCompose(playerNameResolver::getByUniqueId)
            .thenApply(set -> set.stream().collect(Collectors.toMap(Profile::getUuid, Profile::getNameOrId)));

    //convert to messages
    creatorsFuture.thenAcceptBothAsync(warpsFuture, (creators, warps) -> {

      List<Message> messages = warps.stream().map(warp -> {
        Message.Builder builder = Message.builder();
        builder.append("'");
        builder.append(warp);
        builder.append("' (");
        builder.append(CommandUtil.toWorldName(warp.getWorldIdentifier(), game));
        builder.append(") ");
        builder.append(msg.getString("list.by"));
        builder.append(" ");

        if (actor instanceof LocalPlayer && warp.isCreator(((LocalPlayer) actor).getUniqueId())) {
          builder.append(msg.getString("list.you"));
        } else {
          builder.append(creators.get(warp.getCreator()));
        }
        return builder.build();
      }).collect(Collectors.toList());

      platform.createPaginatedContentBuilder().withHeading(msg.getString("list.heading")).build(messages)
          .display(actor, page);

    }, game.getExecutor()).exceptionally((ex) -> {
      UserViewableException userViewableException;
      if (ex.getCause() instanceof UserViewableException) {
        userViewableException = (UserViewableException) ex.getCause();
      } else {
        userViewableException = new UnknownException(ex);
      }
      actor.sendError(userViewableException);
      return null;
    });
  }

  @Command(aliases = {"info", "stats"}, desc = "info.description", help = "info.help")
  @Require("mywarp.cmd.info")
  @Billable(FeeType.INFO)
  public void info(Actor actor, @Viewable Warp warp) {
    new InfoPrinter(warp, authorizationResolver, game, playerNameResolver).print(actor);
  }
}
