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

package org.openksavi.sponge.standalone;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.engine.OnShutdownListener;
import org.openksavi.sponge.engine.OnStartupListener;
import org.openksavi.sponge.signal.SystemSignal;
import org.openksavi.sponge.signal.SystemSignalListener;

/**
 * Standalone onStartup/onShutdown listener.
 */
public class StandaloneEngineListener implements OnStartupListener, OnShutdownListener {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneEngineListener.class);

    /** Operating system signals. */
    private static List<String> SIGNALS =
            Arrays.asList(SystemSignal.SIGTERM, SystemSignal.SIGINT, SystemSignal.SIGABRT, SystemSignal.SIGHUP);

    private StandaloneSpongeEngine engine;

    public StandaloneEngineListener(StandaloneSpongeEngine engine) {
        this.engine = engine;
    }

    @Override
    public void onStartup() {
        initSystemSignals();
        addShutdownHook();
    }

    /**
     * Initializes system signal handlers.
     */
    protected void initSystemSignals() {
        SystemSignalListener systemSignalListener = (signal) -> {
            try {
                if (signal.isSignal(SystemSignal.SIGHUP)) {
                    engine.reload();
                } else {
                    // Shutdown hook will invoke the engine shutdown.
                    System.exit(0);
                }
            } catch (Exception e) {
                logger.error("Signal handler failed", e);
            }
        };

        SIGNALS.forEach(signal -> {
            try {
                SystemSignal.setSystemSignalListener(signal, systemSignalListener);
            } catch (Exception e) {
                logger.warn("Init signal handlers: {}", e.getMessage());
            }
        });
    }

    /**
     * Registers a shutdown hook.
     */
    private void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (engine != null) {
                    engine.shutdown();
                }
            } catch (Throwable e) {
                logger.error("Shutdown hook error", e);
            }
        }));
    }

    @Override
    public void onShutdown() {
        // Ignore.
    }
}
