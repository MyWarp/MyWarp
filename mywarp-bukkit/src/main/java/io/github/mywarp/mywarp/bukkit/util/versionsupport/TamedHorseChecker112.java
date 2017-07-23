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

package io.github.mywarp.mywarp.bukkit.util.versionsupport;

import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Entity;

/**
 * Checks whether a given entity is any variant of a horse and is tamed, when running on 1.12 and newer.
 */
class TamedHorseChecker112 extends TamedHorseChecker {

  private TamedHorseChecker112() {
  }

  static TamedHorseChecker create() throws ClassNotFoundException {
    // this will throw an ClassNotFoundException on anything lower than 1.12
    // because 'org.bukkit.entity.AbstractHorse' does not exist before 1.12
    Class.forName("org.bukkit.entity.AbstractHorse");
    return new TamedHorseChecker112();
  }

  @Override
  public boolean test(Entity entity) {
    return entity instanceof AbstractHorse && ((AbstractHorse) entity).isTamed();
  }
}
