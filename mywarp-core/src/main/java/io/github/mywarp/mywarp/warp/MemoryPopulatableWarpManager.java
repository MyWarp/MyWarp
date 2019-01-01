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

package io.github.mywarp.mywarp.warp;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Stores managed warp in memory.
 */
public class MemoryPopulatableWarpManager implements PopulatableWarpManager {

  private final Map<String, Warp> warpMap = new HashMap<String, Warp>();

  @Override
  public void add(Warp warp) {
    checkArgument(!containsByName(warp.getName()), "A warp with the name '" + warp.getName() + "' does already exist!");
    warpMap.put(warp.getName(), warp);
  }

  @Override
  public void remove(Warp warp) {
    warpMap.remove(warp.getName());
  }

  @Override
  public boolean contains(Warp warp) {
    return containsByName(warp.getName());
  }

  @Override
  public boolean containsByName(String name) {
    return warpMap.containsKey(name);
  }

  @Override
  public Optional<Warp> getByName(String name) {
    return Optional.ofNullable(warpMap.get(name));
  }

  @Override
  public Collection<Warp> getAll(Predicate<Warp> predicate) {
    return warpMap.values().stream().filter(predicate).collect(Collectors.toList());
  }

  @Override
  public int getNumberOfWarps(Predicate<Warp> predicate) {
    return getAll(predicate).size();
  }

  @Override
  public int getNumberOfAllWarps() {
    return warpMap.size();
  }

  @Override
  public void populate(Iterable<Warp> warps) {
    warps.forEach(this::add);
  }

  @Override
  public void depopulate() {
    warpMap.clear();
  }
}
