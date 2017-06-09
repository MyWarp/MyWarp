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

package io.github.mywarp.mywarp.command.util;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.annotation.Nullable;

/**
 * Matches string representations of object instances to a given query and groups the results.
 *
 * <p>Matches are ordered by how close the string representations matches the initial query. From high to low the types
 * of matches are: <ol> <li>The string equals the query,</li> <li>The string equals the query if the case of both is
 * ignored,</li> <li>The string contains the query,</li> <li>The string contains the query if the case of both is
 * ignored.</li> </ol></p>
 *
 * <p>Once created, Match instances are immutable as the matched instances are stored internally.</p>
 *
 * <p>Use {@link #from(Iterable)} to create an instance.</p>
 *
 * @param <E> The type of elements to be matched.
 */
public class Matches<E> {

  private final List<E> equalMatches = new ArrayList<E>();
  private final List<E> equalIgnoreCaseMatches = new ArrayList<E>();
  private final List<E> containsMatches = new ArrayList<E>();
  private final List<E> containsIgnoreCaseMatches = new ArrayList<E>();

  private Matches(String query, Iterable<E> elements, Function<E, String> stringFunction, Comparator<E> comparator) {
    for (E element : elements) {
      String toTest = stringFunction.apply(element);
      if (toTest == null) {
        continue;
      }

      if (toTest.equals(query)) {
        equalMatches.add(element);
      } else if (StringUtils.equalsIgnoreCase(toTest, query)) {
        equalIgnoreCaseMatches.add(element);
      } else if (toTest.contains(query)) {
        containsMatches.add(element);
      } else if (StringUtils.containsIgnoreCase(toTest, query)) {
        containsIgnoreCaseMatches.add(element);
      }
    }

    equalMatches.sort(comparator);
    equalIgnoreCaseMatches.sort(comparator);
    containsMatches.sort(comparator);
    containsIgnoreCaseMatches.sort(comparator);
  }

  /**
   * Gets an Optional with an element that is an exact match (if such an element exists).
   *
   * <p>An element is an exact match if, and only if, it is either the only element that matches the query or all other
   * elements that match the given query are not matches of a lower kind.</p>
   *
   * @return an Optional with an exact match
   */
  public Optional<E> getExactMatch() {
    @Nullable E ret = null;

    if (equalMatches.size() == 1) {
      ret = equalMatches.get(0);
    } else if (equalIgnoreCaseMatches.size() == 1) {
      ret = equalIgnoreCaseMatches.get(0);
    } else if (containsMatches.size() == 1) {
      ret = containsMatches.get(0);
    } else if (containsIgnoreCaseMatches.size() == 1) {
      ret = containsIgnoreCaseMatches.get(0);
    }

    return Optional.ofNullable(ret);
  }

  /**
   * Gets all matches from this Matcher, sorted from highest to lowest match.
   *
   * @return an immutable List of matches
   */
  public ImmutableList<E> getSortedMatches() {
    return ImmutableList.<E>builder().addAll(equalMatches).addAll(equalIgnoreCaseMatches).addAll(containsMatches)
        .addAll(containsIgnoreCaseMatches).build();
  }

  /**
   * Gets an Optional with the element that is the closes match (if such an element exists).
   *
   * <p>An element is a close match if it matches the original query. Potentially other matches are either matches of
   * the of same or of lower kinds. If other matches are of the same kind, the close match comes first when sorting the
   * elements in the order specified when creation this Matcher.</p>
   *
   * @return an Optional with a close match
   */
  public Optional<E> getClosestMatch() {
    return Optional.of(getSortedMatches().get(1));
  }

  /**
   * Returns whether this Matcher contains any matches.
   *
   * @return {@code true} if this Matcher does not contain any matches
   */
  public boolean isEmpty() {
    return getSortedMatches().isEmpty();
  }

  /**
   * Creates a new MatcherData instance with the given elements.
   *
   * @param elements the elements to match
   * @param <E>      the Type of the elements to match
   * @return a new MatcherData instance
   */
  public static <E> MatcherData<E> from(Iterable<E> elements) {
    return new MatcherData<E>(elements);
  }

  /**
   * Collects data for {@link Matches} and creates new instances.
   *
   * @param <E> The type of elements to be matched.
   */
  public static class MatcherData<E> {

    private final Iterable<E> elements;

    private Function<E, String> stringFunction = Object::toString;
    private Comparator<E> valueComparator = Comparator.comparing(Object::toString);

    private MatcherData(Iterable<E> elements) {
      this.elements = elements;
    }

    /**
     * Sets the Function used to create Strings from the elements. These strings are compared with the query to
     * determine which kind of match a certain element is.
     *
     * @param stringFunction the function to use
     * @return this instance
     */
    public MatcherData<E> withStringFunction(Function<E, String> stringFunction) {
      this.stringFunction = stringFunction;
      return this;
    }

    /**
     * Sets the Comparator to use to compare elements that are the same kind of match.
     *
     * @param comparator the Comparator to use
     * @return this instance
     */
    public MatcherData<E> withValueComparator(Comparator<E> comparator) {
      this.valueComparator = comparator;
      return this;
    }

    /**
     * Creates a Matches instance with the given query.
     *
     * @param query the query
     * @return a new Matches instance
     */
    public Matches<E> forQuery(String query) {
      return new Matches<E>(query, elements, stringFunction, valueComparator);
    }
  }
}
