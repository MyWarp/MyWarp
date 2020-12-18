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

package io.github.mywarp.mywarp.bukkit.util.permission;

/**
 * A bundle of various values that are assigned with a single permission.
 */
public abstract class ValueBundle implements Comparable<ValueBundle> {

  private final String identifier;
  private final String basePermission;

  /**
   * Creates an instance.
   *
   * @param identifier     the identifier if this individual ValueBundle
   * @param basePermission the base permission that is identical for all bundles that bundle the same type of values
   */
  protected ValueBundle(String identifier, String basePermission) {
    this.identifier = identifier;
    this.basePermission = basePermission.intern();
  }

  /**
   * Gets the full permission that a user needs to have to have the bundled values.
   *
   * @return the full permission of this bundle
   */
  public String getPermission() {
    return basePermission + "." + identifier;
  }

  @Override
  public int compareTo(ValueBundle that) {
    return this.identifier.compareTo(that.identifier);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ValueBundle that = (ValueBundle) o;

    if (!identifier.equals(that.identifier)) {
      return false;
    }
    return basePermission.equals(that.basePermission);

  }

  @Override
  public int hashCode() {
    int result = identifier.hashCode();
    result = 31 * result + basePermission.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "ValueBundle{" + "identifier='" + identifier + '\'' + ", basePermission='" + basePermission + '\'' + '}';
  }
}
