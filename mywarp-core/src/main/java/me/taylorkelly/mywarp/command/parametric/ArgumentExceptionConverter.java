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

package me.taylorkelly.mywarp.command.parametric;

import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.parametric.handler.ExceptionContext;
import com.sk89q.intake.parametric.handler.ExceptionConverterHelper;
import com.sk89q.intake.parametric.handler.ExceptionMatch;
import com.sk89q.intake.parametric.provider.exception.NonnumericalInputException;
import com.sk89q.intake.parametric.provider.exception.OverRangeException;
import com.sk89q.intake.parametric.provider.exception.StringFormatException;
import com.sk89q.intake.parametric.provider.exception.UnderRangeException;

import me.taylorkelly.mywarp.command.CommandHandler;
import me.taylorkelly.mywarp.command.parametric.provider.exception.NonMatchingInputException;
import me.taylorkelly.mywarp.util.i18n.DynamicMessages;

/**
 * Converts all {@link ArgumentParseException}s into localized {@link InvalidUsageException}s.
 *
 * <p>This class is useless when registered with a {@link com.sk89q.intake.parametric.ParametricBuilder} directly.
 * Instead, another {@code ExceptionConverter} that converts {@link ArgumentParseException}s should be registered with
 * the {@code ParametricBuilder} which in turn calls this class.</p>
 */
public class ArgumentExceptionConverter extends ExceptionConverterHelper {

  private static final DynamicMessages msg = new DynamicMessages(CommandHandler.RESOURCE_BUNDLE_NAME);

  //-- PrimitivesModule

  /**
   * Converts a StringFormatException to an InvalidUsageException.
   *
   * @param e       the StringFormatException
   * @param context the context {@code e} was thrown in
   * @throws InvalidUsageException the converted exception
   */
  @ExceptionMatch
  public void convert(StringFormatException e, ExceptionContext context) throws InvalidUsageException {
    throwInvalidUsage(msg.getString("exception.primitives.invalid.format", e.getFormat()), context, e);
  }

  /**
   * Converts a NonnumericalInputException to an InvalidUsageException.
   *
   * @param e       the NonnumericalInputException
   * @param context the context {@code e} was thrown in
   * @throws InvalidUsageException the converted exception
   */
  @ExceptionMatch
  public void convert(NonnumericalInputException e, ExceptionContext context) throws InvalidUsageException {
    throwInvalidUsage(msg.getString("exception.primitives.invalid.non-number", e.getInput()), context, e);
  }

  /**
   * Converts a OverRangeException to an InvalidUsageException.
   *
   * @param e       the OverRangeException
   * @param context the context {@code e} was thrown in
   * @throws InvalidUsageException the converted exception
   */
  @ExceptionMatch
  public void convert(OverRangeException e, ExceptionContext context) throws InvalidUsageException {
    throwInvalidUsage(msg.getString("exception.primitives.invalid.greater-or-equal", e.getMaximum(), e.getInput()),
            context, e);
  }

  /**
   * Converts a UnderRangeException to an InvalidUsageException.
   *
   * @param e       the UnderRangeException
   * @param context the context {@code e} was thrown in
   * @throws InvalidUsageException the converted exception
   */
  @ExceptionMatch
  public void convert(UnderRangeException e, ExceptionContext context) throws InvalidUsageException {
    throwInvalidUsage(msg.getString("exception.primitives.invalid.less-or-equal", e.getMinimum(), e.getInput()),
            context, e);
  }

  //-- BaseModule

  /**
   * Converts a NonMatchingInputException to an InvalidUsageException.
   *
   * @param e       the NonMatchingInputException
   * @param context the context {@code e} was thrown in
   * @throws InvalidUsageException the converted exception
   */
  @ExceptionMatch
  public void convert(NonMatchingInputException e, ExceptionContext context) throws InvalidUsageException {
    throwInvalidUsage(e.getLocalizedMessage(), context, e);
  }

  /**
   * Throws an {@link InvalidUsageException} with the given values.
   *
   * @param errorMsg the error message
   * @param context  the exception context
   * @param cause    the original cause
   * @throws InvalidUsageException the created exception
   */
  private void throwInvalidUsage(String errorMsg, ExceptionContext context, ArgumentParseException cause)
          throws InvalidUsageException {

    if (cause.getParameter() != null) {
      throw new InvalidUsageException(
              msg.getString("exception.argument.error", cause.getParameter().getName(), errorMsg), context.getCommand(),
              context.getAliasStack(), false, cause);
    }
    throw new InvalidUsageException(msg.getString("exception.argument.error.unknown", errorMsg), context.getCommand(),
            context.getAliasStack(), false, cause);

  }

}
