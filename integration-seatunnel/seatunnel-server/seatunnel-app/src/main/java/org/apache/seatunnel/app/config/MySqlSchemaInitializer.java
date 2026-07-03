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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@Profile("!h2 & !h2-mem")
public class MySqlSchemaInitializer {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    @Autowired private DataSource dataSource;

    @Value(
            "${seatunnel-web.database.mysql-schema-location:classpath:script/seatunnel_server_mysql.sql}")
    private Resource schemaScript;

    @EventListener(ContextRefreshedEvent.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void initialize(ContextRefreshedEvent event) {
        if (event.getApplicationContext().getParent() != null) {
            return;
        }
        if (!INITIALIZED.compareAndSet(false, true)) {
            return;
        }

        if (isSchemaInitialized()) {
            log.info("MySQL database already initialized, using persistent data");
            return;
        }

        log.info("First startup detected, initializing MySQL schema from {}", schemaScript);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(schemaScript);
        populator.setSeparator(";");
        populator.setCommentPrefix("--");
        try (Connection connection = dataSource.getConnection()) {
            populator.populate(connection);
            log.info("MySQL schema initialized successfully");
        } catch (Exception e) {
            INITIALIZED.set(false);
            throw new IllegalStateException(
                    "Failed to initialize MySQL database schema. Please ensure database "
                            + "'seatunnel' exists and the user has DDL privileges.",
                    e);
        }
    }

    private boolean isSchemaInitialized() {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet =
                        statement.executeQuery(
                                "SELECT COUNT(*) FROM information_schema.tables "
                                        + "WHERE table_schema = DATABASE() AND table_name = 'role'")) {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            log.debug("MySQL schema check failed, will initialize: {}", e.getMessage());
        }
        return false;
    }
}
