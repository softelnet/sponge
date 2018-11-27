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

import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.engine.GenericExceptionContext;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.logging.LoggingUtils;

/**
 * Standalone engine class implementing a {@link #main(String...)} method.
 */
public class StandaloneEngineMain {

    // Important: This class must not use the static logger because the logger configuration in the main method would be too late.

    private StandaloneEngineBuilder builder;

    private StandaloneSpongeEngine engine;

    private boolean embeddedMode;

    public StandaloneEngineMain() {
        this(false);
    }

    public StandaloneEngineMain(boolean embeddedMode) {
        this.embeddedMode = embeddedMode;
    }

    public SpongeEngine getEngine() {
        return engine;
    }

    public void startup(String... args) {
        if (engine != null) {
            return;
        }

        try {
            builder = StandaloneSpongeEngine.builder().commandLineArgs(args);
            engine = builder.build();

            // If help or version option are specified (i.e. when the engine is null), do nothing.
            if (engine != null) {
                engine.startup();
                if (engine.getInteractiveMode() != null) {
                    runInteractiveLoop();
                }
            }
        } catch (Throwable e) {
            handleError(e);

            if (!embeddedMode) {
                System.exit(1);
            }
        }
    }

    public void shutdown() {
        if (engine != null) {
            engine.shutdown();
            engine = null;
        }
    }

    protected void runInteractiveLoop() {
        engine.getInteractiveMode().loop();

        try {
            engine.shutdown();
        } catch (Throwable e) {
            handleError(e);
        }

        if (!embeddedMode) {
            System.exit(0);
        }
    }

    protected Logger getLogger() {
        return LoggerFactory.getLogger(StandaloneEngineMain.class);
    }

    protected void handleError(Throwable e) {
        if (engine != null) {
            if (engine.getInteractiveMode() != null) {
                engine.getInteractiveMode().getExceptionHandler().handleException(e,
                        new GenericExceptionContext(engine, ObjectUtils.defaultIfNull(SpongeUtils.getSourceName(e), "interactive")));
            } else {
                engine.handleError("standalone", e);
            }
        } else {
            if (e instanceof StandaloneInitializationException && !embeddedMode) {
                System.out.println(e.getMessage());
                System.out.println("");
                builder.printHelp();
            } else {
                getLogger().error("Error", e);
            }
        }

        if (embeddedMode) {
            throw SpongeUtils.wrapException(e);
        }
    }

    /**
     * Main method.
     *
     * @param args arguments.
     */
    public static void main(String... args) {
        StandaloneUtils.initSystemPropertiesFromCommandLineArgs(args);

        LoggingUtils.initLoggingBridge();

        new StandaloneEngineMain().startup(args);
    }
}
