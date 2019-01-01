/*
 * Copyright (C) 2011 - 2019, MyWarp team and contributors
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

import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.ArgumentParser;
import com.sk89q.intake.parametric.handler.AbstractInvokeListener;
import com.sk89q.intake.parametric.handler.InvokeHandler;

import io.github.mywarp.mywarp.command.parametric.annotation.Billable;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.service.economy.EconomyService;
import io.github.mywarp.mywarp.service.economy.FeeType;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

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
  public boolean preProcess(List<? extends Annotation> annotations, ArgumentParser parser, CommandArgs commandArgs) {
    return true;
  }

  @Override
  public boolean preInvoke(List<? extends Annotation> annotations, ArgumentParser parser, Object[] args,
                           CommandArgs commandArgs) {
    Optional<Billable> billable = findFirst(annotations, Billable.class);
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
                         CommandArgs commandArgs) {
    Optional<Billable> billable = findFirst(annotations, Billable.class);
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

  private <T> Optional<T> findFirst(Collection<?> collection, Class<T> cls) {
    return (Optional<T>) collection.stream().filter(a -> cls.isAssignableFrom(a.getClass())).findFirst();
  }
}
