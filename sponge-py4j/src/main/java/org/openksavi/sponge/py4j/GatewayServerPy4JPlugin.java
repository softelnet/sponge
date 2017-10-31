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

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.util.Utils;

import py4j.GatewayServer;
import py4j.GatewayServer.GatewayServerBuilder;

/**
 * Sponge plugin that provides integration with CPython using Py4J and GatewayServer.
 */
public class GatewayServerPy4JPlugin<T> extends Py4JPlugin<T> {

    private static final Logger logger = LoggerFactory.getLogger(GatewayServerPy4JPlugin.class);

    protected GatewayServer server;

    @SuppressWarnings("unchecked")
    @Override
    public void onStartup() {
        logger.info("Starting Py4J Server");

        server = new GatewayServerBuilder().entryPoint(getEngine().getOperations()).build();

        server.start();

        if (getFacadeInterfaceName() != null) {
            try {
                setFacade((T) server.getPythonServerEntryPoint(new Class[] { ClassUtils.getClass(getFacadeInterfaceName()) }));
            } catch (Exception e) {
                throw Utils.wrapException(getClass().getName(), e);
            }
        }
    }

    @Override
    public void onShutdown() {
        if (server != null) {
            server.shutdown();
        }
    }

    public GatewayServer getServer() {
        return server;
    }
}
