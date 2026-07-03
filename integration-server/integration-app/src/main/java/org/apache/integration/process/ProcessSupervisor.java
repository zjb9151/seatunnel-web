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

import org.apache.commons.lang3.StringUtils;
import org.apache.integration.config.IntegrationProperties;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ProcessSupervisor {

    @Resource private IntegrationProperties properties;

    private final Map<String, ManagedProcess> processes = new ConcurrentHashMap<>();

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        startManagedProcesses();
    }

    public void startManagedProcesses() {
        if (properties.getProcesses().getSeatunnelEngine().isEnabled()
                && properties.getProcesses().getSeatunnelEngine().isAutoStart()) {
            startEngine();
        }
        if (properties.getProcesses().getDolphinscheduler().isEnabled()
                && properties.getProcesses().getDolphinscheduler().isAutoStart()) {
            startDolphinScheduler();
        }
        if (properties.getProcesses().getSeatunnelWeb().isEnabled()
                && properties.getProcesses().getSeatunnelWeb().isAutoStart()) {
            startSeatunnelWeb();
        }
    }

    public void stopAll() {
        processes.values().forEach(ManagedProcess::stop);
        processes.clear();
    }

    public Map<String, ManagedProcess> getProcesses() {
        return processes;
    }

    private void startEngine() {
        String home = properties.getProcesses().getSeatunnelEngine().getHome();
        if (StringUtils.isBlank(home)) {
            log.warn("seatunnel-engine home not configured, skip auto-start");
            return;
        }
        Path script =
                Paths.get(home)
                        .resolve(
                                System.getProperty("os.name", "").toLowerCase().contains("win")
                                        ? "bin/seatunnel-cluster.cmd"
                                        : "bin/seatunnel-cluster.sh");
        if (!Files.exists(script)) {
            log.warn("SeaTunnel engine script not found: {}", script);
            return;
        }
        List<String> cmd = new ArrayList<>();
        if (script.toString().endsWith(".cmd")) {
            cmd.add("cmd.exe");
            cmd.add("/c");
            cmd.add(script.toString());
            cmd.add("-d");
        } else {
            cmd.add("/bin/sh");
            cmd.add(script.toString());
            cmd.add("-d");
        }
        ManagedProcess mp =
                new ManagedProcess(
                        "seatunnel-engine",
                        cmd,
                        Paths.get(home).toFile(),
                        properties.getProcesses().getSeatunnelEngine().getMaxRestarts(),
                        properties.getProcesses().getSeatunnelEngine().getRestartDelayMs());
        mp.start();
        processes.put(mp.getName(), mp);
    }

    private void startDolphinScheduler() {
        String home = properties.getProcesses().getDolphinscheduler().getHome();
        if (StringUtils.isBlank(home)) {
            log.warn("dolphinscheduler home not configured, skip auto-start");
            return;
        }
        String javaHome = System.getenv("JAVA_HOME");
        if (StringUtils.isBlank(javaHome)) {
            javaHome = System.getProperty("java.home");
        }
        Path java = Paths.get(javaHome, "bin", isWindows() ? "java.exe" : "java");
        Path standalone = Paths.get(home, "standalone-server");
        Path cpCache = Paths.get(home, ".cp-cache");
        try {
            ensureDsClasspath(home, cpCache, standalone);
        } catch (Exception e) {
            log.error("Failed to build DS classpath", e);
            return;
        }
        String classpath = cpCache.toString() + File.pathSeparator + standalone.resolve("conf");
        List<String> cmd = new ArrayList<>();
        cmd.add(java.toString());
        cmd.add("-server");
        cmd.add("-Duser.timezone=Asia/Shanghai");
        cmd.add("-Ddolphinscheduler.home=" + home);
        cmd.add("-Ddemo.tenant-code=default");
        cmd.add("-Xms512m");
        cmd.add("-Xmx1g");
        cmd.add("-cp");
        cmd.add(
                cpCache.toString()
                        + File.separator
                        + "*"
                        + File.pathSeparator
                        + standalone.resolve("conf"));
        cmd.add("org.apache.dolphinscheduler.StandaloneServer");
        ManagedProcess mp =
                new ManagedProcess(
                        "dolphinscheduler",
                        cmd,
                        standalone.toFile(),
                        properties.getProcesses().getDolphinscheduler().getMaxRestarts(),
                        properties.getProcesses().getDolphinscheduler().getRestartDelayMs());
        mp.start();
        processes.put(mp.getName(), mp);
    }

    private void startSeatunnelWeb() {
        String distHome = properties.getProcesses().getSeatunnelWeb().getDistHome();
        if (StringUtils.isBlank(distHome)) {
            log.warn("seatunnel-web dist-home not configured, skip auto-start");
            return;
        }
        Path base = Paths.get(distHome).toAbsolutePath().normalize();
        if (!Files.isDirectory(base)) {
            log.warn("seatunnel-web dist not found: {}", base);
            return;
        }
        String javaHome = System.getenv("JAVA_HOME");
        if (StringUtils.isBlank(javaHome)) {
            javaHome = System.getProperty("java.home");
        }
        Path java = Paths.get(javaHome, "bin", isWindows() ? "java.exe" : "java");
        String cp =
                base.resolve("conf")
                        + File.pathSeparator
                        + base.resolve("libs").resolve("*")
                        + File.pathSeparator
                        + base.resolve("datasource").resolve("*");
        List<String> cmd = new ArrayList<>();
        cmd.add(java.toString());
        cmd.add("-server");
        cmd.add("-Xms512m");
        cmd.add("-Xmx1g");
        cmd.add("-cp");
        cmd.add(cp);
        cmd.add("-Dspring.config.location=file:" + base.resolve("conf/application.yml"));
        if (StringUtils.isNotBlank(System.getenv("SEATUNNEL_HOME"))) {
            cmd.add("-DSEATUNNEL_HOME=" + System.getenv("SEATUNNEL_HOME"));
        }
        cmd.add("org.apache.seatunnel.app.SeatunnelApplication");
        ManagedProcess mp =
                new ManagedProcess(
                        "seatunnel-web",
                        cmd,
                        base.toFile(),
                        properties.getProcesses().getSeatunnelWeb().getMaxRestarts(),
                        properties.getProcesses().getSeatunnelWeb().getRestartDelayMs());
        mp.start();
        processes.put(mp.getName(), mp);
    }

    private static void ensureDsClasspath(String dsRoot, Path cpCache, Path standalone)
            throws Exception {
        Path ready = cpCache.resolve(".ready");
        if (Files.exists(ready)) {
            return;
        }
        Files.createDirectories(cpCache);
        Map<String, Path> seen = new ConcurrentHashMap<>();
        for (Path libDir :
                List.of(standalone.resolve("libs/standalone-server"), Paths.get(dsRoot, "libs"))) {
            if (!Files.isDirectory(libDir)) {
                continue;
            }
            try (java.util.stream.Stream<Path> stream = Files.list(libDir)) {
                stream.filter(p -> p.toString().endsWith(".jar"))
                        .filter(
                                p ->
                                        !p.getFileName()
                                                .toString()
                                                .startsWith("dolphinscheduler-tools-"))
                        .forEach(
                                jar -> {
                                    try {
                                        if (!seen.containsKey(jar.getFileName().toString())) {
                                            Files.copy(
                                                    jar,
                                                    cpCache.resolve(jar.getFileName()),
                                                    java.nio.file.StandardCopyOption
                                                            .REPLACE_EXISTING);
                                            seen.put(jar.getFileName().toString(), jar);
                                        }
                                    } catch (Exception ignored) {
                                    }
                                });
            }
        }
        Files.createFile(ready);
    }

    private static boolean isWindows() {
        return System.getProperty("os.name", "").toLowerCase().contains("win");
    }
}
