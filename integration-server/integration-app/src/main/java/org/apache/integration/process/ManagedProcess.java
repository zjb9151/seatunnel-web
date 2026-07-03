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

package org.apache.integration.process;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Getter
public class ManagedProcess {

    private final String name;
    private final List<String> command;
    private final File workDir;
    private final int maxRestarts;
    private final long restartDelayMs;
    private final AtomicInteger restartCount = new AtomicInteger(0);

    private Process process;
    private Instant startedAt;
    private volatile boolean stopping;

    public ManagedProcess(
            String name, List<String> command, File workDir, int maxRestarts, long restartDelayMs) {
        this.name = name;
        this.command = command;
        this.workDir = workDir;
        this.maxRestarts = maxRestarts;
        this.restartDelayMs = restartDelayMs;
    }

    public synchronized void start() {
        stopping = false;
        try {
            Path logDir = Paths.get("logs", name);
            Files.createDirectories(logDir);
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.directory(workDir);
            pb.redirectOutput(logDir.resolve("stdout.log").toFile());
            pb.redirectError(logDir.resolve("stderr.log").toFile());
            process = pb.start();
            startedAt = Instant.now();
            log.info("Started process {} pid={}", name, process.pid());
            Thread monitor =
                    new Thread(
                            () -> {
                                try {
                                    int code = process.waitFor();
                                    if (!stopping) {
                                        log.warn("Process {} exited with code {}", name, code);
                                        maybeRestart();
                                    }
                                } catch (InterruptedException e) {
                                    Thread.currentThread().interrupt();
                                }
                            },
                            "process-monitor-" + name);
            monitor.setDaemon(true);
            monitor.start();
        } catch (IOException e) {
            log.error("Failed to start process {}", name, e);
        }
    }

    private void maybeRestart() {
        if (restartCount.incrementAndGet() > maxRestarts) {
            log.error("Process {} exceeded max restarts ({})", name, maxRestarts);
            return;
        }
        try {
            Thread.sleep(restartDelayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }
        log.info("Restarting process {} (attempt {})", name, restartCount.get());
        start();
    }

    public synchronized void stop() {
        stopping = true;
        if (process != null && process.isAlive()) {
            process.destroyForcibly();
            log.info("Stopped process {}", name);
        }
    }

    public boolean isAlive() {
        return process != null && process.isAlive();
    }

    public Long getPid() {
        return process != null ? process.pid() : null;
    }
}
