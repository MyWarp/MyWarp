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

import io.github.mywarp.mywarp.command.parametric.annotation.Billable;
import io.github.mywarp.mywarp.command.parametric.annotation.Modifiable;
import io.github.mywarp.mywarp.command.parametric.annotation.Sender;
import io.github.mywarp.mywarp.command.parametric.annotation.WarpName;
import io.github.mywarp.mywarp.command.util.ExceedsInitiatorLimitException;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.service.economy.FeeType;
import io.github.mywarp.mywarp.service.limit.LimitService;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpBuilder;
import io.github.mywarp.mywarp.warp.WarpManager;

import javax.annotation.Nullable;

/**
 * Bundles commands that manage Warps.
 */
public final class ManagementCommands {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final WarpManager warpManager;
  @Nullable
  private final LimitService limitService;

  /**
   * Creates an instance.
   *
   * @param warpManager  the WarpManager used by commands
   * @param limitService the LimitService used by commands - may be {@code null} if no limit service is used
   */
  ManagementCommands(WarpManager warpManager, @Nullable LimitService limitService) {
    this.warpManager = warpManager;
    this.limitService = limitService;
  }

  @Command(aliases = {"pcreate", "pset"}, desc = "create.private.description", help = "create.private.help")
  @Require("mywarp.cmd.create-private")
  @Billable(FeeType.CREATE_PRIVATE)
  public void pcreate(@Sender LocalPlayer player, @WarpName String name) throws CommandException {
    addWarp(player, Warp.Type.PRIVATE, name);
    player.sendMessage(msg.getString("create.private.created-successful", name));
  }

  @Command(aliases = {"create", "set"}, desc = "create.public.description", help = "create.public.help")
  @Require("mywarp.cmd.create-public")
  @Billable(FeeType.CREATE)
  public void create(@Sender LocalPlayer player, @WarpName String name) throws CommandException {
    addWarp(player, Warp.Type.PUBLIC, name);
    player.sendMessage(msg.getString("create.public.created-successful", name));
  }

  /**
   * Creates a Warp and adds it to the used WarpManager or fails fast.
   *
   * @param creator the LocalPlayer creating the Warp
   * @param type    the warp's Warp.Type
   * @param name    the warp's name
   * @throws CommandException if the Warp cannot be created
   */
  private void addWarp(LocalPlayer creator, Warp.Type type, String name) throws CommandException {

    if (limitService != null) {
      LimitService.EvaluationResult result = limitService.canAdd(creator, creator.getWorld(), type);
      if (result.exceedsLimit()) {
        throw new ExceedsInitiatorLimitException(result.getExceededValue(), result.getAllowedMaximium());
      }
    }

    warpManager.add(
        new WarpBuilder(name, creator.getUniqueId(), creator.getWorld().getUniqueId(), creator.getPosition(),
                        creator.getRotation()).setType(type).build());
  }

  @Command(aliases = {"delete", "remove"}, desc = "delete.description", help = "delete.help")
  @Require("mywarp.cmd.delete")
  @Billable(FeeType.DELETE)
  public void delete(Actor actor, @Modifiable Warp warp) {
    warpManager.remove(warp);
    actor.sendMessage(msg.getString("delete.deleted-successful", warp.getName()));
  }

  @Command(aliases = {"update"}, desc = "update.description", help = "update.help")
  @Require("mywarp.cmd.update")
  @Billable(FeeType.UPDATE)
  public void update(@Sender LocalPlayer player, @Modifiable Warp warp) {
    warp.setLocation(player.getWorld(), player.getPosition(), player.getRotation());
    player.sendMessage(msg.getString("update.update-successful", warp.getName()));
  }

  @Command(aliases = {"welcome"}, desc = "welcome.description", help = "welcome.help")
  @Require("mywarp.cmd.welcome")
  @Billable(FeeType.WELCOME)
  public void welcome(@Sender LocalPlayer player, @Modifiable Warp warp) {
    player.initiateWelcomeChangeConversation(warp);
  }

}
