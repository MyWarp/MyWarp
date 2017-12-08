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

import static org.jooq.SQLDialect.H2;
import static org.jooq.SQLDialect.MARIADB;
import static org.jooq.SQLDialect.MYSQL;
import static org.jooq.SQLDialect.SQLITE;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import io.github.mywarp.mywarp.warp.storage.generated.Tables;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.FlywayException;
import org.jooq.SQLDialect;
import org.jooq.conf.MappedSchema;
import org.jooq.conf.RenderMapping;
import org.jooq.conf.Settings;
import org.jooq.impl.DefaultConfiguration;
import org.jooq.tools.jdbc.JDBCUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

import javax.annotation.Nullable;
import javax.sql.DataSource;

/**
 * Creates {@link WarpStorage} instances.
 */
public class WarpStorageFactory {

  private static final Set<SQLDialect> SUPPORTED_DIALECTS = ImmutableSet.of(MYSQL, MARIADB, SQLITE, H2);
  private static final String MIGRATION_PATH = "classpath:migrations/";

  private final DataSource dataSource;
  private final SQLDialect dialect;
  private final String schema;

  private WarpStorageFactory(SqlDataService dataService) throws StorageInitializationException {
    this.dataSource = dataService.getDataSource();
    this.schema = dataService.getDatabase().orElse(null);

    try (Connection conn = dataSource.getConnection()) {
      dialect = JDBCUtils.dialect(conn);

      if (!SUPPORTED_DIALECTS.contains(dialect)) {
        throw new StorageInitializationException(String.format("%s is not supported!", dialect.getName()));
      }

    } catch (SQLException e) {
      throw new StorageInitializationException("Connection failed due to an SQLException. Is the configuration valid?",
                                               e);
    }
  }

  /**
   * Creates a new {@code WarpStorage} using the given data service.
   *
   * <p>Use {@link #createAndInitialize(SqlDataService)} to create a {@code WarpStorage} that guarantees
   * existence of MyWarp's table structure.</p>
   *
   * @param dataService the data service to use
   * @return the {@code WarpStorage}
   * @throws StorageInitializationException if a database error occurs or the underling database management system is
   *                                        not supported
   */
  public static WarpStorage create(SqlDataService dataService) throws StorageInitializationException {
    return new WarpStorageFactory(dataService).createStorage();
  }

  /**
   * Creates a new {@code WarpStorage} using the given data service, attempting to create or update
   * MyWarp's table structure if necessary.
   *
   * <p>Use {@link #create(SqlDataService)} to create a {@code WarpStorage} that does not create or update the
   * table structure.</p>
   *
   * @param dataService the data service to use
   * @return the {@code WarpStorage}
   * @throws StorageInitializationException if a database error occurs, the underling database management system is not
   *                                        supported or initialization of MyWarp's table structure fails
   */
  public static WarpStorage createAndInitialize(SqlDataService dataService) throws StorageInitializationException {
    return new WarpStorageFactory(dataService).createTables().createStorage();
  }

  private WarpStorageFactory createTables() throws StorageInitializationException {
    Flyway flyway = new Flyway();
    flyway.setClassLoader(
        getClass().getClassLoader()); //Needed to properly locate bundled migration scripts from the classpath
    flyway.setDataSource(dataSource);
    flyway.setLocations(getMigrationLocation(dialect));

    if (schema != null) {
      flyway.setSchemas(schema);
      flyway.setPlaceholders(ImmutableMap.of("schema", schema));
    }

    try {
      // REVIEW Remove when out of beta?
      // Fix stored checksums on databases that where created with older scripts
      flyway.repair();
      flyway.migrate();
    } catch (FlywayException e) {
      throw new StorageInitializationException("Failed to execute migration process.", e);
    }
    return this;
  }

  private WarpStorage createStorage() {
    return new JooqWarpStorage(new DefaultConfiguration().set(dialect).set(createSettings(schema)).set(dataSource));
  }

  private Settings createSettings(@Nullable String schema) {
    if (schema == null) {
      return new Settings().withRenderSchema(false);
    }
    return new Settings().withRenderMapping(new RenderMapping().withSchemata(
        new MappedSchema().withInput(Tables.WARP.getSchema().getName()).withOutput(schema)));
  }

  private String getMigrationLocation(SQLDialect dialect) throws StorageInitializationException {
    switch (dialect) {
      case H2:
        return MIGRATION_PATH + "h2";
      case MARIADB:
      case MYSQL:
        return MIGRATION_PATH + "mysql";
      case SQLITE:
        return MIGRATION_PATH + "sqlite";
      default:
        throw new StorageInitializationException(
            String.format("Migrations are not supported for %s!", dialect.getName()));
    }
  }

}
