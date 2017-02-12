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

package me.taylorkelly.mywarp.command.util.printer;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

import me.taylorkelly.mywarp.command.CommandHandler;
import me.taylorkelly.mywarp.platform.Actor;
import me.taylorkelly.mywarp.platform.Game;
import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.platform.LocalWorld;
import me.taylorkelly.mywarp.service.limit.Limit;
import me.taylorkelly.mywarp.service.limit.LimitService;
import me.taylorkelly.mywarp.util.Message;
import me.taylorkelly.mywarp.util.WarpUtils;
import me.taylorkelly.mywarp.util.i18n.DynamicMessages;
import me.taylorkelly.mywarp.warp.Warp;
import me.taylorkelly.mywarp.warp.WarpManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Prints a certain player's assets, showing active limit and Warps sorted to the corresponding limit.
 */
//REVIEW rewrite?
public class AssetsPrinter {

  private static final List<Limit.Type> DISPLAYABLE_TYPES = Arrays.asList(Limit.Type.PRIVATE, Limit.Type.PUBLIC);
  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final LocalPlayer creator;
  @Nullable
  private final LimitService limitService;
  private final Game game;
  private final WarpManager manager;

  /**
   * Creates an instance.
   *
   * @param creator      the player whose assets should be displayed
   * @param limitService the limitService that manages the limit that should be displayed  - may be {@code null} if no
   *                     limit service is used
   * @param game         the running game
   * @param manager      the warp manager that hold's the assets to display
   */
  public AssetsPrinter(LocalPlayer creator, @Nullable LimitService limitService, Game game, WarpManager manager) {
    this.creator = creator;
    this.limitService = limitService;
    this.game = game;
    this.manager = manager;
  }

  /**
   * Prints the assets to the given receiver.
   *
   * @param receiver the Actor who is receiving this print
   */
  public void print(Actor receiver) {
    // display the heading
    String heading = " " + msg.getString("assets.heading", creator.getName()) + " ";
    receiver.sendMessage(Message.builder().append(Message.Style.HEADLINE_1).append(heading).build());

    // display the limit
    Map<Limit, Collection<Warp>> index = new HashMap<Limit, Collection<Warp>>();

    if (limitService != null) {
      index.putAll(limitService.getWarpsPerLimit(creator));
    } else {
      index.put(new Limit() {
        @Override
        public int getLimit(Type type) {
          return -1;
        }

        @Override
        public ImmutableSet<LocalWorld> getAffectedWorlds() {
          return game.getWorlds();
        }

        @Override
        public boolean isAffectedWorld(UUID worldIdentifier) {
          return true;
        }
      }, manager.getAll(WarpUtils.isCreator(creator.getUniqueId())));
    }

    for (Entry<Limit, Collection<Warp>> entry : index.entrySet()) {
      printLimit(receiver, entry.getKey(), entry.getValue());
    }
  }

  /**
   * Prints the given Limit to the receiver. The given Warps will be matched to the individual Limit.Types and displayed
   * accordingly.
   *
   * @param receiver the Actor who is receiving this print
   * @param limit    the limit
   * @param warps    the Warps that are affected by the given Limit
   */
  private void printLimit(Actor receiver, Limit limit, Collection<Warp> warps) {
    ImmutableMultimap.Builder<Limit.Type, Warp> builder = ImmutableMultimap.builder();

    // sort warps to types
    for (Warp warp : warps) {
      for (Limit.Type type : Limit.Type.values()) {
        if (type.getCondition().apply(warp)) {
          builder.put(type, warp);
        }
      }
    }

    // display...
    final ImmutableMultimap<Limit.Type, Warp> index = builder.build();

    // ...the total limit
    Message.Builder totalMsg = Message.builder();
    totalMsg.append(Message.Style.HEADLINE_2);

    totalMsg.append(msg.getString("assets.total"));
    totalMsg.append(" ");
    totalMsg.appendWithSeparators(limit.getAffectedWorlds());
    totalMsg.append(" ");
    warpLimitCount(totalMsg, index.get(Limit.Type.TOTAL).size(), limit.getLimit(Limit.Type.TOTAL));
    totalMsg.append(":");

    receiver.sendMessage(totalMsg.build());

    // ... all other limit
    for (Limit.Type type : DISPLAYABLE_TYPES) {
      final Collection<Warp> typeWarps = index.get(type);

      Message.Builder limitMsg = Message.builder();
      limitMsg.append(Message.Style.KEY);
      limitMsg.append(msg.getString("assets." + type.lowerCaseName()));
      limitMsg.append(" ");
      warpLimitCount(limitMsg, typeWarps.size(), limit.getLimit(type));
      limitMsg.append(": ");
      limitMsg.append(Message.Style.VALUE);
      limitMsg.appendWithSeparators(typeWarps);

      receiver.sendMessage(limitMsg.build());
    }
  }

  /**
   * Creates the count-suffix from the given warp-count and the given limit-maximum to the given builder.
   *
   * @param builder      the builder
   * @param warpCount    the warp-count
   * @param limitMaximum the limit-maximum
   * @return builder with appended contents
   */
  private Message.Builder warpLimitCount(Message.Builder builder, int warpCount, int limitMaximum) {
    builder.append("(");
    builder.append(warpCount);
    if (0 <= limitMaximum) {
      builder.append('/').append(limitMaximum);
    }
    builder.append(")");
    return builder;
  }
}
