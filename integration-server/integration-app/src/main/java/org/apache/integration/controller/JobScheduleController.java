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
import org.apache.integration.domain.request.job.JobScheduleReq;
import org.apache.integration.domain.response.job.JobScheduleRes;
import org.apache.integration.service.IJobScheduleService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
public class JobScheduleController {

    @Resource private IJobScheduleService jobScheduleService;

    @GetMapping
    public Result<JobScheduleRes> getSchedule(@RequestParam Long jobDefineId) {
        return Result.success(jobScheduleService.getByJobDefineId(jobDefineId));
    }

    @PostMapping
    public Result<JobScheduleRes> saveSchedule(
            @RequestParam Long jobDefineId, @RequestBody JobScheduleReq req) {
        return Result.success(jobScheduleService.saveOrUpdate(jobDefineId, req));
    }

    @PutMapping("/enable")
    public Result<Void> setEnabled(@RequestParam Long jobDefineId, @RequestParam Boolean enabled) {
        jobScheduleService.setEnabled(jobDefineId, enabled);
        return Result.success();
    }

    @DeleteMapping
    public Result<Void> deleteSchedule(@RequestParam Long jobDefineId) {
        jobScheduleService.deleteByJobDefineId(jobDefineId);
        return Result.success();
    }

    @GetMapping("/preview")
    public Result<List<String>> previewCron(
            @RequestParam String crontab, @RequestParam(defaultValue = "5") Integer count) {
        return Result.success(jobScheduleService.previewCronTimes(crontab, count));
    }
}
