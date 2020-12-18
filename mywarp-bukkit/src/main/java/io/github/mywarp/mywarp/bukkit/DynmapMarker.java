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

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import io.github.mywarp.mywarp.bukkit.settings.BukkitSettings;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.util.MyWarpLogger;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.warp.PlaceholderResolver;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.event.WarpAdditionEvent;
import io.github.mywarp.mywarp.warp.event.WarpDeletionEvent;
import io.github.mywarp.mywarp.warp.event.WarpEvent;
import io.github.mywarp.mywarp.warp.event.WarpUpdateEvent;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.GenericMarker;
import org.dynmap.markers.Marker;
import org.dynmap.markers.MarkerAPI;
import org.dynmap.markers.MarkerIcon;
import org.dynmap.markers.MarkerSet;
import org.slf4j.Logger;

/**
 * Displays markers for warps using <a href="https://github.com/webbukkit/dynmap">dynmap</a>.
 *
 * <p>Warps are only displayed if they match a filter ({@link Predicate#test(Object)})  returns {@code true}) given
 * when an instance is created.</p>
 *
 * <p>In addition to using the provided methods to manually add or remove warps, instances can be registered at an
 * {@link EventBus} that raises {@link WarpEvent}s to automatically add, update or delete warps when the appropriate
 * events are raised.</p>
 */
public class DynmapMarker {

  private static final String DEFAULT_SET_ID = "mywarp.warps";
  private static final String DEFAULT_ICON_ID = "mywarp_warp-32";
  private static final String MARKER_ID_PREFIX = "mywarp.warp.";

  private static final Logger log = MyWarpLogger.getLogger(DynmapMarker.class);
  private static final DynamicMessages MESSAGES = new DynamicMessages("io.github.mywarp.mywarp.lang.DynmapMarkers");

  private final BukkitSettings settings;
  private final Game game;
  private final Plugin plugin;
  private final MarkerAPI api;
  private final Predicate<Warp> filter;

  /**
   * Creates an instance that works on the given {@code DynmapCommonAPI} implementation, uses the given {@code
   * MyWarpPlugin} and {@code settings} and only displays warps if they match the given {@code filter}.
   *
   * @param dynmap   the {@code DynmapCommonAPI} implementation of the running Dynmap instance
   * @param plugin   MyWarp's plugin instance
   * @param platform the Bukkit platform MyWarp runs on
   * @param filter   the filter warps must matched in order to be displayed
   */
  DynmapMarker(DynmapCommonAPI dynmap, MyWarpPlugin plugin, BukkitPlatform platform, Predicate<Warp> filter) {
    this(dynmap.getMarkerAPI(), plugin, platform.getSettings(), platform.getGame(), filter);
  }

  private DynmapMarker(MarkerAPI api, MyWarpPlugin plugin, BukkitSettings settings, Game game, Predicate<Warp> filter) {
    this.api = api;
    this.plugin = plugin;
    this.game = game;
    this.settings = settings;
    this.filter = filter;
  }

  /**
   * Adds markers for all given warps. Warps that do not match the filter of this DynmapMarker instance are silently
   * ignored.
   *
   * @param warps the warps to create markers for
   */
  public void addMarker(Warp... warps) {
    addMarker(Arrays.asList(warps));
  }

  /**
   * Adds markers for all given warps. Warps that do not match the filter of this DynmapMarker instance are silently
   * ignored.
   *
   * @param warps the warps to create markers for
   */
  public void addMarker(Iterable<Warp> warps) {
    for (Warp warp : warps) {
      if (filter.test(warp)) {
        createMarker(warp);
      }
    }
  }

  /**
   * Deletes all markers of the given warps. Warps without markers are ignored.
   *
   * @param warps the warps whose markers should be deleted
   */
  public void deleteMarker(Warp... warps) {
    deleteMarker(Arrays.asList(warps));
  }

  /**
   * Deletes all markers of the given warps. Warps without markers are ignored.
   *
   * @param warps the warps whose markers should be deleted
   */
  public void deleteMarker(Iterable<Warp> warps) {
    for (Warp warp : warps) {
      getMarker(warp).ifPresent(GenericMarker::deleteMarker);
    }
  }

  /**
   * Clears all existing markers previously created by MyWarp.
   */
  public void clear() {
    getOrCreateSet().deleteMarkerSet();
  }

  /**
   * Called when a Warp is added.
   *
   * @param event the event
   * @deprecated will be privatized once support for old Guava versions is removed
   */
  @Deprecated
  @Subscribe
  public void onWarpAddition(WarpAdditionEvent event) {
    addMarker(event.getWarp());
  }

  /**
   * Called when a Warp is deleted.
   *
   * @param event the event
   * @deprecated will be privatized once support for old Guava versions is removed
   */
  @Deprecated
  @Subscribe
  public void onWarpDeletion(WarpDeletionEvent event) {
    deleteMarker(event.getWarp());
  }

  /**
   * Called when a Warp is updated.
   *
   * @param event the event
   * @deprecated will be privatized once support for old Guava versions is removed
   */
  @Deprecated
  @Subscribe
  public void onWarpUpdate(WarpUpdateEvent event) {
    Warp warp = event.getWarp();
    Optional<Marker> markerOptional = getMarker(warp);

    if (!filter.test(warp)) {
      markerOptional.ifPresent(GenericMarker::deleteMarker);
      return;
    }

    if (!markerOptional.isPresent()) {
      createMarker(warp);
      return;
    }

    Marker marker = markerOptional.get();
    if (event.getType().equals(WarpUpdateEvent.UpdateType.LOCATION)) {
      Vector3d pos = warp.getPosition();
      Optional<LocalWorld> worldOptional = game.getWorld(warp.getWorldIdentifier());
      if (!worldOptional.isPresent()) {
        log.debug("The world of the warp {} is not loaded. The warp is ignored.", warp);
        return;
      }
      marker.setLocation(worldOptional.get().getName(), pos.getX(), pos.getY(), pos.getZ());
    }
    marker.setLabel(label(warp), true);
  }

  /**
   * Gets the {@code MarkerSet} appropriated for Warp markers, or creates and configures it according to the settings,
   * if it does not yet exist.
   *
   * @return the appropriated {@code MarkerSet}
   * @throws IllegalStateException if Dynmap fails to create the {@code MarkerSet}
   */
  private MarkerSet getOrCreateSet() {
    MarkerSet ret = api.getMarkerSet(DEFAULT_SET_ID);

    if (ret == null) {
      ret =
          api.createMarkerSet(DEFAULT_SET_ID, settings.getDynmapLayerDisplayName(), ImmutableSet.of(getOrCreateIcon()),
              false);
      if (ret == null) {
        throw new IllegalStateException("Failed to create MarkerSet '" + DEFAULT_SET_ID + "', Dynmap returns null.");
      }

      ret.setMarkerSetLabel(settings.getDynmapLayerDisplayName());
      ret.setLayerPriority(settings.getDynmapLayerPriority());
      ret.setHideByDefault(settings.isDynmapLayerHiddenByDefault());
      ret.setLabelShow(settings.isDynmapMarkerShowLable());
      ret.setMinZoom(settings.getDynmapMarkerMinZoom());
    }
    return ret;
  }

  /**
   * Gets the {@code MarkerIcon} appropriated for Warp markers, or creates it, if it does not yet exist.
   *
   * @return the appropriate {@code MarkerIcon}
   */
  private MarkerIcon getOrCreateIcon() {
    String identifier = settings.getDynmapMarkerIconId();

    MarkerIcon ret = api.getMarkerIcon(identifier);

    if (ret != null) {
      return ret;
    }

    //user has configured a custom icon
    if (!identifier.equals(DEFAULT_ICON_ID)) {
      log.error("MarkerIcon {} does not exist. Using the bundled default.", identifier);
      identifier = DEFAULT_ICON_ID;

      ret = api.getMarkerIcon(identifier);

      if (ret != null) {
        return ret;
      }
    }

    log.debug("Bundled MarkerIcon does not exist. Creating it...");
    ret = api.createMarkerIcon(DEFAULT_ICON_ID, "Warp", plugin.getResource("mywarp_warp-32.png"));

    if (ret != null) {
      return ret;
    }

    log.error("Failed to create bundled MarkerIcon ({}), falling back to dynmap's default.", DEFAULT_ICON_ID);
    return api.getMarkerIcon(MarkerIcon.DEFAULT);
  }

  /**
   * Creates and returns a {@code Marker} for the given {@code Warp}.
   *
   * @param warp the {@code Warp}
   * @return the {@code Marker}
   * @throws IllegalStateException if Dynmap fails to create the {@code Marker}
   */
  @Nullable
  private Marker createMarker(Warp warp) {
    Optional<LocalWorld> worldOptional = game.getWorld(warp.getWorldIdentifier());
    if (!worldOptional.isPresent()) {
      log.debug("The world of the warp {} is not loaded. The warp is ignored.", warp);
      return null;
    }
    Marker
        ret =
        getOrCreateSet()
            .createMarker(identifier(warp), label(warp), true, worldOptional.get().getName(), warp.getPosition().getX(),
                warp.getPosition().getY(), warp.getPosition().getZ(), getOrCreateIcon(), false);

    Preconditions.checkState(ret != null, "Failed to create Marker for " + warp + ", Dynmap returns null.");

    return ret;
  }

  /**
   * Gets an {@code Optional} with the {@code Marker} of the given {@code Warp}, if such a marker exists.
   *
   * @param warp the {@code Warp}
   * @return the Marker
   */
  private Optional<Marker> getMarker(Warp warp) {
    return Optional.ofNullable(getOrCreateSet().findMarker(identifier(warp)));
  }

  /**
   * Returns the appropriate label for a marker of the given {@code warp}.
   *
   * @param warp the {@code Warp}
   * @return the label
   * @see Marker#getLabel()
   */
  private String label(Warp warp) {
    return PlaceholderResolver.from(warp)
        .resolvePlaceholders(MESSAGES.getString("marker.label", settings.getLocalizationDefaultLocale()));
  }

  /**
   * Returns the appropriate identifier for a marker of the given {@code Warp}.
   *
   * @param warp the {@code Warp}
   * @return the identifier
   * @see Marker#getMarkerID()
   */
  private String identifier(Warp warp) {
    return MARKER_ID_PREFIX + warp.getName();
  }

}
