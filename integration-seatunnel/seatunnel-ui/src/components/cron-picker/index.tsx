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

import {
  defineComponent,
  PropType,
  watch,
  computed
} from 'vue'
import {
  NTabs,
  NTabPane,
  NRadioGroup,
  NRadio,
  NInputNumber,
  NCheckbox,
  NInput,
  NSpin,
  NSpace
} from 'naive-ui'
import { useI18n } from 'vue-i18n'
import { useCronPicker } from './use-cron-picker'
import type { CronFieldKey } from './types'
import styles from './index.module.scss'

const FIELD_META: Record<
  CronFieldKey,
  { min: number; max: number; allowUnspecified: boolean }
> = {
  second: { min: 0, max: 59, allowUnspecified: false },
  minute: { min: 0, max: 59, allowUnspecified: false },
  hour: { min: 0, max: 23, allowUnspecified: false },
  day: { min: 1, max: 31, allowUnspecified: true },
  month: { min: 1, max: 12, allowUnspecified: false },
  week: { min: 1, max: 7, allowUnspecified: true }
}

const props = {
  value: {
    type: String as PropType<string>,
    default: '0 0 2 * * ?'
  }
}

export const CronPicker = defineComponent({
  name: 'CronPicker',
  props,
  emits: ['update:value'],
  setup(props, { emit }) {
    const { t } = useI18n()
    const {
      activeTab,
      fields,
      crontab,
      description,
      previewTimes,
      previewLoading,
      previewError,
      setCrontab,
      toggleAppoint
    } = useCronPicker(props.value, t)

    watch(crontab, (value) => {
      emit('update:value', value)
    })

    watch(
      () => props.value,
      (value) => {
        if (value && value !== crontab.value) {
          setCrontab(value)
        }
      }
    )

    const weekLabels = computed(() => [
      t('project.synchronization_definition.cron_week_1'),
      t('project.synchronization_definition.cron_week_2'),
      t('project.synchronization_definition.cron_week_3'),
      t('project.synchronization_definition.cron_week_4'),
      t('project.synchronization_definition.cron_week_5'),
      t('project.synchronization_definition.cron_week_6'),
      t('project.synchronization_definition.cron_week_7')
    ])

    const renderFieldPanel = (key: CronFieldKey) => {
      const field = fields[key]
      const meta = FIELD_META[key]
      const options = Array.from(
        { length: meta.max - meta.min + 1 },
        (_, index) => meta.min + index
      )

      return (
        <div class={styles.fieldPanel}>
          <NRadioGroup v-model={[field.mode, 'value']}>
            <NSpace vertical>
              <NRadio value='every'>
                {t('project.synchronization_definition.cron_mode_every', {
                  field: t(`project.synchronization_definition.cron_field_${key}`)
                })}
              </NRadio>
              {meta.allowUnspecified && (
                <NRadio value='unspecified'>
                  {t('project.synchronization_definition.cron_mode_unspecified')}
                </NRadio>
              )}
              <NSpace align='center' wrap>
                <NRadio value='period' />
                <span>{t('project.synchronization_definition.cron_mode_period')}</span>
                <NInputNumber
                  size='small'
                  min={meta.min}
                  max={meta.max}
                  disabled={field.mode !== 'period'}
                  v-model={[field.periodStart, 'value']}
                  style={{ width: '72px' }}
                />
                <span>{t('project.synchronization_definition.cron_to')}</span>
                <NInputNumber
                  size='small'
                  min={meta.min}
                  max={meta.max}
                  disabled={field.mode !== 'period'}
                  v-model={[field.periodEnd, 'value']}
                  style={{ width: '72px' }}
                />
              </NSpace>
              <NSpace align='center' wrap>
                <NRadio value='loop' />
                <span>{t('project.synchronization_definition.cron_mode_loop')}</span>
                <NInputNumber
                  size='small'
                  min={meta.min}
                  max={meta.max}
                  disabled={field.mode !== 'loop'}
                  v-model={[field.loopStart, 'value']}
                  style={{ width: '72px' }}
                />
                <span>{t('project.synchronization_definition.cron_loop_from')}</span>
                <NInputNumber
                  size='small'
                  min={1}
                  max={meta.max}
                  disabled={field.mode !== 'loop'}
                  v-model={[field.loopStep, 'value']}
                  style={{ width: '72px' }}
                />
                <span>{t('project.synchronization_definition.cron_loop_step')}</span>
              </NSpace>
              <NRadio value='appoint'>
                {t('project.synchronization_definition.cron_mode_appoint')}
              </NRadio>
            </NSpace>
          </NRadioGroup>
          {field.mode === 'appoint' && (
            <div class={styles.appointGrid}>
              {options.map((value) => (
                <NCheckbox
                  key={`${key}-${value}`}
                  checked={field.appoint.includes(value)}
                  onUpdateChecked={(checked: boolean) =>
                    toggleAppoint(key, value, checked)
                  }
                >
                  {key === 'week'
                    ? weekLabels.value[value - 1]
                    : String(value).padStart(2, '0')}
                </NCheckbox>
              ))}
            </div>
          )}
        </div>
      )
    }

    return () => (
      <div class={styles.cronPicker}>
        <div class={styles.expressionRow}>
          <NInput value={crontab.value} readonly />
        </div>
        <div class={styles.description}>{description.value}</div>
        <NTabs
          type='line'
          value={activeTab.value}
          onUpdateValue={(value: CronFieldKey) => {
            activeTab.value = value
          }}
        >
          {(['second', 'minute', 'hour', 'day', 'month', 'week'] as CronFieldKey[]).map(
            (key) => (
              <NTabPane
                key={key}
                name={key}
                tab={t(`project.synchronization_definition.cron_field_${key}`)}
              >
                {renderFieldPanel(key)}
              </NTabPane>
            )
          )}
        </NTabs>
        <div class={styles.previewSection}>
          <div class={styles.previewTitle}>
            {t('project.synchronization_definition.cron_preview_title')}
          </div>
          <NSpin show={previewLoading.value}>
            {previewError.value ? (
              <div class={styles.previewEmpty}>{previewError.value}</div>
            ) : previewTimes.value.length ? (
              <div class={styles.previewList}>
                {previewTimes.value.map((time) => (
                  <div key={time} class={styles.previewItem}>
                    {time}
                  </div>
                ))}
              </div>
            ) : (
              <div class={styles.previewEmpty}>
                {t('project.synchronization_definition.cron_preview_empty')}
              </div>
            )}
          </NSpin>
        </div>
      </div>
    )
  }
})

export default CronPicker
