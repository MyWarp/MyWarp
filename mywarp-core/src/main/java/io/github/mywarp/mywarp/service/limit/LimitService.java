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

package io.github.mywarp.mywarp.service.limit;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.LocalWorld;
import io.github.mywarp.mywarp.platform.capability.LimitCapability;
import io.github.mywarp.mywarp.service.limit.Limit.Value;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Predicate;

import javax.annotation.Nullable;

/**
 * Resolves and evaluates warp creation limits for individual players on a set of worlds.
 */
public class LimitService {

  private final LimitCapability capability;
  private final WarpManager warpManager;

  /**
   * Creates an instance that uses the given {@code capability} to resolve limits an operates on the given {@code
   * warpManager}.
   *
   * @param capability  the capability
   * @param warpManager the warp manager to evaluate limits on
   */
  public LimitService(LimitCapability capability, WarpManager warpManager) {
    this.capability = capability;
    this.warpManager = warpManager;
  }

  /**
   * Evaluates whether the given {@code creator} can add a new Warp of the given {@code warpType} to the given {@code
   * world}.
   *
   * <p>This method will return a positive result if the current number of warps counted under any limit applicable for
   * creator, world and warp type falls below the allowed maximum by at least 1.</p>
   *
   * @param creator  The creator who wants to add a new Warp
   * @param world    The world the warp should be added to
   * @param warpType the type of the warp to add
   * @return the result of the evaluation
   */
  public EvaluationResult canAdd(LocalPlayer creator, LocalWorld world, Warp.Type warpType) {
    return evaluate(creator, world, Value.getApplicableValues(warpType));
  }

  /**
   * Evaluates whether the given {@code creator} can change the type of an existing Warp on the given {@code world} from
   * {@code oldType} to {@code newType}.
   *
   * <p>This method will return a positive result if the current number of warps counted under the limit applicable for
   * creator, world and the new warp type falls below the allowed maximum by at least 1. Any limit that counts warps
   * from both, old and new type, is ignored.</p>
   *
   * @param creator The creator of the warp
   * @param world   The world of the warp
   * @param oldType the old type of the warp
   * @param newType the new type of the warp
   * @return the result of the evaluation
   */
  public EvaluationResult canChangeType(LocalPlayer creator, LocalWorld world, Warp.Type oldType, Warp.Type newType) {

    ImmutableSet.Builder<Value> builder = ImmutableSet.builder();
    for (Value toCheck : Value.getApplicableValues(newType)) {
      if (toCheck.getWarpTypes().contains(oldType)) {
        continue;
      }
      builder.add(toCheck);
    }
    return evaluate(creator, world, builder.build());
  }

  private EvaluationResult evaluate(LocalPlayer creator, LocalWorld world, Iterable<Value> values) {
    LimitValueWarpMapping valueWarpMapping = new LimitValueWarpMapping(warpManager, createPredicate(creator, world));

    for (Value toCheck : values) {
      if (toCheck.canDisobey(creator, world)) {
        continue;
      }
      int max = capability.getLimit(creator, world).get(toCheck);

      if (valueWarpMapping.atLeast(toCheck, max)) {
        return EvaluationResult.exceeded(toCheck, max);
      }
    }
    return EvaluationResult.limitMet();
  }

  private static Predicate<Warp> createPredicate(final LocalPlayer creator, LocalWorld... worlds) {
    return createPredicate(creator, Arrays.asList(worlds));
  }

  private static Predicate<Warp> createPredicate(final LocalPlayer creator, final Iterable<LocalWorld> worlds) {
    return input -> input.isCreator(creator.getUniqueId()) && containsIdentifiedWorld(worlds,
                                                                                      input.getWorldIdentifier());
  }

  /**
   * Returns an ImmutableMap with every Limit that could affect the given player mapped to the corresponding
   * LimitValueMapping.
   *
   * <p>Which limit actually applies to a certain player depends on the world, the check is run for. The map returned by
   * this method contains all limits that could apply for a player; it therefore contains all possible LimitValueMapping
   * and can be used to display a player's assets.</p>
   *
   * @param player the player
   * @return a Map that mapps every limit that could affect the given player to the corresponding LimtiValueMapping
   */
  public ImmutableMap<Limit, LimitValueWarpMapping> getAssets(LocalPlayer player) {
    ImmutableMap.Builder<Limit, LimitValueWarpMapping> builder = ImmutableMap.builder();

    for (Limit limit : capability.getEffectiveLimits(player)) {
      builder.put(limit, new LimitValueWarpMapping(warpManager, createPredicate(player, limit.getAffectedWorlds())));
    }
    return builder.build();
  }

  private static boolean containsIdentifiedWorld(Iterable<LocalWorld> worlds, UUID worldIdentifier) {
    for (LocalWorld world : worlds) {
      if (world.getUniqueId().equals(worldIdentifier)) {
        return true;
      }
    }
    return false;
  }

  /**
   * The result of a limit evaluation.
   */
  public static class EvaluationResult {

    private final boolean exceedsLimit;
    @Nullable
    private final Value exceededValue;
    @Nullable
    private final Integer allowedMaximum;

    /**
     * Creates an instance with the given values.
     *
     * @param exceedsLimit    whether a limit was exceeded
     * @param exceededValue   the exceeded limit or {@code null} if no limit was exceeded
     * @param allowedMaximum the maximum number of warps a user can create under the exceeded limit or {@code null} if
     *                        no limit was exceeded
     */
    private EvaluationResult(boolean exceedsLimit, @Nullable Value exceededValue, @Nullable Integer allowedMaximum) {
      this.exceedsLimit = exceedsLimit;
      this.exceededValue = exceededValue;
      this.allowedMaximum = allowedMaximum;
    }

    /**
     * Returns whether a limit is exceeded.
     *
     * @return true if a limit is exceeded
     */
    public boolean exceedsLimit() {
      return exceedsLimit;
    }

    /**
     * Gets an Optional containing the exceeded limit.
     *
     * @return the exceeded limit
     * @throws IllegalStateException if no limit is exceeded and thus {@link #exceedsLimit()} returns {@code true}.
     */
    public Value getExceededValue() {
      checkState(exceededValue != null);
      return exceededValue;
    }

    /**
     * Gets the maximum number of warps a user can create under the exceeded limit.
     *
     * @return the maximum number of warps of the exceeded limit
     * @throws IllegalStateException if no limit is exceeded and thus {@link #exceedsLimit()} returns {@code true}.
     */
    public Integer getAllowedMaximum() {
      checkState(allowedMaximum != null);
      return allowedMaximum;
    }

    /**
     * Creates an instance that indicates all limits are met.
     *
     * @return a new instance
     */
    static EvaluationResult limitMet() {
      return new EvaluationResult(false, null, null);
    }

    /**
     * Creates an instance that indicates the given limit is exceeded.
     *
     * @param exceededValue  the exceeded limit
     * @param allowedMaximum the limit maximum
     * @return a new instance
     */
    static EvaluationResult exceeded(Value exceededValue, int allowedMaximum) {
      return new EvaluationResult(true, exceededValue, allowedMaximum);
    }

  }
}
