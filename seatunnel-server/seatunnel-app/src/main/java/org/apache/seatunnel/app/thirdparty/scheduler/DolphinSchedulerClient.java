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

import org.apache.seatunnel.app.config.DolphinSchedulerProperties;
import org.apache.seatunnel.common.utils.JsonUtils;
import org.apache.seatunnel.server.common.CodeGenerateUtils;
import org.apache.seatunnel.server.common.SeatunnelErrorEnum;
import org.apache.seatunnel.server.common.SeatunnelException;

import org.apache.commons.lang3.StringUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DolphinSchedulerClient {

    private static final DateTimeFormatter DS_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Resource private DolphinSchedulerProperties properties;
    @Resource private RestTemplate restTemplate;

    public boolean isAvailable() {
        return properties.isEnabled()
                && StringUtils.isNotBlank(properties.getApiUrl())
                && StringUtils.isNotBlank(properties.getToken());
    }

    public DsSyncResult syncSchedule(
            Long jobDefineId,
            String scheduleName,
            String crontab,
            Long existingProcessCode,
            Integer existingScheduleId) {
        if (!isAvailable()) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "DolphinScheduler is not configured. Set dolphinscheduler.enabled=true and "
                            + "configure api-url, token, service-token in application.yml.");
        }
        if (StringUtils.isBlank(properties.getServiceToken())) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "dolphinscheduler.service-token is required for HTTP task authentication.");
        }

        long taskCode = generateCode();

        String processName = "seatunnel_" + jobDefineId;
        String executeUrl =
                properties.getSeatunnelWebUrl()
                        + "/seatunnel/api/v1/job/executor/execute?jobDefineId="
                        + jobDefineId;

        String taskDefinitionJson = buildTaskDefinitionJson(taskCode, executeUrl);
        String taskRelationJson = buildTaskRelationJson(taskCode);
        String locationsJson = buildLocationsJson(taskCode);

        long processCode;
        if (existingProcessCode != null && existingProcessCode > 0) {
            processCode = existingProcessCode;
            updateProcessDefinition(
                    processCode, processName, taskDefinitionJson, taskRelationJson, locationsJson);
        } else {
            processCode =
                    createProcessDefinition(
                            processName, taskDefinitionJson, taskRelationJson, locationsJson);
        }

        onlineProcessDefinition(processCode);

        int scheduleId;
        if (existingScheduleId != null && existingScheduleId > 0) {
            updateSchedule(existingScheduleId, processCode, crontab);
            scheduleId = existingScheduleId;
        } else {
            scheduleId = createSchedule(processCode, crontab);
        }

        DsSyncResult result = new DsSyncResult();
        result.setProjectCode(properties.getProjectCode());
        result.setProcessDefinitionCode(processCode);
        result.setScheduleId(scheduleId);
        return result;
    }

    public void offlineSchedule(Integer scheduleId, Long processDefinitionCode) {
        if (!isAvailable() || scheduleId == null || scheduleId <= 0) {
            return;
        }
        try {
            offlineProcessDefinition(processDefinitionCode);
            deleteSchedule(scheduleId);
        } catch (Exception e) {
            log.warn("Failed to offline DolphinScheduler schedule {}", scheduleId, e);
        }
    }

    private long createProcessDefinition(
            String name, String taskDefinitionJson, String taskRelationJson, String locationsJson) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("name", name);
        form.add("description", "SeaTunnel sync job scheduled by SeaTunnel Web");
        form.add("taskDefinitionJson", taskDefinitionJson);
        form.add("taskRelationJson", taskRelationJson);
        form.add("locations", locationsJson);
        form.add("tenantCode", properties.getTenantCode());
        form.add("executionType", "PARALLEL");

        JsonNode data =
                postForm("/projects/" + properties.getProjectCode() + "/process-definition", form);
        return data.path("code").asLong();
    }

    private void updateProcessDefinition(
            long processCode,
            String name,
            String taskDefinitionJson,
            String taskRelationJson,
            String locationsJson) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("name", name);
        form.add("description", "SeaTunnel sync job scheduled by SeaTunnel Web");
        form.add("taskDefinitionJson", taskDefinitionJson);
        form.add("taskRelationJson", taskRelationJson);
        form.add("locations", locationsJson);
        form.add("tenantCode", properties.getTenantCode());
        form.add("executionType", "PARALLEL");
        form.add("releaseState", "ONLINE");

        putForm(
                "/projects/" + properties.getProjectCode() + "/process-definition/" + processCode,
                form);
    }

    private void onlineProcessDefinition(long processCode) {
        postEmpty(
                "/projects/"
                        + properties.getProjectCode()
                        + "/process-definition/"
                        + processCode
                        + "/release?releaseState=ONLINE");
    }

    private void offlineProcessDefinition(Long processCode) {
        if (processCode == null || processCode <= 0) {
            return;
        }
        postEmpty(
                "/projects/"
                        + properties.getProjectCode()
                        + "/process-definition/"
                        + processCode
                        + "/release?releaseState=OFFLINE");
    }

    private int createSchedule(long processCode, String crontab) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("schedule", buildScheduleJson(crontab));
        form.add("failureStrategy", "CONTINUE");
        form.add("warningType", "NONE");
        form.add("processInstancePriority", "MEDIUM");
        form.add("warningGroupId", "0");
        form.add("workerGroup", "default");
        form.add("tenantCode", properties.getTenantCode());
        form.add("environmentCode", "-1");
        form.add("processDefinitionCode", String.valueOf(processCode));

        JsonNode data = postForm("/projects/" + properties.getProjectCode() + "/schedules", form);
        return data.get("id").asInt();
    }

    private void updateSchedule(int scheduleId, long processCode, String crontab) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("schedule", buildScheduleJson(crontab));
        form.add("failureStrategy", "CONTINUE");
        form.add("warningType", "NONE");
        form.add("processInstancePriority", "MEDIUM");
        form.add("warningGroupId", "0");
        form.add("workerGroup", "default");
        form.add("tenantCode", properties.getTenantCode());
        form.add("environmentCode", "-1");
        form.add("processDefinitionCode", String.valueOf(processCode));
        form.add("id", String.valueOf(scheduleId));

        putForm("/projects/" + properties.getProjectCode() + "/schedules/" + scheduleId, form);
    }

    private void deleteSchedule(int scheduleId) {
        delete("/projects/" + properties.getProjectCode() + "/schedules/" + scheduleId);
    }

    private String buildScheduleJson(String crontab) {
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> schedule = new HashMap<>();
        schedule.put("startTime", now.format(DS_DATE_FORMAT));
        schedule.put("endTime", now.plusYears(10).format(DS_DATE_FORMAT));
        schedule.put("crontab", crontab);
        schedule.put("timezoneId", "Asia/Shanghai");
        return JsonUtils.toJsonString(schedule);
    }

    private String buildTaskDefinitionJson(long taskCode, String executeUrl) {
        Map<String, Object> httpParamToken = new HashMap<>();
        httpParamToken.put("prop", "token");
        httpParamToken.put("httpParametersType", "HEADERS");
        httpParamToken.put("value", properties.getServiceToken());

        Map<String, Object> httpParamContentType = new HashMap<>();
        httpParamContentType.put("prop", "Content-Type");
        httpParamContentType.put("httpParametersType", "HEADERS");
        httpParamContentType.put("value", "application/json");

        List<Map<String, Object>> httpParams = new ArrayList<>();
        httpParams.add(httpParamToken);
        httpParams.add(httpParamContentType);

        Map<String, Object> taskParams = new HashMap<>();
        taskParams.put("localParams", new ArrayList<>());
        taskParams.put("resourceList", new ArrayList<>());
        taskParams.put("httpMethod", "POST");
        taskParams.put("httpCheckCondition", "STATUS_CODE_DEFAULT");
        taskParams.put("httpParams", httpParams);
        taskParams.put("url", executeUrl);
        taskParams.put("condition", "");
        taskParams.put("connectTimeout", 60000);
        taskParams.put("socketTimeout", 60000);

        Map<String, Object> task = new HashMap<>();
        task.put("code", taskCode);
        task.put("name", "seatunnel_execute");
        task.put("version", 1);
        task.put("description", "Trigger SeaTunnel Web job execution");
        task.put("delayTime", "0");
        task.put("taskType", "HTTP");
        task.put("taskParams", taskParams);
        task.put("flag", "YES");
        task.put("taskPriority", "MEDIUM");
        task.put("workerGroup", "default");
        task.put("failRetryTimes", "0");
        task.put("failRetryInterval", "1");
        task.put("timeoutFlag", "CLOSE");
        task.put("timeoutNotifyStrategy", "");
        task.put("timeout", 0);
        task.put("environmentCode", -1);

        List<Map<String, Object>> tasks = new ArrayList<>();
        tasks.add(task);
        return JsonUtils.toJsonString(tasks);
    }

    private String buildTaskRelationJson(long taskCode) {
        Map<String, Object> relation = new HashMap<>();
        relation.put("name", "");
        relation.put("preTaskCode", 0);
        relation.put("preTaskVersion", 0);
        relation.put("postTaskCode", taskCode);
        relation.put("postTaskVersion", 0);
        relation.put("conditionType", "NONE");
        relation.put("conditionParams", new HashMap<>());

        List<Map<String, Object>> relations = new ArrayList<>();
        relations.add(relation);
        return JsonUtils.toJsonString(relations);
    }

    private String buildLocationsJson(long taskCode) {
        Map<String, Object> location = new HashMap<>();
        location.put("taskCode", taskCode);
        location.put("x", 100);
        location.put("y", 100);

        List<Map<String, Object>> locations = new ArrayList<>();
        locations.add(location);
        return JsonUtils.toJsonString(locations);
    }

    private JsonNode postForm(String path, MultiValueMap<String, String> form) {
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        ResponseEntity<String> response =
                restTemplate.exchange(
                        properties.getApiUrl() + path, HttpMethod.POST, entity, String.class);
        return parseResponse(response.getBody());
    }

    private JsonNode putForm(String path, MultiValueMap<String, String> form) {
        HttpHeaders headers = buildHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(form, headers);
        ResponseEntity<String> response =
                restTemplate.exchange(
                        properties.getApiUrl() + path, HttpMethod.PUT, entity, String.class);
        return parseResponse(response.getBody());
    }

    private void postEmpty(String path) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response =
                restTemplate.exchange(
                        properties.getApiUrl() + path, HttpMethod.POST, entity, String.class);
        parseResponse(response.getBody());
    }

    private void delete(String path) {
        HttpHeaders headers = buildHeaders();
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<String> response =
                restTemplate.exchange(
                        properties.getApiUrl() + path, HttpMethod.DELETE, entity, String.class);
        parseResponse(response.getBody());
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", properties.getToken());
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
                log.error("DolphinScheduler API error: {}", body);
                throw new SeatunnelException(SeatunnelErrorEnum.ILLEGAL_STATE, msg);
            }
            return root.path("data");
        } catch (Exception e) {
            if (e instanceof SeatunnelException) {
                throw (SeatunnelException) e;
            }
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "Failed to parse DolphinScheduler response: " + e.getMessage());
        }
    }

    private long generateCode() {
        try {
            return CodeGenerateUtils.getInstance().genCode();
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            throw new SeatunnelException(SeatunnelErrorEnum.ILLEGAL_STATE, e.getMessage());
        }
    }

    @Data
    public static class DsSyncResult {
        private String projectCode;
        private Long processDefinitionCode;
        private Integer scheduleId;
    }
}
