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

import { defineComponent, PropType, toRefs, watch } from 'vue'
import { useI18n } from 'vue-i18n'
import {
  NForm,
  NFormItem,
  NInput,
  NRadioGroup,
  NRadio,
  NSwitch,
  NButton,
  NSpace
} from 'naive-ui'
import { useScheduleModal } from './use-schedule-modal'
import { CronPicker } from '@/components/cron-picker'
import Modal from '@/components/modal'

const props = {
  showModalRef: {
    type: Boolean as PropType<boolean>,
    default: false
  },
  row: {
    type: Object as PropType<any>,
    default: () => ({})
  }
}

export const ScheduleModal = defineComponent({
  name: 'ScheduleModal',
  props,
  emits: ['cancelModal', 'confirmModal'],
  setup(props, ctx) {
    const { t } = useI18n()
    const { variables, loadSchedule, handleSave, handleDelete } = useScheduleModal(
      props,
      ctx
    )

    watch(
      () => props.showModalRef,
      (visible) => {
        if (visible && props.row?.id) {
          loadSchedule(props.row.id)
        }
      }
    )

    const cancelModal = () => {
      ctx.emit('cancelModal')
    }

    const confirmModal = () => {
      handleSave()
    }

    return {
      t,
      ...toRefs(variables),
      cancelModal,
      confirmModal,
      handleDelete
    }
  },
  render() {
    return (
      <Modal
        show={this.showModalRef}
        title={this.t('project.synchronization_definition.schedule_config')}
        width='860px'
        onCancel={this.cancelModal}
        onConfirm={this.confirmModal}
        confirmLoading={this.saving}
        confirmDisabled={this.loading}
      >
        {{
          default: () => (
            <NForm labelPlacement='left' labelWidth={120}>
              <NFormItem
                label={this.t('project.synchronization_definition.schedule_name')}
                required
              >
                <NInput
                  v-model={[this.model.scheduleName, 'value']}
                  placeholder={this.t(
                    'project.synchronization_definition.schedule_name_tips'
                  )}
                />
              </NFormItem>
              <NFormItem
                label={this.t('project.synchronization_definition.cron_mechanism')}
                required
              >
                <CronPicker
                  key={this.showModalRef ? this.model.crontab : 'hidden'}
                  v-model={[this.model.crontab, 'value']}
                />
              </NFormItem>
              <NFormItem
                label={this.t('project.synchronization_definition.schedule_type')}
              >
                <NRadioGroup v-model={[this.model.scheduleType, 'value']}>
                  <NSpace>
                    <NRadio value='INTERNAL'>
                      {this.t('project.synchronization_definition.schedule_type_internal')}
                    </NRadio>
                    <NRadio value='DOLPHIN_SCHEDULER'>
                      {this.t('project.synchronization_definition.schedule_type_dolphin')}
                    </NRadio>
                  </NSpace>
                </NRadioGroup>
              </NFormItem>
              <NFormItem
                label={this.t('project.synchronization_definition.schedule_enabled')}
              >
                <NSwitch v-model={[this.model.enabled, 'value']} />
              </NFormItem>
              <NFormItem label=' '>
                <NSpace>
                  <NButton quaternary type='error' onClick={this.handleDelete}>
                    {this.t('project.synchronization_definition.schedule_delete')}
                  </NButton>
                </NSpace>
              </NFormItem>
              <NFormItem label=' '>
                <span style='color: #999; font-size: 12px;'>
                  {this.model.scheduleType === 'INTERNAL'
                    ? this.t('project.synchronization_definition.crontab_internal_hint')
                    : this.t('project.synchronization_definition.crontab_dolphin_hint')}
                </span>
              </NFormItem>
            </NForm>
          )
        }}
      </Modal>
    )
  }
})
