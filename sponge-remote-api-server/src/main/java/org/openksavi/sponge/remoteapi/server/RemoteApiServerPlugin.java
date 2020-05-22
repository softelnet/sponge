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
import org.openksavi.sponge.remoteapi.server.discovery.ServiceDiscoveryInfo;
import org.openksavi.sponge.remoteapi.server.discovery.ServiceDiscoveryRegistry;
import org.openksavi.sponge.remoteapi.server.security.AccessService;
import org.openksavi.sponge.remoteapi.server.security.AuthTokenService;
import org.openksavi.sponge.remoteapi.server.security.JwtAuthTokenService;
import org.openksavi.sponge.remoteapi.server.security.RequestAuthenticationService;
import org.openksavi.sponge.remoteapi.server.security.SecurityProvider;
import org.openksavi.sponge.remoteapi.server.security.SecurityService;
import org.openksavi.sponge.remoteapi.server.security.UserContext;
import org.openksavi.sponge.remoteapi.server.util.RemoteApiServerUtils;

/**
 * Sponge Remote API server plugin.
 */
public class RemoteApiServerPlugin extends JPlugin implements CamelContextAware {

    private static final Logger logger = LoggerFactory.getLogger(RemoteApiServerPlugin.class);

    public static final String NAME = "remoteApiServer";

    private static final Supplier<RemoteApiService> DEFAULT_API_SERVICE_PROVIDER = () -> new DefaultRemoteApiService();

    private static final Supplier<AuthTokenService> DEFAULT_AUTH_TOKEN_SERVICE_PROVIDER = () -> new JwtAuthTokenService();

    private static final Supplier<RemoteApiRouteBuilder> DEFAULT_ROUTE_BUILDER_PROVIDER = () -> new RemoteApiRouteBuilder();

    private RemoteApiSettings settings = new RemoteApiSettings();

    /** If {@code true} then the Remote API service will start when the plugin starts up. The default value is {@code true}. */
    private boolean autoStart = RemoteApiServerConstants.DEFAULT_AUTO_START;

    private AtomicBoolean started = new AtomicBoolean(false);

    private RemoteApiRouteBuilder routeBuilder;

    private RemoteApiService service;

    private SecurityProvider securityProvider;

    private SecurityService securityService;

    private RequestAuthenticationService requestAuthenticationService;

    private AccessService accessService;

    private AuthTokenService authTokenService;

    private CamelContext camelContext;

    private ServiceDiscoveryRegistry discoveryRegistry;

    private Lock lock = new ReentrantLock(true);

    public RemoteApiServerPlugin() {
        this(NAME);
    }

    public RemoteApiServerPlugin(String name) {
        super(name);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        settings.setComponentId(configuration.getString(RemoteApiServerConstants.TAG_COMPONENT_ID, settings.getComponentId()));
        settings.setHost(configuration.getString(RemoteApiServerConstants.TAG_HOST, settings.getHost()));
        settings.setPort(configuration.getInteger(RemoteApiServerConstants.TAG_PORT, settings.getPort()));

        String path = configuration.getString(RemoteApiServerConstants.TAG_PATH, null);
        if (path != null) {
            settings.setPath(path);
        }

        settings.setName(configuration.getString(RemoteApiServerConstants.TAG_NAME, settings.getName()));
        settings.setDescription(configuration.getString(RemoteApiServerConstants.TAG_DESCRIPTION, settings.getDescription()));
        settings.setVersion(configuration.getString(RemoteApiServerConstants.TAG_VERSION, settings.getVersion()));
        settings.setLicense(configuration.getString(RemoteApiServerConstants.TAG_LICENSE, settings.getLicense()));

        settings.setPrettyPrint(configuration.getBoolean(RemoteApiServerConstants.TAG_PRETTY_PRINT, settings.isPrettyPrint()));

        String publicActionsSpec = configuration.getString(RemoteApiServerConstants.TAG_PUBLIC_ACTIONS, null);
        if (publicActionsSpec != null) {
            settings.setPublicActions(SpongeUtils.getProcessorQualifiedNameList(publicActionsSpec));
        }

        String publicEvents = configuration.getString(RemoteApiServerConstants.TAG_PUBLIC_EVENTS, null);
        if (publicEvents != null) {
            settings.setPublicEvents(SpongeUtils.getNameList(publicEvents));
        }

        if (configuration.hasChildConfiguration(RemoteApiServerConstants.TAG_SSL_CONFIGURATION)) {
            settings.setSslConfiguration(SpongeUtils
                    .createSslConfiguration(configuration.getChildConfiguration(RemoteApiServerConstants.TAG_SSL_CONFIGURATION)));
        }

        settings.setPublishReload(configuration.getBoolean(RemoteApiServerConstants.TAG_PUBLISH_RELOAD, settings.isPublishReload()));
        settings.setAllowAnonymous(configuration.getBoolean(RemoteApiServerConstants.TAG_ALLOW_ANONYMOUS, settings.isAllowAnonymous()));

        autoStart = configuration.getBoolean(RemoteApiServerConstants.TAG_AUTO_START, isAutoStart());

        String routeBuilderClass = configuration.getString(RemoteApiServerConstants.TAG_ROUTE_BUILDER_CLASS, null);
        if (routeBuilderClass != null) {
            routeBuilder = SpongeUtils.createInstance(routeBuilderClass, RemoteApiRouteBuilder.class);
        }

        String apiServiceClass = configuration.getString(RemoteApiServerConstants.TAG_API_SERVICE_CLASS, null);
        if (apiServiceClass != null) {
            service = SpongeUtils.createInstance(apiServiceClass, RemoteApiService.class);
        }

        String securityProviderClass = configuration.getString(RemoteApiServerConstants.TAG_SECURITY_PROVIDER_CLASS, null);
        if (securityProviderClass != null) {
            securityProvider = SpongeUtils.createInstance(securityProviderClass, SecurityProvider.class);
        }

        String authTokenServiceClass = configuration.getString(RemoteApiServerConstants.TAG_AUTH_TOKEN_SERVICE_CLASS, null);
        if (authTokenServiceClass != null) {
            authTokenService = SpongeUtils.createInstance(authTokenServiceClass, AuthTokenService.class);
        }

        Long authTokenExpirationDurationSeconds =
                configuration.getLong(RemoteApiServerConstants.TAG_AUTH_TOKEN_EXPIRATION_DURATION_SECONDS, null);
        if (authTokenExpirationDurationSeconds != null) {
            settings.setAuthTokenExpirationDuration(Duration.ofSeconds(authTokenExpirationDurationSeconds));
        }

        settings.setIncludeResponseTimes(
                configuration.getBoolean(RemoteApiServerConstants.TAG_INCLUDE_RESPONSE_TIMES, settings.isIncludeResponseTimes()));

        Boolean registerServiceDiscovery = configuration.getBoolean(RemoteApiServerConstants.TAG_REGISTER_SERVICE_DISCOVERY, null);
        if (registerServiceDiscovery != null) {
            settings.setRegisterServiceDiscovery(registerServiceDiscovery);
        }

        String serviceDiscoveryUrl = configuration.getString(RemoteApiServerConstants.TAG_SERVICE_DISCOVERY_URL, null);
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
        String propHost = getEngine().getConfigurationManager().getProperty(RemoteApiServerConstants.PROP_HOST);
        if (propHost != null) {
            settings.setHost(propHost.trim());
        }

        String propPortString = getEngine().getConfigurationManager().getProperty(RemoteApiServerConstants.PROP_PORT);
        if (propPortString != null) {
            settings.setPort(Integer.valueOf(propPortString.trim()));
        }

        String propPath = getEngine().getConfigurationManager().getProperty(RemoteApiServerConstants.PROP_PATH);
        if (propPath != null) {
            settings.setPath(propPath.trim());
        }

        String propName = getEngine().getConfigurationManager().getProperty(RemoteApiServerConstants.PROP_NAME);
        if (propName != null) {
            settings.setName(propName.trim());
        }

        String propVersion = getEngine().getConfigurationManager().getProperty(RemoteApiServerConstants.PROP_VERSION);
        if (propVersion != null) {
            settings.setVersion(propVersion.trim());
        }
    }

    protected void refreshServiceDiscoverySettings() {
        String propRegisterServiceDiscoveryString =
                getEngine().getConfigurationManager().getProperty(RemoteApiServerConstants.PROP_REGISTER_SERVICE_DISCOVERY);
        if (propRegisterServiceDiscoveryString != null) {
            settings.setRegisterServiceDiscovery(Boolean.valueOf(propRegisterServiceDiscoveryString.trim()));
        }

        String propServiceDiscoveryUrl =
                getEngine().getConfigurationManager().getProperty(RemoteApiServerConstants.PROP_SERVICE_DISCOVERY_URL);

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
        stop();
    }

    public RemoteApiSettings getSettings() {
        return settings;
    }

    public void start() {
        CamelContext finalCamelContext = camelContext;
        if (finalCamelContext == null) {
            Validate.isTrue(getEngine().getOperations().hasPlugin(CamelPlugin.class),
                    "The Camel plugin is not registered but it is required by the Sponge Remote API if no Camel context is set");

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
                        securityService =
                                Validate.notNull(securityProvider, "Can't create a security service. The security provider is not set")
                                        .createSecurityService();
                    }

                    securityService.setRemoteApiService(service);
                    service.setSecurityService(securityService);

                    if (requestAuthenticationService == null) {
                        // Create a default.
                        requestAuthenticationService = Validate
                                .notNull(securityProvider,
                                        "Can't create a request authentication service. The security provider is not set")
                                .createRequestAuthenticationService();
                    }

                    requestAuthenticationService.setRemoteApiService(service);
                    service.setRequestAuthenticationService(requestAuthenticationService);

                    if (accessService == null) {
                        // Create a default.
                        accessService =
                                Validate.notNull(securityProvider, "Can't create an access service. The security provider is not set")
                                        .createAccessService();
                    }

                    accessService.setRemoteApiService(service);
                    service.setAccessService(accessService);

                    if (authTokenService == null) {
                        // Create a default.
                        authTokenService = DEFAULT_AUTH_TOKEN_SERVICE_PROVIDER.get();
                    }
                    authTokenService.setRemoteApiService(service);
                    service.setAuthTokenService(authTokenService);

                    if (routeBuilder == null) {
                        // Create a default.
                        routeBuilder = DEFAULT_ROUTE_BUILDER_PROVIDER.get();
                    }
                    routeBuilder.setRemoteApiService(service);

                    // Init services.
                    securityService.init();
                    requestAuthenticationService.init();
                    accessService.init();
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

    public void stop() {
        lock.lock();

        try {
            if (started.get()) {
                if (discoveryRegistry != null) {
                    try {
                        discoveryRegistry.unregister();
                    } catch (Exception e) {
                        logger.warn("Error unregistering the service", e);
                    }
                }

                // Dispose services.
                service.dispose();
                authTokenService.dispose();
                accessService.dispose();
                requestAuthenticationService.dispose();
                securityService.dispose();

                started.set(false);
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

    public RemoteApiSession getSession() {
        return service.getSession();
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public RemoteApiRouteBuilder getRouteBuilder() {
        return routeBuilder;
    }

    public void setRouteBuilder(RemoteApiRouteBuilder routeBuilder) {
        this.routeBuilder = routeBuilder;
    }

    public RemoteApiService getService() {
        return service;
    }

    public void setService(RemoteApiService service) {
        this.service = service;
    }

    public SecurityProvider getSecurityProvider() {
        return securityProvider;
    }

    public void setSecurityProvider(SecurityProvider securityProvider) {
        this.securityProvider = securityProvider;
    }

    public SecurityService getSecurityService() {
        return securityService;
    }

    public void setSecurityService(SecurityService securityService) {
        this.securityService = securityService;
    }

    public RequestAuthenticationService getRequestAuthenticationService() {
        return requestAuthenticationService;
    }

    public void setRequestAuthenticationService(RequestAuthenticationService requestAuthenticationService) {
        this.requestAuthenticationService = requestAuthenticationService;
    }

    public AccessService getAccessService() {
        return accessService;
    }

    public void setAccessService(AccessService accessService) {
        this.accessService = accessService;
    }

    public AuthTokenService getAuthTokenService() {
        return authTokenService;
    }

    public void setAuthTokenService(AuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    public boolean canAccessResource(Map<String, Collection<String>> roleToResources, UserContext userContext, String resourceName) {
        return RemoteApiServerUtils.canAccessResource(roleToResources, userContext, resourceName);
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
