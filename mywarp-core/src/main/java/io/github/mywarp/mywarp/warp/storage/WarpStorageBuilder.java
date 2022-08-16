/*
 * Copyright (C) 2011 - 2022, MyWarp team and contributors
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

import com.google.common.collect.ImmutableMap;
import io.github.mywarp.mywarp.util.MyWarpLogger;
import io.github.mywarp.mywarp.warp.storage.generated.Tables;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.flywaydb.core.api.configuration.FluentConfiguration;
import org.flywaydb.core.api.logging.Log;
import org.flywaydb.core.api.logging.LogFactory;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.tools.jdbc.JDBCUtils;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import static org.jooq.SQLDialect.*;

/**
 * Builds {@link WarpStorage} instances.
 */
public class WarpStorageBuilder {

  private static final Map<SQLDialect, String>
      SUPPORTED_DIALECTS =
      ImmutableMap.<SQLDialect, String>builder().put(H2, "h2").put(MARIADB, "mysql").put(MYSQL, "mysql")
          .put(SQLITE, "sqlite").build();
  private static final String MIGRATION_PATH = "classpath:migrations/";
  private static final String FLYWAY_TABLE_NAME = "schema_version";

  private final DataSource dataSource;
  private final SQLDialect dialect;
  @Nullable
  private final String schema;

  private boolean initTables = false;

  private WarpStorageBuilder(SqlDataService dataService) {
    this.dataSource = dataService.getDataSource();
    this.dialect = JDBCUtils.dialect(dataService.getJdbcUrl());
    this.schema = dataService.getDatabase().orElse(null);
  }

  /**
   * Creates a builder that uses the given {@code dataService}.
   *
   * @param dataService the DataService to use
   * @return the builder
   */
  public static WarpStorageBuilder using(SqlDataService dataService) {
    return new WarpStorageBuilder(dataService);
  }

  private static Log logger(Class<?> clazz) {
    return new Log() {
      private final Logger logger = MyWarpLogger.getLogger(clazz);

      @Override
      public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
      }

      @Override
      public void debug(String message) {
        logger.debug(message);
      }

      @Override
      public void info(String message) {
        logger.info(message);
      }

      @Override
      public void warn(String message) {
        logger.warn(message);
      }

      @Override
      public void error(String message) {
        logger.error(message);
      }

      @Override
      public void error(String message, Exception e) {
        logger.error(message, e);
      }
    };
  }

  /**
   * Returns a builder that will build instaces and attempt to initialize the SQL tables as expected.
   *
   * @return the builder
   */
  public WarpStorageBuilder initTables() {
    this.initTables = true;
    return this;
  }

  /**
   * Builds the {@link WarpStorage} according to the previously provided configuration.
   *
   * <p>This method will either return a working {@link WarpStorage} instance or throw an Exception.</p>
   *
   * @return a WarpStorage instance
   * @throws SQLException                 if the connection to the DBMS fails
   * @throws UnsupportedDialectException  if the dialect of if configuration is not supported
   * @throws TableInitializationException if the connection works, but the initialization of the table structure fails
   */
  public WarpStorage build() throws SQLException, UnsupportedDialectException, TableInitializationException {
    //Test if the given dialect is supported, fail if not.
    if (SUPPORTED_DIALECTS.keySet().stream().noneMatch(dialect::supports)) {
      throw new UnsupportedDialectException(dialect);
    }

    //Test if the connection is valid, fail if not.
    try (Connection conn = dataSource.getConnection()) {
      if (!conn.isValid(10)) {
        throw new SQLException("The connection is invalid.");
      }
    }

    //Initialize the tables, fail on error.
    if (initTables) {
      LogFactory.setFallbackLogCreator(WarpStorageBuilder::logger);

      FluentConfiguration
          flywayConfig =
          Flyway.configure(getClass().getClassLoader()).dataSource(dataSource).locations(migrationPath(dialect))
              .table(FLYWAY_TABLE_NAME);

      if (schema != null) {
        flywayConfig =
            flywayConfig.schemas(schema).defaultSchema(schema).placeholders(ImmutableMap.of("schema", schema));
        //REVIEW use default placeholders? https://flywaydb.org/documentation/placeholders
      }

      Flyway flyway = flywayConfig.load();
      try {
        flyway.migrate();
      } catch (FlywayException e) {
        throw new TableInitializationException(e);
      }

    }

    //Create and return the storage. At this point, the operation can no longer fail.
    return new JooqWarpStorage(new DefaultConfiguration().set(dialect).set(settings()).set(dataSource));
  }

  private String migrationPath(SQLDialect dialect) throws UnsupportedDialectException {
    if (SUPPORTED_DIALECTS.containsKey(dialect)) {
      return MIGRATION_PATH + SUPPORTED_DIALECTS.get(dialect);
    }
    throw new UnsupportedDialectException(dialect);
  }

  private Settings settings() {
    if (schema == null) {
      return new Settings().withRenderSchema(false);
    }
    return new Settings().withRenderMapping(new RenderMapping().withSchemata(
        new MappedSchema().withInput(Tables.WARP.getSchema().getName()).withOutput(schema)));
  }
}
