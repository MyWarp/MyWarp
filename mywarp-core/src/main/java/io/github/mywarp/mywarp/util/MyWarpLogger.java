/*
 * Copyright (C) 2011 - 2018, MyWarp team and contributors
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

package io.github.mywarp.mywarp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import java.util.function.Function;

/**
 * A custom logger that actually delegates all input to an underlying {@link Logger}, but allows to modify the
 * message beforehand.
 */
public class MyWarpLogger implements Logger {

  private static Function<String, String> modifier = input -> input;

  private final Logger logger;

  /**
   * Return a logger named corresponding to the class passed as parameter.
   *
   * @param clazz the returned logger will be named after clazz
   * @see LoggerFactory#getLogger(Class)
   */
  private MyWarpLogger(Class<?> clazz) {
    this.logger = LoggerFactory.getLogger(clazz);
  }

  /**
   * Return a logger named corresponding to the class passed as parameter.
   *
   * @param clazz the returned logger will be named after clazz
   * @return the logger
   * @see LoggerFactory#getLogger(Class)
   */
  public static Logger getLogger(Class<?> clazz) {
    return new MyWarpLogger(clazz);
  }

  private String applyModifier(String msg) {
    return modifier.apply(msg);
  }

  /**
   * Sets the modifier of log messages.
   *
   * <p>Each log message is given to the Function given to this method. The output of this Function is than delegated to
   * the underlying logging framework. </p>
   *
   * <p>This method can be used to append custom prefixes to log messages.</p>
   *
   * @param modifier the modifier
   */
  public static void setModifier(Function<String, String> modifier) {
    MyWarpLogger.modifier = modifier;
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    logger.debug(applyModifier(format), arg1, arg2);
  }

  @Override
  public void debug(Marker marker, String msg) {
    logger.debug(marker, applyModifier(msg));
  }

  @Override
  public void debug(String format, Object arg) {
    logger.debug(applyModifier(format), arg);
  }

  @Override
  public void debug(String msg) {
    logger.debug(applyModifier(msg));
  }

  @Override
  public void debug(String format, Object... arguments) {
    logger.debug(applyModifier(format), arguments);
  }

  @Override
  public void debug(Marker marker, String msg, Throwable t) {
    logger.debug(marker, applyModifier(msg), t);
  }

  @Override
  public void debug(String msg, Throwable t) {
    logger.debug(applyModifier(msg), t);
  }

  @Override
  public void debug(Marker marker, String format, Object... arguments) {
    logger.debug(marker, applyModifier(format), arguments);
  }

  @Override
  public void debug(Marker marker, String format, Object arg) {
    logger.debug(marker, applyModifier(format), arg);
  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    logger.debug(marker, applyModifier(format), arg1, arg2);
  }

  @Override
  public void error(Marker marker, String format, Object... arguments) {
    logger.error(marker, applyModifier(format), arguments);
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    logger.error(applyModifier(format), arg1, arg2);
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    logger.error(marker, applyModifier(format), arg1, arg2);
  }

  @Override
  public void error(String msg, Throwable t) {
    logger.error(applyModifier(msg), t);
  }

  @Override
  public void error(Marker marker, String msg) {
    logger.error(marker, applyModifier(msg));
  }

  @Override
  public void error(Marker marker, String msg, Throwable t) {
    logger.error(marker, applyModifier(msg), t);
  }

  @Override
  public void error(String format, Object arg) {
    logger.error(applyModifier(format), arg);
  }

  @Override
  public void error(String format, Object... arguments) {
    logger.error(applyModifier(format), arguments);
  }

  @Override
  public void error(Marker marker, String format, Object arg) {
    logger.error(marker, applyModifier(format), arg);
  }

  @Override
  public void error(String msg) {
    logger.error(applyModifier(msg));
  }

  @Override
  public String getName() {
    return logger.getName();
  }

  @Override
  public void info(Marker marker, String format, Object arg) {
    logger.info(marker, applyModifier(format), arg);
  }

  @Override
  public void info(Marker marker, String msg, Throwable t) {
    logger.info(marker, applyModifier(msg), t);
  }

  @Override
  public void info(String msg) {
    logger.info(applyModifier(msg));
  }

  @Override
  public void info(String format, Object... arguments) {
    logger.info(applyModifier(format), arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    logger.info(applyModifier(msg), t);
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    logger.info(applyModifier(format), arg1, arg2);
  }

  @Override
  public void info(Marker marker, String format, Object... arguments) {
    logger.info(marker, applyModifier(format), arguments);
  }

  @Override
  public void info(String format, Object arg) {
    logger.info(applyModifier(format), arg);
  }

  @Override
  public void info(Marker marker, String msg) {
    logger.info(marker, applyModifier(msg));
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    logger.info(marker, applyModifier(format), arg1, arg2);
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return logger.isDebugEnabled(marker);
  }

  @Override
  public boolean isDebugEnabled() {
    return logger.isDebugEnabled();
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return logger.isErrorEnabled(marker);
  }

  @Override
  public boolean isErrorEnabled() {
    return logger.isErrorEnabled();
  }

  @Override
  public boolean isInfoEnabled() {
    return logger.isInfoEnabled();
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return logger.isInfoEnabled(marker);
  }

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return logger.isTraceEnabled(marker);
  }

  @Override
  public boolean isTraceEnabled() {
    return logger.isTraceEnabled();
  }

  @Override
  public boolean isWarnEnabled() {
    return logger.isWarnEnabled();
  }

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return logger.isWarnEnabled(marker);
  }

  @Override
  public void trace(String format, Object... arguments) {
    logger.trace(applyModifier(format), arguments);
  }

  @Override
  public void trace(Marker marker, String msg, Throwable t) {
    logger.trace(marker, applyModifier(msg), t);
  }

  @Override
  public void trace(Marker marker, String msg) {
    logger.trace(marker, applyModifier(msg));
  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    logger.trace(marker, applyModifier(format), arg1, arg2);
  }

  @Override
  public void trace(Marker marker, String format, Object... argArray) {
    logger.trace(marker, applyModifier(format), argArray);
  }

  @Override
  public void trace(Marker marker, String format, Object arg) {
    logger.trace(marker, applyModifier(format), arg);
  }

  @Override
  public void trace(String msg, Throwable t) {
    logger.trace(applyModifier(msg), t);
  }

  @Override
  public void trace(String msg) {
    logger.trace(applyModifier(msg));
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    logger.trace(applyModifier(format), arg1, arg2);
  }

  @Override
  public void trace(String format, Object arg) {
    logger.trace(applyModifier(format), arg);
  }

  @Override
  public void warn(String msg) {
    logger.warn(applyModifier(msg));
  }

  @Override
  public void warn(String format, Object arg) {
    logger.warn(applyModifier(format), arg);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    logger.warn(applyModifier(format), arg1, arg2);
  }

  @Override
  public void warn(Marker marker, String msg) {
    logger.warn(marker, applyModifier(msg));
  }

  @Override
  public void warn(String format, Object... arguments) {
    logger.warn(applyModifier(format), arguments);
  }

  @Override
  public void warn(Marker marker, String format, Object arg) {
    logger.warn(marker, applyModifier(format), arg);
  }

  @Override
  public void warn(Marker marker, String format, Object... arguments) {
    logger.warn(marker, applyModifier(format), arguments);
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    logger.warn(marker, applyModifier(format), arg1, arg2);
  }

  @Override
  public void warn(Marker marker, String msg, Throwable t) {
    logger.warn(marker, applyModifier(msg), t);
  }

  @Override
  public void warn(String msg, Throwable t) {
    logger.warn(applyModifier(msg), t);
  }
}
