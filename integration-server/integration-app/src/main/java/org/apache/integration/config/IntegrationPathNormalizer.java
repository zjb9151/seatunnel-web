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

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import java.nio.file.Path;

@Component
public class IntegrationPathNormalizer {

    @Resource private IntegrationProperties properties;
    @Resource private IntegrationPathResolver pathResolver;

    @PostConstruct
    public void normalize() {
        normalizeRuntime();
        normalizeProcessHome(properties.getProcesses().getDolphinscheduler());
        normalizeEngineHome(properties.getProcesses().getSeatunnelEngine());
        normalizeSeatunnelWebHome(properties.getProcesses().getSeatunnelWeb());
        normalizeWebRoot(properties.getIntegration().getWebRoot());
    }

    private void normalizeRuntime() {
        IntegrationProperties.Runtime runtime = properties.getRuntime();
        if (StringUtils.isNotBlank(runtime.getBaseDir())) {
            runtime.setBaseDir(pathResolver.resolveString(runtime.getBaseDir()));
        }
        if (StringUtils.isNotBlank(runtime.getBundledDir())) {
            runtime.setBundledDir(pathResolver.resolveString(runtime.getBundledDir()));
        }
        normalizeRuntimeComponent(runtime.getDolphinscheduler());
        normalizeRuntimeComponent(runtime.getSeatunnelEngine());
        normalizeRuntimeComponent(runtime.getSeatunnelWeb());
    }

    private void normalizeRuntimeComponent(IntegrationProperties.RuntimeComponent cfg) {
        if (cfg != null && StringUtils.isNotBlank(cfg.getInstallDir())) {
            if (StringUtils.isNotBlank(properties.getRuntime().getBaseDir())) {
                cfg.setInstallDir(
                        Path.of(properties.getRuntime().getBaseDir(), cfg.getInstallDir())
                                .normalize()
                                .toString());
            } else {
                cfg.setInstallDir(pathResolver.resolveString(cfg.getInstallDir()));
            }
        }
    }

    private void normalizeProcessHome(IntegrationProperties.ProcessConfig config) {
        if (StringUtils.isNotBlank(config.getHome())) {
            config.setHome(pathResolver.resolveString(config.getHome()));
        }
    }

    private void normalizeEngineHome(IntegrationProperties.EngineProcess config) {
        if (StringUtils.isNotBlank(config.getHome())) {
            config.setHome(pathResolver.resolveString(config.getHome()));
        }
    }

    private void normalizeSeatunnelWebHome(IntegrationProperties.SeatunnelWebProcess config) {
        String home = config.getHome();
        if (StringUtils.isBlank(home) && StringUtils.isNotBlank(config.getDistHome())) {
            home = config.getDistHome();
        }
        if (StringUtils.isNotBlank(home)) {
            String resolved = pathResolver.resolveString(home);
            config.setHome(resolved);
            config.setDistHome(resolved);
        }
    }

    private void normalizeWebRoot(String webRoot) {
        if (StringUtils.isNotBlank(webRoot)) {
            properties.getIntegration().setWebRoot(pathResolver.resolveString(webRoot));
        }
    }
}
