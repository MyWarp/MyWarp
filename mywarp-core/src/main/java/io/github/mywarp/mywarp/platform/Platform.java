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

package io.github.mywarp.mywarp.platform;

import io.github.mywarp.mywarp.platform.capability.EconomyCapability;
import io.github.mywarp.mywarp.platform.capability.LimitCapability;
import io.github.mywarp.mywarp.platform.capability.TimerCapability;
import io.github.mywarp.mywarp.platform.paginatedcontent.PaginatedContent;
import io.github.mywarp.mywarp.platform.paginatedcontent.SimplePaginatedContent;
import io.github.mywarp.mywarp.warp.storage.SqlDataService;
import java.util.Optional;

/**
 * A platform MyWarp has been adapted to run on.
 */
public interface Platform {

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
   * Gets an Optional with the instance of the given class or {@link Optional#empty()} if this Platform is unable to
   * provide support.
   *
   * <p>None of the capabilities requested by calling this method are required for MyWarp to run. The following
   * capabilities can be expected and should - if possible - be covered: <ul> <li>{@link EconomyCapability}</li>
   * <li>{@link LimitCapability}</li>
   * <li>{@link TimerCapability}</li> </ul></p>
   *
   * @param capabilityClass the class of the requested capability
   * @param <C>             the type of the capability
   * @return an Optional with an instance of the requested capability
   */
  <C> Optional<C> getCapability(Class<C> capabilityClass);

  /**
   * Creates a {@link SqlDataService} as described by the given {@code config}.
   *
   * <p>The configuration is given as a String. This allows implementations to decide, how the String should be
   * formatted as implementations might need different options or provide different bundled configurations (such as
   * aliases).</p>
   *
   * <p>If the given String is invalid, a {@link InvalidFormatException} may be thrown. This exception allows callers
   * to get the expected format in human readable form.</p>
   *
   * @param config the configuration string
   * @return the {@code SqlDataService}
   * @throws InvalidFormatException if the given String has an invalid format
   */
  SqlDataService createDataService(String config) throws InvalidFormatException;

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

  /**
   * Creates a new Builder for {@link PaginatedContent} instances.
   *
   * <p>Platforms should overwrite this method, and implement their own instances of {@link PaginatedContent} and
   * {@link PaginatedContent.Builder}, if the platform provides a native solution to display paginated content.</p>
   *
   * @return a new builder for PaginatedContent
   */
  default PaginatedContent.Builder createPaginatedContentBuilder() {
    return SimplePaginatedContent.builder();
  }
}
