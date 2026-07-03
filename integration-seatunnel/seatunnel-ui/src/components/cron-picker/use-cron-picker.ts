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

import { computed, reactive, ref, watch } from 'vue'
import { debounce } from 'lodash'
import { previewJobScheduleCron } from '@/service/job-schedule'
import type { ComposerTranslation } from 'vue-i18n'
import type { CronFieldKey, CronFieldMode, CronFieldState, CronFieldsState } from './types'

const FIELD_ORDER: CronFieldKey[] = [
  'second',
  'minute',
  'hour',
  'day',
  'month',
  'week'
]

export function createDefaultField(
  mode: CronFieldMode = 'every',
  appoint: number[] = []
): CronFieldState {
  return {
    mode,
    periodStart: 1,
    periodEnd: 2,
    loopStart: 0,
    loopStep: 5,
    appoint
  }
}

export function createDefaultFields(): CronFieldsState {
  return {
    second: createDefaultField('every'),
    minute: createDefaultField('every'),
    hour: createDefaultField('every'),
    day: createDefaultField('every'),
    month: createDefaultField('every'),
    week: createDefaultField('unspecified')
  }
}

function parsePart(part: string, field: CronFieldState): CronFieldState {
  const next = { ...field, appoint: [...field.appoint] }
  if (part === '*') {
    next.mode = 'every'
    return next
  }
  if (part === '?') {
    next.mode = 'unspecified'
    return next
  }
  if (/^\d+\/\d+$/.test(part)) {
    const [start, step] = part.split('/')
    next.mode = 'loop'
    next.loopStart = Number(start)
    next.loopStep = Number(step)
    return next
  }
  if (/^\d+-\d+$/.test(part)) {
    const [start, end] = part.split('-')
    next.mode = 'period'
    next.periodStart = Number(start)
    next.periodEnd = Number(end)
    return next
  }
  if (/^[\d,]+$/.test(part)) {
    next.mode = 'appoint'
    next.appoint = part.split(',').map(Number)
    return next
  }
  next.mode = 'every'
  return next
}

function partToCron(field: CronFieldState): string {
  switch (field.mode) {
    case 'every':
      return '*'
    case 'unspecified':
      return '?'
    case 'period':
      return `${field.periodStart}-${field.periodEnd}`
    case 'loop':
      return `${field.loopStart}/${field.loopStep}`
    case 'appoint':
      return field.appoint.length ? [...field.appoint].sort((a, b) => a - b).join(',') : '*'
    default:
      return '*'
  }
}

export function buildCronExpression(fields: CronFieldsState): string {
  const day = { ...fields.day }
  const week = { ...fields.week }

  if (day.mode !== 'every' && day.mode !== 'unspecified') {
    week.mode = 'unspecified'
  } else if (week.mode !== 'every' && week.mode !== 'unspecified') {
    day.mode = 'unspecified'
  }

  return FIELD_ORDER.map((key) => {
    if (key === 'day') return partToCron(day)
    if (key === 'week') return partToCron(week)
    return partToCron(fields[key])
  }).join(' ')
}

export function parseCronExpression(expression: string): CronFieldsState {
  const fields = createDefaultFields()
  const parts = expression.trim().split(/\s+/)
  if (parts.length < 6) {
    return fields
  }
  const cronParts = parts.length >= 7 ? parts.slice(0, 6) : parts
  FIELD_ORDER.forEach((key, index) => {
    fields[key] = parsePart(cronParts[index], fields[key])
  })
  return fields
}

export function describeCron(fields: CronFieldsState, t: ComposerTranslation): string {
  const descriptions: string[] = []
  FIELD_ORDER.forEach((key) => {
    const field = fields[key]
    const label = t(`project.synchronization_definition.cron_field_${key}`)
    switch (field.mode) {
      case 'every':
        descriptions.push(t('project.synchronization_definition.cron_desc_every', { field: label }))
        break
      case 'unspecified':
        break
      case 'period':
        descriptions.push(
          t('project.synchronization_definition.cron_desc_period', {
            field: label,
            start: field.periodStart,
            end: field.periodEnd
          })
        )
        break
      case 'loop':
        descriptions.push(
          t('project.synchronization_definition.cron_desc_loop', {
            field: label,
            start: field.loopStart,
            step: field.loopStep
          })
        )
        break
      case 'appoint':
        if (field.appoint.length) {
          descriptions.push(
            t('project.synchronization_definition.cron_desc_appoint', {
              field: label,
              values: field.appoint.join(',')
            })
          )
        }
        break
    }
  })
  return descriptions.length
    ? descriptions.join('；')
    : t('project.synchronization_definition.cron_desc_default')
}

export function useCronPicker(initialCrontab: string, t: ComposerTranslation) {
  const activeTab = ref<CronFieldKey>('second')
  const fields = reactive<CronFieldsState>(parseCronExpression(initialCrontab || '0 0 2 * * ?'))
  const previewTimes = ref<string[]>([])
  const previewLoading = ref(false)
  const previewError = ref('')

  const crontab = computed(() => buildCronExpression(fields))
  const description = computed(() => describeCron(fields, t))

  const refreshPreview = debounce(async (expression: string) => {
    if (!expression.trim()) {
      previewTimes.value = []
      previewError.value = ''
      return
    }
    previewLoading.value = true
    previewError.value = ''
    try {
      previewTimes.value = await previewJobScheduleCron(expression, 5)
    } catch {
      previewTimes.value = []
      previewError.value = t('project.synchronization_definition.cron_preview_failed')
    } finally {
      previewLoading.value = false
    }
  }, 300)

  watch(
    crontab,
    (value) => {
      refreshPreview(value)
    },
    { immediate: true }
  )

  const setCrontab = (expression: string) => {
    Object.assign(fields, parseCronExpression(expression))
    refreshPreview(expression)
  }

  const toggleAppoint = (key: CronFieldKey, value: number, checked: boolean) => {
    const field = fields[key]
    field.mode = 'appoint'
    if (checked) {
      if (!field.appoint.includes(value)) {
        field.appoint.push(value)
      }
    } else {
      field.appoint = field.appoint.filter((item) => item !== value)
    }
  }

  return {
    activeTab,
    fields,
    crontab,
    description,
    previewTimes,
    previewLoading,
    previewError,
    setCrontab,
    toggleAppoint
  }
}
