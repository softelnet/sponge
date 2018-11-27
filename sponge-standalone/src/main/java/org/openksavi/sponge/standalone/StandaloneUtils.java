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

package org.openksavi.sponge.standalone;

import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.ParseException;

/**
 * Utility methods for the standalone Sponge.
 */
public abstract class StandaloneUtils {

    private StandaloneUtils() {
        //
    }

    public static CommandLine parseCommandLine(String... args) throws ParseException {
        return new DefaultParser().parse(StandaloneConstants.OPTIONS, args);
    }

    /**
     * The initial parsing of system properties specified in the command line.
     *
     * @param args the command line arguments.
     */
    public static void initSystemPropertiesFromCommandLineArgs(String... args) {
        try {
            CommandLine commandLine = parseCommandLine(args);

            if (commandLine.hasOption(StandaloneConstants.OPTION_SYSTEM_PROPERTY)) {
                setSystemProperties(commandLine.getOptionProperties(StandaloneConstants.OPTION_SYSTEM_PROPERTY));
            }
        } catch (ParseException e) {
            // Suppress exceptions at this stage.
        }
    }

    public static void setSystemProperties(Properties properties) {
        properties.forEach((name, value) -> System.setProperty((String) name, (String) value));
    }

}
