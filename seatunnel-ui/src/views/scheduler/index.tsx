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

import { defineComponent, onMounted, ref } from 'vue'
import { useI18n } from 'vue-i18n'
import { NAlert, NEmpty, NSpin } from 'naive-ui'
import { getDolphinSchedulerInfo } from '@/service/dolphinscheduler'
import styles from './index.module.scss'

const SchedulerIndex = defineComponent({
  name: 'SchedulerIndex',
  setup() {
    const { t } = useI18n()
    const loading = ref(true)
    const embedUrl = ref('')
    const errorMessage = ref('')

    const resolveIframeSrc = (url: string) => {
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url
      }
      const base = import.meta.env.BASE_URL || '/'
      const normalizedBase = base.endsWith('/') ? base.slice(0, -1) : base
      const normalizedPath = url.startsWith('/') ? url : `/${url}`
      return `${normalizedBase}${normalizedPath}`
    }

    onMounted(async () => {
      try {
        const info = await getDolphinSchedulerInfo()
        if (info.enabled && info.embedUrl) {
          embedUrl.value = resolveIframeSrc(info.embedUrl)
        } else {
          errorMessage.value =
            info.message || t('scheduler.not_configured')
        }
      } catch (err: any) {
        errorMessage.value = err?.message || t('scheduler.load_failed')
      } finally {
        loading.value = false
      }
    })

    return {
      t,
      loading,
      embedUrl,
      errorMessage
    }
  },
  render() {
    if (this.loading) {
      return (
        <div class={styles.container}>
          <NSpin size="large" />
        </div>
      )
    }

    if (this.errorMessage) {
      return (
        <div class={styles.container}>
          <NAlert type="warning" title={this.t('scheduler.unavailable')}>
            {this.errorMessage}
          </NAlert>
          <NEmpty description={this.t('scheduler.check_config')} class={styles.empty} />
        </div>
      )
    }

    return (
      <div class={styles.container}>
        <NAlert type="info" class={styles.hint}>
          {this.t('scheduler.datasource_hint')}
        </NAlert>
        <iframe
          class={styles.frame}
          src={this.embedUrl}
          title={this.t('menu.scheduler')}
        />
      </div>
    )
  }
})

export default SchedulerIndex
