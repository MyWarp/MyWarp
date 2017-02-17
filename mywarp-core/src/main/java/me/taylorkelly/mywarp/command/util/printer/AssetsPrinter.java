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

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import me.taylorkelly.mywarp.command.CommandHandler;
import me.taylorkelly.mywarp.platform.Actor;
import me.taylorkelly.mywarp.platform.Game;
import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.platform.LocalWorld;
import me.taylorkelly.mywarp.service.limit.Limit;
import me.taylorkelly.mywarp.service.limit.LimitService;
import me.taylorkelly.mywarp.service.limit.LimitValueWarpMapping;
import me.taylorkelly.mywarp.util.Message;
import me.taylorkelly.mywarp.util.WarpUtils;
import me.taylorkelly.mywarp.util.i18n.DynamicMessages;
import me.taylorkelly.mywarp.warp.Warp;
import me.taylorkelly.mywarp.warp.WarpManager;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Prints a certain player's assets, showing active limit and Warps sorted to the corresponding limit.
 */
public class AssetsPrinter {

  private static final List<Limit.Value> DISPLAYABLE_VALUES = Arrays.asList(Limit.Value.PRIVATE, Limit.Value.PUBLIC);
  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final LocalPlayer creator;
  @Nullable
  private final LimitService limitService;
  @Nullable
  private final Game game;
  @Nullable
  private final WarpManager warpManager;

  private AssetsPrinter(LocalPlayer creator, @Nullable LimitService limitService, @Nullable Game game,
                        @Nullable WarpManager warpManager) {
    checkState((limitService != null && game == null && warpManager == null) != (limitService == null && game != null
                                                                                 && warpManager != null));
    this.creator = creator;
    this.limitService = limitService;
    this.game = game;
    this.warpManager = warpManager;
  }

  /**
   * Creates a new AssetsPrinter that will display the assets of the given player using the given {@code limitService}.
   *
   * @param forWhom      the player whose assets to display
   * @param limitService the limit service
   * @return a new AssetsPrinter
   */
  public static AssetsPrinter create(LocalPlayer forWhom, LimitService limitService) {
    return new AssetsPrinter(forWhom, limitService, null, null);
  }

  /**
   * Creates a new AssetsPrinter that will display the assets of the given player on the given {@code warpManager}.
   *
   * <p>This method should only be used, if no LimitService is configured. Otherwise use
   * {@link #create(LocalPlayer, LimitService)}.</p>
   *
   * @param forWhom     the player whose assets to display
   * @param game        the Game
   * @param warpManager the WarpManager
   * @return a new AssetsPrinter
   */
  public static AssetsPrinter create(LocalPlayer forWhom, Game game, WarpManager warpManager) {
    return new AssetsPrinter(forWhom, null, game, warpManager);
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
    Map<Limit, LimitValueWarpMapping> index;

    if (limitService != null) {
      index = limitService.getAssets(creator);
    } else {
      assert game != null && warpManager != null;
      index =
          ImmutableMap.of(createDummyLimit(game),
                          new LimitValueWarpMapping(warpManager, WarpUtils.isCreator(creator.getUniqueId())));
    }

    for (Map.Entry<Limit, LimitValueWarpMapping> entry : index.entrySet()) {
      printLimit(receiver, entry.getKey(), entry.getValue());
    }
  }

  private static Limit createDummyLimit(final Game game) {
    return new Limit() {
      @Override
      public ImmutableSet<LocalWorld> getAffectedWorlds() {
        return game.getWorlds();
      }

      @Override
      public boolean isAffectedWorld(UUID worldIdentifier) {
        return true;
      }

      @Override
      public int get(Value value) {
        return -1;
      }
    };
  }

  private void printLimit(Actor receiver, Limit limit, LimitValueWarpMapping mapping) {

    // ...the total value (max. number & worlds)
    Message.Builder totalMsg = Message.builder();
    totalMsg.append(Message.Style.HEADLINE_2);

    totalMsg.append(msg.getString("assets.total"));
    totalMsg.append(" ");
    totalMsg.appendWithSeparators(limit.getAffectedWorlds());
    totalMsg.append(" ");
    appendCurrentAndMaximum(totalMsg, mapping.get(Limit.Value.TOTAL).size(), limit.get(Limit.Value.TOTAL));
    totalMsg.append(":");

    receiver.sendMessage(totalMsg.build());

    // ... all other values (max. number & warp names)
    for (Limit.Value value : DISPLAYABLE_VALUES) {
      final Collection<Warp> typeWarps = mapping.get(value);

      Message.Builder limitMsg = Message.builder();
      limitMsg.append(Message.Style.KEY);
      limitMsg.append(msg.getString("assets." + value.lowerCaseName()));
      limitMsg.append(" ");
      appendCurrentAndMaximum(limitMsg, typeWarps.size(), limit.get(value));
      limitMsg.append(": ");
      limitMsg.append(Message.Style.VALUE);
      limitMsg.appendWithSeparators(typeWarps);

      receiver.sendMessage(limitMsg.build());
    }
  }

  private Message.Builder appendCurrentAndMaximum(Message.Builder builder, int current, int max) {
    builder.append("(");
    builder.append(current);
    if (0 <= max) {
      builder.append('/').append(max);
    }
    builder.append(")");
    return builder;
  }
}
