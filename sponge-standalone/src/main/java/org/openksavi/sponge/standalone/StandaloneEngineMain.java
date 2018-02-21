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

/**
 * Standalone engine class implementing a {@link #main(String...)} method.
 */
public class StandaloneEngineMain {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneEngineMain.class);

    private StandaloneEngineBuilder builder;

    private StandaloneSpongeEngine engine;

    private boolean testMode;

    public StandaloneEngineMain() {
        this(false);
    }

    public StandaloneEngineMain(boolean testMode) {
        this.testMode = testMode;
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

            // If help or version option are not specified.
            if (engine != null) {
                engine.startup();
                if (engine.getInteractiveMode() != null) {
                    engine.getInteractiveMode().loop();
                    engine.shutdown();
                }
            }
        } catch (Throwable e) {
            handleError(e);
        }
    }

    public void shutdown() {
        if (engine != null) {
            engine.shutdown();
            engine = null;
        }
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
            if (e instanceof StandaloneInitializationException && !testMode) {
                System.out.println(e.getMessage());
                System.out.println("");
                builder.printHelp();
            } else {
                logger.error("Error", e);
            }
        }

        if (testMode) {
            throw SpongeUtils.wrapException(e);
        }
    }

    public boolean isTestMode() {
        return testMode;
    }

    public void setTestMode(boolean testMode) {
        this.testMode = testMode;
    }

    /**
     * Main method.
     *
     * @param args arguments.
     */
    public static void main(String... args) {
        new StandaloneEngineMain().startup(args);
    }
}
