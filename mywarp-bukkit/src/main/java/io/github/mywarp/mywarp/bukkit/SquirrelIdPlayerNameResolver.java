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

package io.github.mywarp.mywarp.bukkit;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.sk89q.squirrelid.Profile;
import com.sk89q.squirrelid.cache.HashMapCache;
import com.sk89q.squirrelid.cache.ProfileCache;
import com.sk89q.squirrelid.cache.SQLiteCache;
import com.sk89q.squirrelid.resolver.BukkitPlayerService;
import com.sk89q.squirrelid.resolver.CacheForwardingService;
import com.sk89q.squirrelid.resolver.CombinedProfileService;
import com.sk89q.squirrelid.resolver.HttpRepositoryService;
import io.github.mywarp.mywarp.bukkit.util.AbstractListener;
import io.github.mywarp.mywarp.platform.NoSuchProfileException;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.util.MyWarpLogger;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.Nullable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.slf4j.Logger;

/**
 * A PlayerNameResolver implementation that uses the SquirrelID library to lookup UUIDs.
 */
class SquirrelIdPlayerNameResolver extends AbstractListener implements PlayerNameResolver, AutoCloseable {

  private static final Logger log = MyWarpLogger.getLogger(SquirrelIdPlayerNameResolver.class);

  private final ExecutorService executorService = Executors.newCachedThreadPool();
  private final CacheForwardingService resolver;
  private ProfileCache cache;

  /**
   * Creates an instance, using the given file to store the SQLite cache.
   *
   * @param cacheFile the cache file
   */
  SquirrelIdPlayerNameResolver(File cacheFile) {
    try {
      cache = new SQLiteCache(cacheFile);
    } catch (IOException e) {
      log.warn("Failed to access SQLite profile cache. Player names will be resolved from memory.", e);
      cache = new HashMapCache();
    }
    resolver =
        new CacheForwardingService(
            new CombinedProfileService(BukkitPlayerService.getInstance(), HttpRepositoryService.forMinecraft()), cache);
  }

  @Override
  public CompletableFuture<io.github.mywarp.mywarp.platform.Profile> getByUniqueId(UUID uniqueId) {
    return CompletableFuture.supplyAsync(() -> {
      Profile profile = cache.getIfPresent(uniqueId);
      if (profile == null) {
        throw new CompletionException(new NoSuchProfileException(uniqueId));
      }
      return BukkitProfile.of(profile);
    }, executorService);
  }

  @Override
  public CompletableFuture<Set<io.github.mywarp.mywarp.platform.Profile>> getByUniqueId(Iterable<UUID> uniqueIds) {
    return CompletableFuture.supplyAsync(() -> {
      ImmutableMap<UUID, Profile> allPresent = cache.getAllPresent(uniqueIds);
      Set<io.github.mywarp.mywarp.platform.Profile> ret = new HashSet<>();

      uniqueIds.forEach(uuid -> {
        Profile profile = allPresent.get(uuid);
        if (profile != null) {
          ret.add(BukkitProfile.of(profile));
        }
      });

      return ret;
    }, executorService);
  }

  @Override
  public CompletableFuture<io.github.mywarp.mywarp.platform.Profile> getByName(String name) {
    return CompletableFuture.supplyAsync(() -> {
      try {
        Profile profile = resolver.findByName(name);
        if (profile != null) {
          return BukkitProfile.of(profile);
        }
      } catch (IOException | InterruptedException e) {
        log.error(String.format("Failed to find UUID for '%s'.", name), e);
      }
      throw new CompletionException(new NoSuchProfileException(name));
    }, executorService);
  }

  @Override
  public CompletableFuture<Set<io.github.mywarp.mywarp.platform.Profile>> getByName(Iterable<String> names) {
    return CompletableFuture.supplyAsync(() -> {
      // 'names' can contain duplicates as well as the same name with different cases.
      // User names in Minecraft are case-insensitive so we do not need to lookup these duplicates.
      Set<String> lookup = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
      Iterables.addAll(lookup, names);

      Set<io.github.mywarp.mywarp.platform.Profile> ret = new HashSet<>();

      try {
        resolver.findAllByName(lookup, input -> {
          if (input != null) {
            ret.add(BukkitProfile.of(input));
          }
          return true;
        });
      } catch (IOException | InterruptedException e) {
        log.error("Failed to lookup UUIDs.", e);
      }

      return ret;
    }, executorService);
  }

  /**
   * Called asynchronous when a player logs in.
   *
   * @param event the AsyncPlayerPreLoginEvent
   */
  @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
  public void onPlayerLogin(AsyncPlayerPreLoginEvent event) {
    if (!event.getLoginResult().equals(AsyncPlayerPreLoginEvent.Result.ALLOWED)) {
      return;
    }
    //SquirrelID's cache is thread-safe
    cache.put(new Profile(event.getUniqueId(), event.getName()));
  }

  @Override
  public void close() {
    executorService.shutdown();
  }

  static class BukkitProfile implements io.github.mywarp.mywarp.platform.Profile {

    private final UUID uniqueId;
    @Nullable
    private final String name;

    BukkitProfile(UUID uniqueId, @Nullable String name) {
      this.uniqueId = uniqueId;
      this.name = name;
    }

    static BukkitProfile of(Profile sqProfile) {
      return new BukkitProfile(sqProfile.getUniqueId(), sqProfile.getName());
    }

    @Override
    public UUID getUuid() {
      return uniqueId;
    }

    @Override
    public Optional<String> getName() {
      return Optional.ofNullable(name);
    }
  }
}
