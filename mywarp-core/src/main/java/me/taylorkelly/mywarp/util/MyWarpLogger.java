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

package me.taylorkelly.mywarp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

/**
 * A custom logger that actually delegates all input to an underlying {@link org.slf4j.Logger}, but adds the prefix
 * {@code [MyWarp]} before each messages.
 */
public class MyWarpLogger implements Logger {

  private static final String prefix = "[MyWarp] ";

  private final Logger logger;

  /**
   * Return a logger named corresponding to the class passed as parameter.
   *
   * @param clazz the returned logger will be named after clazz
   * @see org.slf4j.LoggerFactory#getLogger(java.lang.Class)
   */
  private MyWarpLogger(Class<?> clazz) {
    this.logger = LoggerFactory.getLogger(clazz);
  }

  /**
   * Return a logger named corresponding to the class passed as parameter.
   *
   * @param clazz the returned logger will be named after clazz
   * @return the logger
   * @see org.slf4j.LoggerFactory#getLogger(java.lang.Class)
   */
  public static Logger getLogger(Class<?> clazz) {
    return new MyWarpLogger(clazz);
  }

  /**
   * Adds a prefix to the given string.
   *
   * @param str the string
   * @return the prefixed string
   */
  private String prefix(String str) {
    return prefix + str;
  }

  @Override
  public void debug(String format, Object arg1, Object arg2) {
    logger.debug(prefix(format), arg1, arg2);
  }

  @Override
  public void debug(Marker marker, String msg) {
    logger.debug(marker, prefix(msg));
  }

  @Override
  public void debug(String format, Object arg) {
    logger.debug(prefix(format), arg);
  }

  @Override
  public void debug(String msg) {
    logger.debug(prefix(msg));
  }

  @Override
  public void debug(String format, Object... arguments) {
    logger.debug(prefix(format), arguments);
  }

  @Override
  public void debug(Marker marker, String msg, Throwable t) {
    logger.debug(marker, prefix(msg), t);
  }

  @Override
  public void debug(String msg, Throwable t) {
    logger.debug(prefix(msg), t);
  }

  @Override
  public void debug(Marker marker, String format, Object... arguments) {
    logger.debug(marker, prefix(format), arguments);
  }

  @Override
  public void debug(Marker marker, String format, Object arg) {
    logger.debug(marker, prefix(format), arg);
  }

  @Override
  public void debug(Marker marker, String format, Object arg1, Object arg2) {
    logger.debug(marker, prefix(format), arg1, arg2);
  }

  @Override
  public void error(Marker marker, String format, Object... arguments) {
    logger.error(marker, prefix(format), arguments);
  }

  @Override
  public void error(String format, Object arg1, Object arg2) {
    logger.error(prefix(format), arg1, arg2);
  }

  @Override
  public void error(Marker marker, String format, Object arg1, Object arg2) {
    logger.error(marker, prefix(format), arg1, arg2);
  }

  @Override
  public void error(String msg, Throwable t) {
    logger.error(prefix(msg), t);
  }

  @Override
  public void error(Marker marker, String msg) {
    logger.error(marker, prefix(msg));
  }

  @Override
  public void error(Marker marker, String msg, Throwable t) {
    logger.error(marker, prefix(msg), t);
  }

  @Override
  public void error(String format, Object arg) {
    logger.error(prefix(format), arg);
  }

  @Override
  public void error(String format, Object... arguments) {
    logger.error(prefix(format), arguments);
  }

  @Override
  public void error(Marker marker, String format, Object arg) {
    logger.error(marker, prefix(format), arg);
  }

  @Override
  public void error(String msg) {
    logger.error(prefix(msg));
  }

  @Override
  public String getName() {
    return logger.getName();
  }

  @Override
  public void info(Marker marker, String format, Object arg) {
    logger.info(marker, prefix(format), arg);
  }

  @Override
  public void info(Marker marker, String msg, Throwable t) {
    logger.info(marker, prefix(msg), t);
  }

  @Override
  public void info(String msg) {
    logger.info(prefix(msg));
  }

  @Override
  public void info(String format, Object... arguments) {
    logger.info(prefix(format), arguments);
  }

  @Override
  public void info(String msg, Throwable t) {
    logger.info(prefix(msg), t);
  }

  @Override
  public void info(String format, Object arg1, Object arg2) {
    logger.info(prefix(format), arg1, arg2);
  }

  @Override
  public void info(Marker marker, String format, Object... arguments) {
    logger.info(marker, prefix(format), arguments);
  }

  @Override
  public void info(String format, Object arg) {
    logger.info(prefix(format), arg);
  }

  @Override
  public void info(Marker marker, String msg) {
    logger.info(marker, prefix(msg));
  }

  @Override
  public void info(Marker marker, String format, Object arg1, Object arg2) {
    logger.info(marker, prefix(format), arg1, arg2);
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
    logger.trace(prefix(format), arguments);
  }

  @Override
  public void trace(Marker marker, String msg, Throwable t) {
    logger.trace(marker, prefix(msg), t);
  }

  @Override
  public void trace(Marker marker, String msg) {
    logger.trace(marker, prefix(msg));
  }

  @Override
  public void trace(Marker marker, String format, Object arg1, Object arg2) {
    logger.trace(marker, prefix(format), arg1, arg2);
  }

  @Override
  public void trace(Marker marker, String format, Object... argArray) {
    logger.trace(marker, prefix(format), argArray);
  }

  @Override
  public void trace(Marker marker, String format, Object arg) {
    logger.trace(marker, prefix(format), arg);
  }

  @Override
  public void trace(String msg, Throwable t) {
    logger.trace(prefix(msg), t);
  }

  @Override
  public void trace(String msg) {
    logger.trace(prefix(msg));
  }

  @Override
  public void trace(String format, Object arg1, Object arg2) {
    logger.trace(prefix(format), arg1, arg2);
  }

  @Override
  public void trace(String format, Object arg) {
    logger.trace(prefix(format), arg);
  }

  @Override
  public void warn(String msg) {
    logger.warn(prefix(msg));
  }

  @Override
  public void warn(String format, Object arg) {
    logger.warn(prefix(format), arg);
  }

  @Override
  public void warn(String format, Object arg1, Object arg2) {
    logger.warn(prefix(format), arg1, arg2);
  }

  @Override
  public void warn(Marker marker, String msg) {
    logger.warn(marker, prefix(msg));
  }

  @Override
  public void warn(String format, Object... arguments) {
    logger.warn(prefix(format), arguments);
  }

  @Override
  public void warn(Marker marker, String format, Object arg) {
    logger.warn(marker, prefix(format), arg);
  }

  @Override
  public void warn(Marker marker, String format, Object... arguments) {
    logger.warn(marker, prefix(format), arguments);
  }

  @Override
  public void warn(Marker marker, String format, Object arg1, Object arg2) {
    logger.warn(marker, prefix(format), arg1, arg2);
  }

  @Override
  public void warn(Marker marker, String msg, Throwable t) {
    logger.warn(marker, prefix(msg), t);
  }

  @Override
  public void warn(String msg, Throwable t) {
    logger.warn(prefix(msg), t);
  }
}
