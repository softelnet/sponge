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
public class SpongeRestClientConfiguration {

    private String url;

    private String username;

    private String password;

    private boolean useRequestId = false;

    private boolean autoUseAuthToken = false;

    private boolean relogin = true;

    private boolean verifyProcessorVersion = true;

    private boolean useActionMetaCache = true;

    private int actionMetaCacheMaxSize = -1;

    private long actionMetaCacheExpireSeconds = -1;

    private boolean useEventTypeCache = true;

    private int eventTypeCacheMaxSize = -1;

    private long eventTypeCacheExpireSeconds = -1;

    private boolean throwExceptionOnErrorResponse = true;

    private boolean prettyPrint = false;

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

    public boolean isAutoUseAuthToken() {
        return autoUseAuthToken;
    }

    public void setAutoUseAuthToken(boolean autoUseAuthToken) {
        this.autoUseAuthToken = autoUseAuthToken;
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

    public boolean isVerifyProcessorVersion() {
        return verifyProcessorVersion;
    }

    public void setVerifyProcessorVersion(boolean verifyProcessorVersion) {
        this.verifyProcessorVersion = verifyProcessorVersion;
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

    public boolean isUseEventTypeCache() {
        return useEventTypeCache;
    }

    public void setUseEventTypeCache(boolean useEventTypeCache) {
        this.useEventTypeCache = useEventTypeCache;
    }

    public int getEventTypeCacheMaxSize() {
        return eventTypeCacheMaxSize;
    }

    public void setEventTypeCacheMaxSize(int eventTypeCacheMaxSize) {
        this.eventTypeCacheMaxSize = eventTypeCacheMaxSize;
    }

    public long getEventTypeCacheExpireSeconds() {
        return eventTypeCacheExpireSeconds;
    }

    public void setEventTypeCacheExpireSeconds(long eventTypeCacheExpireSeconds) {
        this.eventTypeCacheExpireSeconds = eventTypeCacheExpireSeconds;
    }

    public boolean isThrowExceptionOnErrorResponse() {
        return throwExceptionOnErrorResponse;
    }

    public void setThrowExceptionOnErrorResponse(boolean throwExceptionOnErrorResponse) {
        this.throwExceptionOnErrorResponse = throwExceptionOnErrorResponse;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
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

        private SpongeRestClientConfiguration configuration = new SpongeRestClientConfiguration();

        /**
         * Builds the client.
         *
         * @return the client.
         */
        public SpongeRestClientConfiguration build() {
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
         * Sets the flag telling if the client should automatically use auth token authentication (i.e. the login operation won't be
         * required to use the auth token authentication). The default value is {@code false}.
         *
         * @param autoUseAuthToken the flag to automatically use auth token authentication.
         * @return the builder.
         */
        public Builder autoUseAuthToken(boolean autoUseAuthToken) {
            configuration.setAutoUseAuthToken(autoUseAuthToken);
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
         * Sets the flag to verify a processor version on the server. The default value is {@code true}.
         *
         * @param verifyProcessorVersion the flag to verify a processor version on the server.
         * @return the builder.
         */
        public Builder verifyProcessorVersion(boolean verifyProcessorVersion) {
            configuration.setVerifyProcessorVersion(verifyProcessorVersion);
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

        /**
         * Sets the flag to use the event type cache. The default value is {@code true}.
         *
         * @param useEventTypeCache the flag to use the event type cache.
         * @return the builder.
         */
        public Builder useEventTypeCache(boolean useEventTypeCache) {
            configuration.setUseEventTypeCache(useEventTypeCache);
            return this;
        }

        /**
         * Sets the maximum size of the event type cache. The default value ({@code -1}) implies that there is no maximum size.
         *
         * @param eventTypeCacheMaxSize the maximum size of the event type cache.
         * @return the builder.
         */
        public Builder eventTypeCacheMaxSize(int eventTypeCacheMaxSize) {
            configuration.setEventTypeCacheMaxSize(eventTypeCacheMaxSize);
            return this;
        }

        /**
         * Sets the expire time (in seconds) of the event type cache. The default value ({@code -1}) implies that there is no expire time.
         *
         * @param eventTypeCacheExpireSeconds the expire time (in seconds) of the event type cache.
         * @return the builder.
         */
        public Builder eventTypeCacheExpireSeconds(long eventTypeCacheExpireSeconds) {
            configuration.setEventTypeCacheExpireSeconds(eventTypeCacheExpireSeconds);
            return this;
        }

        /**
         * Instructs the client to throw an exception when a response is an error response. Setting this value to {@code false} makes sense
         * only when using the client API methods {@code *ByRequest()}.
         *
         * @param throwExceptionOnErrorResponse if {@code true} (the default value) the client will throw an exception when a response is an
         *        error response.
         *
         * @return the builder.
         */
        public Builder throwExceptionOnErrorResponse(boolean throwExceptionOnErrorResponse) {
            configuration.setThrowExceptionOnErrorResponse(throwExceptionOnErrorResponse);
            return this;
        }

        /**
         * Sets the pretty print flag. The default value is {@code false}.
         *
         * @param prettyPrint the pretty print flag.
         * @return the builder.
         */
        public Builder prettyPrint(boolean prettyPrint) {
            configuration.setPrettyPrint(prettyPrint);
            return this;
        }
    }
}
