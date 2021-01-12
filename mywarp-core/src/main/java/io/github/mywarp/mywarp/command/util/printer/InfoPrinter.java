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

package io.github.mywarp.mywarp.command.util.printer;

import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.platform.Profile;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.WarpUtils;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.i18n.LocaleManager;
import io.github.mywarp.mywarp.util.playermatcher.GroupPlayerMatcher;
import io.github.mywarp.mywarp.util.playermatcher.UuidPlayerMatcher;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.authorization.AuthorizationResolver;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * Prints information about a certain Warp.
 */
public class InfoPrinter {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);
  private static final DateTimeFormatter
      TIME_FORMATTER =
      DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());

  private final Warp warp;
  private final AuthorizationResolver authorizationResolver;
  private final Game game;
  private final PlayerNameResolver playerNameResolver;

  /**
   * Creates an instance.
   *
   * @param warp                  the Warp whose information should be printed
   * @param authorizationResolver the AuthorizationResolver used to resolve authorizations for the given warp
   * @param game                  the running game instance that holds the warp's world
   * @param playerNameResolver    the resolver to be used when resolving player names
   */
  public InfoPrinter(Warp warp, AuthorizationResolver authorizationResolver, Game game,
      PlayerNameResolver playerNameResolver) {
    this.warp = warp;
    this.authorizationResolver = authorizationResolver;
    this.game = game;
    this.playerNameResolver = playerNameResolver;
  }

  private static List<UUID> invitedUuids(Warp warp) {
    return warp.getInvitations().stream().filter(UuidPlayerMatcher.class::isInstance)
        .map(m -> ((UuidPlayerMatcher) m).getCriteria()).collect(Collectors.toList());
  }

  private static List<String> invitedGroups(Warp warp) {
    return warp.getInvitations().stream().filter(GroupPlayerMatcher.class::isInstance)
        .map(m -> ((GroupPlayerMatcher) m).getCriteria()).sorted().collect(Collectors.toList());
  }

  /**
   * Gets a Future with the info-message.
   *
   * <p>All player-names that are included in the info-message are first converted to human readable names using a
   * {@link PlayerNameResolver}. Only if no name is available, the UUID is displayed. The returned future completes once
   * all UUIDs are available and the information-message has been composed.</p>
   *
   * @param receiver the Actor who will receive the text
   * @return the text
   */
  public CompletableFuture<Message> getText(Actor receiver) {
    CompletableFuture<String>
        creatorFuture =
        playerNameResolver.getByUniqueId(warp.getCreator()).thenApply(Profile::getNameOrId);

    //invitations are only displayed, if the warp is modifiable
    CompletableFuture<List<String>> futureInvitations = CompletableFuture.completedFuture(Collections.emptyList());
    if (authorizationResolver.isModifiable(warp, receiver)) {
      futureInvitations =
          playerNameResolver.getByUniqueId(invitedUuids(warp))
              .thenApply(profiles -> profiles.stream().map(Profile::getNameOrId).sorted().collect(Collectors.toList()));
    }

    return creatorFuture
        .thenCombineAsync(futureInvitations, (creator, invitations) -> getInfo(receiver, creator, invitations),
            game.getExecutor());
  }

  /**
   * Gets the information message.
   *
   * <p>All player-names that are included in the info-message are displayed as human readable names only if the player
   * is online. Otherwise, the UUID is displayed.</p>
   *
   * <p>Use {@link #getText(Actor)} to display cached names for offline players too, <b>wherever the context
   * allows it!</b></p>
   *
   * @param receiver the Actor who will receive the text
   * @return the text
   */
  public Message getTextImmediately(Actor receiver) {
    return getInfo(receiver, nameOrUuid(warp.getCreator()),
        invitedUuids(warp).stream().map(this::nameOrUuid).sorted().collect(Collectors.toList()));
  }

  /**
   * Prints the info message to the given receiver.
   *
   * @param receiver the Actor who is receiving this print
   */
  public void print(Actor receiver) {
    getText(receiver).thenAcceptAsync(receiver::sendMessage, game.getExecutor());
  }

  private String nameOrUuid(UUID uniqueId) {
    return game.getPlayer(uniqueId).map(LocalPlayer::getName).orElse(uniqueId.toString());
  }

  private String worldName(UUID worldIdentifier) {
    return game.getWorld(worldIdentifier).map(LocalWorld::getName).orElse(worldIdentifier.toString());
  }

  private Message getInfo(Actor receiver, String creator, Collection<String> invitedPlayers) {
    Message.Builder info = Message.builder();
    // heading
    info.append(Message.Style.HEADLINE_1);

    info.append(msg.getString("info.heading"));
    info.append(" '");
    info.append(warp);
    info.append("':");
    info.appendNewLine();

    // creator
    info.append(Message.Style.KEY);

    info.append(msg.getString("info.created-by"));
    info.append(" ");
    info.append(Message.Style.VALUE);
    info.append(creator);
    if (receiver instanceof LocalPlayer && warp.isCreator(((LocalPlayer) receiver).getUniqueId())) {
      info.append(" ");
      info.append(msg.getString("info.created-by-you"));
    }
    info.appendNewLine();

    // location
    info.append(Message.Style.KEY);
    info.append(msg.getString("info.location"));
    info.append(" ");
    info.append(Message.Style.VALUE);
    info.append(msg.getString("info.location.position", warp.getPosition().getFloorX(), warp.getPosition().getFloorY(),
        warp.getPosition().getFloorZ(), worldName(warp.getWorldIdentifier())));

    info.appendNewLine();

    // if the warp is modifiable, show information about invitations
    if (authorizationResolver.isModifiable(warp, receiver)) {

      // criteria players
      info.append(Message.Style.KEY);
      info.append(msg.getString("info.invited-players"));
      info.append(" ");
      info.append(Message.Style.VALUE);

      if (invitedPlayers.isEmpty()) {
        info.append("-");
      } else {
        info.appendWithSeparators(invitedPlayers);
      }
      info.appendNewLine();

      // criteria groups
      info.append(Message.Style.KEY);
      info.append(msg.getString("info.invited-groups"));
      info.append(" ");
      info.append(Message.Style.VALUE);

      List<String> invitedGroups = invitedGroups(warp);

      if (invitedGroups.isEmpty()) {
        info.append("-");
      } else {
        info.appendWithSeparators(invitedGroups);
      }
      info.appendNewLine();
    }

    // creation date
    info.append(Message.Style.KEY);
    info.append(msg.getString("info.creation-date", warp.getCreationDate()));
    info.append(" ");
    info.append(Message.Style.VALUE);

    info.append(TIME_FORMATTER.withLocale(LocaleManager.getLocale()).format(warp.getCreationDate()));

    info.appendNewLine();

    // visits
    info.append(Message.Style.KEY);
    info.append(msg.getString("info.visits"));
    info.append(" ");
    info.append(Message.Style.VALUE);
    info.append(msg.getString("info.visits.per-day", warp.getVisits(), WarpUtils.visitsPerDay(warp)));
    return info.build();
  }

}
