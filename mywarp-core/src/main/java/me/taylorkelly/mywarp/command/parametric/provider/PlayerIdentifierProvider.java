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

package me.taylorkelly.mywarp.command.parametric.provider;

import com.google.common.base.Optional;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;

import me.taylorkelly.mywarp.command.parametric.provider.exception.NoSuchPlayerIdentifierException;
import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.platform.PlayerNameResolver;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Provides {@link UUID} instances that identify players.
 *
 * @see LocalPlayer#getUniqueId()
 */
class PlayerIdentifierProvider implements Provider<UUID> {

  private final PlayerNameResolver playerNameResolver;

  PlayerIdentifierProvider(PlayerNameResolver playerNameResolver) {
    this.playerNameResolver = playerNameResolver;
  }

  @Override
  public boolean isProvided() {
    return false;
  }

  @Nullable
  @Override
  public UUID get(CommandArgs arguments, List<? extends Annotation> modifiers)
          throws ArgumentException, ProvisionException {
    String query = arguments.next();

    Optional<UUID> optional = playerNameResolver.getByName(query);

    if (!optional.isPresent()) {
      throw new NoSuchPlayerIdentifierException(query);
    }
    return optional.get();
  }

  @Override
  public List<String> getSuggestions(String prefix, Namespace namespace) {
    //TODO implement
    return Collections.emptyList();
  }

}
