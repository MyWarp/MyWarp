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

package io.github.mywarp.mywarp.platform;

import com.google.common.collect.ImmutableSet;

import java.util.Locale;

/**
 * Provides all user-configurable settings for MyWarp. Implementations are expected to be immutable.
 */
public interface Settings {

  /**
   * Returns whether the world access should be controlled directly by MyWarp.
   *
   * @return {@code true} if world access should be controlled
   */
  boolean isControlWorldAccess();

  /**
   * Returns whether horses ridden by the entity who is teleported, should be teleported too.
   *
   * @return {@code true} if ridden horses should be teleported too
   */
  boolean isTeleportTamedHorses();

  /**
   * Returns whether an effect should be shown at the location of entities who are teleported.
   *
   * @return {@code true} if the effect should be shown
   */
  boolean isShowTeleportEffect();

  /**
   * Returns whether a warp names should be compared case sensitively.
   *
   * @return {@code true} if warp names should be compared case sensitively
   */
  boolean isCaseSensitiveWarpNames();

  /**
   * Gets the default locale.
   *
   * @return the default locale
   */
  Locale getLocalizationDefaultLocale();

  /**
   * Returns whether localizations should be resolved individually per player rather than globally.
   *
   * @return {@code true} if localizations are per player
   */
  boolean isLocalizationPerPlayer();

  /**
   * Returns whether warp signs are enabled.
   *
   * @return {@code true} if warp signs are enabled
   */
  boolean isWarpSignsEnabled();

  /**
   * Gets all identifiers for warp signs.
   *
   * @return all warp sign identifiers
   */
  ImmutableSet<String> getWarpSignsIdentifiers();

  /**
   * Returns whether players should be informed when they are criteria to or uninvited from warps.
   *
   * @return {@code true} if players should be informed
   */
  boolean isInformPlayerOnInvitation();

}
