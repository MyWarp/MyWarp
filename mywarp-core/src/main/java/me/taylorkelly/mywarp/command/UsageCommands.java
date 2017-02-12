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

package me.taylorkelly.mywarp.command;

import com.sk89q.intake.Command;
import com.sk89q.intake.Default;
import com.sk89q.intake.Require;

import me.taylorkelly.mywarp.command.parametric.annotation.Sender;
import me.taylorkelly.mywarp.command.parametric.annotation.Usable;
import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.service.teleport.TeleportService;
import me.taylorkelly.mywarp.warp.Warp;

/**
 * Bundles usage commands.
 */
public final class UsageCommands {

  private static final String CMD_TO_PERMISSION = "mywarp.cmd.to";

  private final TeleportService teleportService;

  /**
   * Creates an instance.
   *
   * @param teleportService the TeleportService used by commands, implementing additional validation on top
   */
  UsageCommands(TeleportService teleportService) {
    this.teleportService = teleportService;
  }

  @Command(aliases = {"to"}, desc = "warp-to.description")
  @Require(CMD_TO_PERMISSION)
  public void to(@Sender LocalPlayer player, @Usable Warp warp) {
    teleportService.teleport(player, warp);
  }

  /**
   * The default usage command.
   *
   * <p>This class contains a single method to be used as a default method for a sub-command.</p>
   *
   * @see Default
   */
  public class DefaultUsageCommand {

    @Command(aliases = {"to"}, desc = "warp-to.description")
    @Default(defaultOnly = true)
    @Require(UsageCommands.CMD_TO_PERMISSION)
    public void to(@Sender LocalPlayer player, @Usable Warp warp) {
      UsageCommands.this.to(player, warp);
    }
  }
}
