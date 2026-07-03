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

import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import lombok.Data;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class RuntimeCatalogLoader {

    private volatile RuntimeCatalog catalog;

    public RuntimeCatalog getCatalog() {
        if (catalog == null) {
            synchronized (this) {
                if (catalog == null) {
                    catalog = load();
                }
            }
        }
        return catalog;
    }

    @SuppressWarnings("unchecked")
    private RuntimeCatalog load() {
        Yaml yaml = new Yaml();
        try (InputStream in =
                getClass().getClassLoader().getResourceAsStream("runtime-catalog.yml")) {
            if (in == null) {
                throw new IllegalStateException("runtime-catalog.yml not found");
            }
            Map<String, Object> root = yaml.load(in);
            Map<String, Object> components = (Map<String, Object>) root.get("components");
            RuntimeCatalog result = new RuntimeCatalog();
            result.setComponents(new LinkedHashMap<>());
            if (components == null) {
                return result;
            }
            for (Map.Entry<String, Object> entry : components.entrySet()) {
                Map<String, Object> comp = (Map<String, Object>) entry.getValue();
                RuntimeComponentCatalog c = new RuntimeComponentCatalog();
                c.setId(entry.getKey());
                c.setDisplayName(String.valueOf(comp.get("display-name")));
                c.setDefaultVersion(String.valueOf(comp.get("default-version")));
                Map<String, Object> versions = (Map<String, Object>) comp.get("versions");
                c.setVersions(new LinkedHashMap<>());
                if (versions != null) {
                    for (Map.Entry<String, Object> ver : versions.entrySet()) {
                        Map<String, Object> meta = (Map<String, Object>) ver.getValue();
                        RuntimeVersionCatalog v = new RuntimeVersionCatalog();
                        v.setVersion(ver.getKey());
                        v.setArchive(str(meta.get("archive")));
                        v.setUrl(str(meta.get("url")));
                        v.setExtractDir(str(meta.get("extract-dir")));
                        v.setNote(str(meta.get("note")));
                        c.getVersions().put(ver.getKey(), v);
                    }
                }
                result.getComponents().put(entry.getKey(), c);
            }
            return result;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load runtime catalog", e);
        }
    }

    private static String str(Object o) {
        return o == null ? "" : String.valueOf(o);
    }

    @Data
    public static class RuntimeCatalog {
        private Map<String, RuntimeComponentCatalog> components = new LinkedHashMap<>();
    }

    @Data
    public static class RuntimeComponentCatalog {
        private String id;
        private String displayName;
        private String defaultVersion;
        private Map<String, RuntimeVersionCatalog> versions = new LinkedHashMap<>();
    }

    @Data
    public static class RuntimeVersionCatalog {
        private String version;
        private String archive;
        private String url;
        private String extractDir;
        private String note;

        public boolean isDownloadable() {
            return StringUtils.isNotBlank(url) && StringUtils.isNotBlank(archive);
        }
    }
}
