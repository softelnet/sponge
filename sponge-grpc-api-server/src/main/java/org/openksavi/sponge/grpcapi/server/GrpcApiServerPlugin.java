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

import io.grpc.Server;
import io.grpc.ServerBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.config.ConfigException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.grpcapi.GrpcApiConstants;
import org.openksavi.sponge.grpcapi.server.core.kb.GrpcApiSubscribeCorrelator;
import org.openksavi.sponge.grpcapi.server.support.kb.GrpcApiManageSubscription;
import org.openksavi.sponge.java.JPlugin;
import org.openksavi.sponge.kb.KnowledgeBaseEngineOperations;
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

    private GrpcApiServiceImpl service;

    private boolean autoStart = GrpcApiServerConstants.DEFAULT_AUTO_START;

    private Server server;

    private Lock lock = new ReentrantLock(true);

    public GrpcApiServerPlugin() {
        setName(NAME);
    }

    public GrpcApiServerPlugin(String name) {
        super(name);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        // TODO
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
            RestApiServerPlugin restPlugin = getEngine().getPluginManager().getPlugin(RestApiServerPlugin.class);
            if (restPlugin == null) {
                throw new ConfigException("The REST API server plugin is not registered but it is required by the Sponge gRPC API");
            }

            setRestApiServerPlugin(restPlugin);
        }

        startServer();

        getSponge().enableJavaByScan(KB_CORE_PACKAGE_TO_SCAN);
    }

    protected int resolverServerPort() {
        String portProperty = getEngine().getConfigurationManager().getProperty(GrpcApiConstants.PROPERTY_GRPC_PORT);
        if (portProperty != null) {
            return Integer.parseInt(portProperty.trim());
        }

        // Convention.
        return restApiServerPlugin.getSettings().getPort() + 1;
    }

    /**
     * Starts the gRPC server.
     */
    protected void startServer() {
        if (server != null) {
            return;
        }

        lock.lock();
        try {
            GrpcApiServiceImpl service = new GrpcApiServiceImpl();
            service.setEngine(getEngine());
            service.setRestApiService(restApiServerPlugin.getService());
            setService(service);

            int port = resolverServerPort();
            server = ServerBuilder.forPort(port).addService(service).build();

            logger.info("Starting the gRPC server on port {}", port);

            server.start();
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            lock.unlock();
        }
    }

    public void stop() {
        getSponge().disableJavaByScan(KB_CORE_PACKAGE_TO_SCAN);

        stopServer();
    }

    protected void stopServer() {
        if (server == null) {
            return;
        }

        lock.lock();
        try {
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

    /**
     * Enables support processors (e.g. subscription actions) in the knowledge base.
     *
     * @param engineOperations the engine operations assosiated with the knowledge base.
     */
    public void enableSupport(KnowledgeBaseEngineOperations engineOperations) {
        engineOperations.enableJavaByScan(KB_SUPPORT_PACKAGE_TO_SCAN);
    }

    public RestApiServerPlugin getRestApiServerPlugin() {
        return restApiServerPlugin;
    }

    public void setRestApiServerPlugin(RestApiServerPlugin restApiServerPlugin) {
        this.restApiServerPlugin = restApiServerPlugin;
    }

    public GrpcApiServiceImpl getService() {
        return service;
    }

    public void setService(GrpcApiServiceImpl service) {
        this.service = service;
    }

    public boolean isAutoStart() {
        return autoStart;
    }

    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }
}
