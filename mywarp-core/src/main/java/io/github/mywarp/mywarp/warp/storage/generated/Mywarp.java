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

/**
 * This class is generated by jOOQ
 */
package io.github.mywarp.mywarp.warp.storage.generated;


import io.github.mywarp.mywarp.warp.storage.generated.tables.*;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;
import org.jooq.Record;

import javax.annotation.Generated;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jooq.Record;

/**
 * This class is generated by jOOQ.
 */
@Generated(value = {"http://www.jooq.org", "jOOQ version:3.6.2"}, comments = "This class is generated by jOOQ")
@SuppressWarnings({"all", "unchecked", "rawtypes"})
public class Mywarp extends SchemaImpl {

  private static final long serialVersionUID = -111186094;

  /**
   * The reference instance of <code>mywarp</code>
   */
  public static final Mywarp MYWARP = new Mywarp();

  /**
   * No further instances allowed
   */
  private Mywarp() {
    super("mywarp");
  }

  @Override
  public final List<Table<?>> getTables() {
    List result = new ArrayList();
    result.addAll(getTables0());
    return result;
  }

  private final List<Table<?>> getTables0() {
    return Arrays.<Table<?>>asList(Group.GROUP, Player.PLAYER, Warp.WARP, WarpGroupMap.WARP_GROUP_MAP,
        WarpPlayerMap.WARP_PLAYER_MAP, World.WORLD);
  }
}
