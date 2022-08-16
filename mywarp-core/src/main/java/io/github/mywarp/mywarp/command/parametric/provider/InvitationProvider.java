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

package io.github.mywarp.mywarp.command.parametric.provider;

import com.sk89q.intake.Require;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import io.github.mywarp.mywarp.command.util.ProfilePlayerMatcher;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.util.playermatcher.GroupPlayerMatcher;
import io.github.mywarp.mywarp.util.playermatcher.PlayerMatcher;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Provides {@link PlayerMatcher} instances.
 */
class InvitationProvider extends AbstractProvider<CompletableFuture<PlayerMatcher>> {

  private final PlayerNameResolver resolver;

  InvitationProvider(PlayerNameResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public CompletableFuture<PlayerMatcher> get(CommandArgs arguments, List<? extends Annotation> modifiers)
      throws ArgumentException {
    String argument = arguments.next();

    if (argument.charAt(1) == ':') {
      String realArgument = argument.substring(2);

      switch (argument.charAt(0)) {
        //group-name
        case 'g':
          Optional<Require>
              require =
              modifiers.stream().filter(Require.class::isInstance).map(Require.class::cast).findFirst();
          if (require.isPresent()) {
            ProviderUtil.checkPermissions(ProviderUtil.actor(arguments.getNamespace()), require.get().value());
          }
          return CompletableFuture.completedFuture(new GroupPlayerMatcher(realArgument));
        //unique identifier
        case 'u':
          return resolver.getByUniqueId(ProviderUtil.parseUuid(realArgument)).thenApply(ProfilePlayerMatcher::new);
        default: //fall-through
      }
    }

    return resolver.getByName(argument).thenApply(ProfilePlayerMatcher::new);
  }
}
