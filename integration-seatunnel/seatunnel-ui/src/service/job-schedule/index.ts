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

import { axios } from '@/service/service'

export function getJobSchedule(jobDefineId: number): any {
  return axios({
    url: '/job/schedule',
    method: 'get',
    params: { jobDefineId }
  })
}

export function saveJobSchedule(jobDefineId: number, data: any): any {
  return axios({
    url: '/job/schedule',
    method: 'post',
    params: { jobDefineId },
    data,
    headers: {
      'Content-Type': 'application/json;charset=UTF-8'
    },
    transformRequest: (params) => JSON.stringify(params)
  })
}

export function deleteJobSchedule(jobDefineId: number): any {
  return axios({
    url: '/job/schedule',
    method: 'delete',
    params: { jobDefineId }
  })
}

export function setJobScheduleEnabled(jobDefineId: number, enabled: boolean): any {
  return axios({
    url: '/job/schedule/enable',
    method: 'put',
    params: { jobDefineId, enabled }
  })
}

export function previewJobScheduleCron(crontab: string, count = 5): any {
  return axios({
    url: '/job/schedule/preview',
    method: 'get',
    params: { crontab, count }
  })
}
