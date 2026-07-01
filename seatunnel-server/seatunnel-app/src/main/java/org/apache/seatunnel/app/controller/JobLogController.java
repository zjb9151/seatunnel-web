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
import org.apache.seatunnel.app.domain.response.job.JobLogNodeRes;
import org.apache.seatunnel.app.service.IJobLogService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import javax.annotation.Resource;

import java.util.List;

@RestController
@RequestMapping("/seatunnel/api/v1/job/log")
public class JobLogController {

    @Resource private IJobLogService jobLogService;

    @GetMapping("/nodes")
    @ApiOperation(value = "List job log nodes", httpMethod = "GET")
    public Result<List<JobLogNodeRes>> listLogNodes(
            @ApiParam(value = "jobEngineId") @RequestParam(required = false) String jobEngineId,
            @ApiParam(value = "jobInstanceId") @RequestParam(required = false) Long jobInstanceId) {
        return Result.success(jobLogService.listLogNodes(jobEngineId, jobInstanceId));
    }

    @GetMapping("/content")
    @ApiOperation(value = "Get job log content", httpMethod = "GET")
    public Result<String> getLogContent(
            @ApiParam(value = "file", required = true) @RequestParam String file,
            @ApiParam(value = "jobEngineId") @RequestParam(required = false) String jobEngineId,
            @ApiParam(value = "jobInstanceId") @RequestParam(required = false) Long jobInstanceId) {
        return Result.success(jobLogService.getLogContent(file, jobEngineId, jobInstanceId));
    }
}
