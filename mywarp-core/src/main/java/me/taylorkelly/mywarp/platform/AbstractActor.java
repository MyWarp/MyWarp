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

package me.taylorkelly.mywarp.platform;

import me.taylorkelly.mywarp.util.Message;

/**
 * An abstract implementation of an Actor.
 */
public abstract class AbstractActor implements Actor {

  @Override
  public void sendMessage(String msg) {
    sendMessage(Message.builder().append(Message.Style.DEFAULT).append(msg).build());
  }

  @Override
  public void sendError(String msg) {
    sendMessage(Message.builder().append(Message.Style.ERROR).append(msg).build());
  }
}
