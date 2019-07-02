/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.grpcapi.server;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import io.grpc.Server;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.SslContextBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.core.util.SslConfiguration;
import org.openksavi.sponge.grpcapi.GrpcApiConstants;
import org.openksavi.sponge.grpcapi.server.core.kb.GrpcApiSubscribeCorrelator;
import org.openksavi.sponge.grpcapi.server.support.kb.GrpcApiManageSubscription;
import org.openksavi.sponge.java.JPlugin;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;

/**
 * Sponge gRPC API server plugin.
 */
public class GrpcApiServerPlugin extends JPlugin {

    private static final Logger logger = LoggerFactory.getLogger(GrpcApiServerPlugin.class);

    public static final String NAME = "grpcApiServer";

    public static final String KB_CORE_PACKAGE_TO_SCAN = GrpcApiSubscribeCorrelator.class.getPackage().getName();

    public static final String KB_SUPPORT_PACKAGE_TO_SCAN = GrpcApiManageSubscription.class.getPackage().getName();

    private RestApiServerPlugin restApiServerPlugin;

    private DefaultGrpcApiService service;

    private boolean autoStart = GrpcApiServerConstants.DEFAULT_AUTO_START;

    private Integer port;

    private Server server;

    private Consumer<NettyServerBuilder> serverConfigurator;

    private final Lock lock = new ReentrantLock(true);

    public GrpcApiServerPlugin() {
        setName(NAME);
    }

    public GrpcApiServerPlugin(String name) {
        super(name);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        autoStart = configuration.getBoolean(GrpcApiServerConstants.TAG_AUTO_START, autoStart);
        port = configuration.getInteger(GrpcApiServerConstants.TAG_PORT, port);
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

    public void start() {
        if (restApiServerPlugin == null) {
            // The REST API server plugin is required by the Sponge gRPC API.
            setRestApiServerPlugin(getEngine().getOperations().getPlugin(RestApiServerPlugin.class));
        }

        startServer();

        restApiServerPlugin.getService().setFeature(RestApiConstants.REMOTE_API_FEATURE_GRPC_ENABLED, true);

        getSponge().enableJavaByScan(KB_CORE_PACKAGE_TO_SCAN);
    }

    protected int resolveServerPort() {
        String portProperty = getEngine().getConfigurationManager().getProperty(GrpcApiConstants.PROPERTY_GRPC_PORT);
        if (portProperty != null) {
            return Integer.parseInt(portProperty.trim());
        }

        if (port != null) {
            return port;
        }

        // Default port convention.
        return restApiServerPlugin.getSettings().getPort() + 1;
    }

    /**
     * Starts the gRPC server.
     */
    protected void startServer() {
        lock.lock();
        try {
            if (server != null) {
                return;
            }

            if (service == null) {
                // Use the default service.
                service = new DefaultGrpcApiService();
            }
            service.setEngine(getEngine());
            service.setRestApiService(restApiServerPlugin.getService());
            service.setSubscriptionManager(new ServerSubscriptionManager(getEngine(), restApiServerPlugin.getService()));

            int port = resolveServerPort();
            NettyServerBuilder builder = NettyServerBuilder.forPort(port).addService(service);

            SslConfiguration sslConfiguration = restApiServerPlugin.getService().getSettings().getSslConfiguration();
            if (sslConfiguration != null) {
                // Use the TLS configuration from the REST API server.
                builder.sslContext(GrpcSslContexts
                        .configure(SslContextBuilder.forServer(SpongeUtils.createKeyManagerFactory(sslConfiguration))).build());
            }

            if (serverConfigurator != null) {
                serverConfigurator.accept(builder);
            }

            server = builder.build();

            logger.info("Starting the {} gRPC server on port {}", sslConfiguration != null ? "secure" : "insecure", port);

            server.start();
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        restApiServerPlugin.getService().setFeature(RestApiConstants.REMOTE_API_FEATURE_GRPC_ENABLED, false);

        getSponge().disableJavaByScan(KB_CORE_PACKAGE_TO_SCAN);

        stopServer();
    }

    protected void stopServer() {
        lock.lock();
        try {
            if (server == null) {
                return;
            }

            logger.info("Stopping the gRPC server");
            server.shutdown();
            server.awaitTermination(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            server = null;
            lock.unlock();
        }
    }

    public boolean isServerRunning() {
        return server != null && !server.isShutdown() && !server.isTerminated();
    }

    /**
     * Enables support processors (e.g. subscription actions) in the knowledge base.
     *
     * @param engineOperations the engine operations assosiated with the knowledge base.
     */
    public void enableSupport(KnowledgeBaseEngineOperations engineOperations) {
        engineOperations.enableJavaByScan(KB_SUPPORT_PACKAGE_TO_SCAN);
    }

    public void pushEvent(org.openksavi.sponge.event.Event event) {
        service.pushEvent(event);
    }

    public RestApiServerPlugin getRestApiServerPlugin() {
        return restApiServerPlugin;
    }

    public void setRestApiServerPlugin(RestApiServerPlugin restApiServerPlugin) {
        this.restApiServerPlugin = restApiServerPlugin;
    }

    public DefaultGrpcApiService getService() {
        return service;
    }

    public void setService(DefaultGrpcApiService service) {
        this.service = service;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Consumer<NettyServerBuilder> getServerConfigurator() {
        return serverConfigurator;
    }

    public void setServerConfigurator(Consumer<NettyServerBuilder> serverConfigurator) {
        this.serverConfigurator = serverConfigurator;
    }
}
