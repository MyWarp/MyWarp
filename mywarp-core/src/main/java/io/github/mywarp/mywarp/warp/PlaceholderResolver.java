/*
 * Copyright (C) 2011 - 2022, MyWarp team and contributors
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

package io.github.mywarp.mywarp.warp;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.ImmutableMap;
import io.github.mywarp.mywarp.platform.Actor;

import javax.annotation.Nullable;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves placeholders in a String by replacing them with the appropriate values from a given Warp.
 *
 * <p>After an instance has been created, the Warp whose values should be used as replacements can be set by using
 * {@link #from(Warp)}.</p>
 *
 * <p>Placeholders are enclosed by {@code %}.</p>
 *
 * <p>The following tokens are guaranteed to be supported: <table> <tr> <th>Placeholder</th> <th>Replacement</th> </tr>
 * <tr> <td>%loc%</td> <td>warp's location</td> </tr> <tr>
 * <td>%visits%</td> <td>the warp's visits</td> </tr> <tr> <td>%warp%</td> <td>the warp's name</td> </tr> </table> </p>
 *
 * <p>In some contexts, additional placeholders might be supported: <table> <tr> <th>Placeholder</th>
 * <th>Replacement</th> </tr> <tr> <td>%player%</td> <td>the name of an Actor that uses the warp</td> </tr> </table>
 * </p>
 */
public class PlaceholderResolver {

  private static final Pattern TOKEN_PATTERN = Pattern.compile("%(.+?)%");

  private final Warp warp;
  @Nullable
  private final Actor actor;

  private final ImmutableMap<String, Supplier<String>> tokens;

  private PlaceholderResolver(Warp warp, @Nullable Actor actor) {
    this.warp = warp;
    this.actor = actor;
    this.tokens = tokens();
  }

  /**
   * Returns a usable resolver that generates replacements from the given {@code Warp}.
   *
   * @param warp the Warp
   * @return a usable resolver
   */
  public static PlaceholderResolver from(Warp warp) {
    return new PlaceholderResolver(warp, null);
  }

  /**
   * Returns a usable resolver that generates replacements from the given {@code Warp} and from the given {@code
   * Actor}.
   *
   * @param warp  the Warp
   * @param actor the Actor
   * @return a usable resolver
   */
  public static PlaceholderResolver from(Warp warp, Actor actor) {
    return new PlaceholderResolver(warp, actor);
  }

  /**
   * Resolves all placeholders in the given String.
   *
   * @param template the template String
   * @return a String with resolved placeholders
   */
  public String resolvePlaceholders(String template) {
    Matcher matcher = TOKEN_PATTERN.matcher(template);
    StringBuffer buffer = new StringBuffer();
    while (matcher.find()) {
      if (tokens.containsKey(matcher.group(1))) {
        String replacement = tokens.get(matcher.group(1)).get();
        // Matcher.quoteReplacement(String) to work properly with $ and {,} signs
        matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
      }
    }
    matcher.appendTail(buffer);
    return buffer.toString();
  }

  private ImmutableMap<String, Supplier<String>> tokens() {
    ImmutableMap.Builder<String, Supplier<String>> builder = ImmutableMap.builder();
    builder.put("warp", warp::getName);
    builder.put("visits", () -> Integer.toString(warp.getVisits()));
    builder.put("loc", () -> {
      Vector3d position = warp.getPosition();
      return String.format("(%d, %d, %d)", position.getFloorX(), position.getFloorY(), position.getFloorZ());
    });
    if (actor != null) {
      builder.put("player", actor::getName);
    }
    return builder.build();
  }

}
