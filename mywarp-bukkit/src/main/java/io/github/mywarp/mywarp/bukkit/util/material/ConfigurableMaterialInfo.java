/*
 * Copyright (C) 2011 - 2021, MyWarp team and contributors
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
import javax.annotation.Nullable;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.slf4j.Logger;

/**
 * Provides information about Materials depending partly on a given configuration.
 *
 * <p>The aim of {@link MaterialInfo} is to provide information about materials that are (1) safe to stand on and
 * materials which are (2) dangerous to stand withing. Bukkit only provides us with {@link Material#isSolid()} which
 * provides information about (1) and (2) for most, but not for all Materials: e.g. {@link Material#LAVA} is not solid,
 * but still dangerous to stand in.</p>
 *
 * <p>This class solves this issue by only forwarding calls to {@link Material#isSolid()}, if a given Material is not
 * explicitly present within the configuration provided upon initialization. <b>Thus, the configuration should include
 * any Material for which {@link Material#isSolid()} returns a false result.</b></p>
 *
 * <p>The configuration holds the exact name of individual Materials. Materials not existing at runtime are
 * ignored; thus the given configuration may also include Materials from other versions than the running one. Dangerous
 * materials always take preference over safe ones.</p>
 *
 * <p>The following paths are expected:
 * <table>
 * <tr>
 * <th>Path</th>
 * <th>Materials that are</th>
 * </tr>
 * <tr>
 * <td>{@code standOn.dangerous}</td>
 * <td>solid but dangerous</td>
 * </tr>
 * <tr>
 * <td>{@code standOn.safe}</td>
 * <td>not solid but safe </td>
 * </tr>
 * <tr>
 * <td>{@code standWithin.dangerous}</td>
 * <td>not solid but dangerous</td>
 * </tr>
 * <tr>
 * <td>{@code standWithin.safe}</td>
 * <td>solid but safe</td>
 * </tr>
 * <tr>
 * <td>{@code clickable}</td>
 * <td>toggleable by clicking on theme</td>
 * </tr>
 * <tr>
 * <td>{@code triggerable}</td>
 * <td>triggerable by stepping on theme</td>
 * </tr>
 * </table>
 * See the bundled {@code material.info.yml} for examples.</p>
 */
public class ConfigurableMaterialInfo implements MaterialInfo {

  private static final Logger log = MyWarpLogger.getLogger(ConfigurableMaterialInfo.class);

  private final ImmutableSet<Material> dangerousToStandOn;
  private final ImmutableSet<Material> safeToStandOn;
  private final ImmutableSet<Material> dangerousToStandWithin;
  private final ImmutableSet<Material> safeToStandWithin;
  private final ImmutableSet<Material> clickable;
  private final ImmutableSet<Material> triggerable;

  /**
   * Creates an instance using the given configuration.
   *
   * @param config the configuration
   */
  public ConfigurableMaterialInfo(Configuration config) {
    dangerousToStandOn = fromConfig("standOn.dangerous", config);
    safeToStandOn = fromConfig("standOn.safe", config);
    dangerousToStandWithin = fromConfig("standWithin.dangerous", config);
    safeToStandWithin = fromConfig("standWithin.safe", config);
    clickable = fromConfig("clickable", config);
    triggerable = fromConfig("triggerable", config);
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
  public boolean safeToStandOn(Material toTest) {
    if (dangerousToStandOn.contains(toTest)) {
      return false;
    }
    if (safeToStandOn.contains(toTest)) {
      return true;
    }
    return toTest.isSolid();
  }

  @Override
  public boolean dangerousToStandWithin(Material toTest) {
    if (dangerousToStandWithin.contains(toTest)) {
      return true;
    }
    if (safeToStandWithin.contains(toTest)) {
      return false;
    }
    return toTest.isSolid();
  }

  @Override
  public boolean isClickable(Material toTest) {
    return clickable.contains(toTest);
  }

  @Override
  public boolean isTriggerable(Material toTest) {
    return triggerable.contains(toTest);
  }
}
