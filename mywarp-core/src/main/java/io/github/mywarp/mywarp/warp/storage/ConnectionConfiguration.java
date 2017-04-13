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

package io.github.mywarp.mywarp.warp.storage;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jooq.SQLDialect;
import org.jooq.tools.jdbc.JDBCUtils;

/**
 * A configuration for a connection to a relational database.
 *
 * <p>An instance is initialized with a connection URL. Further configuration methods may fail if the database
 * management system the URL connects to does not support such configurations.</p>
 *
 * <p>Instances are not thread-safe. If a configuration is shared between multiple threads, modifying methods must not
 * be used after configuring the instance for the first time.</p>
 */
public class ConnectionConfiguration {

  private final String url;
  private final SQLDialect dialect;
  private String schema;
  private String user;
  private String password;

  /**
   * Creates an instance using the given connection {@code url}.
   *
   * @param url connection URL
   * @see java.sql.DriverManager#getConnection(String)
   */
  public ConnectionConfiguration(String url) {
    this.url = checkNotNull(url);
    this.dialect = JDBCUtils.dialect(url);
  }

  /**
   * Gets the JDBC connection URL to the underling database management system.
   *
   * @return the connection URL
   */
  public String getUrl() {
    return url;
  }

  /**
   * Gets the dialect of the underling database management system.
   *
   * @return the dialect of the underling database management system
   */
  SQLDialect getDialect() {
    return dialect;
  }

  /**
   * Gets the class name of the JDBC driver that is used to connect to the database.
   *
   * @return the driver's class name
   * @see java.sql.DriverManager#getDriver(String)
   */
  public String getDriver() {
    return JDBCUtils.driver(url);
  }

  /**
   * Returns if schemas are supported by the underling database management system.
   *
   * @return {@code true} if schemas are supported
   */
  public boolean supportsSchemas() {
    return dialect != SQLDialect.SQLITE;
  }

  /**
   * Returns if authentication is supported by the underling database management system.
   *
   * @return {@code true} if authentication is supported
   */
  public boolean supportsAuthentication() {
    return dialect != SQLDialect.SQLITE;
  }

  /**
   * Sets the schema to be used when reading from or writing to the database.
   *
   * @param schema the schema to set
   * @return the updated configuration
   * @throws IllegalStateException if the underling database management system does not support schemas
   */
  public ConnectionConfiguration setSchema(String schema) {
    checkState(supportsSchemas(), "The underling database management system does not support schemas.");
    this.schema = schema;
    return this;
  }

  /**
   * Sets the user to be used for authentication with the database management system.
   *
   * @param user the user to set
   * @return the updated configuration
   * @throws IllegalStateException if the underling database management system does not support authentication
   */
  public ConnectionConfiguration setUser(String user) {
    checkState(supportsAuthentication(), "The underling database management system does not support users.");
    this.user = user;
    return this;
  }

  /**
   * Sets the password to be used for authentication with the database management system.
   *
   * @param password the password to set
   * @return the updated configuration
   * @throws IllegalStateException if the underling database management system does not support authentication
   */
  public ConnectionConfiguration setPassword(String password) {
    checkState(supportsAuthentication(), "The underling database management system does not support passwords.");
    this.password = password;
    return this;
  }

  /**
   * Gets the schema to be used when reading from or writing to the database.
   *
   * @return the schema
   * @throws IllegalStateException if the underling database management system does not support schemas
   */
  public String getSchema() {
    checkState(supportsSchemas(), "The underling database management system does not support schemas.");
    return schema;
  }

  /**
   * Gets the user to be used for authentication with the database management system.
   *
   * @return the user
   * @throws IllegalStateException if the underling database management system does not support authentication
   */
  public String getUser() {
    checkState(supportsAuthentication(), "The underling database management system does not support users.");
    return user;
  }

  /**
   * Gets the password to be used for authentication with the database management system.
   *
   * @return the password
   * @throws IllegalStateException if the underling database management system does not support authentication
   */
  public String getPassword() {
    checkState(supportsSchemas(), "The underling database management system does not support schemas.");
    return password;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ConnectionConfiguration that = (ConnectionConfiguration) o;

    if (!url.equals(that.url)) {
      return false;
    }
    if (dialect != that.dialect) {
      return false;
    }
    if (schema != null ? !schema.equals(that.schema) : that.schema != null) {
      return false;
    }
    if (user != null ? !user.equals(that.user) : that.user != null) {
      return false;
    }
    return !(password != null ? !password.equals(that.password) : that.password != null);

  }

  @Override
  public int hashCode() {
    int result = url.hashCode();
    result = 31 * result + dialect.hashCode();
    result = 31 * result + (schema != null ? schema.hashCode() : 0);
    result = 31 * result + (user != null ? user.hashCode() : 0);
    result = 31 * result + (password != null ? password.hashCode() : 0);
    return result;
  }
}
