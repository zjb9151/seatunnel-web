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

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

import java.nio.file.Files;
import java.nio.file.Path;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Resource private IntegrationProperties properties;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path webRoot = Path.of(properties.getIntegration().getWebRoot()).normalize();
        registry.addResourceHandler("/**")
                .addResourceLocations("file:" + webRoot + "/")
                .resourceChain(true);

        String uiPath = properties.getSeatunnelUiUrl();
        if (StringUtils.isBlank(uiPath)) {
            uiPath = "/seatunnel-ui";
        }
        if (!uiPath.startsWith("/")) {
            uiPath = "/" + uiPath;
        }
        String uiPrefix = uiPath.endsWith("/") ? uiPath + "**" : uiPath + "/**";

        Path stUiDir = resolveSeatunnelUiDir();
        if (stUiDir != null && Files.isDirectory(stUiDir)) {
            registry.addResourceHandler(uiPrefix)
                    .addResourceLocations("file:" + stUiDir + "/")
                    .resourceChain(true);
        }
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }

    private Path resolveSeatunnelUiDir() {
        String home = properties.getProcesses().getSeatunnelWeb().getEffectiveHome();
        if (StringUtils.isBlank(home)) {
            return null;
        }
        Path ui = Path.of(home, "ui");
        return Files.isDirectory(ui) ? ui : null;
    }
}
