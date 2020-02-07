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

package org.openksavi.sponge.logging;

import ch.qos.logback.core.Appender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * This class defines a set of logging utility methods.
 */
public abstract class LoggingUtils {

    private static final Logger logger = LoggerFactory.getLogger(LoggingUtils.class);

    public static final String CONSOLE_APPENDER_NAME = "console";

    /**
     * Starts or stops logging to the console.
     *
     * @param start starts or stops logging to the console.
     */
    @SuppressWarnings("rawtypes")
    public static void logToConsole(boolean start) {
        Logger rootLogger = LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        if (rootLogger != null && rootLogger instanceof ch.qos.logback.classic.Logger) {
            Appender appender = ((ch.qos.logback.classic.Logger) rootLogger).getAppender(CONSOLE_APPENDER_NAME);

            if (appender != null) {
                if (start) {
                    appender.start();
                } else {
                    appender.stop();
                }

                return;
            }
        }

        logger.warn("Unable to reconfigure Logback console logger (appender name '" + CONSOLE_APPENDER_NAME + "').");
    }

    public static void initLoggingBridge() {
        // Remove existing handlers attached to j.u.l root logger.
        SLF4JBridgeHandler.removeHandlersForRootLogger();

        // Add SLF4JBridgeHandler to j.u.l's root logger, should be done once during the initialization phase of your application.
        SLF4JBridgeHandler.install();
    }
}
