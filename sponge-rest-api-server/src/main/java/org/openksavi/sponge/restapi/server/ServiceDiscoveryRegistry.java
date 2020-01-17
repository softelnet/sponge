/*
 * Copyright 2016-2020 The Sponge authors.
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

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;

/**
 * A REST API service discovery registry manager.
 */
public class ServiceDiscoveryRegistry {

    private static final Logger logger = LoggerFactory.getLogger(ServiceDiscoveryRegistry.class);

    private SpongeEngine engine;

    private RestApiSettings settings;

    private JmDNS jmDns;

    private ServiceInfo serviceInfo;

    public ServiceDiscoveryRegistry(SpongeEngine engine, RestApiSettings settings) {
        this.engine = engine;
        this.settings = settings;
    }

    public SpongeEngine getEngine() {
        return engine;
    }

    public void setEngine(SpongeEngine engine) {
        this.engine = engine;
    }

    public RestApiSettings getSettings() {
        return settings;
    }

    public void setSettings(RestApiSettings settings) {
        this.settings = settings;
    }

    public void register() {
        String serviceName = null;
        String serviceUrl = null;

        try {
            InetAddress localHost = InetAddress.getLocalHost();

            Integer port = getSettings().getPort();
            boolean isDefaultRestComponent =
                    Objects.equals(getSettings().getRestComponentId(), RestApiServerConstants.DEFAULT_REST_COMPONENT_ID);

            // Use defaults.
            if (port != null && isDefaultRestComponent) {
                StringBuilder sb = new StringBuilder(String.format("%s://%s:%d",
                        getSettings().getSslConfiguration() != null ? "https" : "http", localHost.getCanonicalHostName(), port));
                if (getSettings().getPath() != null) {
                    sb.append("/" + getSettings().getPath());
                }

                serviceName = getEngine().getLabel() != null ? getEngine().getLabel()
                        : (getEngine().getName() != null ? getEngine().getName() : "Sponge");
                serviceUrl = sb.toString();
            } else {
                // TODO Use manual serviceName, serviceUrl if configured.
            }

            if (serviceName != null && serviceUrl != null) {
                Map<String, Object> properties = new LinkedHashMap<>();

                properties.put(RestApiConstants.SERVICE_DISCOVERY_PROPERTY_UUID, getEngine().getUuid());
                properties.put(RestApiConstants.SERVICE_DISCOVERY_PROPERTY_NAME, serviceName);
                properties.put(RestApiConstants.SERVICE_DISCOVERY_PROPERTY_URL, serviceUrl);

                jmDns = JmDNS.create(localHost);

                serviceInfo = ServiceInfo.create(RestApiConstants.SERVICE_DISCOVERY_TYPE + ".local.", serviceName, port, 0, 0, properties);

                logger.info("Registering service as '{}' at {}", serviceName, serviceUrl);

                jmDns.registerService(serviceInfo);
            }
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    public void unregister() {
        if (jmDns != null && serviceInfo != null) {
            // Unregister the service.
            jmDns.unregisterService(serviceInfo);
        }
    }

    public JmDNS getJmDns() {
        return jmDns;
    }

    public ServiceInfo getServiceInfo() {
        return serviceInfo;
    }
}
