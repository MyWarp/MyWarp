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

package io.github.mywarp.mywarp.command.parametric;

import static com.google.common.base.Preconditions.checkArgument;

import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.util.auth.Authorizer;

import io.github.mywarp.mywarp.platform.Actor;

/**
 * Resolves authorization for Actors using {@link Actor#hasPermission(String)}.
 */
public class ActorAuthorizer implements Authorizer {

  @Override
  public boolean testPermission(Namespace namespace, String permission) {
    checkArgument(namespace.containsKey(Actor.class), "No Actor in Namespace.");
    return namespace.get(Actor.class).hasPermission(permission);
  }
}
