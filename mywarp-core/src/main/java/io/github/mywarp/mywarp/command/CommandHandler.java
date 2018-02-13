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

package io.github.mywarp.mywarp.command;

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

import io.github.mywarp.mywarp.MyWarp;
import io.github.mywarp.mywarp.command.parametric.ActorAuthorizer;
import io.github.mywarp.mywarp.command.parametric.CommandResourceProvider;
import io.github.mywarp.mywarp.command.parametric.EconomyInvokeHandler;
import io.github.mywarp.mywarp.command.parametric.ExceptionConverter;
import io.github.mywarp.mywarp.command.parametric.namespace.ProvidedModule;
import io.github.mywarp.mywarp.command.parametric.provider.BaseModule;
import io.github.mywarp.mywarp.platform.Actor;
import io.github.mywarp.mywarp.platform.Game;
import io.github.mywarp.mywarp.platform.Platform;
import io.github.mywarp.mywarp.platform.PlayerNameResolver;
import io.github.mywarp.mywarp.platform.Settings;
import io.github.mywarp.mywarp.platform.capability.EconomyCapability;
import io.github.mywarp.mywarp.platform.capability.LimitCapability;
import io.github.mywarp.mywarp.platform.capability.TimerCapability;
import io.github.mywarp.mywarp.service.economy.EconomyService;
import io.github.mywarp.mywarp.service.economy.FeeType;
import io.github.mywarp.mywarp.service.limit.LimitService;
import io.github.mywarp.mywarp.service.teleport.EconomyTeleportService;
import io.github.mywarp.mywarp.service.teleport.HandlerTeleportService;
import io.github.mywarp.mywarp.service.teleport.TeleportService;
import io.github.mywarp.mywarp.service.teleport.TimerTeleportService;
import io.github.mywarp.mywarp.util.Message;
import io.github.mywarp.mywarp.util.MyWarpLogger;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;
import io.github.mywarp.mywarp.util.teleport.TeleportHandler;
import io.github.mywarp.mywarp.warp.WarpManager;
import io.github.mywarp.mywarp.warp.authorization.AuthorizationResolver;

import org.apache.commons.lang.text.StrBuilder;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Nullable;

/**
 * Handles MyWarp's commands.
 */
public final class CommandHandler {

  public static final String RESOURCE_BUNDLE_NAME = "io.github.mywarp.mywarp.lang.Commands";
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
         platform.getGame(), platform.getSettings(), myWarp.getTeleportHandler());
  }

  private CommandHandler(MyWarp myWarp, Platform platform, WarpManager warpManager,
                         AuthorizationResolver authorizationResolver, PlayerNameResolver playerNameResolver, Game game,
                         Settings settings, TeleportHandler teleportHandler) {

    // create injector and register modules
    Injector injector = Intake.createInjector();
    injector.install(new BaseModule(this, platform, authorizationResolver, warpManager));
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
            .registerMethods(new ImportCommands(warpManager, playerNameResolver, game)).graph().getDispatcher();
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
      dispatcher.call(command, createNamespace(caller), new ArrayList<>());

      //handle errors
    } catch (InvocationCommandException | AuthorizationException e) {
      if (e instanceof AuthorizationException || e.getCause() instanceof AuthorizationException) {
        //for some reason, handling AuthorizationExceptions thrown manually by commands in the ExceptionConverter has
        // no result, hence this messy solution.
        caller.sendError(msg.getString("exception.insufficient-permission"));

      } else {
        // An InvocationCommandException can only be thrown if a thrown
        // Exception is not covered by our ExceptionConverter and is
        // therefore unintended behavior.
        caller.sendError(msg.getString("exception.unknown"));
        log.error(String.format("The command '%s' could not be executed.", command), e);
      }

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
    Set<String> usableCommands = new TreeSet<>();

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
