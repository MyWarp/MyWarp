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
import com.sk89q.intake.parametric.Provider;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.platform.Profile;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Provides {@link UUID} instances that identify players.
 *
 * @see LocalPlayer#getUniqueId()
 */
class PlayerIdProvider extends AbstractProvider<CompletableFuture<Profile>>
    implements Provider<CompletableFuture<Profile>> {

  private final PlayerNameResolver resolver;

  PlayerIdProvider(PlayerNameResolver resolver) {
    this.resolver = resolver;
  }

  @Override
  public CompletableFuture<Profile> get(CommandArgs arguments, List<? extends Annotation> modifiers)
      throws ArgumentException {
    String argument = arguments.next();

    if (argument.charAt(1) == ':' && argument.charAt(0) == 'u') {
      UUID uuid = ProviderUtil.parseUuid(argument.substring(2));
      return resolver.getByUniqueId(uuid);
    }

    return resolver.getByName(argument);
  }

}
