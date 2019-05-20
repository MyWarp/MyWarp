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

package io.github.mywarp.mywarp.bukkit.util.versionsupport;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement;

import java.util.Optional;

/**
 * Resolves the attached BlockFace using {@link Directional}.
 */
@IgnoreJRERequirement
class BlockFaceResolver113 implements BlockFaceResolver {

  static BlockFaceResolver create() throws ClassNotFoundException {
    // this will throw an ClassNotFoundException on anything lower than 1.13
    // because 'org.bukkit.block.data.Directional' does not exist before 1.13
    Class.forName("org.bukkit.block.data.Directional");
    return new BlockFaceResolver113();
  }

  @Override
  public Optional<BlockFace> getBlockFace(Block toTest) {
    BlockData blockData = toTest.getBlockData();

    if (blockData instanceof Directional) {
      //Directional.getFacing() returns the opposite of the BlockFace toTest is attached to.
      //Therefore, we have to invert the BlockFace before returning it.
      return Optional.of(((Directional) blockData).getFacing().getOppositeFace());
    }
    return Optional.empty();
  }
}
