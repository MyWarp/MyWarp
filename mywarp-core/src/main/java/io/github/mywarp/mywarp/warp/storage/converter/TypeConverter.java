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

package io.github.mywarp.mywarp.warp.storage.converter;

import io.github.mywarp.mywarp.warp.Warp;

import org.jooq.impl.EnumConverter;
import org.jooq.types.UByte;

/**
 * Converts {@link UByte} to {@link Warp.Type} and back.
 */
public class TypeConverter extends EnumConverter<UByte, Warp.Type> {

  private static final long serialVersionUID = -7704911259321558564L;

  /**
   * Initializes this converter.
   */
  public TypeConverter() {
    super(UByte.class, Warp.Type.class);
  }

}
