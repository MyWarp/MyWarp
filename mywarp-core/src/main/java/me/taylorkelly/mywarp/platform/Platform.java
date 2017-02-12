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

package me.taylorkelly.mywarp.platform;

import com.google.common.base.Optional;

import me.taylorkelly.mywarp.warp.storage.ConnectionConfiguration;
import me.taylorkelly.mywarp.warp.storage.RelationalDataService;

import java.io.File;

/**
 * A platform MyWarp has been adapted to run on.
 */
public interface Platform {

  /**
   * Gets the data-folder when running in this Platform. The folder is expected to exist and be read- and writable for
   * MyWarp.
   *
   * @return the data-folder
   */
  File getDataFolder();

  /**
   * Gets the {@link Game} as implemented by this Platform.
   *
   * @return the {@code Game}
   */
  Game getGame();

  /**
   * Gets the {@link Settings} as implemented by this Platform.
   *
   * @return the {@code Settings}
   */
  Settings getSettings();

  /**
   * Gets the {@link PlayerNameResolver} as implemented by this Platform.
   *
   * @return the {@code PlayerNameResolver}
   */
  PlayerNameResolver getPlayerNameResolver();

  /**
   * Gets an Optional with the instance of the given class or {@link Optional#absent()} if this Platform is unable to
   * provide support.
   *
   * <p>None of the capabilities requested by calling this method are required for MyWarp to run. The following
   * capabilities can be expected and should - if possible - be covered: <ul> <li>{@link
   * me.taylorkelly.mywarp.platform.capability.EconomyCapability}</li>
   * <li>{@link me.taylorkelly.mywarp.platform.capability.LimitCapability}</li>
   * <li>{@link me.taylorkelly.mywarp.platform.capability.TimerCapability}</li> </ul></p>
   *
   * @param capabilityClass the class of the requested capability
   * @param <C>             the type of the capability
   * @return an Optional with an instance of the requested capability
   */
  <C> Optional<C> getCapability(Class<C> capabilityClass);

  /**
   * Creates a {@link RelationalDataService} as described by the given {@code config}.
   *
   * @param configuration the configuration
   * @return the {@code RelationalDataService}
   */
  RelationalDataService createDataService(ConnectionConfiguration configuration);

  /**
   * Called when MyWarp is reloaded. By this state, Warps are no longer available; any services that depend on
   * configuration should be reconstructed.
   *
   * <p>Unless errors occur, calls to this method are followed by a call to {@link #onWarpsLoaded()} shortly after.</p>
   */
  void onCoreReload();

  /**
   * Called when the core has successfully loaded warps from the storage system and populated the active WarpManager. By
   * this state, all initialization should be complete.
   */
  void onWarpsLoaded();
}
