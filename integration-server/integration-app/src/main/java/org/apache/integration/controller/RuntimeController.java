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
import org.apache.integration.runtime.RuntimeInstallService;
import org.apache.integration.runtime.RuntimeInstallService.RuntimeComponentStatus;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;

import javax.annotation.Resource;

import java.util.List;

@RestController
@RequestMapping("/api/v1/runtime")
public class RuntimeController {

    @Resource private RuntimeInstallService runtimeInstallService;

    @GetMapping("/components")
    public Result<List<RuntimeComponentStatus>> listComponents() {
        return Result.success(runtimeInstallService.listStatus());
    }

    @PostMapping("/{componentId}/install")
    public Result<RuntimeComponentStatus> installOfficial(
            @PathVariable String componentId, @RequestBody RuntimeActionReq req) {
        RuntimeComponentStatus status =
                runtimeInstallService.installFromOfficial(componentId, req.getVersion());
        runtimeInstallService.restartManagedProcesses();
        return Result.success(status);
    }

    @PostMapping("/{componentId}/install-local")
    public Result<RuntimeComponentStatus> installLocal(
            @PathVariable String componentId, @RequestBody RuntimeLocalInstallReq req) {
        RuntimeComponentStatus status =
                runtimeInstallService.installFromLocalPath(
                        componentId, req.getVersion(), req.getLocalPath());
        runtimeInstallService.restartManagedProcesses();
        return Result.success(status);
    }

    @PostMapping("/{componentId}/switch")
    public Result<RuntimeComponentStatus> switchVersion(
            @PathVariable String componentId, @RequestBody RuntimeActionReq req) {
        RuntimeComponentStatus status =
                runtimeInstallService.switchVersion(componentId, req.getVersion());
        runtimeInstallService.restartManagedProcesses();
        return Result.success(status);
    }

    @Data
    public static class RuntimeActionReq {
        private String version;
    }

    @Data
    public static class RuntimeLocalInstallReq {
        private String version;
        private String localPath;
    }
}
