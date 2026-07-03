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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
@Profile("h2")
public class H2SchemaInitializer {

    private static final AtomicBoolean INITIALIZED = new AtomicBoolean(false);

    @Autowired private DataSource dataSource;

    @Value("${seatunnel-web.database.data-dir:./data}")
    private String dataDir;

    @Value("${seatunnel-web.database.schema-location:classpath:script/seatunnel_server_h2.sql}")
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

        ensureDataDirectoryExists();

        if (isSchemaInitialized()) {
            log.info("H2 database already initialized, using persistent data from {}", dataDir);
            return;
        }

        log.info("First startup detected, initializing H2 schema from {}", schemaScript);
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(schemaScript);
        populator.setSeparator(";");
        populator.setCommentPrefix("--");
        try (Connection connection = dataSource.getConnection()) {
            populator.populate(connection);
            log.info("H2 schema initialized successfully, data will persist under {}", dataDir);
        } catch (Exception e) {
            INITIALIZED.set(false);
            throw new IllegalStateException("Failed to initialize H2 database schema", e);
        }
    }

    private void ensureDataDirectoryExists() {
        try {
            Path path = Paths.get(dataDir).toAbsolutePath().normalize();
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                log.info("Created H2 data directory: {}", path);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to create H2 data directory: " + dataDir, e);
        }
    }

    private boolean isSchemaInitialized() {
        try (Connection connection = dataSource.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet =
                        statement.executeQuery(
                                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.TABLES "
                                        + "WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME = 'ROLE'")) {
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        } catch (SQLException e) {
            log.debug("Schema check failed, will initialize: {}", e.getMessage());
        }
        return false;
    }
}
