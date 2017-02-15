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

package me.taylorkelly.mywarp.command;

import com.google.common.base.Optional;
import com.sk89q.intake.CommandCallable;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.CommandMapping;
import com.sk89q.intake.Intake;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.dispatcher.NoSubcommandsException;
import com.sk89q.intake.dispatcher.SubcommandRequiredException;
import com.sk89q.intake.fluent.CommandGraph;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.provider.PrimitivesModule;
import com.sk89q.intake.util.auth.AuthorizationException;

import me.taylorkelly.mywarp.MyWarp;
import me.taylorkelly.mywarp.command.parametric.ActorAuthorizer;
import me.taylorkelly.mywarp.command.parametric.CommandResourceProvider;
import me.taylorkelly.mywarp.command.parametric.EconomyInvokeHandler;
import me.taylorkelly.mywarp.command.parametric.ExceptionConverter;
import me.taylorkelly.mywarp.command.parametric.namespace.ProvidedModule;
import me.taylorkelly.mywarp.command.parametric.provider.BaseModule;
import me.taylorkelly.mywarp.platform.Actor;
import me.taylorkelly.mywarp.platform.Game;
import me.taylorkelly.mywarp.platform.Platform;
import me.taylorkelly.mywarp.platform.PlayerNameResolver;
import me.taylorkelly.mywarp.platform.capability.EconomyCapability;
import me.taylorkelly.mywarp.platform.capability.LimitCapability;
import me.taylorkelly.mywarp.platform.capability.TimerCapability;
import me.taylorkelly.mywarp.service.economy.EconomyService;
import me.taylorkelly.mywarp.service.economy.FeeType;
import me.taylorkelly.mywarp.service.limit.LimitService;
import me.taylorkelly.mywarp.service.teleport.EconomyTeleportService;
import me.taylorkelly.mywarp.service.teleport.HandlerTeleportService;
import me.taylorkelly.mywarp.service.teleport.TeleportService;
import me.taylorkelly.mywarp.service.teleport.TimerTeleportService;
import me.taylorkelly.mywarp.util.Message;
import me.taylorkelly.mywarp.util.MyWarpLogger;
import me.taylorkelly.mywarp.util.i18n.DynamicMessages;
import me.taylorkelly.mywarp.util.teleport.TeleportHandler;
import me.taylorkelly.mywarp.warp.WarpManager;
import me.taylorkelly.mywarp.warp.authorization.AuthorizationResolver;

import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

/**
 * Handles MyWarp's commands.
 */
public final class CommandHandler {

  public static final String RESOURCE_BUNDLE_NAME = "me.taylorkelly.mywarp.lang.Commands";
  private static final char CMD_PREFIX = '/';

  private static final DynamicMessages msg = new DynamicMessages(RESOURCE_BUNDLE_NAME);
  private static final Logger log = MyWarpLogger.getLogger(CommandHandler.class);

  private final Dispatcher dispatcher;

  /**
   * Creates an instance.
   *
   * @param myWarp   the MyWarp instance commands will hook into
   * @param platform the platform commands will hook into
   */
  public CommandHandler(MyWarp myWarp, Platform platform) {
    this(myWarp, platform, myWarp.getWarpManager(), myWarp.getAuthorizationResolver(), platform.getPlayerNameResolver(),
            platform.getGame(), myWarp.getTeleportHandler());
  }

  /**
   * Creates an instance.
   *
   * @param myWarp                the MyWarp instance commands will use
   * @param platform              the Platform commands will use
   * @param warpManager           the WarpManager commands will use
   * @param authorizationResolver the AuthorizationResolver commands will use
   * @param playerNameResolver    the PlayerNameResolver commands will use
   * @param game                  the Game commands will use
   * @param teleportHandler       the TeleportHandler commands will use
   */
  private CommandHandler(MyWarp myWarp, Platform platform, WarpManager warpManager,
                         AuthorizationResolver authorizationResolver, PlayerNameResolver playerNameResolver, Game game,
                         TeleportHandler teleportHandler) {

    // create injector and register modules
    Injector injector = Intake.createInjector();
    injector.install(
            new BaseModule(warpManager, authorizationResolver, playerNameResolver, game, this,
                    platform.getDataFolder()));
    injector.install(new PrimitivesModule());
    injector.install(new ProvidedModule());

    //create the builder
    ParametricBuilder builder = new ParametricBuilder(injector);
    builder.setAuthorizer(new ActorAuthorizer());
    builder.setResourceProvider(new CommandResourceProvider());
    builder.addExceptionConverter(new ExceptionConverter());

    //economy support (optional)
    Optional<EconomyCapability> economyOptional = platform.getCapability(EconomyCapability.class);
    if (economyOptional.isPresent()) {
      builder.addInvokeListener(new EconomyInvokeHandler(new EconomyService(economyOptional.get())));
    }

    //create services...

    //...basic TeleportService used by '/warp player <player> <warp>'
    TeleportService basic = new HandlerTeleportService(teleportHandler, playerNameResolver);

    //...usage service used by '/warp <warp>'
    TeleportService usageService = basic;
    if (economyOptional.isPresent()) {
      usageService = new EconomyTeleportService(basic, new EconomyService(economyOptional.get()), FeeType.WARP_TO);
    }
    Optional<TimerCapability> timerOptional = platform.getCapability(TimerCapability.class);
    if (timerOptional.isPresent()) {
      usageService = new TimerTeleportService(usageService, game, timerOptional.get());
    }

    //...limit service
    @Nullable LimitService limitService = null;
    Optional<LimitCapability> limitOptional = platform.getCapability(LimitCapability.class);
    if (limitOptional.isPresent()) {
      limitService = new LimitService(limitOptional.get(), warpManager);
    }

    //create some command instances (used below)
    UsageCommands usageCmd = new UsageCommands(usageService);
    UsageCommands.DefaultUsageCommand defaultUsageCmd = usageCmd.new DefaultUsageCommand();

    //register commands
    dispatcher =
            new CommandGraph().builder(builder).commands().registerMethods(usageCmd).group("warp", "mywarp", "mw")
                    .registerMethods(defaultUsageCmd).registerMethods(
                    new InformativeCommands(warpManager, limitService, authorizationResolver, game, playerNameResolver))
                    .registerMethods(new ManagementCommands(warpManager, limitService))
                    .registerMethods(new SocialCommands(game, playerNameResolver, limitService))
                    .registerMethods(new UtilityCommands(myWarp, this, basic, game)).group("import", "migrate")
                    .registerMethods(new ImportCommands(warpManager, platform, playerNameResolver, game)).graph()
                    .getDispatcher();
  }

  /**
   * Gets a list of suggestions based on the given {@code arguments}.
   * <p/>
   * More appropriate suggestions will come first, less appropriate after. If no suggestions are appropriate, an empty
   * list will be returned.
   *
   * @param arguments the arguments already given by the user
   * @param caller    the command caller
   * @return a list of suggestions
   */
  public List<String> getSuggestions(String arguments, Actor caller) {
    try {
      return dispatcher.getSuggestions(arguments, createNamespace(caller));
    } catch (CommandException e) {
      caller.sendMessage(e.getLocalizedMessage());
    }
    return Collections.emptyList();
  }

  /**
   * Executes the given {@code command} with the given Actor.
   *
   * @param command the full command string as given by the caller
   * @param caller  the calling Actor
   */
  public void callCommand(String command, Actor caller) {
    //call the command
    try {
      dispatcher.call(command, createNamespace(caller), new ArrayList<String>());

      //handle errors
    } catch (InvocationCommandException e) {
      // An InvocationCommandException can only be thrown if a thrown
      // Exception is not covered by our ExceptionConverter and is
      // therefore unintended behavior.
      caller.sendError(msg.getString("exception.unknown"));
      log.error(String.format("The command '%s' could not be executed.", command), e);

    } catch (SubcommandRequiredException e) {
      Message.Builder error = createUsageString(e);

      error.appendNewLine();
      error.append(msg.getString("exception.subcommand.choose"));

      caller.sendMessage(error.build());
    } catch (NoSubcommandsException e) {
      Message.Builder error = createUsageString(e);

      error.appendNewLine();
      error.append(msg.getString("exception.subcommand.none"));

      caller.sendMessage(error.build());
    } catch (InvalidUsageException e) {
      Message.Builder error = createUsageString(e);

      String errorMsg = e.getLocalizedMessage();
      if (errorMsg != null && !errorMsg.isEmpty()) {
        error.appendNewLine();
        error.append(e.getLocalizedMessage());
      }
      if (e.isFullHelpSuggested()) {
        error.appendNewLine();
        error.append(Message.Style.INFO);
        error.append(e.getCommand().getDescription().getHelp());
      }

      caller.sendMessage(error.build());

    } catch (CommandException e) {
      caller.sendError(e.getLocalizedMessage());

    } catch (AuthorizationException e) {
      caller.sendError(msg.getString("exception.insufficient-permission"));
    }
  }


  /**
   * Returns whether the given String is a sub command of the {@code warp} command.
   *
   * @param str the String
   * @return {@code true} if the String is a sub command of the warp command
   */
  public boolean isSubCommand(String str) {
    //XXX this should probably be covered by unit tests
    CommandMapping mapping = dispatcher.get("mywarp");
    if (mapping == null || !(mapping.getCallable() instanceof Dispatcher)) {
      return false;
    }
    Dispatcher dispatcher = (Dispatcher) mapping.getCallable();
    return dispatcher.contains(str);
  }

  /**
   * Gets a Set with all commands usable for the given Actor. <p>The commands are represented as Strings. Each command
   * is prefixed with {@code /}, aliases are separated by {@code |}.</p>
   *
   * @param forWhom the Actor for whom the returned commands should be usable
   * @return all usable commands as strings
   */
  Set<String> getUsableCommands(Actor forWhom) {
    Set<String> usableCommands = new TreeSet<String>();

    flattenCommands(usableCommands, createNamespace(forWhom), "", dispatcher);

    return usableCommands;
  }

  /**
   * Adds a all commands from the given Dispatcher to the given Collection, transforming them into Strings that include
   * the full command string as the user would enter it. Commands that are not usable under the given CommandLocals are
   * excluded and the given prefix is added before all commands. <p>This algorithm actually calls every Command, it is
   * <b> not</b> lazy.</p>
   *
   * @param entries    the Collection the Commands are added to
   * @param namespace  the Namespace
   * @param prefix     the prefix
   * @param dispatcher the Dispatcher to add
   */
  private void flattenCommands(Collection<String> entries, Namespace namespace, String prefix, Dispatcher dispatcher) {
    for (CommandMapping rootCommand : dispatcher.getCommands()) {
      flattenCommands(entries, namespace, prefix, rootCommand);
    }
  }

  /**
   * Adds a all commands from the given CommandMapping to the given Collection, transforming them into Strings that
   * include the full command string as the user would enter it. Commands that are not usable under the given
   * CommandLocals are excluded and the given prefix is added before all commands. <p>This algorithm actually calls
   * every Command, it is <b>not</b> lazy.</p>
   *
   * @param entries   the Collection the Commands are added to
   * @param namespace the Namespace
   * @param prefix    the prefix
   * @param current   the CommandMapping to add
   */
  private void flattenCommands(Collection<String> entries, Namespace namespace, String prefix, CommandMapping current) {
    CommandCallable currentCallable = current.getCallable();
    if (!currentCallable.testPermission(namespace)) {
      return;
    }
    StrBuilder builder = new StrBuilder().append(prefix).append(prefix.isEmpty() ? CMD_PREFIX : ' ');

    //subcommands
    if (currentCallable instanceof Dispatcher) {
      builder.append(current.getPrimaryAlias());
      flattenCommands(entries, namespace, builder.toString(), (Dispatcher) currentCallable);
    } else {
      // the end
      builder.appendWithSeparators(current.getAllAliases(), "|");
      builder.append(' ');
      builder.append(current.getDescription().getUsage());
      entries.add(builder.toString());
    }
  }

  /**
   * Creates a new Namespace instance and adds the given Actor to it.
   *
   * @param forWhom the Actor
   * @return the new Namespace
   */
  private Namespace createNamespace(Actor forWhom) {
    Namespace namespace = new Namespace();
    namespace.put(Actor.class, forWhom);
    return namespace;
  }

  /**
   * Returns a Message.Builder that contains the command's usage information taken from the given Exception.
   *
   * @param e the InvalidUsageException
   * @return the populated Message.Builder
   */
  private Message.Builder createUsageString(InvalidUsageException e) {
    Message.Builder ret = Message.builder();
    ret.append(Message.Style.ERROR);
    ret.append(CMD_PREFIX);
    ret.appendWithSeparators(e.getAliasStack(), " ");
    ret.append(" ");
    ret.append(e.getCommand().getDescription().getUsage());
    return ret;
  }
}
