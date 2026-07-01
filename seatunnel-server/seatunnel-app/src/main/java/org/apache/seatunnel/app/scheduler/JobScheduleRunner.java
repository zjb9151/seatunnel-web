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

package org.apache.seatunnel.app.scheduler;

import org.apache.seatunnel.app.dal.dao.IJobScheduleDao;
import org.apache.seatunnel.app.dal.entity.JobSchedule;
import org.apache.seatunnel.app.dal.entity.User;
import org.apache.seatunnel.app.domain.request.job.JobExecParam;
import org.apache.seatunnel.app.security.UserContext;
import org.apache.seatunnel.app.security.UserContextHolder;
import org.apache.seatunnel.app.service.IJobExecutorService;
import org.apache.seatunnel.app.service.impl.JobScheduleServiceImpl;
import org.apache.seatunnel.common.access.AccessInfo;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class JobScheduleRunner {

    @Resource private IJobScheduleDao jobScheduleDao;
    @Resource private IJobExecutorService jobExecutorService;

    private final ConcurrentHashMap<Long, Long> lastTriggeredMinute = new ConcurrentHashMap<>();

    @Scheduled(cron = "0 * * * * *")
    public void triggerInternalSchedules() {
        List<JobSchedule> schedules = jobScheduleDao.listAllEnabledInternalSchedules();
        if (schedules.isEmpty()) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        for (JobSchedule schedule : schedules) {
            try {
                if (!JobScheduleServiceImpl.SCHEDULE_TYPE_INTERNAL.equals(
                        schedule.getScheduleType())) {
                    continue;
                }
                if (!CronExpression.isValidExpression(schedule.getCrontab())) {
                    log.warn(
                            "Skip invalid cron for schedule id={}, crontab={}",
                            schedule.getId(),
                            schedule.getCrontab());
                    continue;
                }

                CronExpression cron = CronExpression.parse(schedule.getCrontab());
                LocalDateTime previousMinute = now.minusMinutes(1).withSecond(0).withNano(0);
                LocalDateTime next = cron.next(previousMinute);
                if (next == null) {
                    continue;
                }

                long scheduledEpochMinute =
                        next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() / 60_000;
                long currentEpochMinute =
                        now.withSecond(0)
                                        .withNano(0)
                                        .atZone(ZoneId.systemDefault())
                                        .toInstant()
                                        .toEpochMilli()
                                / 60_000;
                if (scheduledEpochMinute != currentEpochMinute) {
                    continue;
                }

                Long lastMinute = lastTriggeredMinute.get(schedule.getId());
                if (lastMinute != null && lastMinute == currentEpochMinute) {
                    continue;
                }

                executeScheduledJob(schedule);
                lastTriggeredMinute.put(schedule.getId(), currentEpochMinute);
                jobScheduleDao.updateLastTriggerTime(schedule.getId(), new Date());
            } catch (Exception e) {
                log.error(
                        "Failed to trigger internal schedule id={}, jobDefineId={}",
                        schedule.getId(),
                        schedule.getJobDefineId(),
                        e);
            }
        }
    }

    private void executeScheduledJob(JobSchedule schedule) {
        try {
            UserContext userContext = new UserContext();
            User user = new User();
            user.setId(schedule.getCreateUserId());
            user.setUsername("scheduler");
            userContext.setUser(user);
            userContext.setWorkspaceId(schedule.getWorkspaceId());
            AccessInfo accessInfo = new AccessInfo();
            accessInfo.setUsername("scheduler");
            userContext.setAccessInfo(accessInfo);
            UserContextHolder.setUserContext(userContext);

            log.info(
                    "Triggering scheduled job jobDefineId={}, scheduleId={}",
                    schedule.getJobDefineId(),
                    schedule.getId());
            jobExecutorService.jobExecute(schedule.getJobDefineId(), new JobExecParam());
        } finally {
            UserContextHolder.clear();
        }
    }
}
