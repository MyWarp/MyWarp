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

package io.github.mywarp.mywarp.command.parametric;

import com.sk89q.intake.util.i18n.ResourceProvider;
import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;

/**
 * Provides resources to commands.
 *
 * @see DynamicMessages
 */
public class CommandResourceProvider implements ResourceProvider {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  @Override
  public String getString(String key) {
    return msg.getString(key);
  }
}
