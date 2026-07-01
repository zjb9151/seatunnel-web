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

import org.apache.seatunnel.app.dal.dao.IJobInstanceDao;
import org.apache.seatunnel.app.dal.entity.JobInstance;
import org.apache.seatunnel.app.domain.response.job.JobLogNodeRes;
import org.apache.seatunnel.app.service.IJobLogService;
import org.apache.seatunnel.server.common.SeatunnelErrorEnum;
import org.apache.seatunnel.server.common.SeatunnelException;

import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class JobLogServiceImpl implements IJobLogService {

    private static final Pattern LOG_FILE_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]+\\.log$");

    private static final String[] ENGINE_SERVER_LOG_FILES = {
        "seatunnel-engine-server.log", "seatunnel.log"
    };

    @Value("${seatunnel-web.engine.http-url:http://127.0.0.1:8080}")
    private String engineHttpUrl;

    @Value("${seatunnel-web.engine.log-path:}")
    private String configuredLogPath;

    @Resource private RestTemplate restTemplate;

    @Resource private IJobInstanceDao jobInstanceDao;

    @Override
    public List<JobLogNodeRes> listLogNodes(String jobEngineId, Long jobInstanceId) {
        String resolvedJobEngineId = resolveJobEngineId(jobEngineId, jobInstanceId);
        if (StringUtils.isBlank(resolvedJobEngineId)) {
            throw new SeatunnelException(
                    SeatunnelErrorEnum.ILLEGAL_STATE,
                    "Job engine id is not available for this task");
        }

        List<JobLogNodeRes> nodes = fetchLogNodesFromEngine(resolvedJobEngineId);
        if (!nodes.isEmpty()) {
            return nodes;
        }

        return fetchLogNodesFromLocalFiles(resolvedJobEngineId);
    }

    @Override
    public String getLogContent(String logFileName, String jobEngineId, Long jobInstanceId) {
        String normalizedFileName = normalizeLogFileName(logFileName);
        validateLogFileName(normalizedFileName);
        String resolvedJobEngineId = resolveJobEngineId(jobEngineId, jobInstanceId);

        String localContent = readLocalLogContent(normalizedFileName, resolvedJobEngineId);
        if (StringUtils.isNotBlank(localContent)) {
            return localContent;
        }

        String engineContent = fetchLogContentFromEngine(normalizedFileName);
        if (isValidLogContent(engineContent)) {
            return filterLogContent(engineContent, normalizedFileName, resolvedJobEngineId);
        }

        return localContent;
    }

    private String readLocalLogContent(String logFileName, String jobEngineId) {
        Path logFile = findLogFile(logFileName);
        if (logFile == null) {
            return "";
        }
        try {
            String content = Files.readString(logFile, StandardCharsets.UTF_8);
            return filterLogContent(content, logFileName, jobEngineId);
        } catch (IOException e) {
            log.warn("Failed to read log file {}", logFile, e);
            return "";
        }
    }

    private boolean isValidLogContent(String content) {
        if (StringUtils.isBlank(content)) {
            return false;
        }
        String trimmed = content.trim();
        if (trimmed.startsWith("{")
                && (trimmed.contains("\"code\"") || trimmed.contains("\"msg\""))) {
            return false;
        }
        if (trimmed.startsWith("<!DOCTYPE") || trimmed.startsWith("<html")) {
            return false;
        }
        return true;
    }

    private String resolveJobEngineId(String jobEngineId, Long jobInstanceId) {
        if (StringUtils.isNotBlank(jobEngineId)) {
            return jobEngineId.trim();
        }
        if (jobInstanceId == null) {
            return null;
        }
        JobInstance jobInstance = jobInstanceDao.getJobInstance(jobInstanceId);
        if (jobInstance == null) {
            return null;
        }
        return jobInstance.getJobEngineId();
    }

    private List<JobLogNodeRes> fetchLogNodesFromEngine(String jobEngineId) {
        try {
            String url =
                    String.format(
                            Locale.ROOT,
                            "%s/logs/%s?format=json",
                            trimTrailingSlash(engineHttpUrl),
                            jobEngineId);
            ResponseEntity<List<JobLogNodeRes>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            null,
                            new ParameterizedTypeReference<List<JobLogNodeRes>>() {});
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                return Collections.emptyList();
            }
            List<JobLogNodeRes> nodes = new ArrayList<>();
            for (JobLogNodeRes node : response.getBody()) {
                nodes.add(normalizeLogNode(node));
            }
            return nodes;
        } catch (Exception e) {
            log.warn("Failed to fetch log nodes from engine for jobEngineId={}", jobEngineId, e);
            return Collections.emptyList();
        }
    }

    private List<JobLogNodeRes> fetchLogNodesFromLocalFiles(String jobEngineId) {
        List<JobLogNodeRes> nodes = new ArrayList<>();
        for (Path logDir : resolveLogDirectories()) {
            collectJobLogNodes(logDir, jobEngineId, nodes);
        }

        if (!nodes.isEmpty()) {
            return nodes;
        }

        for (Path logDir : resolveLogDirectories()) {
            collectEngineServerLogNodes(logDir, jobEngineId, nodes);
        }
        return nodes;
    }

    private void collectJobLogNodes(Path logDir, String jobEngineId, List<JobLogNodeRes> nodes) {
        String exactName = "job-" + jobEngineId + ".log";
        Path exactPath = logDir.resolve(exactName);
        if (Files.isRegularFile(exactPath)) {
            addLocalLogNode(nodes, exactName);
        }

        String suffix = "job-" + jobEngineId + ".log";
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(logDir, "*" + suffix)) {
            for (Path path : stream) {
                String fileName = path.getFileName().toString();
                if (Files.isRegularFile(path)) {
                    addLocalLogNode(nodes, fileName);
                }
            }
        } catch (IOException e) {
            log.warn("Failed to scan log directory {}", logDir, e);
        }
    }

    private void collectEngineServerLogNodes(
            Path logDir, String jobEngineId, List<JobLogNodeRes> nodes) {
        for (String fileName : ENGINE_SERVER_LOG_FILES) {
            Path logFile = logDir.resolve(fileName);
            if (Files.isRegularFile(logFile) && logFileContainsJobId(logFile, jobEngineId)) {
                addLocalLogNode(nodes, fileName);
            }
        }
    }

    private void addLocalLogNode(List<JobLogNodeRes> nodes, String fileName) {
        if (nodes.stream().noneMatch(node -> fileName.equals(node.getLogName()))) {
            nodes.add(createLocalLogNode(fileName));
        }
    }

    private boolean logFileContainsJobId(Path logFile, String jobEngineId) {
        String marker = "[" + jobEngineId + "]";
        try (Stream<String> lines = Files.lines(logFile, StandardCharsets.UTF_8)) {
            return lines.anyMatch(line -> line.contains(marker));
        } catch (IOException e) {
            log.warn("Failed to scan log file {}", logFile, e);
            return false;
        }
    }

    private String fetchLogContentFromEngine(String logFileName) {
        try {
            String url =
                    String.format(
                            Locale.ROOT,
                            "%s/logs/%s",
                            trimTrailingSlash(engineHttpUrl),
                            logFileName);
            ResponseEntity<String> response =
                    restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response.getStatusCode().is2xxSuccessful()
                    && StringUtils.isNotBlank(response.getBody())) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.warn("Failed to fetch log content from engine for file={}", logFileName, e);
        }
        return null;
    }

    private Path findLogFile(String logFileName) {
        for (Path logDir : resolveLogDirectories()) {
            Path logFile = logDir.resolve(logFileName).normalize();
            if (logFile.startsWith(logDir.normalize()) && Files.isRegularFile(logFile)) {
                return logFile;
            }
        }
        return null;
    }

    private String filterLogContent(String content, String logFileName, String jobEngineId) {
        if (StringUtils.isBlank(jobEngineId) || isDedicatedJobLogFile(logFileName, jobEngineId)) {
            return content;
        }
        String marker = "[" + jobEngineId.trim() + "]";
        return content.lines()
                .filter(line -> line.contains(marker))
                .collect(Collectors.joining(System.lineSeparator()));
    }

    private boolean isDedicatedJobLogFile(String logFileName, String jobEngineId) {
        return logFileName.equals("job-" + jobEngineId.trim() + ".log")
                || logFileName.endsWith("job-" + jobEngineId.trim() + ".log");
    }

    private JobLogNodeRes normalizeLogNode(JobLogNodeRes node) {
        String logName = normalizeLogFileName(node.getLogName());
        if (StringUtils.isBlank(logName)) {
            logName = normalizeLogFileName(node.getLogLink());
        }
        String nodeName = StringUtils.defaultIfBlank(node.getNode(), "engine");
        return new JobLogNodeRes(nodeName, logName, logName);
    }

    private JobLogNodeRes createLocalLogNode(String fileName) {
        return new JobLogNodeRes("local", fileName, fileName);
    }

    private String normalizeLogFileName(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            try {
                trimmed = Paths.get(URI.create(trimmed).getPath()).getFileName().toString();
            } catch (Exception e) {
                trimmed = trimmed.substring(trimmed.lastIndexOf('/') + 1);
            }
        } else if (trimmed.contains("/")) {
            trimmed = trimmed.substring(trimmed.lastIndexOf('/') + 1);
        }
        return trimmed;
    }

    private void validateLogFileName(String fileName) {
        if (StringUtils.isBlank(fileName) || !LOG_FILE_NAME_PATTERN.matcher(fileName).matches()) {
            throw new SeatunnelException(SeatunnelErrorEnum.ILLEGAL_STATE, "Invalid log file name");
        }
    }

    private List<Path> resolveLogDirectories() {
        Set<Path> directories = new LinkedHashSet<>();
        if (StringUtils.isNotBlank(configuredLogPath)) {
            directories.add(Paths.get(configuredLogPath));
        }
        String logsPath = System.getProperty("seatunnel.logs.path");
        if (StringUtils.isNotBlank(logsPath)) {
            directories.add(Paths.get(logsPath));
        }
        String seatunnelHome = System.getenv("SEATUNNEL_HOME");
        if (StringUtils.isBlank(seatunnelHome)) {
            seatunnelHome = System.getProperty("seatunnel.home");
        }
        if (StringUtils.isNotBlank(seatunnelHome)) {
            directories.add(Paths.get(seatunnelHome, "logs"));
        }
        directories.add(Paths.get("logs"));
        return directories.stream().filter(Files::isDirectory).collect(Collectors.toList());
    }

    private String trimTrailingSlash(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
