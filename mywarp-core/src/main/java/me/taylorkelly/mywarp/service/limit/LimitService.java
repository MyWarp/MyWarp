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

package me.taylorkelly.mywarp.service.limit;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import me.taylorkelly.mywarp.platform.LocalPlayer;
import me.taylorkelly.mywarp.platform.LocalWorld;
import me.taylorkelly.mywarp.platform.capability.LimitCapability;
import me.taylorkelly.mywarp.util.IterableUtils;
import me.taylorkelly.mywarp.util.WarpUtils;
import me.taylorkelly.mywarp.warp.Warp;
import me.taylorkelly.mywarp.warp.WarpManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
   * Evaluates whether the given creator exceeds limit on the given LocalWorld, testing against the given Limit.Type.
   *
   * @param creator         the creator
   * @param world           the LocalWorld
   * @param type            the Limit.Type
   * @param evaluateParents Whether parents of the given {@code type} will be evaluated too (recursively). This is
   *                        useful if not just a specific Limit.Type might be exceeded, but also a more general limit
   *                        that includes the given one
   * @return an EvaluationResult representing the result of the evaluation
   */
  public EvaluationResult evaluateLimit(LocalPlayer creator, LocalWorld world, Limit.Type type,
                                        boolean evaluateParents) {
    if (!type.canDisobey(creator, world)) {

      Iterable<Warp> filteredWarps = warpManager.getAll(WarpUtils.isCreator(creator.getUniqueId()));
      Limit limit = capability.getLimit(creator, world);

      List<Limit.Type> limitsToCheck = Lists.newArrayList(type);
      if (evaluateParents) {
        limitsToCheck.addAll(type.getParentsRecusive());
      }

      for (Limit.Type check : limitsToCheck) {
        EvaluationResult result = evaluateLimit(limit, check, filteredWarps);
        if (result.exceedsLimit()) {
          return result;
        }
      }
    }
    return EvaluationResult.limitMet();
  }

  private EvaluationResult evaluateLimit(Limit limit, Limit.Type type, Iterable<Warp> filteredWarps) {
    int limitMaximum = limit.getLimit(type);
    if (IterableUtils.atLeast(Iterables.filter(filteredWarps, type.getCondition()), limitMaximum)) {
      return EvaluationResult.exceeded(type, limitMaximum);
    }
    return EvaluationResult.limitMet();
  }

  /**
   * Gets all warps created by the given player mapped under the applicable {@link Limit}. <p/> The map is guaranteed to
   * include all limit that effect the given player. If no warp exists for a certain limit, {@code getByName(Limit)}
   * will return an empty list.
   *
   * @param creator the creator
   * @return a Map with all matching warps
   */
  public Map<Limit, List<Warp>> getWarpsPerLimit(LocalPlayer creator) {
    Collection<Warp> warps = warpManager.getAll(WarpUtils.isCreator(creator.getUniqueId()));
    Map<Limit, List<Warp>> ret = new HashMap<Limit, List<Warp>>();

    for (Limit limit : capability.getEffectiveLimits(creator)) {
      ret.put(limit, new ArrayList<Warp>());
    }

    // sort warps to limit
    for (Warp warp : warps) {
      for (Limit limit : ret.keySet()) {
        if (limit.isAffectedWorld(warp.getWorldIdentifier())) {
          ret.get(limit).add(warp);
        }
      }
    }
    return ret;
  }

  /**
   * The result of a limit evaluation.
   */
  public static class EvaluationResult {

    private final boolean exceedsLimit;
    @Nullable
    private final Limit.Type exceededLimit;
    @Nullable
    private final Integer limitMaximum;

    /**
     * Creates an instance with the given values.
     *
     * @param exceedsLimit  whether a limit was exceeded
     * @param exceededLimit the exceeded limit or {@code null} if no limit was exceeded
     * @param limitMaximum  the maximum number of warps a user can create under the exceeded limit or {@code null} if no
     *                      limit was exceeded
     */
    private EvaluationResult(boolean exceedsLimit, @Nullable Limit.Type exceededLimit, @Nullable Integer limitMaximum) {
      this.exceedsLimit = exceedsLimit;
      this.exceededLimit = exceededLimit;
      this.limitMaximum = limitMaximum;
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
    public Limit.Type getExceededLimit() {
      checkState(exceededLimit != null);
      return exceededLimit;
    }

    /**
     * Gets the maximum number of warps a user can create under the exceeded limit.
     *
     * @return the maximum number of warps of the exceeded limit
     * @throws IllegalStateException if no limit is exceeded and thus {@link #exceedsLimit()} returns {@code true}.
     */
    public Integer getLimitMaximum() {
      checkState(limitMaximum != null);
      return limitMaximum;
    }

    /**
     * Creates an instance that indicates all limits are met.
     *
     * @return a new instance
     */
    protected static EvaluationResult limitMet() {
      return new EvaluationResult(false, null, -1);
    }

    /**
     * Creates an instance that inidicates the given limit is exceeded.
     *
     * @param exceededLimit the exceeded limit
     * @param limitMaximum  the limit maximum
     * @return a new instance
     */
    protected static EvaluationResult exceeded(Limit.Type exceededLimit, int limitMaximum) {
      return new EvaluationResult(true, exceededLimit, limitMaximum);
    }

  }
}
