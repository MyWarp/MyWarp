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

import static java.util.Objects.requireNonNull;

import io.github.mywarp.mywarp.platform.InvalidFormatException;

import org.bukkit.configuration.ConfigurationSection;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

/**
 * A configuration for a JDBC connection to a DBMS.
 */
public class JdbcConfiguration {

  private static final Pattern
      FULL_URL_PATTERN =
      Pattern.compile("(?:jdbc:)?([^:]+):(//)?(?:([^:]+)(?::([^@]+))?@)?((?:(?!//).)*)?(?://)?(.*)?");
  private static final Pattern JDBC_URL_PATTERN = Pattern.compile("(?:jdbc:)?([^:]+):(//)?(.*)");

  private final String protocol;
  private final String jdbcUrl;
  private final Map<String, Object> connectionProperties;

  @Nullable
  private final String database;
  @Nullable
  private final String username;
  @Nullable
  private final String password;

  private JdbcConfiguration(String protocol, String jdbcUrl, @Nullable String database, @Nullable String username,
                            @Nullable String password, Map<String, Object> connectionProperties) {
    this.protocol = requireNonNull(protocol).toLowerCase();
    this.jdbcUrl = requireNonNull(jdbcUrl);
    this.database = nullOnEmpty(database);
    this.username = nullOnEmpty(username);
    this.password = nullOnEmpty(password);
    this.connectionProperties = requireNonNull(connectionProperties);
  }

  /**
   * Creates a new JdbcConfiguration from the given {@code String}.
   *
   * <p>The String is expected to match the following format:
   * {@code jdbc:<engine>://[<username>[:<password>]@]<host>[//<database>]}
   * </p>
   *
   * <p>If a required value is missing of has an invalid format, an {@link IllegalArgumentException} is thrown.</p>
   *
   * @param fullUrl the String to use
   * @return a new JdbcConfiguration with the values from the given section
   * @throws InvalidFormatException if {@code fullUrl} has an invalid format
   */
  public static JdbcConfiguration fromString(String fullUrl) throws InvalidFormatException {
    //see https://github.com/SpongePowered/SpongeCommon/blob/9cc526999effed415c23b79f222569add12a4b57/src/main/java
    // /org/spongepowered/common/service/sql/SqlServiceImpl.java#L242, although we use '//' to separate host and
    // database
    Matcher match = FULL_URL_PATTERN.matcher(fullUrl);
    if (!match.matches()) {
      throw new InvalidFormatException(fullUrl, "jdbc:<engine>://[<username>[:<password>]@]<host>[//<database>]");
    }

    final String protocol = match.group(1);
    final boolean hasSlashes = match.group(2) != null;
    final String user = match.group(3);
    final String pass = match.group(4);
    final String url = match.group(5);
    final String database = match.group(6);

    final String unauthedUrl = "jdbc:" + protocol + (hasSlashes ? "://" : ":") + url;
    return new JdbcConfiguration(protocol, unauthedUrl, database, user, pass, Collections.emptyMap());
  }

  /**
   * Creates a new JdbcConfiguration from the given {@code ConfigurationSection}.
   *
   * <p>The ConfigurationSection is expected to provide the following values:
   * <ul>
   * <li>{@code url} - the JDBC connection URL to the DBMS in the form: {@code jdbc:<engine>://<host>}</li>
   * </ul>
   * The following values may be provided by the ConfigurationSection, but are optional:
   * <ul>
   * <li>{@code database} - the name of the database to use</li>
   * <li>{@code user} - the user to connect to the DBMS</li>
   * <li>{@code password} - the password to connect to the DBMS</li>
   * <li>{@code properties} - a section of key-value pairs with connection properties for the DBMS.</li>
   * </ul>
   * </p>
   *
   * <p>If a required value is missing of has an invalid format, an {@link IllegalArgumentException} is thrown.</p>
   *
   * @param section the ConfigurationSection with the values to use
   * @return a new JdbcConfiguration with the values from the given section
   * @throws InvalidFormatException if the url has an invalid format
   */
  public static JdbcConfiguration fromConfig(ConfigurationSection section) throws InvalidFormatException {
    String jdbcUrl = section.getString("url");
    Matcher matcher = JDBC_URL_PATTERN.matcher(jdbcUrl);
    if (!matcher.matches()) {
      throw new InvalidFormatException(jdbcUrl, "jdbc:<engine>://<host>");
    }
    final String protocol = matcher.group(1);

    Map<String, Object> connectionProperties = Collections.emptyMap();
    final ConfigurationSection propertiesSection = section.getConfigurationSection("properties");
    if (propertiesSection != null) {
      connectionProperties = propertiesSection.getValues(false);
    }
    return new JdbcConfiguration(protocol, jdbcUrl, section.getString("schema"), section.getString("user"),
                                 section.getString("password"), connectionProperties);
  }

  @Nullable
  private static String nullOnEmpty(@Nullable String s) {
    return s == null || s.isEmpty() ? null : s;
  }

  /**
   * Gets an Optional with the name of the database that should be used. This database name is sometimes also called
   * <i>schema</i>.
   *
   * <p>If the DBMS does not support multiple databases (such as SQLite), or no particular database is configured,
   * an empty Optional is returned.</p>
   *
   * @return An Optional with the name of the database to use
   */
  public Optional<String> getDatabase() {
    return Optional.ofNullable(database);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    JdbcConfiguration that = (JdbcConfiguration) o;
    return Objects.equals(protocol, that.protocol) && Objects.equals(jdbcUrl, that.jdbcUrl) && Objects
        .equals(database, that.database) && Objects.equals(username, that.username) && Objects
               .equals(password, that.password) && Objects.equals(connectionProperties, that.connectionProperties);
  }

  @Override
  public int hashCode() {
    return Objects.hash(protocol, jdbcUrl, database, username, password, connectionProperties);
  }

  @Override
  public String toString() {
    return "JdbcConfiguration{" + "protocol='" + protocol + '\'' + ", jdbcUrl='" + jdbcUrl + '\'' + ", database='"
           + database + '\'' + ", username='" + username + '\'' + ", password='" + password + '\''
           + ", connectionProperties=" + connectionProperties + '}';
  }

  Properties getConnectionProperties() {
    Properties ret = new Properties();

    if (username != null) {
      ret.setProperty("user", username);
      if (password != null) {
        ret.setProperty("password", password);
      }
    }
    connectionProperties.forEach((k, v) -> ret.setProperty(k, v.toString()));
    return ret;
  }

  String getJdbcUrl() {
    return jdbcUrl;
  }

  String getProtocol() {
    return protocol;
  }
}
