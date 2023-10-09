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

package io.github.mywarp.mywarp.command;

import com.sk89q.intake.Command;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.Switch;
import com.sk89q.intake.util.auth.AuthorizationException;
import io.github.mywarp.mywarp.command.parametric.annotation.Billable;
import io.github.mywarp.mywarp.command.parametric.annotation.Modifiable;
import io.github.mywarp.mywarp.command.parametric.provider.exception.ArgumentAuthorizationException;
import io.github.mywarp.mywarp.command.parametric.provider.exception.NoSuchPlayerException;
import io.github.mywarp.mywarp.command.util.*;
import io.github.mywarp.mywarp.platform.*;
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

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Bundles commands that involve social interaction with other players.
 */
public final class SocialCommands {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final Game game;
  private final PlayerNameResolver nameResolver;
  @Nullable
  private final LimitService limitService;

  /**
   * Creates an instance.
   *
   * @param game         the Game instance used by commands
   * @param nameResolver the PlayerNameResolver used by commands
   * @param limitService the LimitService used by commands - may be {@code null} if no limit service is used
   */
  SocialCommands(Game game, PlayerNameResolver nameResolver, @Nullable LimitService limitService) {
    this.game = game;
    this.nameResolver = nameResolver;
    this.limitService = limitService;
  }

  private static int parse(PlayerMatcher invitation) {
    if (invitation instanceof UuidPlayerMatcher) {
      return 0;
    }
    if (invitation instanceof GroupPlayerMatcher) {
      return 1;
    }
    return -1;
  }

  private static String toName(PlayerMatcher invitation) {
    if (invitation instanceof UuidPlayerMatcher) {
      return ((UuidPlayerMatcher) invitation).getCriteria().toString();
    }
    if (invitation instanceof GroupPlayerMatcher) {
      return ((GroupPlayerMatcher) invitation).getCriteria();
    }
    return invitation.toString();
  }

  @Command(aliases = {"give"}, desc = "give.description", help = "give.help")
  @Require("mywarp.cmd.give")
  @Billable(FeeType.GIVE)
  public void give(Actor actor, @Switch('d') boolean giveDirectly, @Switch('f') boolean ignoreLimits,
      CompletableFuture<Profile> receiverFuture, @Modifiable Warp warp) {

    receiverFuture.thenAcceptAsync(receiverProfile -> {

      UUID receiverId = receiverProfile.getUuid();

      if (warp.isCreator(receiverId)) {
        actor.sendError(msg.getString("give.is-owner", receiverProfile.getNameOrId()));
        return;
      }

      Optional<LocalPlayer> receiverPlayerOptional = game.getPlayer(receiverProfile.getUuid());
      if (!ignoreLimits && limitService != null) {
        if (!receiverPlayerOptional.isPresent()) {
          actor.sendError(new NoSuchPlayerException(receiverProfile));
          return;
        }
        LocalPlayer receiverPlayer = receiverPlayerOptional.get();

        LimitService.EvaluationResult result;
        try {
          result = limitService.canAdd(receiverPlayer, CommandUtil.toWorld(warp, game), warp.getType());
        } catch (NoSuchWorldException e) {
          actor.sendError(e);
          return;
        }
        if (result.exceedsLimit()) {
          actor.sendError(new ExceedsLimitException(receiverPlayer));
          return;
        }
      } else if (!actor.hasPermission("mywarp.cmd.give.force")) {
        actor.sendError(new ArgumentAuthorizationException());
        return;
      }

      if (giveDirectly) {
        if (!actor.hasPermission("mywarp.cmd.give.direct")) {
          actor.sendError(new ArgumentAuthorizationException());
          return;
        }

        warp.setCreator(receiverId);

        actor.sendMessage(msg.getString("give.given-successful", warp.getName(), receiverProfile.getNameOrId()));

        receiverPlayerOptional.ifPresent(
            localPlayer -> localPlayer.sendMessage(msg.getString("give.givee-owner", actor.getName(), warp.getName())));
        return;
      }

      if (!receiverPlayerOptional.isPresent()) {
        actor.sendError(new NoSuchPlayerException(receiverProfile));
        return;
      }
      receiverPlayerOptional.get().initiateAcceptanceConversation(actor, warp);
      actor.sendMessage(msg.getString("give.asked-successful", receiverPlayerOptional.get().getName(), warp.getName()));

    }, game.getExecutor());


  }

  @Command(aliases = {"private"}, desc = "private.description", help = "private.help")
  @Require("mywarp.cmd.private")
  @Billable(FeeType.PRIVATE)
  public void privatize(Actor actor, @Switch('f') boolean ignoreLimits, @Modifiable Warp warp)
      throws CommandException, AuthorizationException {
    if (warp.isType(Type.PRIVATE)) {
      throw new CommandException(msg.getString("private.already-private", warp.getName()));
    }
    if (!ignoreLimits && limitService != null) {
      UUID creator = warp.getCreator();
      Optional<LocalPlayer> creatorPlayerOptional = game.getPlayer(creator);
      if (!creatorPlayerOptional.isPresent()) {
        actor.sendError(new NoSuchPlayerException(creator));
        return;
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
        throw new NoSuchPlayerException(creator);
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
  public void invite(Actor actor, @Require("mywarp.cmd.invite.group") CompletableFuture<Invitation> invitationFuture,
                     @Modifiable Warp warp) {
    nameResolver.getByUniqueId(warp.getCreator()).thenApply(Profile::getNameOrId)
            .thenAcceptBothAsync(invitationFuture, (creatorName, invitation) -> {
              PlayerMatcher matcher = invitation.getMatcher();
              if (!warp.hasInvitation(matcher)) {
                if (matcher instanceof UuidPlayerMatcher && warp
                        .isCreator(((UuidPlayerMatcher) matcher).getCriteria())) {
                  actor.sendError(msg.getString("invite.is-creator", creatorName));
                }
                warp.addInvitation(matcher);

                actor
                        .sendMessage(msg.getString("invite.successful", parse(matcher), invitation.getIdentifier(), warp.getName()));
                if (warp.isType(Type.PUBLIC)) {
                  actor.sendMessage(Message.of(Style.INFO, msg.getString("invite.public", warp.getName())));
                }
              } else {
                actor.sendError(msg.getString("invite.already-invited", parse(matcher), invitation.getIdentifier()));
          }
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

  @Command(aliases = {"uninvite"}, desc = "uninvite.description", help = "uninvite.help")
  @Require("mywarp.cmd.uninvite")
  @Billable(FeeType.UNINVITE)
  public void uninvite(Actor actor,
                       @Require("mywarp.cmd.uninvite.group") CompletableFuture<Invitation> invitationFuture,
                       @Modifiable Warp warp) {
    nameResolver.getByUniqueId(warp.getCreator()).thenApply(Profile::getNameOrId)
        .thenAcceptBothAsync(invitationFuture, (creatorName, invitation) -> {
          PlayerMatcher matcher = invitation.getMatcher();
          if (warp.hasInvitation(matcher)) {
            warp.removeInvitation(matcher);

            actor.sendMessage(
                    msg.getString("uninvite.successful", parse(matcher), invitation.getIdentifier(), warp.getName()));
            if (warp.isType(Type.PUBLIC)) {
              actor.sendMessage(Message.of(Style.INFO, msg.getString("uninvite.public", warp.getName())));
            }
          } else {
            actor.sendError(msg.getString("uninvite.not-invited", parse(matcher), invitation.getIdentifier()));
          }
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

}
