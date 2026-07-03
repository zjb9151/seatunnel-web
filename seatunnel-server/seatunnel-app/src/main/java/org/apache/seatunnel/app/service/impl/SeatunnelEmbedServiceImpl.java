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

package org.apache.seatunnel.app.service.impl;

import org.apache.seatunnel.app.common.UserTokenStatusEnum;
import org.apache.seatunnel.app.config.DolphinSchedulerProperties;
import org.apache.seatunnel.app.dal.dao.IUserDao;
import org.apache.seatunnel.app.dal.entity.User;
import org.apache.seatunnel.app.dal.entity.Workspace;
import org.apache.seatunnel.app.domain.dto.user.UserLoginLogDto;
import org.apache.seatunnel.app.domain.request.embed.EmbedAuthReq;
import org.apache.seatunnel.app.domain.response.dolphinscheduler.SeatunnelUiInfoRes;
import org.apache.seatunnel.app.domain.response.user.UserSimpleInfoRes;
import org.apache.seatunnel.app.security.JwtUtils;
import org.apache.seatunnel.app.service.ISeatunnelEmbedService;
import org.apache.seatunnel.app.service.WorkspaceService;
import org.apache.seatunnel.app.thirdparty.scheduler.DolphinSchedulerSessionClient;
import org.apache.seatunnel.server.common.SeatunnelErrorEnum;
import org.apache.seatunnel.server.common.SeatunnelException;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.Map;

@Slf4j
@Service
public class SeatunnelEmbedServiceImpl implements ISeatunnelEmbedService {

    @Resource private DolphinSchedulerProperties properties;
    @Resource private DolphinSchedulerSessionClient dolphinSchedulerSessionClient;
    @Resource private IUserDao userDaoImpl;
    @Resource private WorkspaceService workspaceService;
    @Resource private JwtUtils jwtUtils;

    @Override
    public SeatunnelUiInfoRes getUiInfo() {
        if (!properties.isEnabled()) {
            return new SeatunnelUiInfoRes(
                    false,
                    null,
                    "DolphinScheduler integration is disabled. Set dolphinscheduler.enabled=true.");
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
        User user = userDaoImpl.getByName(username);
        if (user == null) {
            user = userDaoImpl.getByName(properties.getUsername());
        }
        if (user == null) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.USERNAME_PASSWORD_NO_MATCHED,
                    "No matching SeaTunnel user for DolphinScheduler account: " + username);
        }
        UserSimpleInfoRes info = translate(user);
        Workspace workspace = workspaceService.getDefaultWorkspace();
        Map<String, Object> map = info.toMap();
        map.put("workspaceName", workspace.getWorkspaceName());
        map.put("workspaceId", workspace.getId());
        String token = jwtUtils.genToken(map);
        info.setToken(token);
        userDaoImpl.insertLoginLog(
                UserLoginLogDto.builder()
                        .token(token)
                        .tokenStatus(UserTokenStatusEnum.ENABLE.enable())
                        .userId(user.getId())
                        .workspaceId(workspace.getId())
                        .build());
        return info;
    }

    private UserSimpleInfoRes translate(User user) {
        UserSimpleInfoRes info = new UserSimpleInfoRes();
        info.setId(user.getId());
        info.setStatus(user.getStatus());
        info.setType(user.getType());
        info.setCreateTime(user.getCreateTime());
        info.setUpdateTime(user.getUpdateTime());
        info.setName(user.getUsername());
        return info;
    }

    private String resolveSeatunnelUiUrl() {
        if (StringUtils.isNotBlank(properties.getSeatunnelUiUrl())) {
            return normalizeUrl(properties.getSeatunnelUiUrl());
        }
        return "/ui";
    }

    private String normalizeUrl(String url) {
        String trimmed = url.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
        }
        if (!trimmed.startsWith("/")) {
            trimmed = "/" + trimmed;
        }
        return trimmed.endsWith("/") ? trimmed.substring(0, trimmed.length() - 1) : trimmed;
    }
}
