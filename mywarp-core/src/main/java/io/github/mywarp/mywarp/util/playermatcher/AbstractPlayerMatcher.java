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

package io.github.mywarp.mywarp.util.playermatcher;

import java.util.Objects;

/**
 * A Invitation that takes and stores a criteria that implementations use for matching.
 */
abstract class AbstractPlayerMatcher<C> implements PlayerMatcher {

  protected final C criteria;

  AbstractPlayerMatcher(C criteria) {
    this.criteria = Objects.requireNonNull(criteria);
  }

  /**
   * Gets the criteria.
   *
   * @return the criteria
   */
  public C getCriteria() {
    return criteria;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AbstractPlayerMatcher that = (AbstractPlayerMatcher) o;
    return Objects.equals(criteria, that.criteria);
  }

  @Override
  public int hashCode() {
    return Objects.hash(criteria);
  }

  @Override
  public String toString() {
    return Objects.toString(criteria);
  }
}
