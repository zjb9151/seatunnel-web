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

import { reactive } from 'vue'
import { useI18n } from 'vue-i18n'
import { useMessage } from 'naive-ui'
import {
  getJobSchedule,
  saveJobSchedule,
  deleteJobSchedule
} from '@/service/job-schedule'

export function useScheduleModal(props: any, ctx: any) {
  const { t } = useI18n()
  const message = useMessage()

  const variables = reactive({
    loading: false,
    saving: false,
    model: {
      scheduleName: '',
      crontab: '0 0 2 * * ?',
      scheduleType: 'INTERNAL',
      enabled: true
    }
  })

  const loadSchedule = async (jobDefineId: number) => {
    variables.loading = true
    try {
      const res = await getJobSchedule(jobDefineId)
      if (res) {
        variables.model.scheduleName = res.scheduleName || props.row?.name || ''
        variables.model.crontab = res.crontab || '0 0 2 * * ?'
        variables.model.scheduleType = res.scheduleType || 'INTERNAL'
        variables.model.enabled = res.enabled !== false
      } else {
        variables.model.scheduleName = props.row?.name || ''
        variables.model.crontab = '0 0 2 * * ?'
        variables.model.scheduleType = 'INTERNAL'
        variables.model.enabled = true
      }
    } catch {
      variables.model.scheduleName = props.row?.name || ''
    } finally {
      variables.loading = false
    }
  }

  const handleSave = async () => {
    if (!variables.model.scheduleName?.trim()) {
      message.error(t('project.synchronization_definition.schedule_name_validate'))
      return
    }
    if (!variables.model.crontab?.trim()) {
      message.error(t('project.synchronization_definition.crontab_validate'))
      return
    }

    variables.saving = true
    try {
      await saveJobSchedule(props.row.id, {
        scheduleName: variables.model.scheduleName.trim(),
        crontab: variables.model.crontab.trim(),
        scheduleType: variables.model.scheduleType,
        enabled: variables.model.enabled
      })
      message.success(t('project.synchronization_definition.schedule_save_success'))
      ctx.emit('confirmModal')
    } catch {
      message.error(t('project.synchronization_definition.schedule_save_failed'))
    } finally {
      variables.saving = false
    }
  }

  const handleDelete = async () => {
    variables.saving = true
    try {
      await deleteJobSchedule(props.row.id)
      message.success(t('project.synchronization_definition.schedule_delete_success'))
      ctx.emit('confirmModal')
    } catch {
      message.error(t('project.synchronization_definition.schedule_delete_failed'))
    } finally {
      variables.saving = false
    }
  }

  return {
    variables,
    loadSchedule,
    handleSave,
    handleDelete
  }
}
