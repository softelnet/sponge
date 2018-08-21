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

import py4j.ClientServer;
import py4j.ClientServer.ClientServerBuilder;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.SpongeUtils;

/**
 * Sponge plugin that provides integration with CPython using Py4J ClientServer.
 */
public class ClientServerPy4JPlugin<T> extends BasePy4JPlugin<T> {

    private static final Logger logger = LoggerFactory.getLogger(ClientServerPy4JPlugin.class);

    public static final String TAG_AUTO_START_JAVA_SERVER = "autoStartJavaServer";

    protected ClientServer server;

    protected Boolean autoStartJavaServer;

    public ClientServerPy4JPlugin() {
        //
    }

    public ClientServerPy4JPlugin(String name) {
        super(name);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        super.onConfigure(configuration);

        autoStartJavaServer = configuration.getBoolean(TAG_AUTO_START_JAVA_SERVER, null);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onStartup() {
        super.onStartup();

        if (isPythonScriptBeforeStartup()) {
            executePythonScript();
        }

        if (server == null) {
            logger.info("Creating and starting the Py4J Server.");

            ClientServerBuilder builder =
                    new ClientServerBuilder().javaPort(getJavaPort()).pythonPort(getPythonPort()).entryPoint(getEngine().getOperations());
            if (getSecurity() != null) {
                SSLContext sslContext = createSslContext();
                builder.serverSocketFactory(sslContext.getServerSocketFactory()).socketFactory(sslContext.getSocketFactory());
            }

            if (autoStartJavaServer != null) {
                builder.autoStartJavaServer(autoStartJavaServer);
            }

            if (getAuthToken() != null) {
                builder.authToken(getAuthToken());
            }

            server = build(builder);
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

    protected ClientServer build(ClientServerBuilder builder) {
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

    public ClientServer getServer() {
        return server;
    }

    public void setServer(ClientServer server) {
        this.server = server;
    }

    public Boolean getAutoStartJavaServer() {
        return autoStartJavaServer;
    }

    public void setAutoStartJavaServer(Boolean autoStartJavaServer) {
        this.autoStartJavaServer = autoStartJavaServer;
    }
}
