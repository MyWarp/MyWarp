/*
 * Copyright (C) 2011 - 2018, MyWarp team and contributors
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

import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

/**
 * Resolves placeholders in a String by replacing them with the appropriate values from a given Warp.
 *
 * <p>After an instance has been created, the Warp whose values should be used as replacements can be set by using
 * {@link #values(Warp)}.</p>
 *
 * <p>Placeholders are enclosed by {@code %}.</p>
 *
 * <p>The following tokens are guaranteed to be supported: <table> <tr> <th>Placeholder</th> <th>Replacement</th> </tr>
 * <tr> <td>%creator%</td> <td>warp's creator</td> </tr> <tr> <td>%loc%</td> <td>warp's location</td> </tr> <tr>
 * <td>%visits%</td> <td>the warp's visits</td> </tr> <tr> <td>%warp%</td> <td>the warp's name</td> </tr> </table> </p>
 *
 * <p>In some contexts, additional placeholders might be supported: <table> <tr> <th>Placeholder</th>
 * <th>Replacement</th> </tr> <tr> <td>%player%</td> <td>the name of an Actor that uses the warp</td> </tr> </table>
 * </p>
 */
public class PlaceholderResolver {

  private static final Pattern TOKEN_PATTERN = Pattern.compile("%(.+?)%");

  private final PlayerNameResolver resolver;

  /**
   * Creates an instance.
   *
   * @param resolver the PlayerNameResolver used to resolve UUIDs in the replacement process
   */
  public PlaceholderResolver(PlayerNameResolver resolver) {
    this.resolver = resolver;
  }

  /**
   * Returns a usable resolver that generates replacements from the given {@code Warp}.
   *
   * @param warp the Warp
   * @return a usable resolver
   */
  public ConfiguredPlaceholderResolver values(Warp warp) {
    return values(warp, null);
  }

  /**
   * Returns a usable resolver that generates replacements from the given {@code Warp} and from the given {@code
   * Actor}.
   *
   * <p>This method is only used internally when resolving warp welcome messages.</p>
   *
   * @param warp  the Warp
   * @param actor the Actor
   * @return a usable resolver
   */
  public ConfiguredPlaceholderResolver values(Warp warp, Actor actor) {
    return new ConfiguredPlaceholderResolver(tokens(resolver, actor), warp);
  }

  private Map<String, Token> tokens(PlayerNameResolver resolver, @Nullable Actor actor) {
    Set<Token> tokens = new HashSet<Token>();

    tokens.add(new NameToken());
    tokens.add(new LocationToken());
    tokens.add(new VisitsToken());
    tokens.add(new CreatorToken(resolver));

    if (actor != null) {
      tokens.add(new ActorToken(actor.getName()));
    }

    return tokens.stream().collect(Collectors.toMap(Token::token, i -> i));
  }

  /**
   * A PlaceholderResolver that has been configured to use a certain Warp's values for replacements.
   *
   * @see PlaceholderResolver To create an instance, use a PlaceholderResolver.
   */
  public class ConfiguredPlaceholderResolver {

    private final Map<String, Token> tokens;
    private final Warp warp;

    private ConfiguredPlaceholderResolver(Map<String, Token> tokens, Warp warp) {
      this.tokens = tokens;
      this.warp = warp;
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
          String replacement = tokens.get(matcher.group(1)).apply(warp);
          // Matcher.quoteReplacement() to work properly with $ and {,} signs
          matcher.appendReplacement(buffer, replacement != null ? Matcher.quoteReplacement(replacement) : "null");
        }
      }
      matcher.appendTail(buffer);
      return buffer.toString();
    }

  }

  private abstract class Token implements Function<Warp, String> {

    abstract String token();

  }

  private class NameToken extends Token {

    @Override
    public String apply(Warp input) {
      return input.getName();
    }

    @Override
    String token() {
      return "warp";
    }
  }

  private class CreatorToken extends Token {

    private final PlayerNameResolver resolver;

    private CreatorToken(PlayerNameResolver resolver) {
      this.resolver = resolver;
    }

    @Override
    public String apply(Warp input) {
      UUID creator = input.getCreator();
      return resolver.getByUniqueId(creator).orElse(creator.toString());
    }

    @Override
    String token() {
      return "creator";
    }
  }

  private class VisitsToken extends Token {

    @Override
    public String apply(Warp input) {
      return String.valueOf(input.getVisits());
    }

    @Override
    String token() {
      return "visits";
    }
  }

  private class LocationToken extends Token {

    @Override
    public String apply(Warp input) {
      return "(" + input.getPosition().getFloorX() + ", " + input.getPosition().getFloorY() + ", " + input.getPosition()
          .getFloorZ() + ")";
    }

    @Override
    String token() {
      return "loc";
    }
  }

  private class ActorToken extends Token {

    private final String actorName;

    private ActorToken(String actorName) {
      this.actorName = actorName;
    }

    @Override
    public String apply(Warp input) {
      return actorName;
    }

    @Override
    String token() {
      return "player";
    }
  }

}
