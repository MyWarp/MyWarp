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

package io.github.mywarp.mywarp.warp.storage.converter;

import org.jooq.Converter;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Converts {@link Timestamp} values to {@link Instant}s and back.
 */
public class InstantTimestampConverter implements Converter<Timestamp, Instant> {

  private static final long serialVersionUID = 5420942769269889198L;

  @Override
  public Instant from(Timestamp databaseObject) {
    if (databaseObject == null) {
      return null;
    }

    return databaseObject.toInstant();
  }

  @Override
  public Timestamp to(Instant userObject) {
    if (userObject == null) {
      return null;
    }

    return Timestamp.from(userObject);
  }

  @Override
  public Class<Timestamp> fromType() {
    return Timestamp.class;
  }

  @Override
  public Class<Instant> toType() {
    return Instant.class;
  }

}
