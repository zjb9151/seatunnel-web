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

package org.apache.integration.service.impl;

import org.apache.seatunnel.server.common.CodeGenerateUtils;
import org.apache.seatunnel.server.common.SeatunnelErrorEnum;
import org.apache.seatunnel.server.common.SeatunnelException;

import org.apache.commons.lang3.StringUtils;
import org.apache.integration.client.SeatunnelApiClient;
import org.apache.integration.dal.dao.IJobScheduleDao;
import org.apache.integration.dal.entity.JobSchedule;
import org.apache.integration.domain.request.job.JobScheduleReq;
import org.apache.integration.domain.response.job.JobScheduleRes;
import org.apache.integration.service.IJobScheduleService;
import org.apache.integration.thirdparty.scheduler.DolphinSchedulerClient;
import org.apache.integration.thirdparty.scheduler.DolphinSchedulerClient.DsSyncResult;
import org.apache.integration.utils.RequestContext;

import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class JobScheduleServiceImpl implements IJobScheduleService {

    public static final String SCHEDULE_TYPE_INTERNAL = "INTERNAL";
    public static final String SCHEDULE_TYPE_DOLPHIN = "DOLPHIN_SCHEDULER";

    @Resource private IJobScheduleDao jobScheduleDao;
    @Resource private SeatunnelApiClient seatunnelApiClient;
    @Resource private DolphinSchedulerClient dolphinSchedulerClient;

    @Override
    public JobScheduleRes getByJobDefineId(Long jobDefineId) {
        return jobScheduleDao.getByJobDefineId(jobDefineId).map(this::toRes).orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public JobScheduleRes saveOrUpdate(Long jobDefineId, JobScheduleReq req) {
        validateRequest(jobDefineId, req);

        if (!seatunnelApiClient.jobDefinitionExists(jobDefineId)) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.RESOURCE_NOT_FOUND,
                    "Job definition " + jobDefineId + " not found");
        }

        String scheduleType =
                StringUtils.defaultIfBlank(req.getScheduleType(), SCHEDULE_TYPE_INTERNAL);
        boolean enabled = req.getEnabled() == null || req.getEnabled();

        JobSchedule existing = jobScheduleDao.getByJobDefineId(jobDefineId).orElse(null);
        if (existing != null
                && !Objects.equals(existing.getScheduleType(), scheduleType)
                && SCHEDULE_TYPE_DOLPHIN.equals(existing.getScheduleType())) {
            dolphinSchedulerClient.offlineSchedule(
                    existing.getDsScheduleId(), existing.getDsProcessDefinitionCode());
        }

        JobSchedule schedule =
                existing != null
                        ? existing
                        : JobSchedule.builder()
                                .id(generateId())
                                .jobDefineId(jobDefineId)
                                .createUserId(RequestContext.getCurrentUserId())
                                .createTime(new Date())
                                .build();

        schedule.setScheduleName(req.getScheduleName());
        schedule.setCrontab(req.getCrontab().trim());
        schedule.setScheduleType(scheduleType);
        schedule.setEnabled(enabled ? 1 : 0);
        schedule.setUpdateUserId(RequestContext.getCurrentUserId());
        schedule.setUpdateTime(new Date());

        if (SCHEDULE_TYPE_DOLPHIN.equals(scheduleType)) {
            syncToDolphinScheduler(schedule);
        } else if (existing != null && SCHEDULE_TYPE_DOLPHIN.equals(existing.getScheduleType())) {
            dolphinSchedulerClient.offlineSchedule(
                    existing.getDsScheduleId(), existing.getDsProcessDefinitionCode());
            schedule.setDsProjectCode(null);
            schedule.setDsProcessDefinitionCode(null);
            schedule.setDsScheduleId(null);
        }

        if (existing == null) {
            jobScheduleDao.insert(schedule);
        } else {
            jobScheduleDao.update(schedule);
        }
        return toRes(schedule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByJobDefineId(Long jobDefineId) {
        jobScheduleDao
                .getByJobDefineId(jobDefineId)
                .ifPresent(
                        schedule -> {
                            if (SCHEDULE_TYPE_DOLPHIN.equals(schedule.getScheduleType())) {
                                dolphinSchedulerClient.offlineSchedule(
                                        schedule.getDsScheduleId(),
                                        schedule.getDsProcessDefinitionCode());
                            }
                            jobScheduleDao.deleteById(schedule.getId());
                        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setEnabled(Long jobDefineId, boolean enabled) {
        JobSchedule schedule =
                jobScheduleDao
                        .getByJobDefineId(jobDefineId)
                        .orElseThrow(
                                () ->
                                        new SeatunnelException(
                                                SeatunnelErrorEnum.RESOURCE_NOT_FOUND,
                                                "Schedule for job " + jobDefineId + " not found"));
        schedule.setEnabled(enabled ? 1 : 0);
        schedule.setUpdateUserId(RequestContext.getCurrentUserId());
        schedule.setUpdateTime(new Date());

        if (SCHEDULE_TYPE_DOLPHIN.equals(schedule.getScheduleType())) {
            if (enabled) {
                syncToDolphinScheduler(schedule);
            } else {
                dolphinSchedulerClient.offlineSchedule(
                        schedule.getDsScheduleId(), schedule.getDsProcessDefinitionCode());
            }
        }
        jobScheduleDao.update(schedule);
    }

    private void syncToDolphinScheduler(JobSchedule schedule) {
        DsSyncResult result =
                dolphinSchedulerClient.syncSchedule(
                        schedule.getJobDefineId(),
                        schedule.getScheduleName(),
                        schedule.getCrontab(),
                        schedule.getDsProcessDefinitionCode(),
                        schedule.getDsScheduleId());
        schedule.setDsProjectCode(result.getProjectCode());
        schedule.setDsProcessDefinitionCode(result.getProcessDefinitionCode());
        schedule.setDsScheduleId(result.getScheduleId());
    }

    private void validateRequest(Long jobDefineId, JobScheduleReq req) {
        if (jobDefineId == null) {
            throw new SeatunnelException(SeatunnelErrorEnum.PARAM_CAN_NOT_BE_NULL, "jobDefineId");
        }
        if (req == null) {
            throw new SeatunnelException(SeatunnelErrorEnum.PARAM_CAN_NOT_BE_NULL, "schedule");
        }
        if (StringUtils.isBlank(req.getScheduleName())) {
            throw new SeatunnelException(SeatunnelErrorEnum.PARAM_CAN_NOT_BE_NULL, "scheduleName");
        }
        if (StringUtils.isBlank(req.getCrontab())) {
            throw new SeatunnelException(SeatunnelErrorEnum.PARAM_CAN_NOT_BE_NULL, "crontab");
        }

        String scheduleType =
                StringUtils.defaultIfBlank(req.getScheduleType(), SCHEDULE_TYPE_INTERNAL);
        if (SCHEDULE_TYPE_INTERNAL.equals(scheduleType)
                && !CronExpression.isValidExpression(req.getCrontab().trim())) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "Invalid cron expression for internal scheduler: " + req.getCrontab());
        }
        if (SCHEDULE_TYPE_DOLPHIN.equals(scheduleType) && !dolphinSchedulerClient.isAvailable()) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "DolphinScheduler integration is not enabled or not configured");
        }
    }

    private JobScheduleRes toRes(JobSchedule schedule) {
        JobScheduleRes res = new JobScheduleRes();
        res.setId(schedule.getId());
        res.setJobDefineId(schedule.getJobDefineId());
        res.setScheduleName(schedule.getScheduleName());
        res.setCrontab(schedule.getCrontab());
        res.setScheduleType(schedule.getScheduleType());
        res.setEnabled(schedule.getEnabled() != null && schedule.getEnabled() == 1);
        res.setDsProjectCode(schedule.getDsProjectCode());
        res.setDsProcessDefinitionCode(schedule.getDsProcessDefinitionCode());
        res.setDsScheduleId(schedule.getDsScheduleId());
        res.setLastTriggerTime(schedule.getLastTriggerTime());
        return res;
    }

    @Override
    public List<String> previewCronTimes(String crontab, int count) {
        if (StringUtils.isBlank(crontab)) {
            throw new SeatunnelException(SeatunnelErrorEnum.PARAM_CAN_NOT_BE_NULL, "crontab");
        }
        String expression = crontab.trim();
        if (!CronExpression.isValidExpression(expression)) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE, "Invalid cron expression: " + expression);
        }
        int previewCount = Math.min(Math.max(count, 1), 20);
        CronExpression cron = CronExpression.parse(expression);
        List<String> result = new ArrayList<>();
        LocalDateTime cursor = LocalDateTime.now().withNano(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < previewCount; i++) {
            cursor = cron.next(cursor);
            if (cursor == null) {
                break;
            }
            result.add(cursor.format(formatter));
        }
        return result;
    }

    private long generateId() {
        try {
            return CodeGenerateUtils.getInstance().genCode();
        } catch (CodeGenerateUtils.CodeGenerateException e) {
            throw new SeatunnelException(SeatunnelErrorEnum.ILLEGAL_STATE, e.getMessage());
        }
    }
}
