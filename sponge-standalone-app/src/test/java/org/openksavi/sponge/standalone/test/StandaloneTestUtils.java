/* Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.standalone.test;

import java.util.Arrays;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import org.slf4j.LoggerFactory;

import org.openksavi.sponge.standalone.StandaloneEngineMain;

public class StandaloneTestUtils {

    public static StandaloneEngineMain startupStandaloneEngineMain(String... args) {
        System.out.println("Starting up standalone engine with args: " + Arrays.asList(args));
        StandaloneEngineMain result = new StandaloneEngineMain(true);
        result.startup(args);

        return result;
    }

    public static void shutdownStandaloneEngineMain(StandaloneEngineMain standaloneEngineMain) {
        if (standaloneEngineMain != null) {
            standaloneEngineMain.shutdown();
        }
    }

    public static void reloadLogback(String configFile) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        try {
            JoranConfigurator configurator = new JoranConfigurator();
            configurator.setContext(context);
            context.reset();
            configurator.doConfigure(configFile);
        } catch (JoranException je) {
            // StatusPrinter will handle this.
        }
        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }
}
