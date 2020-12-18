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

package io.github.mywarp.mywarp.bukkit;

import io.github.mywarp.mywarp.bukkit.util.versionsupport.BlockFaceResolver;
import io.github.mywarp.mywarp.bukkit.util.versionsupport.VersionSupport;
import io.github.mywarp.mywarp.platform.Sign;
import io.github.mywarp.mywarp.util.BlockFace;
import java.util.Optional;

/**
 * A reference to an existing sign in Bukkit.
 */
class BukkitSign implements Sign {

  private final BlockFaceResolver blockFaceResolver = VersionSupport.getBlockFaceResolver();
  private final org.bukkit.block.Sign bukkitSign;

  /**
   * Creates an instance that references the given sign.
   *
   * @param bukkitSign the sign
   */
  BukkitSign(org.bukkit.block.Sign bukkitSign) {
    this.bukkitSign = bukkitSign;
  }

  @Override
  public String getLine(int line) {
    return bukkitSign.getLine(line);
  }

  @Override
  public void setLine(int line, String text) {
    bukkitSign.setLine(line, text);
  }

  /**
   * Returns whether this sign is attached to the given block face.
   *
   * @param toTest the block face.
   * @return {@code true} if this sign is attached to the given block face
   */
  boolean isAttachedTo(BlockFace toTest) {
    Optional<BlockFace>
        signBlockFace =
        blockFaceResolver.getBlockFace(bukkitSign.getBlock()).flatMap(BukkitAdapter::adapt);
    return signBlockFace.isPresent() && signBlockFace.get().equals(toTest);
  }
}
