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

package org.apache.integration.controller;

import org.apache.integration.common.Result;
import org.apache.integration.config.IntegrationProperties;
import org.apache.integration.process.ManagedProcess;
import org.apache.integration.process.ProcessSupervisor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import lombok.Data;

import javax.annotation.Resource;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/platform")
public class PlatformController {

    @Resource private IntegrationProperties properties;
    @Resource private ProcessSupervisor processSupervisor;
    @Resource private RestTemplate restTemplate;

    @GetMapping("/info")
    public Result<PlatformInfo> info() {
        PlatformInfo info = new PlatformInfo();
        info.setIntegrationEnabled(true);
        info.setTimezone(properties.getIntegration().getTimezone());
        info.setWebRoot(properties.getIntegration().getWebRoot());

        Map<String, ServiceHealth> services = new LinkedHashMap<>();
        services.put("integration-server", healthSelf());
        services.put("dolphinscheduler", healthDs());
        services.put("seatunnel-web", healthSt());
        services.put("seatunnel-engine", healthEngine());
        info.setServices(services);

        for (Map.Entry<String, ManagedProcess> entry :
                processSupervisor.getProcesses().entrySet()) {
            ManagedProcess mp = entry.getValue();
            ServiceHealth h = services.computeIfAbsent(entry.getKey(), k -> new ServiceHealth());
            h.setManaged(true);
            h.setPid(mp.getPid());
            h.setAlive(mp.isAlive());
        }
        return Result.success(info);
    }

    private ServiceHealth healthSelf() {
        ServiceHealth h = new ServiceHealth();
        h.setAlive(true);
        h.setMessage("running");
        return h;
    }

    private ServiceHealth healthDs() {
        ServiceHealth h = new ServiceHealth();
        if (!properties.getProcesses().getDolphinscheduler().isEnabled()) {
            h.setMessage("disabled");
            return h;
        }
        try {
            String url =
                    properties.getApiUrl()
                            + properties.getProcesses().getDolphinscheduler().getHealthCheckPath();
            ResponseEntity<String> r = restTemplate.getForEntity(url, String.class);
            h.setAlive(r.getStatusCode().is2xxSuccessful());
            h.setMessage(h.isAlive() ? "reachable" : "HTTP " + r.getStatusCode());
        } catch (Exception e) {
            h.setAlive(false);
            h.setMessage(e.getMessage());
        }
        return h;
    }

    private ServiceHealth healthSt() {
        ServiceHealth h = new ServiceHealth();
        if (!properties.getProcesses().getSeatunnelWeb().isEnabled()) {
            h.setMessage("disabled");
            return h;
        }
        try {
            String base = properties.getProcesses().getSeatunnelWeb().getApiUrl();
            restTemplate.getForEntity(base + "/ui/", String.class);
            h.setAlive(true);
            h.setMessage("reachable");
        } catch (Exception e) {
            h.setAlive(false);
            h.setMessage(e.getMessage());
        }
        return h;
    }

    private ServiceHealth healthEngine() {
        ServiceHealth h = new ServiceHealth();
        if (!properties.getProcesses().getSeatunnelEngine().isEnabled()) {
            h.setMessage("disabled");
            return h;
        }
        h.setMessage("check port 5801 manually");
        h.setAlive(true);
        return h;
    }

    @Data
    public static class PlatformInfo {
        private boolean integrationEnabled;
        private String timezone;
        private String webRoot;
        private Map<String, ServiceHealth> services;
    }

    @Data
    public static class ServiceHealth {
        private boolean alive;
        private boolean managed;
        private Long pid;
        private String message;
    }
}
