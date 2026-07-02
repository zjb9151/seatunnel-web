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

package org.apache.seatunnel.app.interceptor;

import org.apache.seatunnel.app.common.Constants;
import org.apache.seatunnel.app.config.DolphinSchedulerProperties;
import org.apache.seatunnel.app.dal.dao.IJobDefinitionDao;
import org.apache.seatunnel.app.dal.dao.IUserDao;
import org.apache.seatunnel.app.dal.entity.JobDefinition;
import org.apache.seatunnel.app.dal.entity.User;
import org.apache.seatunnel.app.dal.entity.UserLoginLog;
import org.apache.seatunnel.app.security.JwtUtils;
import org.apache.seatunnel.app.security.UserContext;
import org.apache.seatunnel.app.security.UserContextHolder;
import org.apache.seatunnel.common.access.AccessInfo;

import org.apache.commons.lang3.StringUtils;

import org.eclipse.jetty.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;
import java.util.Objects;

import static io.jsonwebtoken.Claims.EXPIRATION;
import static org.apache.seatunnel.server.common.Constants.OPTIONS;
import static org.apache.seatunnel.server.common.Constants.TOKEN;
import static org.apache.seatunnel.server.common.Constants.USER_ID;

@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Resource private IUserDao userDaoImpl;

    @Resource private IJobDefinitionDao jobDefinitionDao;

    @Resource private JwtUtils jwtUtils;

    @Resource private DolphinSchedulerProperties dolphinSchedulerProperties;

    private static final String DS_EXECUTE_PATH = "/seatunnel/api/v1/job/executor/execute";

    @Override
    @SuppressWarnings("MagicNumber")
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (request.getMethod().equals(OPTIONS)) {
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Headers", "*");
            response.setHeader("Access-Control-Allow-Methods", "*");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setHeader("Access-Control-Max-Age", "3600");
            return true;
        }

        long currentTimestamp = System.currentTimeMillis();
        final String token = request.getHeader(TOKEN);
        if (isDolphinSchedulerExecute(request)) {
            if (!isValidServiceToken(token)) {
                log.info("DolphinScheduler execute endpoint requires a valid service token");
                response.setStatus(HttpStatus.UNAUTHORIZED_401);
                return false;
            }
            if (bindSchedulerServiceContext(request)) {
                return true;
            }
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
            return false;
        }
        if (StringUtils.isBlank(token)) {
            log.info("user does not exist");
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
            return false;
        }
        final Map<String, Object> map = jwtUtils.parseToken(token);
        final Integer userId = (Integer) map.get(USER_ID);
        if (Objects.isNull(userId)) {
            log.info("userId does not exist");
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
            return false;
        }
        long workspaceIdFromToken = ((Number) map.get("workspaceId")).longValue();
        final UserLoginLog userLoginLog = userDaoImpl.getLastLoginLog(userId, workspaceIdFromToken);
        if (Objects.isNull(userLoginLog) || !userLoginLog.getTokenStatus()) {
            log.info("userLoginLog does not exist");
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
            return false;
        }

        final Integer expireDate = (Integer) map.get(EXPIRATION);
        if (Objects.isNull(expireDate) || currentTimestamp - (long) expireDate * 1000 > 0) {
            log.info("user token has expired");
            response.setStatus(HttpStatus.UNAUTHORIZED_401);
            return false;
        }

        map.forEach(request::setAttribute);
        User user = new User();
        user.setUsername((String) map.get("name"));
        user.setId((Integer) map.get("id"));
        log.debug(
                "Setting user to request attributes: userId={}, username={}",
                user.getId(),
                user.getUsername());

        UserContext userContext = new UserContext();
        userContext.setUser(user);
        userContext.setWorkspaceId(workspaceIdFromToken);

        AccessInfo accessInfo = new AccessInfo();
        accessInfo.setUsername(user.getUsername());
        accessInfo.setWorkspaceName((String) map.get("workspaceName"));
        userContext.setAccessInfo(accessInfo);

        request.setAttribute(Constants.SESSION_USER_CONTEXT, userContext);

        request.setAttribute("userId", userId);
        return true;
    }

    @Override
    public void afterCompletion(
            HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        UserContextHolder.clear();
    }

    private boolean isDolphinSchedulerExecute(HttpServletRequest request) {
        if (!"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String pathWithinApp = resolvePathWithinApp(request);
        return pathWithinApp.equals(DS_EXECUTE_PATH) || pathWithinApp.equals(DS_EXECUTE_PATH + "/");
    }

    private String resolvePathWithinApp(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        if (StringUtils.isBlank(requestUri)) {
            return "";
        }
        String contextPath = StringUtils.defaultString(request.getContextPath());
        if (StringUtils.isEmpty(contextPath) || !requestUri.startsWith(contextPath)) {
            return requestUri;
        }
        return requestUri.substring(contextPath.length());
    }

    private boolean isValidServiceToken(String token) {
        return dolphinSchedulerProperties.isEnabled()
                && StringUtils.isNotBlank(dolphinSchedulerProperties.getServiceToken())
                && StringUtils.equals(token, dolphinSchedulerProperties.getServiceToken());
    }

    private boolean bindSchedulerServiceContext(HttpServletRequest request) {
        Long jobDefineId = parseJobDefineId(request.getParameter("jobDefineId"));
        if (jobDefineId == null) {
            log.warn("DolphinScheduler execute request missing jobDefineId");
            return false;
        }
        JobDefinition jobDefinition = jobDefinitionDao.getJobByIdForSystem(jobDefineId);
        if (jobDefinition == null || jobDefinition.getWorkspaceId() == null) {
            log.warn("Job definition not found for scheduler execute: jobDefineId={}", jobDefineId);
            return false;
        }
        int userId = jobDefinition.getCreateUserId() != null ? jobDefinition.getCreateUserId() : 1;
        User user = new User();
        user.setId(userId);
        user.setUsername("scheduler");
        UserContext userContext = new UserContext();
        userContext.setUser(user);
        userContext.setWorkspaceId(jobDefinition.getWorkspaceId());
        AccessInfo accessInfo = new AccessInfo();
        accessInfo.setUsername("scheduler");
        userContext.setAccessInfo(accessInfo);
        UserContextHolder.setUserContext(userContext);
        request.setAttribute(Constants.SESSION_USER_CONTEXT, userContext);
        request.setAttribute("userId", userId);
        return true;
    }

    private Long parseJobDefineId(String jobDefineIdParam) {
        if (StringUtils.isBlank(jobDefineIdParam)) {
            return null;
        }
        try {
            return Long.parseLong(jobDefineIdParam.trim());
        } catch (NumberFormatException e) {
            log.warn("Invalid jobDefineId in scheduler execute request: {}", jobDefineIdParam);
            return null;
        }
    }

    @Override
    public void postHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler,
            ModelAndView modelAndView)
            throws Exception {
        // do nothing
    }
}
