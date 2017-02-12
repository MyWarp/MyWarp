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

package me.taylorkelly.mywarp.command.parametric.namespace;

import static com.google.common.base.Preconditions.checkState;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.parametric.ProvisionException;

import me.taylorkelly.mywarp.platform.Actor;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Provides an {@link Actor} from the command's {@link Namespace}.
 */
class ActorProvider extends NonProvidingProvider<Actor> {

  @Override
  public Actor get(CommandArgs arguments, List<? extends Annotation> modifiers)
          throws ArgumentException, ProvisionException {
    Namespace namespace = arguments.getNamespace();
    checkState(namespace.containsKey(Actor.class), "Namespace does not contain an Actor");

    return namespace.get(Actor.class);
  }
}
