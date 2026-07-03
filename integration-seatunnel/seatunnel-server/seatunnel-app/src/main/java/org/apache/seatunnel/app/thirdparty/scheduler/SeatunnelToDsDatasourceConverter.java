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

package org.apache.seatunnel.app.thirdparty.scheduler;

import org.apache.seatunnel.common.utils.JsonUtils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SeatunnelToDsDatasourceConverter {

    private static final Pattern JDBC_URL_PATTERN =
            Pattern.compile("jdbc:[^:]+://([^:/]+):(\\d+)/([^?;]+)");

    private SeatunnelToDsDatasourceConverter() {}

    public static Optional<String> toDsJsonPayload(
            String pluginName,
            String datasourceName,
            String description,
            Map<String, String> config) {
        String dsType = mapPluginToDsType(pluginName);
        if (dsType == null) {
            return Optional.empty();
        }

        String user = firstNonBlank(config, "user", "username", "userName");
        String password = firstNonBlank(config, "password");
        String host = config.get("host");
        String portStr = config.get("port");
        String database = config.get("database");

        String url = firstNonBlank(config, "url", "jdbcUrl");
        if (StringUtils.isNotBlank(url)) {
            Matcher matcher = JDBC_URL_PATTERN.matcher(url);
            if (matcher.find()) {
                host = matcher.group(1);
                portStr = matcher.group(2);
                database = matcher.group(3);
            }
        }

        if (StringUtils.isAnyBlank(host, portStr, database, user)) {
            return Optional.empty();
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("type", dsType);
        payload.put("name", buildDsName(datasourceName));
        payload.put("note", StringUtils.defaultIfBlank(description, "Synced from SeaTunnel Web"));
        payload.put("host", host);
        payload.put("port", Integer.parseInt(portStr));
        payload.put("userName", user);
        payload.put("password", StringUtils.defaultString(password));
        payload.put("database", database);

        Map<String, String> other = new HashMap<>();
        if (StringUtils.isNotBlank(url) && url.contains("?")) {
            String query = url.substring(url.indexOf('?') + 1);
            for (String pair : query.split("&")) {
                if (StringUtils.isBlank(pair) || !pair.contains("=")) {
                    continue;
                }
                String[] kv = pair.split("=", 2);
                if (kv.length == 2 && StringUtils.isNotBlank(kv[0])) {
                    other.put(kv[0], kv[1]);
                }
            }
        }
        if (!other.containsKey("serverTimezone")) {
            other.put("serverTimezone", "Asia/Shanghai");
        }
        if ("MYSQL".equals(dsType) && !other.containsKey("useSSL")) {
            other.put("useSSL", "false");
        }
        payload.put("other", other);

        return Optional.of(JsonUtils.toJsonString(payload));
    }

    public static String buildDsName(String datasourceName) {
        String normalized = "st_" + StringUtils.trimToEmpty(datasourceName);
        if (normalized.length() <= 64) {
            return normalized;
        }
        return normalized.substring(0, 64);
    }

    private static String mapPluginToDsType(String pluginName) {
        if (StringUtils.isBlank(pluginName)) {
            return null;
        }
        switch (pluginName.toUpperCase(Locale.ROOT)) {
            case "JDBC-MYSQL":
                return "MYSQL";
            case "JDBC-POSTGRES":
            case "JDBC-POSTGRESQL":
                return "POSTGRESQL";
            case "JDBC-ORACLE":
                return "ORACLE";
            case "JDBC-SQLSERVER":
                return "SQLSERVER";
            case "JDBC-HIVE":
            case "HIVE":
                return "HIVE";
            case "JDBC-DB2":
                return "DB2";
            default:
                return null;
        }
    }

    private static String firstNonBlank(Map<String, String> config, String... keys) {
        for (String key : keys) {
            String value = config.get(key);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }
        return null;
    }
}
