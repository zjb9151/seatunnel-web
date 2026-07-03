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

package org.apache.integration.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
@ConfigurationProperties(prefix = "")
public class IntegrationProperties {

    private IntegrationMeta integration = new IntegrationMeta();
    private Processes processes = new Processes();
    private DolphinScheduler dolphinscheduler = new DolphinScheduler();
    private SeatunnelWeb seatunnelWeb = new SeatunnelWeb();

    /** Compatibility with migrated DolphinSchedulerProperties accessors. */
    public boolean isEnabled() {
        return processes.getDolphinscheduler().isEnabled();
    }

    public String getApiUrl() {
        return processes.getDolphinscheduler().getApiUrl();
    }

    public String getToken() {
        return dolphinscheduler.getToken();
    }

    public String getProjectCode() {
        return dolphinscheduler.getProjectCode();
    }

    public String getSeatunnelWebUrl() {
        return processes.getSeatunnelWeb().getApiUrl();
    }

    public String getServiceToken() {
        return seatunnelWeb.getServiceToken();
    }

    public String getTenantCode() {
        return dolphinscheduler.getTenantCode();
    }

    public String getUiUrl() {
        return "/dolphinscheduler/ui/";
    }

    public String getUsername() {
        return dolphinscheduler.getUsername();
    }

    public String getPassword() {
        return dolphinscheduler.getPassword();
    }

    public boolean isSyncDatasource() {
        return dolphinscheduler.isSyncDatasource();
    }

    public String getSeatunnelUiUrl() {
        return seatunnelWeb.getUiPath();
    }

    public boolean isUseDsDatasource() {
        return dolphinscheduler.isUseDsDatasource();
    }

    @Data
    public static class IntegrationMeta {
        private String timezone = "Asia/Shanghai";
        private String webRoot = "../integration-web/dist";
    }

    @Data
    public static class Processes {
        private ProcessConfig dolphinscheduler = new ProcessConfig();
        private EngineProcess seatunnelEngine = new EngineProcess();
        private SeatunnelWebProcess seatunnelWeb = new SeatunnelWebProcess();
    }

    @Data
    public static class ProcessConfig {
        private boolean enabled = true;
        private String home = "";
        private String apiUrl = "http://127.0.0.1:12345/dolphinscheduler";
        private boolean autoStart = true;
        private String healthCheckPath = "/login";
        private int maxRestarts = 5;
        private long restartDelayMs = 5000;
    }

    @Data
    public static class EngineProcess {
        private boolean enabled = true;
        private String home = "";
        private boolean autoStart = true;
        private int maxRestarts = 5;
        private long restartDelayMs = 5000;
    }

    @Data
    public static class SeatunnelWebProcess {
        private boolean enabled = true;
        private String distHome = "";
        private String apiUrl = "http://127.0.0.1:8801";
        private boolean autoStart = true;
        private int maxRestarts = 5;
        private long restartDelayMs = 10000;
    }

    @Data
    public static class DolphinScheduler {
        private String token = "";
        private String projectCode = "";
        private String username = "admin";
        private String password = "dolphinscheduler123";
        private String tenantCode = "default";
        private boolean syncDatasource = false;
        private boolean useDsDatasource = true;
    }

    @Data
    public static class SeatunnelWeb {
        private String serviceToken = "";
        private String jwtSecret = "";
        private String uiPath = "/seatunnel-ui";
    }
}
