/*
 * Copyright 2016-2018 The Sponge authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openksavi.sponge.restapi.client;

import org.openksavi.sponge.restapi.RestApiConstants;

/**
 * A Sponge REST API configuration.
 */
public class RestApiClientConfiguration {

    private String host;

    private int port;

    private String baseUrl;

    private boolean ssl;

    private String username;

    private String password;

    private boolean useRequestId = false;

    private boolean relogin = true;

    public RestApiClientConfiguration(String host, int port, String baseUrl, boolean ssl) {
        this.host = host;
        this.port = port;
        this.baseUrl = baseUrl;
        this.ssl = ssl;
    }

    public RestApiClientConfiguration(String host, int port, String baseUrl) {
        this(host, port, baseUrl, false);
    }

    public RestApiClientConfiguration(String host, int port) {
        this(host, port, RestApiConstants.BASE_URL);
    }

    public RestApiClientConfiguration(String host) {
        this(host, RestApiConstants.DEFAULT_PORT);
    }

    public RestApiClientConfiguration() {
        this("localhost");
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isUseRequestId() {
        return useRequestId;
    }

    public void setUseRequestId(boolean useRequestId) {
        this.useRequestId = useRequestId;
    }

    public boolean isRelogin() {
        return relogin;
    }

    public void setRelogin(boolean relogin) {
        this.relogin = relogin;
    }

    public void setUsernameAndPassword(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    /**
     * A Sponge REST API configuration builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private RestApiClientConfiguration configuration = new RestApiClientConfiguration();

        public RestApiClientConfiguration build() {
            return configuration;
        }

        public Builder ssl(boolean ssl) {
            configuration.setSsl(ssl);
            return this;
        }

        public Builder host(String host) {
            configuration.setHost(host);
            return this;
        }

        public Builder port(int port) {
            configuration.setPort(port);
            return this;
        }

        public Builder baseUrl(String baseUrl) {
            configuration.setBaseUrl(baseUrl);
            return this;
        }

        public Builder username(String username) {
            configuration.setUsername(username);
            return this;
        }

        public Builder password(String password) {
            configuration.setPassword(password);
            return this;
        }

        public Builder useRequestId(boolean useRequestId) {
            configuration.setUseRequestId(useRequestId);
            return this;
        }

        public Builder relogin(boolean relogin) {
            configuration.setRelogin(relogin);
            return this;
        }
    }
}
