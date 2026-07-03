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

import org.apache.seatunnel.shade.com.fasterxml.jackson.databind.JsonNode;
import org.apache.seatunnel.shade.com.fasterxml.jackson.databind.node.ArrayNode;

import org.apache.seatunnel.app.config.DolphinSchedulerProperties;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.apache.seatunnel.server.common.SeatunnelErrorEnum;
import org.apache.seatunnel.server.common.SeatunnelException;

import org.apache.commons.lang3.StringUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
public class DolphinSchedulerSessionClient {

    @Resource private DolphinSchedulerProperties properties;
    @Resource private RestTemplate restTemplate;

    public boolean isAvailable() {
        return properties.isEnabled() && StringUtils.isNotBlank(properties.getApiUrl());
    }

    public String resolveUsername(String sessionId) {
        if (StringUtils.isBlank(sessionId)) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE, "DolphinScheduler sessionId is required");
        }
        if (StringUtils.isNotBlank(properties.getToken())
                && StringUtils.equals(sessionId.trim(), properties.getToken().trim())) {
            return properties.getUsername();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("sessionId", sessionId.trim());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url =
                String.format(
                        Locale.ROOT, "%s/users/get", trimTrailingSlash(properties.getApiUrl()));
        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        JsonNode data = parseResponse(response.getBody());
        String username = data.path("userName").asText(null);
        if (StringUtils.isBlank(username)) {
            username = data.path("username").asText(null);
        }
        if (StringUtils.isBlank(username)) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "Unable to resolve DolphinScheduler user from session");
        }
        return username;
    }

    public List<JsonNode> listDatasources(int pageNo, int pageSize) {
        HttpHeaders headers = buildAuthHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        String url =
                String.format(
                        Locale.ROOT,
                        "%s/datasources/list?pageNo=%d&pageSize=%d",
                        trimTrailingSlash(properties.getApiUrl()),
                        pageNo,
                        pageSize);
        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
        JsonNode data = parseResponse(response.getBody());
        List<JsonNode> result = new ArrayList<>();
        if (data.isArray()) {
            data.forEach(result::add);
        } else if (data.has("totalList") && data.get("totalList").isArray()) {
            data.get("totalList").forEach(result::add);
        } else if (data instanceof ArrayNode) {
            data.forEach(result::add);
        }
        return result;
    }

    private HttpHeaders buildAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        if (StringUtils.isNotBlank(properties.getToken())) {
            headers.add("token", properties.getToken());
        }
        return headers;
    }

    private JsonNode parseResponse(String body) {
        try {
            JsonNode root = JsonUtils.stringToJsonNode(body);
            if (root == null) {
                throw new SeatunnelException(
                        SeatunnelErrorEnum.ILLEGAL_STATE, "Empty response from DolphinScheduler");
            }
            int code = root.path("code").asInt(-1);
            if (code != 0) {
                String msg = root.path("msg").asText("Unknown DolphinScheduler error");
                throw new SeatunnelException(SeatunnelErrorEnum.ILLEGAL_STATE, msg);
            }
            return root.path("data");
        } catch (SeatunnelException e) {
            throw e;
        } catch (Exception e) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "Failed to parse DolphinScheduler response: " + e.getMessage());
        }
    }

    private String trimTrailingSlash(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
