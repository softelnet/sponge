/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.examples.project.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;

/**
 * Example class containing main method.
 */
public class NewsExampleMain {

    private static final Logger logger = LoggerFactory.getLogger(NewsExampleMain.class);

    /** XML configuration file. */
    public static final String CONFIG_FILE = "config/config.xml";

    /** The engine. */
    private Engine engine;

    /**
     * Starts up an engine.
     */
    public void startup() {
        if (engine != null) {
            return;
        }

        // Use EngineBuilder API to create an engine.
        engine = DefaultEngine.builder().config(CONFIG_FILE).build();

        // Start the engine.
        engine.startup();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                shutdown();
            } catch (Throwable e) {
                logger.error("Shutdown hook error", e);
            }
        }));
    }

    /**
     * Shutdown the engine.
     */
    public void shutdown() {
        if (engine != null) {
            engine.shutdown();
            engine = null;
        }
    }

    public Engine getEngine() {
        return engine;
    }

    /**
     * Main method. Arguments are ignored.
     */
    public static void main(String... args) {
        new NewsExampleMain().startup();
    }
}
