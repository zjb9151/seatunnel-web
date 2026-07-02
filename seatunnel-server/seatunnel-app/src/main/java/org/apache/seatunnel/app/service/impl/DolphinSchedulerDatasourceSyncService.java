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

import org.apache.seatunnel.app.dal.dao.IDatasourceDao;
import org.apache.seatunnel.app.dal.entity.Datasource;
import org.apache.seatunnel.app.thirdparty.scheduler.DolphinSchedulerDatasourceClient;
import org.apache.seatunnel.app.thirdparty.scheduler.SeatunnelToDsDatasourceConverter;
import org.apache.seatunnel.app.utils.ConfigShadeUtil;
import org.apache.seatunnel.common.utils.JsonUtils;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class DolphinSchedulerDatasourceSyncService {

    @Resource private DolphinSchedulerDatasourceClient dolphinSchedulerDatasourceClient;
    @Resource private IDatasourceDao datasourceDao;
    @Resource private ConfigShadeUtil configShadeUtil;

    public void syncAfterCreate(Datasource datasource) {
        if (!dolphinSchedulerDatasourceClient.isAvailable() || datasource == null) {
            return;
        }
        try {
            Optional<String> payload = buildPayload(datasource);
            if (!payload.isPresent()) {
                log.debug(
                        "Skip DS datasource sync for unsupported plugin {}",
                        datasource.getPluginName());
                return;
            }
            int dsId = dolphinSchedulerDatasourceClient.createDatasource(payload.get());
            persistDsDatasourceId(datasource, dsId);
            log.info(
                    "Synced SeaTunnel datasource {} to DolphinScheduler id={}",
                    datasource.getId(),
                    dsId);
        } catch (Exception e) {
            log.warn(
                    "Failed to sync datasource {} to DolphinScheduler: {}",
                    datasource.getId(),
                    e.getMessage());
        }
    }

    public void syncAfterUpdate(Datasource datasource) {
        if (!dolphinSchedulerDatasourceClient.isAvailable() || datasource == null) {
            return;
        }
        try {
            Optional<String> payload = buildPayload(datasource);
            if (!payload.isPresent()) {
                return;
            }
            if (datasource.getDsDatasourceId() != null && datasource.getDsDatasourceId() > 0) {
                dolphinSchedulerDatasourceClient.updateDatasource(
                        datasource.getDsDatasourceId(), payload.get());
                log.info(
                        "Updated DolphinScheduler datasource id={} from SeaTunnel datasource {}",
                        datasource.getDsDatasourceId(),
                        datasource.getId());
            } else {
                syncAfterCreate(datasource);
            }
        } catch (Exception e) {
            log.warn(
                    "Failed to update DS datasource for SeaTunnel id {}: {}",
                    datasource.getId(),
                    e.getMessage());
        }
    }

    public void syncAfterDelete(Datasource datasource) {
        if (!dolphinSchedulerDatasourceClient.isAvailable()
                || datasource == null
                || datasource.getDsDatasourceId() == null
                || datasource.getDsDatasourceId() <= 0) {
            return;
        }
        try {
            dolphinSchedulerDatasourceClient.deleteDatasource(datasource.getDsDatasourceId());
            log.info(
                    "Deleted DolphinScheduler datasource id={} for SeaTunnel datasource {}",
                    datasource.getDsDatasourceId(),
                    datasource.getId());
        } catch (Exception e) {
            log.warn(
                    "Failed to delete DS datasource id {}: {}",
                    datasource.getDsDatasourceId(),
                    e.getMessage());
        }
    }

    public void syncAllExisting() {
        if (!dolphinSchedulerDatasourceClient.isAvailable()) {
            return;
        }
        List<Datasource> datasources = datasourceDao.queryAllForSystemSync();
        for (Datasource datasource : datasources) {
            if (datasource.getDsDatasourceId() != null && datasource.getDsDatasourceId() > 0) {
                syncAfterUpdate(datasource);
            } else {
                syncAfterCreate(datasource);
            }
        }
    }

    private void persistDsDatasourceId(Datasource datasource, int dsId) {
        if (datasource.getId() == null) {
            log.warn("Skip persisting DS datasource id: SeaTunnel datasource id is null");
            return;
        }
        boolean updated =
                datasourceDao.updateDsDatasourceId(
                        datasource.getId(), datasource.getWorkspaceId(), dsId);
        if (!updated) {
            log.warn(
                    "Failed to persist DS datasource id={} for SeaTunnel datasource id={}",
                    dsId,
                    datasource.getId());
        }
    }

    private Optional<String> buildPayload(Datasource datasource) {
        Map<String, String> config =
                JsonUtils.toMap(datasource.getDatasourceConfig(), String.class, String.class);
        configShadeUtil.decryptData(config);
        return SeatunnelToDsDatasourceConverter.toDsJsonPayload(
                datasource.getPluginName(),
                datasource.getDatasourceName(),
                datasource.getDescription(),
                config);
    }
}
