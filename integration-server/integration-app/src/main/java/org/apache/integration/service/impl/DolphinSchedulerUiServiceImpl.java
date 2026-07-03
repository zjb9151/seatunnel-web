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

package org.apache.integration.service.impl;

import org.apache.seatunnel.shade.com.fasterxml.jackson.databind.JsonNode;

import org.apache.seatunnel.common.utils.JsonUtils;
import org.apache.seatunnel.server.common.SeatunnelErrorEnum;
import org.apache.seatunnel.server.common.SeatunnelException;

import org.apache.commons.lang3.StringUtils;
import org.apache.integration.config.IntegrationProperties;
import org.apache.integration.domain.response.dolphinscheduler.DolphinSchedulerInfoRes;
import org.apache.integration.service.IDolphinSchedulerUiService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.time.Instant;
import java.util.Locale;

@Slf4j
@Service
public class DolphinSchedulerUiServiceImpl implements IDolphinSchedulerUiService {

    private static final long SESSION_TTL_SECONDS = 3600;

    @Resource private IntegrationProperties properties;

    @Resource private RestTemplate restTemplate;

    private volatile String cachedSessionId;

    private volatile Instant cachedSessionExpireAt;

    @Override
    public DolphinSchedulerInfoRes getUiInfo() {
        if (!properties.isEnabled()) {
            return new DolphinSchedulerInfoRes(
                    false,
                    null,
                    "DolphinScheduler integration is disabled. Set dolphinscheduler.enabled=true.");
        }
        if (StringUtils.isBlank(properties.getApiUrl())) {
            return new DolphinSchedulerInfoRes(
                    false, null, "DolphinScheduler api-url is not configured.");
        }
        try {
            resolveSessionId();
        } catch (Exception e) {
            log.warn("DolphinScheduler session is not available", e);
            return new DolphinSchedulerInfoRes(
                    false, null, "Unable to authenticate with DolphinScheduler: " + e.getMessage());
        }
        return new DolphinSchedulerInfoRes(
                true, resolveEmbedUrl(), "DolphinScheduler UI is ready.");
    }

    @Override
    public String resolveSessionId() {
        if (StringUtils.isNotBlank(properties.getToken())) {
            return properties.getToken().trim();
        }
        if (isCachedSessionValid()) {
            return cachedSessionId;
        }
        synchronized (this) {
            if (isCachedSessionValid()) {
                return cachedSessionId;
            }
            cachedSessionId = loginAndGetSessionId();
            cachedSessionExpireAt = Instant.now().plusSeconds(SESSION_TTL_SECONDS);
            return cachedSessionId;
        }
    }

    public String resolveEmbedUrl() {
        if (StringUtils.isNotBlank(properties.getUiUrl())) {
            return normalizeEmbedUrl(properties.getUiUrl());
        }
        return "/dolphinscheduler/ui/";
    }

    public String resolveTargetBaseUrl() {
        return trimTrailingSlash(properties.getApiUrl());
    }

    private String loginAndGetSessionId() {
        if (StringUtils.isBlank(properties.getUsername())
                || StringUtils.isBlank(properties.getPassword())) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "Configure dolphinscheduler.token or dolphinscheduler.username/password");
        }
        String loginUrl =
                String.format(Locale.ROOT, "%s/login", trimTrailingSlash(properties.getApiUrl()));
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("userName", properties.getUsername());
        form.add("userPassword", properties.getPassword());
        ResponseEntity<String> response =
                restTemplate.postForEntity(loginUrl, new HttpEntity<>(form, headers), String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "DolphinScheduler login failed with HTTP status "
                            + response.getStatusCode().value());
        }
        try {
            JsonNode root = JsonUtils.stringToJsonNode(response.getBody());
            if (root == null || root.path("code").asInt(-1) != 0) {
                String msg =
                        root == null ? "empty response" : root.path("msg").asText("login failed");
                throw new SeatunnelException(
                        SeatunnelErrorEnum.ILLEGAL_STATE, "DolphinScheduler login failed: " + msg);
            }
            JsonNode data = root.path("data");
            String sessionId = data.path("sessionId").asText(null);
            if (StringUtils.isBlank(sessionId)) {
                sessionId = data.path("token").asText(null);
            }
            if (StringUtils.isBlank(sessionId)) {
                throw new SeatunnelException(
                        SeatunnelErrorEnum.ILLEGAL_STATE,
                        "DolphinScheduler login response does not contain sessionId");
            }
            return sessionId;
        } catch (SeatunnelException e) {
            throw e;
        } catch (Exception e) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "Failed to parse DolphinScheduler login response: " + e.getMessage());
        }
    }

    private boolean isCachedSessionValid() {
        return StringUtils.isNotBlank(cachedSessionId)
                && cachedSessionExpireAt != null
                && Instant.now().isBefore(cachedSessionExpireAt);
    }

    private String normalizeEmbedUrl(String uiUrl) {
        String trimmed = uiUrl.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed;
        }
        if (!trimmed.startsWith("/")) {
            trimmed = "/" + trimmed;
        }
        return trimmed.endsWith("/") ? trimmed : trimmed + "/";
    }

    private String trimTrailingSlash(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
