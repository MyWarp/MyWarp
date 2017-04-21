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

package io.github.mywarp.mywarp.command.parametric.provider;

import static com.google.common.base.Preconditions.checkArgument;

import com.sk89q.intake.parametric.AbstractModule;

import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.command.parametric.annotation.Modifiable;
import io.github.mywarp.mywarp.command.parametric.annotation.Usable;
import io.github.mywarp.mywarp.command.parametric.annotation.Viewable;
import io.github.mywarp.mywarp.command.parametric.annotation.WarpName;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.LocalEntity;
import io.github.mywarp.mywarp.platform.LocalPlayer;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.platform.Settings;
import io.github.mywarp.mywarp.warp.Warp;
import io.github.mywarp.mywarp.warp.WarpManager;
import io.github.mywarp.mywarp.warp.authorization.AuthorizationResolver;
import io.github.mywarp.mywarp.warp.storage.ConnectionConfiguration;

import java.io.File;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Provides most of MyWarp's internal objects by converting the user given arguments.
 */
public class BaseModule extends AbstractModule {

  private final WarpManager warpManager;
  private final AuthorizationResolver authorizationResolver;
  private final PlayerNameResolver playerNameResolver;
  private final Game game;
  private final Settings settings;
  private CommandHandler commandHandler;
  private File base;

  /**
   * Creates an instance.
   *
   * @param warpManager           the WarpManager to use
   * @param authorizationResolver the AuthorizationResolver to use
   * @param playerNameResolver    the PlayerNameResolver to use
   * @param game                  the Game to use
   * @param settings              the Settings to use
   * @param commandHandler        the CommandHandler to use
   * @param base                  the base File to use
   */
  public BaseModule(WarpManager warpManager, AuthorizationResolver authorizationResolver,
                    PlayerNameResolver playerNameResolver, Game game, Settings settings, CommandHandler commandHandler,
                    File base) {
    this.warpManager = warpManager;
    this.authorizationResolver = authorizationResolver;
    this.playerNameResolver = playerNameResolver;
    this.game = game;
    this.settings = settings;
    this.commandHandler = commandHandler;
    this.base = base;
  }

  @Override
  protected void configure() {
    //game related objects
    bind(LocalPlayer.class).toProvider(new PlayerProvider(game));
    bind(UUID.class).toProvider(new PlayerIdentifierProvider(playerNameResolver));

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
        .toProvider(new WarpNameProvider(warpManager, commandHandler, settings));

    //configuration
    bind(ConnectionConfiguration.class).toProvider(new ConnectionConfigurationProvider());
    bind(File.class).toProvider(new FileProvider(base));
  }
}
