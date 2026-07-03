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

package org.apache.integration.client;

import org.apache.commons.lang3.StringUtils;
import org.apache.integration.common.IntegrationErrorEnum;
import org.apache.integration.common.IntegrationException;
import org.apache.integration.config.IntegrationProperties;
import org.apache.integration.domain.response.user.UserSimpleInfoRes;
import org.apache.integration.utils.JsonHelper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class SeatunnelApiClient {

    @Resource private IntegrationProperties properties;
    @Resource private RestTemplate restTemplate;

    public boolean jobDefinitionExists(Long jobDefineId) {
        try {
            String url = baseUrl() + "/seatunnel/api/v1/job/definition?jobId=" + jobDefineId;
            HttpHeaders headers = jwtHeaders();
            ResponseEntity<String> response =
                    restTemplate.exchange(
                            url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
            return response.getStatusCode().is2xxSuccessful()
                    && response.getBody() != null
                    && response.getBody().contains("\"code\":0");
        } catch (Exception e) {
            log.debug("Job definition check failed for {}: {}", jobDefineId, e.getMessage());
            return false;
        }
    }

    public void executeJob(Long jobDefineId) {
        String url =
                baseUrl() + "/seatunnel/api/v1/job/executor/execute?jobDefineId=" + jobDefineId;
        HttpHeaders headers = jwtHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = new HashMap<>();
        ResponseEntity<String> response =
                restTemplate.exchange(
                        url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IntegrationException(
                    IntegrationErrorEnum.ILLEGAL_STATE,
                    "SeaTunnel job execute failed: HTTP " + response.getStatusCode());
        }
        try {
            JsonNode root = JsonHelper.stringToJsonNode(response.getBody());
            if (root != null && root.path("code").asInt(-1) != 0) {
                throw new IntegrationException(
                        IntegrationErrorEnum.ILLEGAL_STATE,
                        "SeaTunnel job execute failed: " + root.path("msg").asText());
            }
        } catch (IntegrationException e) {
            throw e;
        } catch (Exception e) {
            throw new IntegrationException(
                    IntegrationErrorEnum.ILLEGAL_STATE,
                    "Failed to parse SeaTunnel execute response: " + e.getMessage());
        }
    }

    public UserSimpleInfoRes loginAsDefaultUser() {
        String url = baseUrl() + "/seatunnel/api/v1/user/login";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, String> body = new HashMap<>();
        body.put("username", "admin");
        body.put("password", "admin");
        ResponseEntity<String> response =
                restTemplate.exchange(
                        url, HttpMethod.POST, new HttpEntity<>(body, headers), String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IntegrationException(
                    IntegrationErrorEnum.ILLEGAL_STATE,
                    "SeaTunnel login failed: HTTP " + response.getStatusCode());
        }
        try {
            JsonNode root = JsonHelper.stringToJsonNode(response.getBody());
            if (root == null || root.path("code").asInt(-1) != 0) {
                throw new IntegrationException(
                        IntegrationErrorEnum.ILLEGAL_STATE,
                        root == null ? "empty response" : root.path("msg").asText("login failed"));
            }
            return JsonHelper.parseObject(root.path("data").toString(), UserSimpleInfoRes.class);
        } catch (IntegrationException e) {
            throw e;
        } catch (Exception e) {
            throw new IntegrationException(
                    IntegrationErrorEnum.ILLEGAL_STATE,
                    "Failed to parse login response: " + e.getMessage());
        }
    }

    public boolean isReachable() {
        try {
            restTemplate.getForEntity(baseUrl() + "/seatunnel/api/v1/user/login", String.class);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String baseUrl() {
        String url = properties.getProcesses().getSeatunnelWeb().getApiUrl();
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private HttpHeaders jwtHeaders() {
        UserSimpleInfoRes user = loginAsDefaultUser();
        if (user == null || StringUtils.isBlank(user.getToken())) {
            throw new IntegrationException(
                    IntegrationErrorEnum.ILLEGAL_STATE, "SeaTunnel login returned no token");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.set("token", user.getToken());
        return headers;
    }
}
