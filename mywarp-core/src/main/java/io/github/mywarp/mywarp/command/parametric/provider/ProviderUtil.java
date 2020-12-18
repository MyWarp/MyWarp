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

import com.sk89q.intake.argument.Namespace;
import io.github.mywarp.mywarp.command.parametric.provider.exception.ArgumentAuthorizationException;
import io.github.mywarp.mywarp.command.parametric.provider.exception.InvalidUuidFormatException;
import io.github.mywarp.mywarp.platform.Actor;
import java.util.UUID;

/**
 * Utilities for providers.
 */
class ProviderUtil {

  private ProviderUtil() {
  }

  static UUID parseUuid(String argument) throws InvalidUuidFormatException {
    try {
      return UUID.fromString(argument);
    } catch (IllegalArgumentException e) {
      throw new InvalidUuidFormatException(argument);
    }
  }

  static Actor actor(Namespace namespace) {
    if (!namespace.containsKey(Actor.class)) {
      throw new IllegalStateException("This Binding must be used by an Actor.");
    }
    return namespace.get(Actor.class);
  }

  static void checkPermissions(Actor actor, String... permissions) throws ArgumentAuthorizationException {
    for (String permission : permissions) {
      if (!actor.hasPermission(permission)) {
        throw new ArgumentAuthorizationException();
      }
    }
  }

}
