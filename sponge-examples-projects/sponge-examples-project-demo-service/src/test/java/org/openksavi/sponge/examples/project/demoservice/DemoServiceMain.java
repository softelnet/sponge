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

package org.openksavi.sponge.examples.project.demoservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.logging.LoggingUtils;

public class DemoServiceMain {

    private static final Logger logger = LoggerFactory.getLogger(DemoServiceMain.class);

    public void run() {
        LoggingUtils.initLoggingBridge();

        DemoServiceTestEnvironment environment = new DemoServiceTestEnvironment();
        environment.init();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                environment.stop();
            } catch (Throwable e) {
                logger.error("Shutdown hook error", e);
            }
        }));
        environment.start(8080);
    }

    /**
     * Main method.
     *
     * @param args arguments.
     */
    public static void main(String... args) {
        new DemoServiceMain().run();
    }
}
