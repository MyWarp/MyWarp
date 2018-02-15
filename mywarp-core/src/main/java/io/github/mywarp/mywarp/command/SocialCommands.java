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

package io.github.mywarp.mywarp.command;

import com.sk89q.intake.Command;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Switch;
import com.sk89q.intake.util.auth.AuthorizationException;

import io.github.mywarp.mywarp.command.parametric.annotation.Billable;
import io.github.mywarp.mywarp.command.parametric.annotation.Modifiable;
import io.github.mywarp.mywarp.command.parametric.provider.exception.NoSuchPlayerException;
import io.github.mywarp.mywarp.command.util.CommandUtil;
import io.github.mywarp.mywarp.command.util.ExceedsInitiatorLimitException;
import io.github.mywarp.mywarp.command.util.ExceedsLimitException;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.service.economy.FeeType;
import io.github.mywarp.mywarp.service.limit.LimitService;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.Message.Style;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.playermatcher.GroupPlayerMatcher;
import io.github.mywarp.mywarp.util.playermatcher.PlayerMatcher;
import io.github.mywarp.mywarp.util.playermatcher.UuidPlayerMatcher;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.Warp.Type;

import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Bundles commands that involve social interaction with other players.
 */
public final class SocialCommands {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final Game game;
  private final PlayerNameResolver playerNameResolver;
  @Nullable
  private final LimitService limitService;

  /**
   * Creates an instance.
   *
   * @param game               the Game instance used by commands
   * @param playerNameResolver the PlayerNameResolver used by commands
   * @param limitService       the LimitService used by commands - may be {@code null} if no limit service is used
   */
  SocialCommands(Game game, PlayerNameResolver playerNameResolver, @Nullable LimitService limitService) {
    this.game = game;
    this.playerNameResolver = playerNameResolver;
    this.limitService = limitService;
  }

  @Command(aliases = {"give"}, desc = "give.description", help = "give.help")
  @Require("mywarp.cmd.give")
  @Billable(FeeType.GIVE)
  public void give(Actor actor, @Switch('d') boolean giveDirectly, @Switch('f') boolean ignoreLimits, UUID receiver,
                   @Modifiable Warp warp) throws CommandException, AuthorizationException, NoSuchPlayerException {

    Optional<LocalPlayer> receiverPlayerOptional = game.getPlayer(receiver);
    if (warp.isCreator(receiver)) {
      throw new CommandException(
          msg.getString("give.is-owner", receiverPlayerOptional.map(Actor::getName).orElse(receiver.toString())));
    }

    if (!ignoreLimits && limitService != null) {
      if (!receiverPlayerOptional.isPresent()) {
        throw new NoSuchPlayerException(receiver, playerNameResolver);
      }
      LocalPlayer receiverPlayer = receiverPlayerOptional.get();

      LimitService.EvaluationResult
          result =
          limitService.canAdd(receiverPlayer, CommandUtil.toWorld(warp, game), warp.getType());
      if (result.exceedsLimit()) {
        throw new ExceedsLimitException(receiverPlayer);
      }
    } else if (!actor.hasPermission("mywarp.cmd.give.force")) {
      throw new AuthorizationException();
    }

    if (giveDirectly) {
      if (!actor.hasPermission("mywarp.cmd.give.direct")) {
        throw new AuthorizationException();
      }

      warp.setCreator(receiver);

      actor.sendMessage(
          msg.getString("give.given-successful", warp.getName(), CommandUtil.toName(receiver, playerNameResolver)));

      receiverPlayerOptional.ifPresent(
          localPlayer -> localPlayer.sendMessage(msg.getString("give.givee-owner", actor.getName(), warp.getName())));
      return;
    }

    if (!receiverPlayerOptional.isPresent()) {
      throw new NoSuchPlayerException(receiver, playerNameResolver);
    }
    receiverPlayerOptional.get().initiateAcceptanceConversation(actor, warp);
    actor.sendMessage(msg.getString("give.asked-successful", receiverPlayerOptional.get().getName(), warp.getName()));

  }

  @Command(aliases = {"private"}, desc = "private.description", help = "private.help")
  @Require("mywarp.cmd.private")
  @Billable(FeeType.PRIVATE)
  public void privatize(Actor actor, @Switch('f') boolean ignoreLimits, @Modifiable Warp warp)
      throws CommandException, AuthorizationException, NoSuchPlayerException {
    if (warp.isType(Type.PRIVATE)) {
      throw new CommandException(msg.getString("private.already-private", warp.getName()));
    }
    if (!ignoreLimits && limitService != null) {
      UUID creator = warp.getCreator();
      Optional<LocalPlayer> creatorPlayerOptional = game.getPlayer(creator);
      if (!creatorPlayerOptional.isPresent()) {
        throw new NoSuchPlayerException(creator, playerNameResolver);
      }
      LocalPlayer creatorPlayer = creatorPlayerOptional.get();

      LimitService.EvaluationResult
          result =
          limitService.canChangeType(creatorPlayer, CommandUtil.toWorld(warp, game), warp.getType(), Type.PRIVATE);

      if (result.exceedsLimit()) {
        if (actor instanceof LocalPlayer && creatorPlayer.equals(actor)) {
          throw new ExceedsInitiatorLimitException(result.getExceededValue(), result.getAllowedMaximum());
        } else {
          throw new ExceedsLimitException(creatorPlayer);
        }
      }

    } else if (!actor.hasPermission("mywarp.cmd.private.force")) {
      throw new AuthorizationException();
    }

    warp.setType(Type.PRIVATE);
    actor.sendMessage(msg.getString("private.privatized", warp.getName()));
  }


  @Command(aliases = {"public"}, desc = "public.description", help = "public.help")
  @Require("mywarp.cmd.public")
  @Billable(FeeType.PUBLIC)
  public void publicize(Actor actor, @Switch('f') boolean ignoreLimits, @Modifiable Warp warp)
      throws CommandException, AuthorizationException, NoSuchPlayerException {
    if (warp.isType(Type.PUBLIC)) {
      throw new CommandException(msg.getString("public.already-public", warp.getName()));
    }
    if (!ignoreLimits && limitService != null) {
      UUID creator = warp.getCreator();
      Optional<LocalPlayer> creatorPlayerOptional = game.getPlayer(creator);
      if (!creatorPlayerOptional.isPresent()) {
        throw new NoSuchPlayerException(creator, playerNameResolver);
      }
      LocalPlayer creatorPlayer = creatorPlayerOptional.get();

      LimitService.EvaluationResult
          result =
          limitService.canChangeType(creatorPlayer, CommandUtil.toWorld(warp, game), warp.getType(), Type.PUBLIC);

      if (result.exceedsLimit()) {
        if (actor instanceof LocalPlayer && creatorPlayer.equals(actor)) {
          throw new ExceedsInitiatorLimitException(result.getExceededValue(), result.getAllowedMaximum());
        } else {
          throw new ExceedsLimitException(creatorPlayer);
        }
      }

    } else if (!actor.hasPermission("mywarp.cmd.public.force")) {
      throw new AuthorizationException();
    }
    warp.setType(Type.PUBLIC);
    actor.sendMessage(msg.getString("public.publicized", warp.getName()));
  }

  @Command(aliases = {"invite"}, desc = "invite.description", help = "invite.help")
  @Require("mywarp.cmd.invite")
  @Billable(FeeType.INVITE)
  public void invite(Actor actor, @Require("mywarp.cmd.invite.group") PlayerMatcher invitation, @Modifiable Warp warp)
      throws CommandException {
    if (warp.hasInvitation(invitation)) {
      throw new CommandException(msg.getString("invite.already-invited", parse(invitation),
                                               CommandUtil.toName(invitation, playerNameResolver)));
    }
    if (invitation instanceof UuidPlayerMatcher && warp.isCreator(((UuidPlayerMatcher) invitation).getCriteria())) {
      throw new CommandException(
          msg.getString("invite.is-creator", CommandUtil.toName(warp.getCreator(), playerNameResolver)));
    }
    warp.addInvitation(invitation);

    actor.sendMessage(
        msg.getString("invite.successful", parse(invitation), CommandUtil.toName(invitation, playerNameResolver),
                      warp.getName()));
    if (warp.isType(Type.PUBLIC)) {
      actor.sendMessage(
          Message.builder().append(Style.INFO).append(msg.getString("invite.public", warp.getName())).build());
    }
  }

  @Command(aliases = {"uninvite"}, desc = "uninvite.description", help = "uninvite.help")
  @Require("mywarp.cmd.uninvite")
  @Billable(FeeType.UNINVITE)
  public void uninvite(Actor actor, @Require("mywarp.cmd.uninvite.group") PlayerMatcher invitation,
                       @Modifiable Warp warp) throws CommandException {
    if (!warp.hasInvitation(invitation)) {
      throw new CommandException(
          msg.getString("uninvite.not-invited", parse(invitation), CommandUtil.toName(invitation, playerNameResolver)));
    }
    warp.removeInvitation(invitation);

    actor.sendMessage(
        msg.getString("uninvite.successful", parse(invitation), CommandUtil.toName(invitation, playerNameResolver),
                      warp.getName()));
    if (warp.isType(Type.PUBLIC)) {
      actor.sendMessage(
          Message.builder().append(Style.INFO).append(msg.getString("uninvite.public", warp.getName())).build());
    }
  }

  private int parse(PlayerMatcher invitation) {
    if (invitation instanceof UuidPlayerMatcher) {
      return 0;
    }
    if (invitation instanceof GroupPlayerMatcher) {
      return 1;
    }
    return -1;
  }

}
