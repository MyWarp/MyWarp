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

package io.github.mywarp.mywarp.bukkit.util.material;

import com.google.common.collect.ImmutableSet;

import io.github.mywarp.mywarp.util.MyWarpLogger;

import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

import javax.annotation.Nullable;

/**
 * Uses information provided by a Configuration before defaulting to {@link Material#isSolid()}.
 */
//REVIEW write proper documentation
public class ConfigurableMaterialInfo implements MaterialInfo {

  private static final Logger log = MyWarpLogger.getLogger(ConfigurableMaterialInfo.class);

  private final ImmutableSet<Material> dangerousToStandOn;
  private final ImmutableSet<Material> safeToStandOn;
  private final ImmutableSet<Material> dangerousToStandWithin;
  private final ImmutableSet<Material> safeToStandWithin;

  /**
   * Creates an instance from the given configuration.
   *
   * @param config the configuration
   */
  public ConfigurableMaterialInfo(Configuration config) {
    dangerousToStandOn = fromConfig("standOn.dangerous", config);
    safeToStandOn = fromConfig("standOn.safe", config);
    dangerousToStandWithin = fromConfig("standWithin.dangerous", config);
    safeToStandWithin = fromConfig("standWithin.safe", config);
  }

  private static ImmutableSet<Material> fromConfig(String path, ConfigurationSection config) {
    ImmutableSet.Builder<Material> builder = ImmutableSet.builder();
    config.getStringList(path).forEach(name -> {
      @Nullable Material material = Material.getMaterial(name);
      if (material != null) {
        builder.add(material);
      } else {
        log.debug("'{}' does not match any known Material.", name);
      }
    });
    return builder.build();
  }

  @Override
  public boolean safeToStandOn(Material material) {
    if (safeToStandOn.contains(material)) {
      return true;
    }
    if (dangerousToStandOn.contains(material)) {
      return false;
    }
    return material.isSolid();
  }

  @Override
  public boolean dangerousToStandWithin(Material material) {
    if (dangerousToStandWithin.contains(material)) {
      return true;
    }
    if (safeToStandWithin.contains(material)) {
      return false;
    }
    return material.isSolid();
  }
}
