/*
 * Copyright (C) 2011 - 2020, MyWarp team and contributors
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

package io.github.mywarp.mywarp.command.parametric.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.Namespace;
import io.github.mywarp.mywarp.command.parametric.provider.exception.NoSuchPlayerException;
import io.github.mywarp.mywarp.command.util.Matches;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides {@link LocalPlayer} instances.
 */
class PlayerProvider extends AbstractProvider<LocalPlayer> {

  private final Game game;

  PlayerProvider(Game game) {
    this.game = game;
  }

  @Override
  public LocalPlayer get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException {
    String query = arguments.next();
    return Matches.from(game.getPlayers()).withStringFunction(Actor::getName).forQuery(query).getExactMatch()
        .orElseThrow(() -> new NoSuchPlayerException(query));
  }

  @Override
  public List<String> getSuggestions(String prefix, Namespace namespace) {
    return Matches.from(game.getPlayers().stream().map(Actor::getName).collect(Collectors.toList())).forQuery(prefix)
        .getSortedMatches();
  }

}
