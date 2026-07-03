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

package org.apache.integration.dal.dao.impl;

import org.apache.integration.dal.dao.IJobScheduleDao;
import org.apache.integration.dal.entity.JobSchedule;
import org.apache.integration.dal.mapper.JobScheduleMapper;
import org.apache.integration.utils.RequestContext;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.NonNull;

import javax.annotation.Resource;

import java.util.List;
import java.util.Optional;

@Repository
public class JobScheduleDaoImpl implements IJobScheduleDao {

    @Resource private JobScheduleMapper jobScheduleMapper;

    private Long getWorkspaceId() {
        return RequestContext.getCurrentWorkspaceId();
    }

    @Override
    public void insert(@NonNull JobSchedule jobSchedule) {
        jobSchedule.setWorkspaceId(getWorkspaceId());
        jobScheduleMapper.insert(jobSchedule);
    }

    @Override
    public void update(@NonNull JobSchedule jobSchedule) {
        jobSchedule.setWorkspaceId(getWorkspaceId());
        jobScheduleMapper.updateById(jobSchedule);
    }

    @Override
    public void deleteById(@NonNull Long id) {
        jobScheduleMapper.delete(
                new LambdaQueryWrapper<JobSchedule>()
                        .eq(JobSchedule::getId, id)
                        .eq(JobSchedule::getWorkspaceId, getWorkspaceId()));
    }

    @Override
    public Optional<JobSchedule> getByJobDefineId(@NonNull Long jobDefineId) {
        JobSchedule schedule =
                jobScheduleMapper.selectOne(
                        new LambdaQueryWrapper<JobSchedule>()
                                .eq(JobSchedule::getJobDefineId, jobDefineId)
                                .eq(JobSchedule::getWorkspaceId, getWorkspaceId()));
        return Optional.ofNullable(schedule);
    }

    @Override
    public Optional<JobSchedule> getById(@NonNull Long id) {
        JobSchedule schedule =
                jobScheduleMapper.selectOne(
                        new LambdaQueryWrapper<JobSchedule>()
                                .eq(JobSchedule::getId, id)
                                .eq(JobSchedule::getWorkspaceId, getWorkspaceId()));
        return Optional.ofNullable(schedule);
    }

    @Override
    public List<JobSchedule> listEnabledInternalSchedules() {
        return jobScheduleMapper.selectList(
                new LambdaQueryWrapper<JobSchedule>()
                        .eq(JobSchedule::getEnabled, 1)
                        .eq(JobSchedule::getScheduleType, "INTERNAL")
                        .eq(JobSchedule::getWorkspaceId, getWorkspaceId()));
    }

    @Override
    public List<JobSchedule> listAllEnabledInternalSchedules() {
        return jobScheduleMapper.selectList(
                new LambdaQueryWrapper<JobSchedule>()
                        .eq(JobSchedule::getEnabled, 1)
                        .eq(JobSchedule::getScheduleType, "INTERNAL"));
    }

    @Override
    public void updateLastTriggerTime(@NonNull Long id, @NonNull java.util.Date lastTriggerTime) {
        JobSchedule update = JobSchedule.builder().id(id).lastTriggerTime(lastTriggerTime).build();
        jobScheduleMapper.updateById(update);
    }
}
