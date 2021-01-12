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

import static com.google.common.base.Preconditions.checkState;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.service.limit.Limit;
import io.github.mywarp.mywarp.service.limit.LimitService;
import io.github.mywarp.mywarp.service.limit.LimitValueWarpMapping;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
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

  private static <I> String join(Collection<I> items, Function<I, String> mapper) {
    return Joiner.on(", ").join(items.stream().map(mapper).sorted().collect(Collectors.toList()));
  }

  /**
   * Prints the assets to the given receiver.
   *
   * @param receiver the Actor who is receiving this print
   */
  public void print(Actor receiver) {
    // display the heading
    receiver.sendMessage(Message.of(Message.Style.HEADLINE_1, msg.getString("assets.heading", creator.getName())));

    // display the limit
    Map<Limit, LimitValueWarpMapping> index;

    if (limitService != null) {
      index = limitService.getAssets(creator);
    } else {
      assert game != null && warpManager != null;
      index =
          ImmutableMap.of(createDummyLimit(game),
              new LimitValueWarpMapping(warpManager, w -> w.isCreator(creator.getUniqueId())));
    }

    for (Map.Entry<Limit, LimitValueWarpMapping> entry : index.entrySet()) {
      printLimit(receiver, entry.getKey(), entry.getValue());
    }
  }

  private void printLimit(Actor receiver, Limit limit, LimitValueWarpMapping mapping) {

    // ...the total value (max. number & worlds)
    Message.Builder totalMsg = Message.builder();
    totalMsg.append(Message.Style.HEADLINE_2);
    totalMsg.append(msg.getString("assets.total", join(limit.getAffectedWorlds(), LocalWorld::getName),
        mapping.get(Limit.Value.TOTAL).size(), limit.get(Limit.Value.TOTAL)));

    receiver.sendMessage(totalMsg.build());

    // ... all other values (max. number & warp names)
    for (Limit.Value value : DISPLAYABLE_VALUES) {
      final Collection<Warp> typeWarps = mapping.get(value);

      Message.Builder limitMsg = Message.builder();
      limitMsg.append(Message.Style.KEY);
      limitMsg.append(msg.getString("assets." + value.lowerCaseName(), typeWarps.size(), limit.get(value)));
      limitMsg.append(Message.Style.VALUE);
      limitMsg.append(" ");
      limitMsg.appendWithSeparators(typeWarps);

      receiver.sendMessage(limitMsg.build());
    }
  }

}
