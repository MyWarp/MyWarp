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

package me.taylorkelly.mywarp.command.parametric;

import com.google.common.base.Optional;
import com.google.common.collect.Iterables;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.ArgumentParser;
import com.sk89q.intake.parametric.handler.AbstractInvokeListener;
import com.sk89q.intake.parametric.handler.InvokeHandler;

import me.taylorkelly.mywarp.command.parametric.annotation.Billable;
import me.taylorkelly.mywarp.platform.Actor;
import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.service.economy.EconomyService;
import me.taylorkelly.mywarp.service.economy.FeeType;
import me.taylorkelly.mywarp.util.IterableUtils;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Makes commands require a certain fee if annotated with with {@link Billable}.
 */
public class EconomyInvokeHandler extends AbstractInvokeListener implements InvokeHandler {

  private final EconomyService economyService;

  /**
   * Creates an instance.
   *
   * @param economyService the EconomyService uses to handle economy tasks
   */
  public EconomyInvokeHandler(EconomyService economyService) {
    this.economyService = economyService;
  }

  @Override
  public InvokeHandler createInvokeHandler() {
    return this;
  }

  @Override
  public boolean preProcess(List<? extends Annotation> annotations, ArgumentParser parser, CommandArgs commandArgs)
          throws CommandException, ArgumentException {
    return true;
  }

  @Override
  public boolean preInvoke(List<? extends Annotation> annotations, ArgumentParser parser, Object[] args,
                           CommandArgs commandArgs) throws CommandException, ArgumentException {
    Optional<Billable> billable = IterableUtils.getFirst(Iterables.filter(annotations, Billable.class));
    if (!billable.isPresent()) {
      return true;
    }

    Actor actor = commandArgs.getNamespace().get(Actor.class);
    if (actor == null || !(actor instanceof LocalPlayer)) {
      return true;
    }

    FeeType feeType = billable.get().value();
    return economyService.hasAtLeast((LocalPlayer) actor, feeType);
  }

  @Override
  public void postInvoke(List<? extends Annotation> annotations, ArgumentParser parser, Object[] args,
                         CommandArgs commandArgs) throws CommandException, ArgumentException {
    Optional<Billable> billable = IterableUtils.getFirst(Iterables.filter(annotations, Billable.class));
    if (!billable.isPresent()) {
      return;
    }

    Actor actor = commandArgs.getNamespace().get(Actor.class);
    if (actor == null || !(actor instanceof LocalPlayer)) {
      return;
    }

    FeeType feeType = billable.get().value();
    economyService.withdraw((LocalPlayer) actor, feeType);
  }
}
