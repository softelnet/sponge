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

package org.openksavi.sponge.standalone;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.Appender;

/**
 * This class defines a set of utility methods.
 */
public abstract class StandaloneUtils {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneUtils.class);

    public static final String CONSOLE_APPENDER_NAME = "console";

    /**
     * Starts or stops logging to the console.
     *
     * @param start
     *            starts or stops logging to the console.
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
}
