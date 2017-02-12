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

package me.taylorkelly.mywarp.warp.storage.converter;

import org.jooq.Converter;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Converts {@link Timestamp} values to {@link Date}s and back.
 */
public class DateTimestampConverter implements Converter<Timestamp, Date> {

  private static final long serialVersionUID = 5420942769269889198L;

  @Override
  public Date from(Timestamp databaseObject) {
    if (databaseObject == null) {
      return null;
    }

    return new Date(databaseObject.getTime());
  }

  @Override
  public Timestamp to(Date userObject) {
    if (userObject == null) {
      return null;
    }

    return new Timestamp(userObject.getTime());
  }

  @Override
  public Class<Timestamp> fromType() {
    return Timestamp.class;
  }

  @Override
  public Class<Date> toType() {
    return Date.class;
  }

}
