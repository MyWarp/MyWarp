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

package me.taylorkelly.mywarp.util;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import me.taylorkelly.mywarp.warp.Warp;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Provides delegated access to a given Collection of warps via name filtering.
 *
 * <p>Once created the matching is 'static' as matching warps are internally stored in the instance. If changes need to
 * be reflected, a new instance needs to be created.</p>
 */
public class MatchList {

  private final List<Warp> matchingWarps;
  private final String filter;

  /**
   * Creates an instance that uses the given {@code filer} to filter warp-names from the given warps.
   *
   * @param filter        the filter
   * @param matchingWarps an Iterable of matching warps this MatchList should operate on
   */
  public MatchList(String filter, Iterable<Warp> matchingWarps) {
    this.filter = filter;
    this.matchingWarps = new ArrayList<Warp>();

    // filter for warps that contain the name-filter (case insensitive)
    for (Warp warp : matchingWarps) {
      if (StringUtils.containsIgnoreCase(warp.getName(), filter)) {
        this.matchingWarps.add(warp);
      }
    }
  }

  /**
   * Gets an Optional that contains an exact match (if such a Warp exists).
   *
   * <p>A Warp is an exact match if it fulfills one of the following conditions, starting from the top: <ol> <li>the
   * only Warp whose name contains case-insensitively this MatchList's filer,</li> <li>the only Warp whose name is case
   * insensitively equal to this MatchList's filter, <li>the only Warp whose name is equal to this MatchList's
   * filter.</li> </ol></p>
   *
   * @return a exactly matching Warp
   */
  // REVIEW this could be done when the MatchList is created?
  public Optional<Warp> getExactMatch() {
    // only one warp contains the filter sequence (case insensitive)
    if (matchingWarps.size() <= 1) {
      return IterableUtils.getFirst(matchingWarps);
    }

    // filter for warps that have the exact name (case insensitive)
    List<Warp> sameNameWarps = new ArrayList<Warp>();
    for (Warp warp : matchingWarps) {
      if (StringUtils.equalsIgnoreCase(warp.getName(), filter)) {
        sameNameWarps.add(warp);
      }
    }
    if (sameNameWarps.size() <= 1) {
      return IterableUtils.getFirst(sameNameWarps);
    }

    // filter for warps that have the exact name (case sensitive)
    List<Warp> sameCaseNameWarps = new ArrayList<Warp>();
    for (Warp warp : sameNameWarps) {
      if (warp.getName().equals(filter)) {
        sameCaseNameWarps.add(warp);
      }
    }
    return IterableUtils.getFirst(sameCaseNameWarps);
  }

  /**
   * Gets a naturally sorted immutable list that contains all warps matching this Matchers criteria.
   *
   * @return a list of all matches
   */
  public ImmutableList<Warp> getMatches() {
    return Ordering.natural().immutableSortedCopy(matchingWarps);
  }

  /**
   * Gets a sorted immutable list that contains all warps matching this Matchers criteria.
   *
   * @param comparator the comparator used to sort the list
   * @return a list of all matches
   */
  public ImmutableList<Warp> getMatches(Comparator<Warp> comparator) {
    return Ordering.from(comparator).immutableSortedCopy(matchingWarps);
  }

  /**
   * Gets an Optional that contains the Warp that matches this Matchers criteria and is the first when sorting all
   * matching warps naturally (if such a Warp exists).
   *
   * @return a matching Warp
   */
  public Optional<Warp> getMatch() {
    return IterableUtils.getFirst(getMatches());
  }

  /**
   * Gets an Optional that contains the Warp that matches this Matchers criteria and is the first when sorting all
   * matching warps with the given Comparator (if such a Warp exists).
   *
   * @param comparator the comparator used to sort the matches
   * @return a matching Warp
   */
  public Optional<Warp> getMatch(Comparator<Warp> comparator) {
    return IterableUtils.getFirst(getMatches(comparator));
  }

  /**
   * Returns whether this MatchList contains any elements.
   *
   * @return true if this Matcher is empty
   */
  public boolean isEmpty() {
    return matchingWarps.isEmpty();
  }
}
