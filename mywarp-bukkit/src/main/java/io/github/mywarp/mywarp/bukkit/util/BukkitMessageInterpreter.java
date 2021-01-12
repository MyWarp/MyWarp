/*
 * Copyright (C) 2011 - 2021, MyWarp team and contributors
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

package io.github.mywarp.mywarp.bukkit.util;

import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.warp.Warp;
import org.bukkit.ChatColor;

/**
 * Interprets {@link Message} instances by returning human readable string representations that are usable for Bukkit's
 * message system.
 */
public class BukkitMessageInterpreter {

  private Message.Style lastStyle = Message.Style.DEFAULT;
  private final StringBuilder builder;

  private BukkitMessageInterpreter(StringBuilder builder) {
    this.builder = builder;
  }

  /**
   * Interprets the given {@code message} and returns a human readable string with the appropriate styles.
   *
   * @param message the message to interpret
   * @return a string representation
   */
  public static String interpret(Message message) {
    return new BukkitMessageInterpreter(new StringBuilder()).interpret(message.getElements()).toString();

  }

  /**
   * Interprets the given objects and returns a human readable string.
   *
   * @param elements the objects to interpret
   * @return a StringBuilder containing the string representations
   */
  private BukkitMessageInterpreter interpret(Iterable<Object> elements) {
    for (Object element : elements) {
      interpret(element);
    }
    return this;
  }

  /**
   * Interprets the given object and returns a human readable string.
   *
   * @param element the object to interpret
   */
  private BukkitMessageInterpreter interpret(Object element) {
    if (element instanceof Message.Style) {
      Message.Style style = (Message.Style) element;

      lastStyle = style;
      builder.append(resolveStyle(style));
      return this;
    }

    // Warp
    if (element instanceof Warp) {
      Warp warp = (Warp) element;
      switch (warp.getType()) {
        case PRIVATE:
          builder.append(ChatColor.RED);
          break;
        case PUBLIC:
        default:
          builder.append(ChatColor.GREEN);
      }

      builder.append(warp.getName());
      builder.append(resolveStyle(lastStyle));
      return this;
    }

    // LocalWorld
    if (element instanceof LocalWorld) {
      builder.append(((LocalWorld) element).getName());
      return this;
    }

    // Actor
    if (element instanceof Actor) {
      builder.append(ChatColor.ITALIC);
      builder.append(((Actor) element).getName());

      builder.append(resolveStyle(lastStyle));
      return this;
    }

    //default
    builder.append(element);
    return this;
  }

  @Override
  public String toString() {
    return builder.toString();
  }

  private String resolveStyle(Message.Style style) {
    // Bukkit's ChatColors directly represent Minecraft's formatting codes and their behavior: colors always reset
    // formatting to normal! To use a formatting with a color, the formatting must be given after the color.
    switch (style) {
      case ERROR:
        return ChatColor.RED.toString();
      case INFO:
        return ChatColor.GRAY.toString();
      case HEADLINE_1:
        return ChatColor.GOLD.toString() + ChatColor.BOLD.toString();
      case HEADLINE_2:
        return ChatColor.WHITE.toString();
      case KEY:
        return ChatColor.GRAY.toString();
      case VALUE:
        return ChatColor.WHITE.toString();
      case DEFAULT:
      default:
        return ChatColor.AQUA.toString();
    }
  }

}
