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

import org.apache.commons.lang3.Validate;

/**
 * A Sponge REST API configuration.
 */
public class RestApiClientConfiguration {

    private String url;

    private String username;

    private String password;

    private boolean useRequestId = false;

    private boolean relogin = true;

    private boolean verifyKnowledgeBaseVersion = true;

    private boolean useActionMetaCache = true;

    private int actionMetaCacheMaxSize = -1;

    private long actionMetaCacheExpireSeconds = -1;

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
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

    public boolean isVerifyKnowledgeBaseVersion() {
        return verifyKnowledgeBaseVersion;
    }

    public void setVerifyKnowledgeBaseVersion(boolean verifyKnowledgeBaseVersion) {
        this.verifyKnowledgeBaseVersion = verifyKnowledgeBaseVersion;
    }

    public boolean isSsl() {
        return url != null && url.toLowerCase().startsWith("https");
    }

    public boolean isUseActionMetaCache() {
        return useActionMetaCache;
    }

    public void setUseActionMetaCache(boolean useActionMetaCache) {
        this.useActionMetaCache = useActionMetaCache;
    }

    public int getActionMetaCacheMaxSize() {
        return actionMetaCacheMaxSize;
    }

    public void setActionMetaCacheMaxSize(int actionMetaCacheMaxSize) {
        this.actionMetaCacheMaxSize = actionMetaCacheMaxSize;
    }

    public long getActionMetaCacheExpireSeconds() {
        return actionMetaCacheExpireSeconds;
    }

    public void setActionMetaCacheExpireSeconds(long actionMetaCacheExpireSeconds) {
        this.actionMetaCacheExpireSeconds = actionMetaCacheExpireSeconds;
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

        /**
         * Builds the client.
         *
         * @return the client.
         */
        public RestApiClientConfiguration build() {
            Validate.notNull(configuration.getUrl(), "URL not set");

            return configuration;
        }

        /**
         * Sets the service URL.
         *
         * @param url the service URL.
         * @return the builder.
         */
        public Builder url(String url) {
            configuration.setUrl(url);
            return this;
        }

        /**
         * Sets the username.
         *
         * @param username the username.
         * @return the builder.
         */
        public Builder username(String username) {
            configuration.setUsername(username);
            return this;
        }

        /**
         * Sets the password.
         *
         * @param password the password.
         * @return the builder.
         */
        public Builder password(String password) {
            configuration.setPassword(password);
            return this;
        }

        /**
         * Sets the flag to use a request ID. The default value is {@code false}.
         *
         * @param useRequestId the flag to use a request ID.
         * @return the builder.
         */
        public Builder useRequestId(boolean useRequestId) {
            configuration.setUseRequestId(useRequestId);
            return this;
        }

        /**
         * Sets the flag to automatically relogin when an auth token expires. The default value is {@code true}.
         *
         * @param relogin the flag to automatically relogin when an auth token expires.
         * @return the builder.
         */
        public Builder relogin(boolean relogin) {
            configuration.setRelogin(relogin);
            return this;
        }

        /**
         * Sets the flag to verify a knowledge base version on the server. The default value is {@code true}.
         *
         * @param verifyKnowledgeBaseVersion the flag to verify a knowledge base version on the server.
         * @return the builder.
         */
        public Builder verifyKnowledgeBaseVersion(boolean verifyKnowledgeBaseVersion) {
            configuration.setVerifyKnowledgeBaseVersion(verifyKnowledgeBaseVersion);
            return this;
        }

        /**
         * Sets the flag to use the action meta cache. The default value is {@code true}.
         *
         * @param useActionMetaCache the flag to use the action meta cache.
         * @return the builder.
         */
        public Builder useActionMetaCache(boolean useActionMetaCache) {
            configuration.setUseActionMetaCache(useActionMetaCache);
            return this;
        }

        /**
         * Sets the maximum size of the action meta cache. The default value ({@code -1}) implies that there is no maximum size.
         *
         * @param actionMetaCacheMaxSize the maximum size of the action meta cache.
         * @return the builder.
         */
        public Builder actionMetaCacheMaxSize(int actionMetaCacheMaxSize) {
            configuration.setActionMetaCacheMaxSize(actionMetaCacheMaxSize);
            return this;
        }

        /**
         * Sets the expire time (in seconds) of the action meta cache. The default value ({@code -1}) implies that there is no expire time.
         *
         * @param actionMetaCacheExpireSeconds the expire time (in seconds) of the action meta cache.
         * @return the builder.
         */
        public Builder actionMetaCacheExpireSeconds(long actionMetaCacheExpireSeconds) {
            configuration.setActionMetaCacheExpireSeconds(actionMetaCacheExpireSeconds);
            return this;
        }
    }
}
