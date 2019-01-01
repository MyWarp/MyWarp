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

package io.github.mywarp.mywarp.command;

import com.sk89q.intake.Command;
import com.sk89q.intake.Require;
import com.sk89q.intake.parametric.annotation.OptArg;

import io.github.mywarp.mywarp.MyWarp;
import io.github.mywarp.mywarp.command.parametric.annotation.Billable;
import io.github.mywarp.mywarp.command.parametric.annotation.Sender;
import io.github.mywarp.mywarp.command.parametric.annotation.Usable;
import io.github.mywarp.mywarp.command.parametric.annotation.Viewable;
import io.github.mywarp.mywarp.command.util.CommandUtil;
import io.github.mywarp.mywarp.command.util.NoSuchWorldException;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.Platform;
import io.github.mywarp.mywarp.service.economy.FeeType;
import io.github.mywarp.mywarp.service.teleport.TeleportService;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.Warp;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bundles utility commands.
 */
public final class UtilityCommands {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final MyWarp myWarp;
  private final CommandHandler commandHandler;
  private final TeleportService teleportService;
  private final Game game;
  private final Platform platform;

  /**
   * Creates an instance.
   *
   * @param myWarp          the MyWarp instance used in commands
   * @param commandHandler  the CommandHandler instance used in commands
   * @param teleportService the TeleportService to be used as base in commands
   * @param platform        the Platform instance used in commands
   * @param game            the Game instance used in commands
   */
  UtilityCommands(MyWarp myWarp, CommandHandler commandHandler, TeleportService teleportService, Platform platform,
                  Game game) {
    this.myWarp = myWarp;
    this.commandHandler = commandHandler;
    this.teleportService = teleportService;
    this.platform = platform;
    this.game = game;
  }

  @Command(aliases = {"help"}, desc = "help.description", help = "help.help")
  @Require("mywarp.cmd.help")
  @Billable(FeeType.HELP)
  public void help(Actor actor, @OptArg("1") int page) {
    List<Message>
        usableCommands =
        commandHandler.getUsableCommands(actor).stream().sorted().map(Message::of).collect(Collectors.toList());

    platform.createPaginatedContentBuilder().withHeading(msg.getString("help.heading"))
        .withNote(msg.getString("help.note")).build(usableCommands).display(actor, page);
  }

  @Command(aliases = {"point"}, desc = "point.description", help = "point.help")
  @Require("mywarp.cmd.point")
  @Billable(FeeType.POINT)
  public void point(@Sender LocalPlayer player, @OptArg @Usable Warp warp) throws NoSuchWorldException {
    if (warp != null) {
      player.setCompassTarget(CommandUtil.toWorld(warp, game), warp.getPosition());
      player.sendMessage(msg.getString("point.set", warp.getName()));
    } else {
      player.resetCompass();
      player.sendMessage(msg.getString("point.reset"));
    }
  }

  @Command(aliases = {"player"}, desc = "warp-player.description", help = "warp-player.help")
  @Require("mywarp.cmd.player")
  @Billable(FeeType.WARP_PLAYER)
  public void player(Actor actor, LocalPlayer teleportee, @Viewable Warp warp) {
    if (teleportService.teleport(teleportee, warp).isPositionModified()) {
      actor.sendMessage(msg.getString("warp-player.teleport-successful", teleportee.getName(), warp.getName()));
    } else {
      actor.sendError(msg.getString("warp-player.teleport-failed", teleportee.getName(), warp.getName()));
    }
  }

  @Command(aliases = {"reload"}, desc = "reload.description", help = "reload.help")
  @Require("mywarp.cmd.reload")
  public void reload(Actor actor) {
    myWarp.reload();
    actor.sendMessage(msg.getString("reload.reload-message"));
  }

}
