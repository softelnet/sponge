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

package org.openksavi.sponge.restapi;

import java.util.List;

import org.openksavi.sponge.ProcessorQualifiedName;
import org.openksavi.sponge.core.util.SecurityConfiguration;

/**
 * REST API settings.
 */
public class RestApiSettings {

    private String restComponentId = RestApiConstants.DEFAULT_REST_COMPONENT_ID;

    private String host;

    private int port = RestApiConstants.DEFAULT_PORT;

    private int version = RestApiConstants.API_VERSION;

    private boolean prettyPrint = true;

    private List<ProcessorQualifiedName> publicActions;

    private List<String> publicEvents;

    private String sslContextParametersBeanName = RestApiConstants.DEFAULT_SSL_CONTEXT_PARAMETERS_BEAN_NAME;

    private SecurityConfiguration security;

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getVersion() {
        return version;
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

    public SecurityConfiguration getSecurity() {
        return security;
    }

    public void setSecurity(SecurityConfiguration security) {
        this.security = security;
    }
}
