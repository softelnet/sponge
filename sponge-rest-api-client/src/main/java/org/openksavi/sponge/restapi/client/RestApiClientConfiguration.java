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

    private String url = String.format("http://localhost:%d/%s", RestApiConstants.DEFAULT_PORT, RestApiConstants.DEFAULT_PATH);

    private String username;

    private String password;

    private boolean useRequestId = false;

    private boolean relogin = true;

    private boolean verifyKnowledgeBaseVersion = true;

    public String getUsername() {
        return username;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
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

    public boolean isVerifyKnowledgeBaseVersion() {
        return verifyKnowledgeBaseVersion;
    }

    public void setVerifyKnowledgeBaseVersion(boolean verifyKnowledgeBaseVersion) {
        this.verifyKnowledgeBaseVersion = verifyKnowledgeBaseVersion;
    }

    public boolean isSsl() {
        return url != null && url.toLowerCase().startsWith("https");
    }

    /**
     * A Sponge REST API configuration builder.
     *
     * @return the builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private RestApiClientConfiguration configuration = new RestApiClientConfiguration();

        public RestApiClientConfiguration build() {
            return configuration;
        }

        public Builder url(String url) {
            configuration.setUrl(url);
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

        public Builder verifyKnowledgeBaseVersion(boolean verifyKnowledgeBaseVersion) {
            configuration.setVerifyKnowledgeBaseVersion(verifyKnowledgeBaseVersion);
            return this;
        }
    }
}
