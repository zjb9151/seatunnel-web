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

package org.apache.integration.config;

import org.apache.commons.lang3.StringUtils;

import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class IntegrationPathResolver {

    private static final String CONFIG_DIR_PROPERTY = "integration.config.dir";

    public Path resolveBaseDir() {
        String configDir = System.getProperty(CONFIG_DIR_PROPERTY);
        if (StringUtils.isNotBlank(configDir)) {
            return Paths.get(configDir).toAbsolutePath().normalize().getParent();
        }
        return Paths.get("").toAbsolutePath().normalize();
    }

    public Path resolve(String path) {
        if (StringUtils.isBlank(path)) {
            return null;
        }
        Path p = Paths.get(path.trim());
        if (p.isAbsolute()) {
            return p.normalize();
        }
        return resolveBaseDir().resolve(p).normalize();
    }

    public String resolveString(String path) {
        Path resolved = resolve(path);
        return resolved == null ? path : resolved.toString();
    }
}
