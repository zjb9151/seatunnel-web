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

package org.apache.integration.service.impl;

import org.apache.integration.client.SeatunnelApiClient;
import org.apache.integration.config.IntegrationProperties;
import org.apache.integration.domain.request.embed.EmbedAuthReq;
import org.apache.integration.domain.response.dolphinscheduler.SeatunnelUiInfoRes;
import org.apache.integration.domain.response.user.UserSimpleInfoRes;
import org.apache.integration.service.ISeatunnelEmbedService;
import org.apache.integration.thirdparty.scheduler.DolphinSchedulerSessionClient;
import org.apache.seatunnel.server.common.SeatunnelErrorEnum;
import org.apache.seatunnel.server.common.SeatunnelException;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

@Slf4j
@Service
public class SeatunnelEmbedServiceImpl implements ISeatunnelEmbedService {

    @Resource private IntegrationProperties properties;
    @Resource private DolphinSchedulerSessionClient dolphinSchedulerSessionClient;
    @Resource private SeatunnelApiClient seatunnelApiClient;

    @Override
    public SeatunnelUiInfoRes getUiInfo() {
        if (!properties.isEnabled()) {
            return new SeatunnelUiInfoRes(
                    false, null, "DolphinScheduler integration is disabled.");
        }
        return new SeatunnelUiInfoRes(true, resolveSeatunnelUiUrl(), "SeaTunnel UI is ready.");
    }

    @Override
    public UserSimpleInfoRes exchangeDsSession(EmbedAuthReq req) {
        if (!properties.isEnabled()) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE, "DolphinScheduler integration is disabled");
        }
        if (req == null || StringUtils.isBlank(req.getDsSessionId())) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE, "dsSessionId is required");
        }
        String username = dolphinSchedulerSessionClient.resolveUsername(req.getDsSessionId());
        log.debug("DS embed auth for user {}", username);
        return seatunnelApiClient.loginAsDefaultUser();
    }

    private String resolveSeatunnelUiUrl() {
        String path = properties.getSeatunnelUiUrl();
        if (StringUtils.isBlank(path)) {
            return "/seatunnel-ui";
        }
        return path.startsWith("/") ? path : "/" + path;
    }
}
