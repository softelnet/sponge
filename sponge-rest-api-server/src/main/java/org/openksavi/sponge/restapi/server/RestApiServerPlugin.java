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
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.apache.camel.CamelContext;
import org.apache.camel.CamelContextAware;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.spi.Registry;
import org.apache.camel.support.DefaultRegistry;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.camel.CamelPlugin;
import org.openksavi.sponge.camel.CamelUtils;
import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JPlugin;
import org.openksavi.sponge.restapi.server.discovery.ServiceDiscoveryInfo;
import org.openksavi.sponge.restapi.server.discovery.ServiceDiscoveryRegistry;
import org.openksavi.sponge.restapi.server.security.JwtRestApiAuthTokenService;
import org.openksavi.sponge.restapi.server.security.NoSecuritySecurityService;
import org.openksavi.sponge.restapi.server.security.RestApiAuthTokenService;
import org.openksavi.sponge.restapi.server.security.RestApiSecurityService;
import org.openksavi.sponge.restapi.server.security.UserContext;
import org.openksavi.sponge.restapi.server.util.RestApiServerUtils;

/**
 * Sponge REST API server plugin.
 */
public class RestApiServerPlugin extends JPlugin implements CamelContextAware {

    private static final Logger logger = LoggerFactory.getLogger(RestApiServerPlugin.class);

    public static final String NAME = "restApiServer";

    private static final Supplier<RestApiService> DEFAULT_API_SERVICE_PROVIDER = () -> new DefaultRestApiService();

    private static final Supplier<RestApiSecurityService> DEFAULT_SECURITY_SERVICE_PROVIDER = () -> new NoSecuritySecurityService();

    private static final Supplier<RestApiAuthTokenService> DEFAULT_AUTH_TOKEN_SERVICE_PROVIDER = () -> new JwtRestApiAuthTokenService();

    private static final Supplier<RestApiRouteBuilder> DEFAULT_ROUTE_BUILDER_PROVIDER = () -> new RestApiRouteBuilder();

    private RestApiSettings settings = new RestApiSettings();

    /** If {@code true} then the REST service will start when the plugin starts up. The default value is {@code true}. */
    private boolean autoStart = RestApiServerConstants.DEFAULT_AUTO_START;

    private AtomicBoolean started = new AtomicBoolean(false);

    private RestApiRouteBuilder routeBuilder;

    private RestApiService service;

    private RestApiSecurityService securityService;

    private RestApiAuthTokenService authTokenService;

    private CamelContext camelContext;

    private ServiceDiscoveryRegistry discoveryRegistry;

    private Lock lock = new ReentrantLock(true);

    public RestApiServerPlugin() {
        this(NAME);
    }

    public RestApiServerPlugin(String name) {
        super(name);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        settings.setRestComponentId(configuration.getString(RestApiServerConstants.TAG_REST_COMPONENT_ID, settings.getRestComponentId()));
        settings.setHost(configuration.getString(RestApiServerConstants.TAG_HOST, settings.getHost()));
        settings.setPort(configuration.getInteger(RestApiServerConstants.TAG_PORT, settings.getPort()));

        String path = configuration.getString(RestApiServerConstants.TAG_PATH, null);
        if (path != null) {
            settings.setPath(path);
        }

        settings.setName(configuration.getString(RestApiServerConstants.TAG_NAME, settings.getName()));

        settings.setPrettyPrint(configuration.getBoolean(RestApiServerConstants.TAG_PRETTY_PRINT, settings.isPrettyPrint()));

        String publicActionsSpec = configuration.getString(RestApiServerConstants.TAG_PUBLIC_ACTIONS, null);
        if (publicActionsSpec != null) {
            settings.setPublicActions(SpongeUtils.getProcessorQualifiedNameList(publicActionsSpec));
        }

        String publicEvents = configuration.getString(RestApiServerConstants.TAG_PUBLIC_EVENTS, null);
        if (publicEvents != null) {
            settings.setPublicEvents(SpongeUtils.getNameList(publicEvents));
        }

        if (configuration.hasChildConfiguration(RestApiServerConstants.TAG_SSL_CONFIGURATION)) {
            settings.setSslConfiguration(
                    SpongeUtils.createSslConfiguration(configuration.getChildConfiguration(RestApiServerConstants.TAG_SSL_CONFIGURATION)));
        }

        settings.setPublishReload(configuration.getBoolean(RestApiServerConstants.TAG_PUBLISH_RELOAD, settings.isPublishReload()));
        settings.setAllowAnonymous(configuration.getBoolean(RestApiServerConstants.TAG_ALLOW_ANONYMOUS, settings.isAllowAnonymous()));

        autoStart = configuration.getBoolean(RestApiServerConstants.TAG_AUTO_START, isAutoStart());

        String routeBuilderClass = configuration.getString(RestApiServerConstants.TAG_ROUTE_BUILDER_CLASS, null);
        if (routeBuilderClass != null) {
            routeBuilder = SpongeUtils.createInstance(routeBuilderClass, RestApiRouteBuilder.class);
        }

        String apiServiceClass = configuration.getString(RestApiServerConstants.TAG_API_SERVICE_CLASS, null);
        if (apiServiceClass != null) {
            service = SpongeUtils.createInstance(apiServiceClass, RestApiService.class);
        }

        String securityServiceClass = configuration.getString(RestApiServerConstants.TAG_SECURITY_SERVICE_CLASS, null);
        if (securityServiceClass != null) {
            securityService = SpongeUtils.createInstance(securityServiceClass, RestApiSecurityService.class);
        }

        String authTokenServiceClass = configuration.getString(RestApiServerConstants.TAG_AUTH_TOKEN_SERVICE_CLASS, null);
        if (authTokenServiceClass != null) {
            authTokenService = SpongeUtils.createInstance(authTokenServiceClass, RestApiAuthTokenService.class);
        }

        Long authTokenExpirationDurationSeconds =
                configuration.getLong(RestApiServerConstants.TAG_AUTH_TOKEN_EXPIRATION_DURATION_SECONDS, null);
        if (authTokenExpirationDurationSeconds != null) {
            settings.setAuthTokenExpirationDuration(Duration.ofSeconds(authTokenExpirationDurationSeconds));
        }

        settings.setIncludeResponseTimes(
                configuration.getBoolean(RestApiServerConstants.TAG_INCLUDE_RESPONSE_TIMES, settings.isIncludeResponseTimes()));

        Boolean registerServiceDiscovery = configuration.getBoolean(RestApiServerConstants.TAG_REGISTER_SERVICE_DISCOVERY, null);
        if (registerServiceDiscovery != null) {
            settings.setRegisterServiceDiscovery(registerServiceDiscovery);
        }

        String serviceDiscoveryUrl = configuration.getString(RestApiServerConstants.TAG_SERVICE_DISCOVERY_URL, null);
        if (serviceDiscoveryUrl != null) {
            settings.setServiceDiscoveryInfo(new ServiceDiscoveryInfo(serviceDiscoveryUrl));

            // Assume that the service should be registered.
            if (registerServiceDiscovery == null) {
                settings.setRegisterServiceDiscovery(true);
            }
        }
    }

    protected void refreshSettings() {
        refreshBaseSettings();
        refreshServiceDiscoverySettings();

        logger.debug("Using settings: {}", settings);
    }

    protected void refreshBaseSettings() {
        String propHost = getEngine().getConfigurationManager().getProperty(RestApiServerConstants.PROP_HOST);
        if (propHost != null) {
            settings.setHost(propHost.trim());
        }

        String propPortString = getEngine().getConfigurationManager().getProperty(RestApiServerConstants.PROP_PORT);
        if (propPortString != null) {
            settings.setPort(Integer.valueOf(propPortString.trim()));
        }

        String propPath = getEngine().getConfigurationManager().getProperty(RestApiServerConstants.PROP_PATH);
        if (propPath != null) {
            settings.setPath(propPath.trim());
        }

        String propName = getEngine().getConfigurationManager().getProperty(RestApiServerConstants.PROP_NAME);
        if (propName != null) {
            settings.setName(propName.trim());
        }
    }

    protected void refreshServiceDiscoverySettings() {
        String propRegisterServiceDiscoveryString =
                getEngine().getConfigurationManager().getProperty(RestApiServerConstants.PROP_REGISTER_SERVICE_DISCOVERY);
        if (propRegisterServiceDiscoveryString != null) {
            settings.setRegisterServiceDiscovery(Boolean.valueOf(propRegisterServiceDiscoveryString.trim()));
        }

        String propServiceDiscoveryUrl =
                getEngine().getConfigurationManager().getProperty(RestApiServerConstants.PROP_SERVICE_DISCOVERY_URL);

        if (propServiceDiscoveryUrl != null) {
            if (settings.getServiceDiscoveryInfo() == null) {
                settings.setServiceDiscoveryInfo(new ServiceDiscoveryInfo());
            }

            settings.getServiceDiscoveryInfo().setUrl(propServiceDiscoveryUrl);

            // Assume that the service should be registered.
            if (propRegisterServiceDiscoveryString == null) {
                settings.setRegisterServiceDiscovery(true);
            }
        }
    }

    @Override
    public void onStartup() {
        if (isAutoStart()) {
            start();
        }
    }

    @Override
    public void onShutdown() {
        if (discoveryRegistry != null) {
            try {
                discoveryRegistry.unregister();
            } catch (Exception e) {
                logger.warn("Error unregistering the service", e);
            }
        }
    }

    public RestApiSettings getSettings() {
        return settings;
    }

    public void start() {
        CamelContext finalCamelContext = camelContext;
        if (finalCamelContext == null) {
            Validate.isTrue(getEngine().getOperations().hasPlugin(CamelPlugin.class),
                    "The Camel plugin is not registered but it is required by the Sponge REST API if no Camel context is set");

            finalCamelContext = getEngine().getOperations().getPlugin(CamelPlugin.class).getCamelContext();
        }

        start(finalCamelContext);
    }

    public void start(CamelContext camelContext) {
        if (camelContext == null) {
            throw new ConfigException("Camel context is not available");
        }

        lock.lock();

        try {
            if (!started.get()) {
                try {
                    refreshSettings();

                    if (settings.getSslConfiguration() != null && settings.getSslContextParametersBeanName() != null) {
                        setupSecurity(camelContext);
                    }

                    if (service == null) {
                        // Create a default.
                        service = DEFAULT_API_SERVICE_PROVIDER.get();
                    }
                    service.setSettings(settings);
                    service.setEngine(getEngine());

                    if (securityService == null) {
                        // Create a default.
                        securityService = DEFAULT_SECURITY_SERVICE_PROVIDER.get();
                    }

                    securityService.setRestApiService(service);
                    service.setSecurityService(securityService);

                    if (authTokenService == null) {
                        // Create a default.
                        authTokenService = DEFAULT_AUTH_TOKEN_SERVICE_PROVIDER.get();
                    }
                    authTokenService.setRestApiService(service);
                    service.setAuthTokenService(authTokenService);

                    if (routeBuilder == null) {
                        // Create a default.
                        routeBuilder = DEFAULT_ROUTE_BUILDER_PROVIDER.get();
                    }
                    routeBuilder.setRestApiService(service);

                    // Init services.
                    securityService.init();
                    authTokenService.init();
                    service.init();

                    camelContext.addRoutes(routeBuilder);

                    if (settings.isRegisterServiceDiscovery()) {
                        discoveryRegistry = new ServiceDiscoveryRegistry(getEngine(), settings);
                        try {
                            discoveryRegistry.register();
                        } catch (Exception e) {
                            logger.warn("Error registering the service", e);
                        }
                    }
                } catch (Exception e) {
                    throw SpongeUtils.wrapException(e);
                }

                started.set(true);
            }
        } finally {
            lock.unlock();
        }
    }

    protected void setupSecurity(CamelContext camelContext) {
        Registry registry = new DefaultRegistry();
        registry.bind(settings.getSslContextParametersBeanName(), CamelUtils.createSslContextParameters(settings.getSslConfiguration()));

        // TODO Handle many invocations of this method that cause growing of the registry list.
        ((DefaultCamelContext) camelContext).setRegistry(new DefaultRegistry(Arrays.asList(registry, camelContext.getRegistry())));
    }

    public RestApiSession getSession() {
        return service.getSession();
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public RestApiRouteBuilder getRouteBuilder() {
        return routeBuilder;
    }

    public void setRouteBuilder(RestApiRouteBuilder routeBuilder) {
        this.routeBuilder = routeBuilder;
    }

    public RestApiService getService() {
        return service;
    }

    public void setService(RestApiService service) {
        this.service = service;
    }

    public RestApiSecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(RestApiSecurityService securityService) {
        this.securityService = securityService;
    }

    public RestApiAuthTokenService getAuthTokenService() {
        return authTokenService;
    }

    public void setAuthTokenService(RestApiAuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    public boolean canAccessResource(Map<String, Collection<String>> roleToResources, UserContext userContext, String resourceName) {
        return RestApiServerUtils.canAccessResource(roleToResources, userContext, resourceName);
    }

    @Override
    public CamelContext getCamelContext() {
        return camelContext;
    }

    @Override
    public void setCamelContext(CamelContext camelContext) {
        this.camelContext = camelContext;
    }

    public ServiceDiscoveryRegistry getDiscoveryRegistry() {
        return discoveryRegistry;
    }
}
