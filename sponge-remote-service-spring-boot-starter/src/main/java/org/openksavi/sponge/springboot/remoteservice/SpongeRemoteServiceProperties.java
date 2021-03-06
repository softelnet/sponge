/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.springboot.remoteservice;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import org.openksavi.sponge.remoteapi.server.discovery.ServiceDiscoveryInfo;

@ConfigurationProperties(prefix = "sponge.remote")
public class SpongeRemoteServiceProperties {

    /** The API version. */
    private String version;

    /** The API name. */
    private String name;

    /** The API description. */
    private String description;

    /** The API license. */
    private String license;

    /** The pretty print option. */
    private Boolean prettyPrint;

    /** If {@code true} then the reload method will be published. The default value is {@code true}. */
    private Boolean publishReload;

    private Boolean allowAnonymous;

    private String adminRole;

    private String anonymousRole;

    /** The flag specifying if an error response should contain a detailed error message. Defaults to {@code false}. */
    private Boolean includeDetailedErrorMessage;

    /** The flag specifying if an error response message should contain an error location. Defaults to {@code false}. */
    private Boolean includeErrorLocationInErrorMessage;

    /** The duration after which an authentication token will expire. The value {@code null} means infinity. Defaults to 30 minutes. */
    private Duration authTokenExpirationDuration;

    private Map<String, String> openApiProperties = new LinkedHashMap<>();

    /** The flag specifying if a response header should have request and response time set. Defaults to {@code false}. */
    private Boolean includeResponseTimes;

    /** The flag specifying if the service should be registered in a service discovery. Defaults to {@code false}. */
    private Boolean registerServiceDiscovery;

    /** The service discovery info. */
    private final ServiceDiscoveryInfo discovery = new ServiceDiscoveryInfo();

    /** The flag specifying if the service should ignore unknown action arguments passed by the client. Defaults to {@code false}. */
    private Boolean ignoreUnknownArgs;

    /** The flag specifying if the HTTP request headers should be copied to the HTTP response. Defaults to {@code false}. */
    private Boolean copyHttpRequestHeaders = false;

    /**
     * The flag specifying if CORS is enabled, i.e. CORS headers will be included in the response HTTP headers. Defaults to {@code false}.
     */
    private Boolean corsEnabled = false;

    /**
     * The flag specifying if the Open API specification should also contain GET operations. Defaults to {@code false}.
     */
    private Boolean openApiDocsForGetVerbOperations = false;

    /** Suffix for Open API operation ID for GET operations. */
    private String openApiOperationIdSuffixForGetVerbOperations;

    /** The regexp specifying which endpoints should be included in the Open API specification. */
    private String openApiDocsForEndpoints;

    private final GrpcProperties grpc = new GrpcProperties();

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
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

    public Boolean getPrettyPrint() {
        return prettyPrint;
    }

    public void setPrettyPrint(Boolean prettyPrint) {
        this.prettyPrint = prettyPrint;
    }

    public Boolean getPublishReload() {
        return publishReload;
    }

    public void setPublishReload(Boolean publishReload) {
        this.publishReload = publishReload;
    }

    public Boolean getAllowAnonymous() {
        return allowAnonymous;
    }

    public void setAllowAnonymous(Boolean allowAnonymous) {
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

    public Boolean getIncludeDetailedErrorMessage() {
        return includeDetailedErrorMessage;
    }

    public void setIncludeDetailedErrorMessage(Boolean includeDetailedErrorMessage) {
        this.includeDetailedErrorMessage = includeDetailedErrorMessage;
    }

    public Boolean getIncludeErrorLocationInErrorMessage() {
        return includeErrorLocationInErrorMessage;
    }

    public void setIncludeErrorLocationInErrorMessage(Boolean includeErrorLocationInErrorMessage) {
        this.includeErrorLocationInErrorMessage = includeErrorLocationInErrorMessage;
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

    public Boolean getIncludeResponseTimes() {
        return includeResponseTimes;
    }

    public void setIncludeResponseTimes(Boolean includeResponseTimes) {
        this.includeResponseTimes = includeResponseTimes;
    }

    public Boolean getRegisterServiceDiscovery() {
        return registerServiceDiscovery;
    }

    public void setRegisterServiceDiscovery(Boolean registerServiceDiscovery) {
        this.registerServiceDiscovery = registerServiceDiscovery;
    }

    public ServiceDiscoveryInfo getDiscovery() {
        return discovery;
    }

    public Boolean getIgnoreUnknownArgs() {
        return ignoreUnknownArgs;
    }

    public void setIgnoreUnknownArgs(Boolean ignoreUnknownArgs) {
        this.ignoreUnknownArgs = ignoreUnknownArgs;
    }

    public Boolean getCopyHttpRequestHeaders() {
        return copyHttpRequestHeaders;
    }

    public void setCopyHttpRequestHeaders(Boolean copyHttpRequestHeaders) {
        this.copyHttpRequestHeaders = copyHttpRequestHeaders;
    }

    public Boolean getCorsEnabled() {
        return corsEnabled;
    }

    public void setCorsEnabled(Boolean corsEnabled) {
        this.corsEnabled = corsEnabled;
    }

    public Boolean getOpenApiDocsForGetVerbOperations() {
        return openApiDocsForGetVerbOperations;
    }

    public void setOpenApiDocsForGetVerbOperations(Boolean openApiDocsForGetVerbOperations) {
        this.openApiDocsForGetVerbOperations = openApiDocsForGetVerbOperations;
    }

    public String getOpenApiOperationIdSuffixForGetVerbOperations() {
        return openApiOperationIdSuffixForGetVerbOperations;
    }

    public void setOpenApiOperationIdSuffixForGetVerbOperations(String openApiOperationIdSuffixForGetVerbOperations) {
        this.openApiOperationIdSuffixForGetVerbOperations = openApiOperationIdSuffixForGetVerbOperations;
    }

    public String getOpenApiDocsForEndpoints() {
        return openApiDocsForEndpoints;
    }

    public void setOpenApiDocsForEndpoints(String openApiDocsForEndpoints) {
        this.openApiDocsForEndpoints = openApiDocsForEndpoints;
    }

    public GrpcProperties getGrpc() {
        return grpc;
    }

    public static class GrpcProperties {

        private Boolean autoStart;

        private Integer port;

        public Boolean getAutoStart() {
            return autoStart;
        }

        public void setAutoStart(Boolean autoStart) {
            this.autoStart = autoStart;
        }

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }
}
