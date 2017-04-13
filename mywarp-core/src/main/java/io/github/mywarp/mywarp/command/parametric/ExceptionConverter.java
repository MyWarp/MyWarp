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

package io.github.mywarp.mywarp.command.parametric;

import com.sk89q.intake.CommandException;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.AlreadyPresentFlagException;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.MissingArgumentException;
import com.sk89q.intake.argument.MissingFlagValueException;
import com.sk89q.intake.argument.UnusedArgumentException;
import com.sk89q.intake.parametric.handler.ExceptionContext;
import com.sk89q.intake.parametric.handler.ExceptionConverterHelper;
import com.sk89q.intake.parametric.handler.ExceptionMatch;

import io.github.mywarp.mywarp.command.CommandHandler;
import io.github.mywarp.mywarp.command.parametric.namespace.IllegalCommandSenderException;
import io.github.mywarp.mywarp.command.util.NoSuchWorldException;
import io.github.mywarp.mywarp.util.i18n.DynamicMessages;

import javax.annotation.Nullable;

/**
 * Converts specific Exceptions into human readable {@link CommandException}s.
 *
 * <p>External exceptions, such as those  native to Intake, are also localised.</p>
 */
public class ExceptionConverter extends ExceptionConverterHelper {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  private final ArgumentExceptionConverter argumentExceptionConverter = new ArgumentExceptionConverter();

  //-- Arguments

  /**
   * Converts a MissingArgumentException to an InvalidUsageException.
   *
   * @param e       the MissingArgumentException
   * @param context the context {@code e} was thrown in
   * @throws InvalidUsageException the converted exception
   */
  @ExceptionMatch
  public void convert(MissingArgumentException e, ExceptionContext context) throws InvalidUsageException {
    if (e.getParameter() != null) {
      throw new InvalidUsageException(msg.getString("exception.argument.missing", e.getParameter().getName()),
                                      context.getCommand(), context.getAliasStack(), false, e);
    }
    throw new InvalidUsageException(msg.getString("exception.argument.missing.unknown"), context.getCommand(),
                                    context.getAliasStack(), false, e);
  }

  /**
   * Converts a UnusedArgumentException to an InvalidUsageException.
   *
   * @param e       the UnusedArgumentException
   * @param context the context {@code e} was thrown in
   * @throws InvalidUsageException the converted exception
   */
  @ExceptionMatch
  public void convert(UnusedArgumentException e, ExceptionContext context) throws InvalidUsageException {
    throw new InvalidUsageException(msg.getString("exception.argument.unused", e.getUnconsumed()), context.getCommand(),
                                    context.getAliasStack(), false, e);
  }

  /**
   * Converts a ArgumentException to an InvalidUsageException.
   *
   * @param e       the ArgumentException
   * @param context the context {@code e} was thrown in
   * @throws CommandException           the converted exception
   * @throws InvocationCommandException if there is a problem with command invocation
   */
  @ExceptionMatch
  public void convert(ArgumentException e, ExceptionContext context)
      throws InvocationCommandException, CommandException {
    @Nullable Throwable cause = e.getCause();
    argumentExceptionConverter.convert(cause != null ? cause : e, context);
  }

  //-- Flags

  /**
   * Converts a MissingFlagValueException to an InvalidUsageException.
   *
   * @param e       the MissingFlagValueException
   * @param context the context {@code e} was thrown in
   * @throws InvalidUsageException the converted exception
   */
  @ExceptionMatch
  public void convert(MissingFlagValueException e, ExceptionContext context) throws InvalidUsageException {
    throw new InvalidUsageException(msg.getString("exception.flag.value.missing", e.getFlagName()),
                                    context.getCommand(), context.getAliasStack(), true, e);
  }

  /**
   * Converts a AlreadyPresentFlagException to an InvalidUsageException.
   *
   * @param e       the AlreadyPresentFlagException
   * @param context the context {@code e} was thrown in
   * @throws InvalidUsageException the converted exception
   */
  @ExceptionMatch
  public void convert(AlreadyPresentFlagException e, ExceptionContext context) throws InvalidUsageException {
    throw new InvalidUsageException(msg.getString("exception.flag.value.already-given", e.getFlagName()),
                                    context.getCommand(), context.getAliasStack(), true, e);
  }

  //-- Custom

  /**
   * Converts a IllegalCommandSenderException to an CommandException.
   *
   * @param e       the IllegalCommandSenderException
   * @param context the context {@code e} was thrown in
   * @throws InvalidUsageException the converted exception
   */
  @ExceptionMatch
  public void convert(IllegalCommandSenderException e, ExceptionContext context) throws InvalidUsageException {
    throw new InvalidUsageException(e.getLocalizedMessage(), context.getCommand(), context.getAliasStack(), false, e);
  }

  /**
   * Converts a NoSuchWorldException to a CommandException.
   *
   * @param e the NoSuchWorldException
   * @throws CommandException the converted exception
   */
  @ExceptionMatch
  public void convert(NoSuchWorldException e) throws CommandException {
    throw new CommandException(msg.getString("exception.no-such-world", e.getWorldIdentifier()), e);
  }
}
