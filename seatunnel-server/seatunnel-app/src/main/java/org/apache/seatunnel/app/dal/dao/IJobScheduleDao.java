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

package org.apache.seatunnel.app.dal.dao;

import org.apache.seatunnel.app.dal.entity.JobSchedule;

import lombok.NonNull;

import java.util.List;
import java.util.Optional;

public interface IJobScheduleDao {

    void insert(@NonNull JobSchedule jobSchedule);

    void update(@NonNull JobSchedule jobSchedule);

    void deleteById(@NonNull Long id);

    Optional<JobSchedule> getByJobDefineId(@NonNull Long jobDefineId);

    Optional<JobSchedule> getById(@NonNull Long id);

    List<JobSchedule> listEnabledInternalSchedules();

    List<JobSchedule> listAllEnabledInternalSchedules();

    void updateLastTriggerTime(@NonNull Long id, @NonNull java.util.Date lastTriggerTime);
}
