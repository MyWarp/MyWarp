/*
 * Copyright (C) 2011 - 2019, MyWarp team and contributors
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

package io.github.mywarp.mywarp.bukkit.util.jdbc;

import com.google.common.base.Objects;

import io.github.mywarp.mywarp.util.MyWarpLogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Properties;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.sql.DataSource;

/**
 * A {@link DataSource} implementation that uses a single {@link Connection}. {@code Connection}s returned by this the
 * {@code DataSource} are actually wrapper around the original {@code Connection} that forbid closing.
 *
 * <p>Obviously this class is not threadsafe.</p>
 */
public class SingleConnectionDataSource implements DataSource, AutoCloseable {

  private static final org.slf4j.Logger log = MyWarpLogger.getLogger(SingleConnectionDataSource.class);

  private final String url;
  private final boolean driverSupportsIsValid;
  private final Properties properties;

  @Nullable
  private Connection target;

  @Nullable
  private Connection proxy;

  /**
   * Creates a new instance that connects to a database using the given {@code url}, {@code username} and {@code
   * password}.
   *
   * @param url                   the database url
   * @param properties            the properties used to connect with the database
   * @param driverSupportsIsValid whether the driver used to connect with the given URL implements {@link
   *                              Connection#isValid(int)}
   * @see DriverManager#getConnection(String, String, String)
   */
  SingleConnectionDataSource(String url, Properties properties, boolean driverSupportsIsValid) {
    this.url = url;
    this.properties = properties;
    this.driverSupportsIsValid = driverSupportsIsValid;
  }

  @Override
  public Connection getConnection() throws SQLException {
    if (target == null || proxy == null) {
      log.debug("Initializing connection.");
      initiate();

    }
    if (!isValid(target)) {
      log.debug("Target connection is invalid. Reconnecting...");
      initiate();
    }
    return proxy;
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    if (Objects.equal(this.properties.getProperty("user"), username) && Objects
        .equal(this.properties.getProperty("password"), password)) {
      throw new SQLException(
          "SingleConnectionDataSource does not support retrieving of connections with custom username and "
          + "password.");
    }
    return getConnection();
  }

  @Override
  public int getLoginTimeout() {
    return 0;
  }

  @Override
  public void setLoginTimeout(int timeout) {
    throw new UnsupportedOperationException("setLoginTimeout");
  }

  @Override
  public PrintWriter getLogWriter() {
    throw new UnsupportedOperationException("getLogWriter");
  }

  @Override
  public void setLogWriter(PrintWriter pw) {
    throw new UnsupportedOperationException("setLogWriter");
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T unwrap(Class<T> iface) throws SQLException {
    if (iface.isInstance(this)) {
      return (T) this;
    }
    throw new SQLException(
        "DataSource of type [" + getClass().getName() + "] cannot be unwrapped as [" + iface.getName() + "]");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) {
    return iface.isInstance(this);
  }

  /**
   * Initiates target and proxy {@code Connection}.
   *
   * @throws SQLException          if a database error occurs
   * @throws IllegalStateException if {@code url} is not set
   */
  private void initiate() throws SQLException {

    try {
      close();
    } catch (IOException e) {
      if (e.getCause() instanceof SQLException) {
        throw (SQLException) e.getCause();
      } else {
        throw new SQLException(e);
      }
    }

    log.debug("Connecting to {} with properties {},", url, properties);
    target = DriverManager.getConnection(url, properties);
    proxy = getCloseSuppressingConnectionProxy(target);
  }

  private boolean isValid(Connection conn) throws SQLException {
    if (conn.isClosed()) {
      return false;
    }

    if (driverSupportsIsValid) {
      return conn.isValid(5);
    }

    try (Statement statement = conn.createStatement()) {
      return statement.execute("select 1");
    }
  }

  @Override
  public void close() throws IOException {
    if (target != null) {
      try {
        target.close();
      } catch (SQLException e) {
        throw new IOException("Failed to close DataSource", e);
      }
    }
  }

  /**
   * Wrap the given {@code Connection} with a proxy that delegates every method call, but suppresses close calls.
   *
   * @param target the {@code Connection} to wrap
   * @return the wrapped {@code Connection}
   */
  private Connection getCloseSuppressingConnectionProxy(Connection target) {
    return (Connection) Proxy.newProxyInstance(Connection.class.getClassLoader(), new Class<?>[]{Connection.class},
                                               new CloseSuppressingInvocationHandler(target));
  }


  /**
   * Invocation handler that suppresses close calls on JDBC Connections.
   */
  private static class CloseSuppressingInvocationHandler implements InvocationHandler {

    private final Connection target;

    /**
     * Creates an instance that works on the given {@code Connection}.
     *
     * @param target the {@code Connection}
     */
    public CloseSuppressingInvocationHandler(Connection target) {
      this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      // Invocation on ConnectionProxy interface coming in...

      switch (method.getName()) {
        case "equals":
          // Only consider equal when proxies are identical.
          return (proxy == args[0]);
        case "hashCode":
          // Use hashCode of Connection proxy.
          return System.identityHashCode(proxy);
        case "unwrap":
          if (((Class<?>) args[0]).isInstance(proxy)) {
            return proxy;
          }
          break;
        case "isWrapperFor":
          if (((Class<?>) args[0]).isInstance(proxy)) {
            return true;
          }
          break;
        case "close":
          // Handle close method: don't pass the call on.
          return null;
        case "isClosed":
          return false;
        default:
          //fall though
      }

      // Invoke method on target Connection.
      try {
        return method.invoke(this.target, args);
      } catch (InvocationTargetException ex) {
        throw ex.getTargetException();
      }
    }
  }
}
