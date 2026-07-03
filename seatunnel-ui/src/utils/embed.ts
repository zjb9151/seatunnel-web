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
import { useUserStore } from '@/store/user'
import type { UserDetail } from '@/service/user/types'

export function isEmbedMode(): boolean {
  const params = new URLSearchParams(window.location.hash.split('?')[1] || '')
  return params.get('embed') === '1'
}

export function isDsDatasourceMode(): boolean {
  const params = new URLSearchParams(window.location.hash.split('?')[1] || '')
  return params.get('datasourceSource') === 'dolphinscheduler'
}

export async function bootstrapEmbedAuth(): Promise<void> {
  if (!isEmbedMode()) {
    return
  }
  const params = new URLSearchParams(window.location.hash.split('?')[1] || '')
  const dsSessionId = params.get('dsSessionId')
  if (!dsSessionId) {
    return
  }
  const userStore = useUserStore()
  if (Object.keys(userStore.getUserInfo).length > 0) {
    return
  }
  const data = await axios({
    url: '/embed/auth',
    method: 'post',
    data: { dsSessionId }
  })
  userStore.setUserInfo(data as UserDetail)
}
