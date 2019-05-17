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

package io.github.mywarp.mywarp.warp.storage;

import java.util.Optional;
import java.util.concurrent.ExecutorService;

import javax.sql.DataSource;

/**
 * Provides access to a relational database management systems (DBMS).
 *
 * <p>This interface can hide connection pools, single-connection implementations or custom SQL services provided by
 * platform implementations. It provides:
 * <ul>
 * <li>Access to a configured {@link DataSource} that can be used to connect to the DBMS.</li>
 * <li>A configured {@link ExecutorService} that should execute any database queries.</li>
 * <li>Information about the DBMS configuration, such as JDBC_URL and the name of the database to use (if any).</li>
 * <li>A method to close previously acquired DataSources and ExecutorServices.</li>
 * </ul></p>
 */
public interface SqlDataService extends AutoCloseable {

  /**
   * Gets the JDBC-URL that this data service uses to connect to the database.
   *
   * @return the JDBC-URL
   */
  String getJdbcUrl();

  /**
   * Gets a {@code DataSource} that provides a connection to the DBMS.
   *
   * @return a {@code DataSource}
   */
  DataSource getDataSource();

  /**
   * Gets an Optional with the name of the database that should be used. This database name is sometimes also called
   * <i>schema</i>.
   *
   * <p>If the DBMS does not support multiple databases (such as SQLite), or no particular database is configured,
   * an empty Optional is returned.</p>
   *
   * @return An Optional with the name of the database to use
   */
  Optional<String> getDatabase();

  /**
   * Gets a {@code ListeningExecutorService} that should be used to run queries on the DBMS.
   *
   * @return a {@code ListeningExecutorService}
   */
  ExecutorService getExecutorService();

  /**
   * Closes this DataService.
   *
   * <p>Calling this method will close any DataSource and ExecutorService that are configured under this DataService.
   * After calling this method, calls to {@link #getDataSource()} and {@link #getExecutorService()} may fail.</p>
   */
  @Override
  void close();
}
