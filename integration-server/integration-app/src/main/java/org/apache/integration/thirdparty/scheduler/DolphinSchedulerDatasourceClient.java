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

package org.apache.integration.thirdparty.scheduler;

import org.apache.commons.lang3.StringUtils;
import org.apache.integration.common.IntegrationErrorEnum;
import org.apache.integration.common.IntegrationException;
import org.apache.integration.config.IntegrationProperties;
import org.apache.integration.utils.JsonHelper;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

@Slf4j
@Component
public class DolphinSchedulerDatasourceClient {

    @Resource private IntegrationProperties properties;
    @Resource private RestTemplate restTemplate;

    public boolean isAvailable() {
        return properties.isEnabled()
                && properties.isSyncDatasource()
                && StringUtils.isNotBlank(properties.getApiUrl())
                && StringUtils.isNotBlank(properties.getToken());
    }

    public int createDatasource(String jsonPayload) {
        JsonNode data = exchangeJson(HttpMethod.POST, "/datasources", jsonPayload);
        return data.path("id").asInt();
    }

    public void updateDatasource(int dsDatasourceId, String jsonPayload) {
        try {
            JsonNode root = JsonHelper.stringToJsonNode(jsonPayload);
            if (root instanceof ObjectNode) {
                ((ObjectNode) root).put("id", dsDatasourceId);
                jsonPayload = JsonHelper.toJsonString(root);
            }
        } catch (Exception e) {
            throw new IntegrationException(
                    IntegrationErrorEnum.ILLEGAL_STATE,
                    "Failed to build DS datasource update payload: " + e.getMessage());
        }
        exchangeJson(HttpMethod.PUT, "/datasources/" + dsDatasourceId, jsonPayload);
    }

    public void deleteDatasource(int dsDatasourceId) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response =
                restTemplate.exchange(
                        properties.getApiUrl() + "/datasources/" + dsDatasourceId,
                        HttpMethod.DELETE,
                        entity,
                        String.class);
        parseResponse(response.getBody());
    }

    private JsonNode exchangeJson(HttpMethod method, String path, String jsonPayload) {
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
        ResponseEntity<String> response =
                restTemplate.exchange(properties.getApiUrl() + path, method, entity, String.class);
        return parseResponse(response.getBody());
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", properties.getToken());
        return headers;
    }

    private JsonNode parseResponse(String body) {
        try {
            JsonNode root = JsonHelper.stringToJsonNode(body);
            if (root == null) {
                throw new IntegrationException(
                        IntegrationErrorEnum.ILLEGAL_STATE, "Empty response from DolphinScheduler");
            }
            int code = root.path("code").asInt(-1);
            if (code != 0) {
                String msg = root.path("msg").asText("Unknown DolphinScheduler error");
                throw new IntegrationException(IntegrationErrorEnum.ILLEGAL_STATE, msg);
            }
            return root.path("data");
        } catch (IntegrationException e) {
            throw e;
        } catch (Exception e) {
            throw new IntegrationException(
                    IntegrationErrorEnum.ILLEGAL_STATE,
                    "Failed to parse DolphinScheduler response: " + e.getMessage());
        }
    }
}
