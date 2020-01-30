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

package org.openksavi.sponge.restapi.server.discovery;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.server.RestApiServerConstants;
import org.openksavi.sponge.restapi.server.RestApiSettings;

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

    protected String resolveServiceName() {
        if (settings.getName() != null) {
            return settings.getName();
        }

        if (getEngine().getLabel() != null) {
            return getEngine().getLabel();
        }

        if (getEngine().getName() != null) {
            return getEngine().getName();
        }

        return RestApiServerConstants.DEFAULT_NAME;
    }

    protected String createDefaultServiceUrl(InetAddress localHost, Integer port) {
        if (port == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(String.format("%s://%s:%d", settings.getSslConfiguration() != null ? "https" : "http",
                localHost.getCanonicalHostName(), port));
        if (settings.getPath() != null) {
            sb.append("/" + settings.getPath());
        }

        return sb.toString();
    }

    public void register() {
        try {
            InetAddress localHost = InetAddress.getLocalHost();

            Integer port = getSettings().getPort();
            ServiceDiscoveryInfo serviceDiscoveryInfo = settings.getServiceDiscoveryInfo();

            String serviceName = resolveServiceName();
            String serviceUrl = serviceDiscoveryInfo != null && serviceDiscoveryInfo.getUrl() != null ? serviceDiscoveryInfo.getUrl()
                    : createDefaultServiceUrl(localHost, port);

            if (serviceUrl != null) {
                Map<String, Object> properties = new LinkedHashMap<>();

                properties.put(RestApiConstants.SERVICE_DISCOVERY_PROPERTY_UUID, getEngine().getUuid());
                properties.put(RestApiConstants.SERVICE_DISCOVERY_PROPERTY_NAME, serviceName);
                properties.put(RestApiConstants.SERVICE_DISCOVERY_PROPERTY_URL, serviceUrl);

                jmDns = JmDNS.create(localHost);

                String type = RestApiConstants.SERVICE_DISCOVERY_TYPE + ".local.";
                serviceInfo = ServiceInfo.create(type, serviceName, port != null ? port : 0, 0, 0, properties);

                logger.info("Registering service '{}' with URL {} as type {}", serviceName, serviceUrl, type);

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
