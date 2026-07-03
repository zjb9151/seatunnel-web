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

package org.apache.integration.controller;

import org.apache.integration.common.Result;
import org.apache.integration.domain.request.embed.EmbedAuthReq;
import org.apache.integration.domain.response.user.UserSimpleInfoRes;
import org.apache.integration.service.ISeatunnelEmbedService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/v1/auth")
public class EmbedAuthController {

    @Resource private ISeatunnelEmbedService seatunnelEmbedService;

    @PostMapping("/embed")
    public Result<UserSimpleInfoRes> exchangeDsSession(@RequestBody EmbedAuthReq req) {
        return Result.success(seatunnelEmbedService.exchangeDsSession(req));
    }
}
