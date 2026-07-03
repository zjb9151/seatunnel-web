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

package org.apache.integration.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.integration.config.IntegrationProperties;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class SeatunnelWebProxyFilter implements Filter {

    private static final String PROXY_PREFIX = "/seatunnel/api";

    private static final Set<String> HOP_BY_HOP_HEADERS =
            Collections.unmodifiableSet(
                    new HashSet<>(
                            List.of(
                                    "connection",
                                    "keep-alive",
                                    "proxy-authenticate",
                                    "proxy-authorization",
                                    "te",
                                    "trailers",
                                    "transfer-encoding",
                                    "upgrade",
                                    "host",
                                    "content-length")));

    @Resource private IntegrationProperties properties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String contextPath = httpRequest.getContextPath();
        String pathWithinApp = httpRequest.getRequestURI().substring(contextPath.length());

        if (!pathWithinApp.startsWith(PROXY_PREFIX)) {
            chain.doFilter(request, response);
            return;
        }

        String apiBase = trimTrailingSlash(properties.getProcesses().getSeatunnelWeb().getApiUrl());
        if (StringUtils.isBlank(apiBase)) {
            httpResponse.sendError(503, "SeaTunnel-Web api-url is not configured");
            return;
        }

        String suffix = pathWithinApp.substring("/seatunnel".length());
        String query = httpRequest.getQueryString();
        String targetUrl = apiBase + suffix + (StringUtils.isNotBlank(query) ? "?" + query : "");

        HttpURLConnection connection = openConnection(httpRequest, targetUrl);
        copyResponseHeaders(connection, httpResponse);
        httpResponse.setStatus(connection.getResponseCode());
        try (InputStream inputStream =
                        connection.getResponseCode() >= 400
                                ? connection.getErrorStream()
                                : connection.getInputStream();
                OutputStream outputStream = httpResponse.getOutputStream()) {
            if (inputStream != null) {
                inputStream.transferTo(outputStream);
            }
        } finally {
            connection.disconnect();
        }
    }

    private HttpURLConnection openConnection(HttpServletRequest request, String targetUrl)
            throws IOException {
        HttpURLConnection connection =
                (HttpURLConnection) URI.create(targetUrl).toURL().openConnection();
        connection.setRequestMethod(request.getMethod());
        connection.setInstanceFollowRedirects(false);
        connection.setDoInput(true);

        boolean hasBody =
                "POST".equalsIgnoreCase(request.getMethod())
                        || "PUT".equalsIgnoreCase(request.getMethod())
                        || "PATCH".equalsIgnoreCase(request.getMethod());
        if (hasBody) {
            connection.setDoOutput(true);
        }

        copyRequestHeaders(request, connection);

        if (hasBody) {
            try (InputStream requestBody = request.getInputStream();
                    OutputStream outputStream = connection.getOutputStream()) {
                requestBody.transferTo(outputStream);
            }
        }
        return connection;
    }

    private void copyRequestHeaders(HttpServletRequest request, HttpURLConnection connection) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            if (HOP_BY_HOP_HEADERS.contains(headerName.toLowerCase(Locale.ROOT))) {
                continue;
            }
            Enumeration<String> values = request.getHeaders(headerName);
            while (values.hasMoreElements()) {
                connection.addRequestProperty(headerName, values.nextElement());
            }
        }
    }

    private void copyResponseHeaders(HttpURLConnection connection, HttpServletResponse response) {
        connection
                .getHeaderFields()
                .forEach(
                        (name, values) -> {
                            if (StringUtils.isBlank(name) || values == null) {
                                return;
                            }
                            String lower = name.toLowerCase(Locale.ROOT);
                            if (HOP_BY_HOP_HEADERS.contains(lower)
                                    || "x-frame-options".equals(lower)) {
                                return;
                            }
                            for (String value : values) {
                                response.addHeader(name, value);
                            }
                        });
    }

    private static String trimTrailingSlash(String url) {
        if (StringUtils.isBlank(url)) {
            return url;
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
