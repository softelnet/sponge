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

package org.openksavi.sponge.remoteapi.server;

import java.time.Duration;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.ProcessorQualifiedName;
import org.openksavi.sponge.core.util.SslConfiguration;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.server.discovery.ServiceDiscoveryInfo;

/**
 * Remote API settings.
 */
public class RemoteApiSettings {

    /** The Camel component id. */
    private String componentId = RemoteApiServerConstants.DEFAULT_COMPONENT_ID;

    /** The host. */
    private String host;

    /** The port. */
    private Integer port = RemoteApiConstants.DEFAULT_PORT;

    /** The URL path. */
    private String path;

    /** The API version. */
    private String version;

    /** The API name. */
    private String name;

    /** The API description. */
    private String description;

    /** The API license. */
    private String license;

    /** The pretty print option. */
    private boolean prettyPrint = RemoteApiServerConstants.DEFAULT_PRETTY_PRINT;

    /** Public actions. */
    private List<ProcessorQualifiedName> publicActions;

    /** Public event names. */
    private List<String> publicEvents;

    /** The SSLContextParameters bean name. */
    private String sslContextParametersBeanName = RemoteApiServerConstants.DEFAULT_SSL_CONTEXT_PARAMETERS_BEAN_NAME;

    /** The SSL configuration. */
    private SslConfiguration sslConfiguration;

    /** If {@code true} then the reload method will be published. The default value is {@code true}. */
    private boolean publishReload = RemoteApiServerConstants.DEFAULT_PUBLISH_RELOAD;

    private boolean allowAnonymous = RemoteApiServerConstants.DEFAULT_ALLOW_ANONYMOUS;

    private String adminRole = RemoteApiServerConstants.DEFAULT_ROLE_ADMIN;

    private String anonymousRole = RemoteApiServerConstants.DEFAULT_ROLE_ANONYMOUS;

    private boolean includeDetailedErrorMessage = RemoteApiServerConstants.DEFAULT_INCLUDE_DETAILED_ERROR_MESSAGE;

    /** The duration after which an authentication token will expire. The value {@code null} means infinity. Defaults to 30 minutes. */
    private Duration authTokenExpirationDuration = Duration.ofMinutes(30);

    private Map<String, String> openApiProperties = Collections.synchronizedMap(new LinkedHashMap<>());

    /** The flag specifying if a response header should have request and response time set. Defaults to {@code false}. */
    private boolean includeResponseTimes = RemoteApiServerConstants.DEFAULT_INCLUDE_RESPONSE_TIMES;

    /** The flag specifying if the service should be registered in a service discovery. Defaults to {@code false}. */
    private boolean registerServiceDiscovery = false;

    /** The service discovery info. */
    private ServiceDiscoveryInfo serviceDiscoveryInfo;

    /** The flag specifying if the service should ignore unknown action arguments passed by the client. Defaults to {@code false}. */
    private boolean ignoreUnknownArgs = false;

    public String getComponentId() {
        return componentId;
    }

    public void setComponentId(String componentId) {
        this.componentId = componentId;
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

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setVersion(String version) {
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

    public boolean isRegisterServiceDiscovery() {
        return registerServiceDiscovery;
    }

    public void setRegisterServiceDiscovery(boolean registerServiceDiscovery) {
        this.registerServiceDiscovery = registerServiceDiscovery;
    }

    public ServiceDiscoveryInfo getServiceDiscoveryInfo() {
        return serviceDiscoveryInfo;
    }

    public void setServiceDiscoveryInfo(ServiceDiscoveryInfo serviceDiscoveryInfo) {
        this.serviceDiscoveryInfo = serviceDiscoveryInfo;
    }

    public boolean isIgnoreUnknownArgs() {
        return ignoreUnknownArgs;
    }

    public void setIgnoreUnknownArgs(boolean ignoreUnknownArgs) {
        this.ignoreUnknownArgs = ignoreUnknownArgs;
    }

    @Override
    public String toString() {
        return "RemoteApiSettings [componentId=" + componentId + ", host=" + host + ", port=" + port + ", path=" + path + ", version="
                + version + ", name=" + name + ", description=" + description + ", license=" + license + ", prettyPrint=" + prettyPrint
                + ", publicActions=" + publicActions + ", publicEvents=" + publicEvents + ", sslContextParametersBeanName="
                + sslContextParametersBeanName + ", sslConfiguration=" + sslConfiguration + ", publishReload=" + publishReload
                + ", allowAnonymous=" + allowAnonymous + ", adminRole=" + adminRole + ", anonymousRole=" + anonymousRole
                + ", includeDetailedErrorMessage=" + includeDetailedErrorMessage + ", authTokenExpirationDuration="
                + authTokenExpirationDuration + ", openApiProperties=" + openApiProperties + ", includeResponseTimes="
                + includeResponseTimes + ", registerServiceDiscovery=" + registerServiceDiscovery + ", serviceDiscoveryInfo="
                + serviceDiscoveryInfo + ", ignoreUnknownArgs=" + ignoreUnknownArgs + "]";
    }
}
