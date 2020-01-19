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

package org.openksavi.sponge.restapi.server;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorQualifiedName;
import org.openksavi.sponge.core.util.SslConfiguration;
import org.openksavi.sponge.restapi.RestApiConstants;

/**
 * REST API settings.
 */
public class RestApiSettings {

    /** The Camel REST component id. */
    private String restComponentId = RestApiServerConstants.DEFAULT_REST_COMPONENT_ID;

    /** The host. */
    private String host;

    /** The port. */
    private Integer port = RestApiConstants.DEFAULT_PORT;

    /** The URL path. */
    private String path = RestApiConstants.DEFAULT_PATH;

    /** The API version. */
    private int version = RestApiConstants.API_VERSION;

    /** The API title. */
    private String title = RestApiConstants.DEFAULT_TITLE;

    /** The API description. */
    private String description = RestApiConstants.DEFAULT_DESCRIPTION;

    /** The pretty print option. */
    private boolean prettyPrint = RestApiServerConstants.DEFAULT_PRETTY_PRINT;

    /** Public actions. */
    private List<ProcessorQualifiedName> publicActions;

    /** Public event names. */
    private List<String> publicEvents;

    /** The SSLContextParameters bean name. */
    private String sslContextParametersBeanName = RestApiServerConstants.DEFAULT_SSL_CONTEXT_PARAMETERS_BEAN_NAME;

    /** The SSL configuration. */
    private SslConfiguration sslConfiguration;

    /** If {@code true} then the reload operation will be published. The default value is {@code true}. */
    private boolean publishReload = RestApiServerConstants.DEFAULT_PUBLISH_RELOAD;

    private boolean allowAnonymous = RestApiServerConstants.DEFAULT_ALLOW_ANONYMOUS;

    private String adminRole = RestApiServerConstants.DEFAULT_ROLE_ADMIN;

    private String anonymousRole = RestApiServerConstants.DEFAULT_ROLE_ANONYMOUS;

    private boolean includeDetailedErrorMessage = RestApiServerConstants.DEFAULT_INCLUDE_DETAILED_ERROR_MESSAGE;

    /** The duration after which an authentication token will expire. The value {@code null} means infinity. Defaults to 30 minutes. */
    private Duration authTokenExpirationDuration = Duration.ofMinutes(30);

    private Map<String, String> openApiProperties = Collections.synchronizedMap(new LinkedHashMap<>());

    /** The flag specifying if a response header should have request and response time set. Defaults to {@code true}. */
    private boolean includeResponseTimes = RestApiServerConstants.DEFAULT_INCLUDE_RESPONSE_TIMES;

    public String getRestComponentId() {
        return restComponentId;
    }

    public void setRestComponentId(String restComponentId) {
        this.restComponentId = restComponentId;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getVersion() {
        return version;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public List<ProcessorQualifiedName> getPublicActions() {
        return publicActions;
    }

    public void setPublicActions(List<ProcessorQualifiedName> publicActions) {
        this.publicActions = publicActions;
    }

    public List<String> getPublicEvents() {
        return publicEvents;
    }

    public void setPublicEvents(List<String> publicEvents) {
        this.publicEvents = publicEvents;
    }

    public String getSslContextParametersBeanName() {
        return sslContextParametersBeanName;
    }

    public void setSslContextParametersBeanName(String sslContextParametersBeanName) {
        this.sslContextParametersBeanName = sslContextParametersBeanName;
    }

    public SslConfiguration getSslConfiguration() {
        return sslConfiguration;
    }

    public void setSslConfiguration(SslConfiguration sslConfiguration) {
        this.sslConfiguration = sslConfiguration;
    }

    public boolean isPublishReload() {
        return publishReload;
    }

    public void setPublishReload(boolean publishReload) {
        this.publishReload = publishReload;
    }

    public boolean isAllowAnonymous() {
        return allowAnonymous;
    }

    public void setAllowAnonymous(boolean allowAnonymous) {
        this.allowAnonymous = allowAnonymous;
    }

    public String getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(String adminRole) {
        this.adminRole = adminRole;
    }

    public String getAnonymousRole() {
        return anonymousRole;
    }

    public void setAnonymousRole(String anonymousRole) {
        this.anonymousRole = anonymousRole;
    }

    public boolean isIncludeDetailedErrorMessage() {
        return includeDetailedErrorMessage;
    }

    public void setIncludeDetailedErrorMessage(boolean includeDetailedErrorMessage) {
        this.includeDetailedErrorMessage = includeDetailedErrorMessage;
    }

    public Duration getAuthTokenExpirationDuration() {
        return authTokenExpirationDuration;
    }

    public void setAuthTokenExpirationDuration(Duration authTokenExpirationDuration) {
        this.authTokenExpirationDuration = authTokenExpirationDuration;
    }

    public Map<String, String> getOpenApiProperties() {
        return openApiProperties;
    }

    public void setOpenApiProperties(Map<String, String> openApiProperties) {
        this.openApiProperties = openApiProperties;
    }

    /**
     * Adds an OpenAPI property. Supports the Swagger Java Component String options that start with {@code "api."}.
     *
     * @see <a href="https://camel.apache.org/components/latest/swagger-java.html">Swagger Java Component options</a>
     *
     * @param name the property name.
     * @param value the property value.
     */
    public void addOpenApiProperty(String name, String value) {
        openApiProperties.put(name, value);
    }

    public boolean isIncludeResponseTimes() {
        return includeResponseTimes;
    }

    public void setIncludeResponseTimes(boolean includeResponseTimes) {
        this.includeResponseTimes = includeResponseTimes;
    }
}
