/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.py4j;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import py4j.GatewayServer;
import py4j.GatewayServer.GatewayServerBuilder;

import org.openksavi.sponge.core.util.SpongeUtils;

/**
 * Sponge plugin that provides integration with CPython using Py4J GatewayServer.
 */
public class GatewayServerPy4JPlugin<T> extends BasePy4JPlugin<T> {

    private static final Logger logger = LoggerFactory.getLogger(GatewayServerPy4JPlugin.class);

    protected GatewayServer server;

    public GatewayServerPy4JPlugin() {
        //
    }

    public GatewayServerPy4JPlugin(String name) {
        super(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onStartup() {
        if (isPythonScriptBeforeStartup()) {
            executePythonScript();
        }

        if (server == null) {
            logger.info("Creating and starting the Py4J Server.");

            GatewayServerBuilder builder = new GatewayServerBuilder().javaPort(getJavaPort()).entryPoint(getEngine().getOperations());
            if (getSecurity() != null) {
                SSLContext sslContext = createSslContext();
                builder.serverSocketFactory(sslContext.getServerSocketFactory());
            }

            if (getAuthToken() != null) {
                builder.authToken(getAuthToken());
            }

            server = build(builder);

            server.start();
        } else {
            logger.info("Using the manually created Py4J Server. Note that the server should have already been started.");
        }

        if (getFacadeInterfaceName() != null) {
            try {
                setFacade((T) server.getPythonServerEntryPoint(new Class[] { ClassUtils.getClass(getFacadeInterfaceName()) }));
            } catch (Exception e) {
                throw SpongeUtils.wrapException(e);
            }
        }

        if (!isPythonScriptBeforeStartup()) {
            executePythonScript();
        }
    }

    protected GatewayServer build(GatewayServerBuilder builder) {
        return builder.build();
    }

    @Override
    public void onShutdown() {
        // Reversed order of killing the script process.
        if (!isPythonScriptBeforeStartup()) {
            killPythonScript();
        }

        if (server != null) {
            server.shutdown();
        }

        if (isPythonScriptBeforeStartup()) {
            killPythonScript();
        }
    }

    public GatewayServer getServer() {
        return server;
    }

    public void setServer(GatewayServer server) {
        this.server = server;
    }
}
