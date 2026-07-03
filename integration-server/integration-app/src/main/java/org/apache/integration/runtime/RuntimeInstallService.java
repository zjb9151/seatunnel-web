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

package org.apache.integration.runtime;

import org.apache.commons.lang3.StringUtils;
import org.apache.integration.common.IntegrationErrorEnum;
import org.apache.integration.common.IntegrationException;
import org.apache.integration.config.IntegrationPathResolver;
import org.apache.integration.config.IntegrationProperties;
import org.apache.integration.process.ProcessSupervisor;
import org.apache.integration.runtime.RuntimeCatalogLoader.RuntimeComponentCatalog;
import org.apache.integration.runtime.RuntimeCatalogLoader.RuntimeVersionCatalog;
import org.apache.integration.utils.JsonHelper;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

@Slf4j
@Service
public class RuntimeInstallService {

    public static final String COMPONENT_DS = "dolphinscheduler";
    public static final String COMPONENT_ENGINE = "seatunnel-engine";
    public static final String COMPONENT_ST_WEB = "seatunnel-web";

    @Resource private IntegrationProperties properties;
    @Resource private IntegrationPathResolver pathResolver;
    @Resource private RuntimeCatalogLoader catalogLoader;
    @Resource @Lazy private ProcessSupervisor processSupervisor;

    public void bootstrapDefaults() {
        try {
            ensureComponent(COMPONENT_DS, properties.getRuntime().getDolphinscheduler());
            ensureComponent(COMPONENT_ENGINE, properties.getRuntime().getSeatunnelEngine());
            ensureComponent(COMPONENT_ST_WEB, properties.getRuntime().getSeatunnelWeb());
            syncProcessHomes();
            persistState();
        } catch (IOException e) {
            throw new IntegrationException(IntegrationErrorEnum.ILLEGAL_STATE, e.getMessage());
        }
    }

    public List<RuntimeComponentStatus> listStatus() {
        List<RuntimeComponentStatus> list = new ArrayList<>();
        list.add(statusOf(COMPONENT_DS, properties.getRuntime().getDolphinscheduler()));
        list.add(statusOf(COMPONENT_ENGINE, properties.getRuntime().getSeatunnelEngine()));
        list.add(statusOf(COMPONENT_ST_WEB, properties.getRuntime().getSeatunnelWeb()));
        return list;
    }

    public RuntimeComponentStatus installFromOfficial(String componentId, String version) {
        try {
            RuntimeVersionCatalog ver = requireVersion(componentId, version);
            if (!ver.isDownloadable()) {
                throw new IntegrationException(
                        IntegrationErrorEnum.ILLEGAL_STATE,
                        "Component "
                                + componentId
                                + " version "
                                + version
                                + " has no official download URL. Use install from local path.");
            }
            IntegrationProperties.RuntimeComponent cfg = cfgFor(componentId);
            Path versionHome = resolveVersionHome(cfg, version);
            if (isInstalled(versionHome, componentId)) {
                activate(componentId, version);
                return statusOf(componentId, cfg);
            }
            Path cacheDir = bundledCacheDir();
            Files.createDirectories(cacheDir);
            Path archive = cacheDir.resolve(ver.getArchive());
            download(ver.getUrl(), archive);
            extractArchive(archive, versionHome.getParent());
            Path extracted = versionHome.getParent().resolve(ver.getExtractDir());
            if (Files.isDirectory(extracted) && !extracted.equals(versionHome)) {
                moveDirectory(extracted, versionHome);
            }
            writeMarker(versionHome, componentId, version);
            activate(componentId, version);
            persistState();
            return statusOf(componentId, cfg);
        } catch (IOException e) {
            throw new IntegrationException(IntegrationErrorEnum.ILLEGAL_STATE, e.getMessage());
        }
    }

    public RuntimeComponentStatus installFromLocalPath(
            String componentId, String version, String localPath) {
        try {
            if (StringUtils.isBlank(localPath)) {
                throw new IntegrationException(
                        IntegrationErrorEnum.PARAM_CAN_NOT_BE_NULL, "localPath");
            }
            Path source = pathResolver.resolve(localPath);
            if (!Files.isDirectory(source)) {
                throw new IntegrationException(
                        IntegrationErrorEnum.ILLEGAL_STATE, "Local path not found: " + source);
            }
            IntegrationProperties.RuntimeComponent cfg = cfgFor(componentId);
            Path versionHome = resolveVersionHome(cfg, version);
            if (Files.exists(versionHome)) {
                deleteRecursively(versionHome);
            }
            Files.createDirectories(versionHome.getParent());
            copyRecursively(source, versionHome);
            writeMarker(versionHome, componentId, version);
            activate(componentId, version);
            persistState();
            return statusOf(componentId, cfg);
        } catch (IOException e) {
            throw new IntegrationException(IntegrationErrorEnum.ILLEGAL_STATE, e.getMessage());
        }
    }

    public RuntimeComponentStatus switchVersion(String componentId, String version) {
        IntegrationProperties.RuntimeComponent cfg = cfgFor(componentId);
        Path versionHome = resolveVersionHome(cfg, version);
        if (!isInstalled(versionHome, componentId)) {
            throw new IntegrationException(
                    IntegrationErrorEnum.ILLEGAL_STATE,
                    "Version not installed: " + componentId + "@" + version);
        }
        activate(componentId, version);
        persistState();
        return statusOf(componentId, cfg);
    }

    public void restartManagedProcesses() {
        processSupervisor.stopAll();
        processSupervisor.startManagedProcesses();
    }

    private void ensureComponent(String componentId, IntegrationProperties.RuntimeComponent cfg)
            throws IOException {
        String version =
                StringUtils.defaultIfBlank(
                        cfg.getVersion(),
                        catalogLoader
                                .getCatalog()
                                .getComponents()
                                .get(componentId)
                                .getDefaultVersion());
        cfg.setVersion(version);
        Path versionHome = resolveVersionHome(cfg, version);
        if (isInstalled(versionHome, componentId)) {
            activate(componentId, version);
            return;
        }
        Path bundled = findBundledArchive(componentId, version);
        if (bundled != null && Files.isRegularFile(bundled)) {
            log.info("Extract bundled {} {} from {}", componentId, version, bundled);
            extractArchive(bundled, versionHome.getParent());
            RuntimeVersionCatalog ver = requireVersion(componentId, version);
            if (StringUtils.isNotBlank(ver.getExtractDir())) {
                Path extracted = versionHome.getParent().resolve(ver.getExtractDir());
                if (Files.isDirectory(extracted) && !extracted.equals(versionHome)) {
                    moveDirectory(extracted, versionHome);
                }
            }
            writeMarker(versionHome, componentId, version);
            activate(componentId, version);
            return;
        }
        RuntimeVersionCatalog ver =
                catalogLoader
                        .getCatalog()
                        .getComponents()
                        .get(componentId)
                        .getVersions()
                        .get(version);
        if (ver != null && ver.isDownloadable()) {
            log.info("Downloading official {} {} on first bootstrap", componentId, version);
            installFromOfficial(componentId, version);
        } else if (COMPONENT_ST_WEB.equals(componentId)) {
            tryLegacySeatunnelWeb(cfg, version, versionHome);
        } else {
            log.warn(
                    "Runtime {}@{} not installed and no bundled/download source. "
                            + "Install via UI or API.",
                    componentId,
                    version);
        }
    }

    private void tryLegacySeatunnelWeb(
            IntegrationProperties.RuntimeComponent cfg, String version, Path versionHome)
            throws IOException {
        Path legacy = pathResolver.resolve("../vendors/seatunnel-web");
        if (!Files.isDirectory(legacy.resolve("libs"))) {
            return;
        }
        log.info("Migrating legacy vendors/seatunnel-web -> {}", versionHome);
        Files.createDirectories(versionHome.getParent());
        copyRecursively(legacy, versionHome);
        writeMarker(versionHome, COMPONENT_ST_WEB, version);
        activate(COMPONENT_ST_WEB, version);
    }

    private void activate(String componentId, String version) {
        IntegrationProperties.RuntimeComponent cfg = cfgFor(componentId);
        cfg.setVersion(version);
        Path home = resolveVersionHome(cfg, version);
        switch (componentId) {
            case COMPONENT_DS:
                properties.getProcesses().getDolphinscheduler().setHome(home.toString());
                break;
            case COMPONENT_ENGINE:
                properties.getProcesses().getSeatunnelEngine().setHome(home.toString());
                break;
            case COMPONENT_ST_WEB:
                properties.getProcesses().getSeatunnelWeb().setHome(home.toString());
                properties.getProcesses().getSeatunnelWeb().setDistHome(home.toString());
                break;
            default:
                throw new IntegrationException(
                        IntegrationErrorEnum.ILLEGAL_STATE, "Unknown component: " + componentId);
        }
    }

    private void syncProcessHomes() {
        activate(COMPONENT_DS, properties.getRuntime().getDolphinscheduler().getVersion());
        activate(COMPONENT_ENGINE, properties.getRuntime().getSeatunnelEngine().getVersion());
        activate(COMPONENT_ST_WEB, properties.getRuntime().getSeatunnelWeb().getVersion());
    }

    private RuntimeComponentStatus statusOf(
            String componentId, IntegrationProperties.RuntimeComponent cfg) {
        RuntimeComponentCatalog cat = catalogLoader.getCatalog().getComponents().get(componentId);
        RuntimeComponentStatus status = new RuntimeComponentStatus();
        status.setComponentId(componentId);
        status.setDisplayName(cat != null ? cat.getDisplayName() : componentId);
        status.setActiveVersion(cfg.getVersion());
        status.setInstallDir(resolveComponentRoot(cfg).toString());
        status.setActiveHome(resolveVersionHome(cfg, cfg.getVersion()).toString());
        status.setInstalled(isInstalled(resolveVersionHome(cfg, cfg.getVersion()), componentId));
        if (cat != null) {
            status.setAvailableVersions(new ArrayList<>(cat.getVersions().keySet()));
        }
        Path bundled = findBundledArchive(componentId, cfg.getVersion());
        status.setBundledAvailable(bundled != null && Files.isRegularFile(bundled));
        return status;
    }

    private IntegrationProperties.RuntimeComponent cfgFor(String componentId) {
        switch (componentId) {
            case COMPONENT_DS:
                return properties.getRuntime().getDolphinscheduler();
            case COMPONENT_ENGINE:
                return properties.getRuntime().getSeatunnelEngine();
            case COMPONENT_ST_WEB:
                return properties.getRuntime().getSeatunnelWeb();
            default:
                throw new IntegrationException(
                        IntegrationErrorEnum.ILLEGAL_STATE, "Unknown component: " + componentId);
        }
    }

    private RuntimeVersionCatalog requireVersion(String componentId, String version) {
        RuntimeComponentCatalog cat = catalogLoader.getCatalog().getComponents().get(componentId);
        if (cat == null || !cat.getVersions().containsKey(version)) {
            throw new IntegrationException(
                    IntegrationErrorEnum.ILLEGAL_STATE,
                    "Unknown version: " + componentId + "@" + version);
        }
        return cat.getVersions().get(version);
    }

    private Path resolveComponentRoot(IntegrationProperties.RuntimeComponent cfg) {
        return pathResolver.resolve(cfg.getInstallDir());
    }

    private Path resolveVersionHome(IntegrationProperties.RuntimeComponent cfg, String version) {
        return resolveComponentRoot(cfg).resolve(version);
    }

    private Path bundledDir() {
        return pathResolver.resolve(properties.getRuntime().getBundledDir());
    }

    private Path bundledCacheDir() {
        return pathResolver
                .resolve(properties.getRuntime().getBaseDir())
                .resolve("runtime")
                .resolve(".cache");
    }

    private Path stateFile() {
        return pathResolver
                .resolve(properties.getRuntime().getBaseDir())
                .resolve("runtime")
                .resolve("state.json");
    }

    private Path findBundledArchive(String componentId, String version) {
        RuntimeVersionCatalog ver =
                catalogLoader
                        .getCatalog()
                        .getComponents()
                        .get(componentId)
                        .getVersions()
                        .get(version);
        if (ver == null || StringUtils.isBlank(ver.getArchive())) {
            return null;
        }
        Path p = bundledDir().resolve(ver.getArchive());
        return Files.isRegularFile(p) ? p : null;
    }

    private boolean isInstalled(Path versionHome, String componentId) {
        if (!Files.isDirectory(versionHome)) {
            return false;
        }
        if (COMPONENT_ST_WEB.equals(componentId)) {
            return Files.isDirectory(versionHome.resolve("libs"));
        }
        if (COMPONENT_DS.equals(componentId)) {
            return Files.isDirectory(versionHome.resolve("standalone-server"));
        }
        return Files.isDirectory(versionHome.resolve("bin"));
    }

    private void writeMarker(Path versionHome, String componentId, String version)
            throws IOException {
        Map<String, String> marker = new LinkedHashMap<>();
        marker.put("component", componentId);
        marker.put("version", version);
        Files.writeString(versionHome.resolve(".installed"), JsonHelper.toJsonString(marker));
    }

    private void persistState() {
        try {
            Map<String, Object> state = new LinkedHashMap<>();
            state.put("dolphinscheduler", properties.getRuntime().getDolphinscheduler());
            state.put("seatunnel-engine", properties.getRuntime().getSeatunnelEngine());
            state.put("seatunnel-web", properties.getRuntime().getSeatunnelWeb());
            Path file = stateFile();
            Files.createDirectories(file.getParent());
            Files.writeString(file, JsonHelper.toJsonString(state));
        } catch (Exception e) {
            log.warn("Failed to persist runtime state", e);
        }
    }

    private void download(String url, Path target) throws IOException {
        log.info("Downloading {} -> {}", url, target);
        Files.createDirectories(target.getParent());
        try (InputStream in = new BufferedInputStream(URI.create(url).toURL().openStream())) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void extractArchive(Path archive, Path destDir) throws IOException {
        Files.createDirectories(destDir);
        ProcessBuilder pb =
                new ProcessBuilder("tar", "-xzf", archive.toString(), "-C", destDir.toString());
        pb.redirectErrorStream(true);
        Process p = pb.start();
        try {
            int code = p.waitFor();
            if (code != 0) {
                throw new IntegrationException(
                        IntegrationErrorEnum.ILLEGAL_STATE, "tar extract failed: " + archive);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IntegrationException(IntegrationErrorEnum.ILLEGAL_STATE, e.getMessage());
        }
    }

    private void copyRecursively(Path source, Path target) throws IOException {
        Files.walkFileTree(
                source,
                new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs)
                            throws IOException {
                        Path rel = source.relativize(dir);
                        Files.createDirectories(target.resolve(rel.toString()));
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                            throws IOException {
                        Path rel = source.relativize(file);
                        Files.copy(
                                file,
                                target.resolve(rel.toString()),
                                StandardCopyOption.REPLACE_EXISTING);
                        return FileVisitResult.CONTINUE;
                    }
                });
    }

    private void moveDirectory(Path source, Path target) throws IOException {
        if (Files.exists(target)) {
            deleteRecursively(target);
        }
        Files.createDirectories(target.getParent());
        Files.move(source, target);
    }

    private void deleteRecursively(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        try (Stream<Path> walk = Files.walk(path)) {
            walk.sorted((a, b) -> b.compareTo(a))
                    .forEach(
                            p -> {
                                try {
                                    Files.deleteIfExists(p);
                                } catch (IOException ignored) {
                                }
                            });
        }
    }

    @Data
    public static class RuntimeComponentStatus {
        private String componentId;
        private String displayName;
        private String activeVersion;
        private String installDir;
        private String activeHome;
        private boolean installed;
        private boolean bundledAvailable;
        private List<String> availableVersions;
    }
}
