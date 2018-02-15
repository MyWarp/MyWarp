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

package io.github.mywarp.mywarp.command.parametric.provider;

import com.google.common.collect.Lists;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.MissingArgumentException;
import com.sk89q.intake.argument.Namespace;

import io.github.mywarp.mywarp.command.parametric.provider.exception.NoSuchWarpException;
import io.github.mywarp.mywarp.command.util.Matches;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;
import io.github.mywarp.mywarp.warp.authorization.AuthorizationResolver;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.function.Predicate;

/**
 * Provides {@link Warp} instances.
 */
abstract class WarpProvider extends AbstractProvider<Warp> {

  private final AuthorizationResolver authorizationResolver;
  private final WarpManager warpManager;

  WarpProvider(AuthorizationResolver authorizationResolver, WarpManager warpManager) {
    this.authorizationResolver = authorizationResolver;
    this.warpManager = warpManager;
  }

  @Override
  public Warp get(CommandArgs arguments, List<? extends Annotation> modifiers)
      throws MissingArgumentException, NoSuchWarpException {
    String query = arguments.next();

    Matches<Warp>
        matches =
        Matches.from(warpManager.getAll(isValid(arguments.getNamespace()))).withStringFunction(Warp::getName)
            .withValueComparator(new Warp.PopularityComparator()).forQuery(query);
    return matches.getExactMatch().orElseThrow(() -> new NoSuchWarpException(query, matches.getSortedMatches()));
  }

  /**
   * Returns a Predicate that evaluates to {@code true} if the tested warp is valid for the given {@code Actor}.
   *
   * <p>This method is called whenever warps are parsed from user input. Only warps that this method evaluates
   * positively are considered valid matches for the user input.</p>
   *
   * @param resolver the used AuthorizationResolver
   * @param actor    the Actor
   * @return a Predicate that evaluates to {@code true} if the tested warp is valid for the given {@code Actor}.
   */
  abstract Predicate<Warp> isValid(AuthorizationResolver resolver, Actor actor);

  private Predicate<Warp> isValid(Namespace namespace) {
    return isValid(authorizationResolver, ProviderUtil.actor(namespace));
  }

  @Override
  public List<String> getSuggestions(String prefix, Namespace locals) {
    return Lists.transform(Matches.from(warpManager.getAll(isValid(locals))).withStringFunction(Warp::getName)
                               .withValueComparator(new Warp.PopularityComparator()).forQuery(prefix)
                               .getSortedMatches(), Warp::getName);
  }

}
