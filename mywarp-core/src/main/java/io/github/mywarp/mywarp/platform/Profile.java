/*
 * Copyright (C) 2011 - 2019, MyWarp team and contributors
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

package io.github.mywarp.mywarp.platform;

import java.util.Optional;
import java.util.UUID;

/**
 * A profile of a user.
 */
public interface Profile {

  /**
   * Gets the unique identifier of this profile.
   *
   * @return the unique ID
   */
  UUID getUuid();

  /**
   * Gets the name associated with this profile, if any.
   *
   * @return the name
   */
  Optional<String> getName();

  /**
   * Gets the name associated with this profile or, if no such name exists, the profile's unique ID as a String.
   *
   * @return name or unique ID
   */
  default String getNameOrId() {
    return getName().orElse(getUuid().toString());
  }

}
