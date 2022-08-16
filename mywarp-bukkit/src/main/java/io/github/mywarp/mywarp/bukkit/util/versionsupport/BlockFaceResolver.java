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

package io.github.mywarp.mywarp.bukkit.util.versionsupport;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

import java.util.Optional;

/**
 * Resolves the face a block is attached to.
 */
public interface BlockFaceResolver extends VersionSupportable {

  /**
   * Attempts to resolve the BlockFace the given Block is attached to. If the block is not attached to another block,
   * Optional.empty() is returned.
   *
   * <p> However, depending on the implementation, this method may return a BlockFace even though the given Block is
   * not attachable. To prevent against this, callers should check the Material of the given blcok before calling this
   * method.</p>
   *
   * @param toTest the Block to test
   * @return theBlockFace toTest is attached to or Optional.empty()
   */
  Optional<BlockFace> getBlockFace(Block toTest);
}
