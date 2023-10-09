/*
 * Copyright (C) 2011 - 2022, MyWarp team and contributors
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

package io.github.mywarp.mywarp.command.parametric.provider;

import com.sk89q.intake.parametric.AbstractModule;
import com.sk89q.intake.parametric.Key;
import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.command.parametric.annotation.Modifiable;
import io.github.mywarp.mywarp.command.parametric.annotation.Usable;
import io.github.mywarp.mywarp.command.parametric.annotation.Viewable;
import io.github.mywarp.mywarp.command.parametric.annotation.WarpName;
import io.github.mywarp.mywarp.command.util.Invitation;
import io.github.mywarp.mywarp.platform.*;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;
import io.github.mywarp.mywarp.warp.authorization.AuthorizationResolver;
import io.github.mywarp.mywarp.warp.storage.SqlDataService;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Provides most of MyWarp's internal objects by converting the user given arguments.
 */
public class BaseModule extends AbstractModule {

  private final WarpManager warpManager;
  private final AuthorizationResolver authorizationResolver;
  private final CommandHandler commandHandler;
  private final Platform platform;

  /**
   * Creates an instance.
   *
   * @param commandHandler        the CommandHandler to use
   * @param platform              the platform to use
   * @param authorizationResolver the AuthorizationResolver to use
   * @param warpManager           the WarpManager to use
   */
  public BaseModule(CommandHandler commandHandler, Platform platform, AuthorizationResolver authorizationResolver,
      WarpManager warpManager) {
    this.commandHandler = commandHandler;
    this.platform = platform;
    this.authorizationResolver = authorizationResolver;
    this.warpManager = warpManager;
  }

  private static <T> Key<T> key(TypeCapture<T> token) {
    return Key.get(token.getType());
  }

  @Override
  protected void configure() {
    //game related objects
    bind(LocalPlayer.class).toProvider(new PlayerProvider(platform.getGame()));
    bind(key(new TypeCapture<CompletableFuture<Profile>>() {
    })).toProvider(new PlayerIdProvider(platform.getPlayerNameResolver()));

    //warps
    bind(Warp.class).annotatedWith(Viewable.class).toProvider(new WarpProvider(authorizationResolver, warpManager) {
      @Override
      Predicate<Warp> isValid(AuthorizationResolver resolver, Actor actor) {
        return resolver.isViewable(actor);
      }
    });
    bind(Warp.class).annotatedWith(Modifiable.class).toProvider(new WarpProvider(authorizationResolver, warpManager) {
      @Override
      Predicate<Warp> isValid(AuthorizationResolver resolver, Actor actor) {
        return resolver.isModifiable(actor);
      }
    });
    bind(Warp.class).annotatedWith(Usable.class).toProvider(new WarpProvider(authorizationResolver, warpManager) {
      @Override
      Predicate<Warp> isValid(AuthorizationResolver resolver, Actor actor) {
        checkArgument(actor instanceof LocalEntity, "This Binding must be used by an LocalEntity");
        return resolver.isUsable((LocalEntity) actor);
      }
    });

    //warp name
    bind(String.class).annotatedWith(WarpName.class)
        .toProvider(new WarpNameProvider(warpManager, commandHandler, platform.getSettings()));

    //warp comparators
    bind(key(new TypeCapture<Comparator<Warp>>() {
    })).toProvider(new WarpComparatorProvider());

    //invitations
    bind(key(new TypeCapture<CompletableFuture<Invitation>>() {
    })).toProvider(new InvitationProvider(platform.getPlayerNameResolver()));

    //configuration
    bind(SqlDataService.class).toProvider(new DataServiceProvider(platform));
  }

  //See Guava's TypeToken (https://github.com/google/guava/wiki/ReflectionExplained#typetoken). Unfortunately it is
  // not yet present in Guava 10, so we have to use our own solution.
  //REVIEW when Guava >10: use TypeToken instead
  private abstract class TypeCapture<C> {

    private Type getType() {
      Type superclass = getClass().getGenericSuperclass();
      checkArgument(superclass instanceof ParameterizedType, superclass + " isn't parameterized.");
      return ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

  }
}
