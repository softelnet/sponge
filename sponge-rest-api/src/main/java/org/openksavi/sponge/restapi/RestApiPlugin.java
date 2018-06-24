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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.apache.camel.CamelContext;
import org.apache.camel.impl.CompositeRegistry;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

import org.openksavi.sponge.camel.CamelPlugin;
import org.openksavi.sponge.camel.CamelUtils;
import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JPlugin;
import org.openksavi.sponge.restapi.security.NoSecuritySecurityService;
import org.openksavi.sponge.restapi.security.RestApiSecurityService;
import org.openksavi.sponge.restapi.security.User;

/**
 * Sponge REST API plugin.
 */
public class RestApiPlugin extends JPlugin {

    public static final String NAME = "restApi";

    private static final Supplier<RestApiService> DEFAULT_API_SERVICE_PROVIDER = () -> new DefaultRestApiService();

    private static final Supplier<RestApiSecurityService> DEFAULT_SECURITY_SERVICE_PROVIDER = () -> new NoSecuritySecurityService();

    private static final Supplier<RestApiRouteBuilder> DEFAULT_ROUTE_BUILDER_PROVIDER = () -> new RestApiRouteBuilder();

    private RestApiSettings settings = new RestApiSettings();

    /** If {@code true} then the REST service will start when the plugin starts up. The default value is {@code true}. */
    private boolean autoStart = RestApiConstants.DEFAULT_AUTO_START;

    private AtomicBoolean started = new AtomicBoolean(false);

    private RestApiRouteBuilder routeBuilder;

    private RestApiService service;

    private RestApiSecurityService securityService;

    private Lock lock = new ReentrantLock(true);

    public RestApiPlugin() {
        setName(NAME);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        settings.setRestComponentId(configuration.getString(RestApiConstants.TAG_REST_COMPONENT_ID, settings.getRestComponentId()));
        settings.setHost(configuration.getString(RestApiConstants.TAG_HOST, settings.getHost()));
        settings.setPort(configuration.getInteger(RestApiConstants.TAG_PORT, settings.getPort()));
        settings.setPrettyPrint(configuration.getBoolean(RestApiConstants.TAG_PRETTY_PRINT, settings.isPrettyPrint()));

        String publicActionsSpec = configuration.getString(RestApiConstants.TAG_PUBLIC_ACTIONS, null);
        if (publicActionsSpec != null) {
            settings.setPublicActions(SpongeUtils.getProcessorQualifiedNameList(publicActionsSpec));
        }

        String publicEvents = configuration.getString(RestApiConstants.TAG_PUBLIC_EVENTS, null);
        if (publicEvents != null) {
            settings.setPublicEvents(SpongeUtils.getNameList(publicEvents));
        }

        if (configuration.hasChildConfiguration(RestApiConstants.TAG_SSL_CONFIGURATION)) {
            settings.setSslConfiguration(
                    SpongeUtils.createSecurityConfiguration(configuration.getChildConfiguration(RestApiConstants.TAG_SSL_CONFIGURATION)));
        }

        settings.setPublishReload(configuration.getBoolean(RestApiConstants.TAG_PUBLISH_RELOAD, settings.isPublishReload()));
        settings.setAllowAnonymous(configuration.getBoolean(RestApiConstants.TAG_ALLOW_ANONYMOUS, settings.isAllowAnonymous()));

        autoStart = configuration.getBoolean(RestApiConstants.TAG_AUTO_START, isAutoStart());

        String routeBuilderClass = configuration.getString(RestApiConstants.TAG_ROUTE_BUILDER_CLASS, null);
        if (routeBuilderClass != null) {
            routeBuilder = SpongeUtils.createInstance(routeBuilderClass, RestApiRouteBuilder.class);
        }

        String apiServiceClass = configuration.getString(RestApiConstants.TAG_API_SERVICE_CLASS, null);
        if (apiServiceClass != null) {
            service = SpongeUtils.createInstance(apiServiceClass, RestApiService.class);
        }

        String securityServiceClass = configuration.getString(RestApiConstants.TAG_SECURITY_SERVICE_CLASS, null);
        if (securityServiceClass != null) {
            securityService = SpongeUtils.createInstance(securityServiceClass, RestApiSecurityService.class);
        }
    }

    @Override
    public void onStartup() {
        if (isAutoStart()) {
            start();
        }
    }

    public RestApiPlugin(String name) {
        super(name);
    }

    public RestApiSettings getSettings() {
        return settings;
    }

    public void start() {
        CamelPlugin camelPlugin = getEngine().getPluginManager().getPlugin(CamelPlugin.class);
        if (camelPlugin == null) {
            throw new ConfigException("Camel plugin is not registered but it is required by the Sponge REST API");
        }

        start(camelPlugin.getCamelContext());
    }

    public void start(CamelContext camelContext) {
        if (camelContext == null) {
            throw new ConfigException("Camel context is not available");
        }

        lock.lock();

        try {
            if (!started.get()) {
                try {
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

                    securityService.setEngine(getEngine());
                    service.setSecurityService(securityService);

                    if (routeBuilder == null) {
                        // Create a default.
                        routeBuilder = DEFAULT_ROUTE_BUILDER_PROVIDER.get();
                    }
                    routeBuilder.setSettings(settings);
                    routeBuilder.setApiService(service);

                    camelContext.addRoutes(routeBuilder);
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
        SimpleRegistry simpleRegistry = new SimpleRegistry();
        simpleRegistry.put(settings.getSslContextParametersBeanName(),
                CamelUtils.createSslContextParameters(settings.getSslConfiguration()));

        // TODO Handle many invocations of this method resulting in a growing registry list.
        ((DefaultCamelContext) camelContext).setRegistry(new CompositeRegistry(Arrays.asList(simpleRegistry, camelContext.getRegistry())));
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

    public boolean canUseKnowledgeBase(Map<String, Collection<String>> roleToKnowledgeBases, User user, String kbName) {
        return RestApiUtils.canUseKnowledgeBase(roleToKnowledgeBases, user, kbName);
    }
}
