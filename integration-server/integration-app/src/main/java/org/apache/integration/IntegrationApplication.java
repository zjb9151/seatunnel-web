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

package org.apache.integration;

import org.apache.integration.config.IntegrationProperties;
import org.apache.integration.process.ProcessSupervisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PreDestroy;
import javax.annotation.Resource;

import java.util.TimeZone;

@SpringBootApplication(scanBasePackages = {"org.apache.integration"})
@EnableConfigurationProperties(IntegrationProperties.class)
@EnableScheduling
public class IntegrationApplication {

    @Resource private ProcessSupervisor processSupervisor;

    public static void main(String[] args) {
        String tz = System.getProperty("integration.timezone", "Asia/Shanghai");
        TimeZone.setDefault(TimeZone.getTimeZone(tz));
        SpringApplication.run(IntegrationApplication.class, args);
    }

    @PreDestroy
    public void shutdown() {
        processSupervisor.stopAll();
    }
}
