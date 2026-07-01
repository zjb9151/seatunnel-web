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

package org.apache.seatunnel.app.controller;

import org.apache.seatunnel.app.common.Result;
import org.apache.seatunnel.app.domain.request.job.JobScheduleReq;
import org.apache.seatunnel.app.domain.response.job.JobScheduleRes;
import org.apache.seatunnel.app.service.IJobScheduleService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.annotation.Resource;

import java.util.List;

@RestController
@RequestMapping("/seatunnel/api/v1/job/schedule")
public class JobScheduleController {

    @Resource private IJobScheduleService jobScheduleService;

    @GetMapping
    @ApiOperation(value = "Get job schedule by job define id", httpMethod = "GET")
    public Result<JobScheduleRes> getSchedule(
            @ApiParam(value = "jobDefineId", required = true) @RequestParam Long jobDefineId) {
        return Result.success(jobScheduleService.getByJobDefineId(jobDefineId));
    }

    @PostMapping
    @ApiOperation(value = "Create or update job schedule", httpMethod = "POST")
    public Result<JobScheduleRes> saveSchedule(
            @ApiParam(value = "jobDefineId", required = true) @RequestParam Long jobDefineId,
            @RequestBody JobScheduleReq req) {
        return Result.success(jobScheduleService.saveOrUpdate(jobDefineId, req));
    }

    @PutMapping("/enable")
    @ApiOperation(value = "Enable or disable job schedule", httpMethod = "PUT")
    public Result<Void> setEnabled(
            @ApiParam(value = "jobDefineId", required = true) @RequestParam Long jobDefineId,
            @ApiParam(value = "enabled", required = true) @RequestParam Boolean enabled) {
        jobScheduleService.setEnabled(jobDefineId, enabled);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation(value = "Delete job schedule", httpMethod = "DELETE")
    public Result<Void> deleteSchedule(
            @ApiParam(value = "jobDefineId", required = true) @RequestParam Long jobDefineId) {
        jobScheduleService.deleteByJobDefineId(jobDefineId);
        return Result.success();
    }

    @GetMapping("/preview")
    @ApiOperation(value = "Preview next cron execution times", httpMethod = "GET")
    public Result<List<String>> previewCron(
            @ApiParam(value = "crontab", required = true) @RequestParam String crontab,
            @ApiParam(value = "count", required = false) @RequestParam(defaultValue = "5")
                    Integer count) {
        return Result.success(jobScheduleService.previewCronTimes(crontab, count));
    }
}
