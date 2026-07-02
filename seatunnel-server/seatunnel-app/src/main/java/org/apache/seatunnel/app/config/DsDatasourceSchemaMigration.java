/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.seatunnel.app.config;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@Slf4j
@Component
public class DsDatasourceSchemaMigration {

    @Resource private DataSource dataSource;

    @EventListener(ContextRefreshedEvent.class)
    @Order(50)
    public void migrate(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        addColumnIfMissing("t_st_datasource", "ds_datasource_id", "int DEFAULT NULL");
    }

    private void addColumnIfMissing(String table, String column, String definition) {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement()) {
            if (columnExists(connection, table, column)) {
                return;
            }
            statement.execute(
                    String.format("ALTER TABLE %s ADD COLUMN %s %s", table, column, definition));
            log.info("Added column {}.{}", table, column);
        } catch (Exception e) {
            log.warn("Schema migration skipped for {}.{}: {}", table, column, e.getMessage());
        }
    }

    private boolean columnExists(Connection connection, String table, String column)
            throws Exception {
        String product = connection.getMetaData().getDatabaseProductName().toLowerCase();
        String sql;
        if (product.contains("h2")) {
            sql =
                    String.format(
                            "SELECT COUNT(*) FROM INFORMATION_SCHEMA.COLUMNS "
                                    + "WHERE TABLE_NAME = '%s' AND COLUMN_NAME = '%s'",
                            table.toUpperCase(), column.toUpperCase());
        } else {
            sql =
                    String.format(
                            "SELECT COUNT(*) FROM information_schema.columns "
                                    + "WHERE table_schema = DATABASE() AND table_name = '%s' "
                                    + "AND column_name = '%s'",
                            table, column);
        }
        try (Statement statement = connection.createStatement();
                ResultSet rs = statement.executeQuery(sql)) {
            return rs.next() && rs.getInt(1) > 0;
        }
    }
}
