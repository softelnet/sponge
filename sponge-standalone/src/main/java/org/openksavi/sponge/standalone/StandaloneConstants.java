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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Standalone engine constants.
 */
public final class StandaloneConstants {

    public static final int MAIN_PROCESSING_UNIT_THREAD_COUNT = 10;

    public static final int EVENT_QUEUE_CAPACITY = 100000;

    public static final String OPTION_CONFIG = "c";

    public static final String OPTION_KNOWLEDGE_BASE = "k";

    public static final String OPTION_SPRING = "s";

    public static final String OPTION_CAMEL = "m";

    public static final String OPTION_INTERACTIVE = "i";

    public static final String OPTION_PRINT_ALL_EXCEPTIONS = "e";

    public static final String OPTION_HELP = "h";

    public static final String OPTION_VERSION = "v";

    public static final String OPTION_SYSTEM_PROPERTY = "D";

    public static final Options OPTIONS = StandaloneConstants.createOptions();

    private StandaloneConstants() {
        //
    }

    private static Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder(OPTION_CONFIG).longOpt("config").hasArg().argName("file")
                .desc("Use given Sponge XML configuration file. Only one configuration file may be provided.").build());
        options.addOption(Option.builder(OPTION_KNOWLEDGE_BASE).longOpt("knowledge-base").hasArg().argName("[name=]files]")
                .desc("Use given knowledge base by setting its name (optional) and files (comma-separated). "
                        + "When no name is provided, a default name 'kb' will be used. "
                        + "This option may be used more than once to provide many knowledge bases. Each of them could use many files.")
                .build());
        options.addOption(
                Option.builder(OPTION_SPRING).longOpt("spring").hasArg().argName("file").desc("Use given Spring configuration file. "
                        + "This option may be used more than once to provide many Spring configuration files.").build());
        options.addOption(Option.builder(OPTION_CAMEL).longOpt("camel")
                .desc("Create an Apache Camel context. Works only if one or more 'spring' options are present.").build());
        options.addOption(Option.builder(OPTION_INTERACTIVE).longOpt("interactive").hasArg().argName("[name]").optionalArg(true)
                .desc("Run in an interactive mode by connecting to a knowledge base interpreter. "
                        + "You may provide the name of one of the loaded knowledge bases, otherwise "
                        + "the first loaded knowledge base will be chosen.")
                .build());
        options.addOption(Option.builder(OPTION_PRINT_ALL_EXCEPTIONS).longOpt("print-all-exceptions")
                .desc("Applicable only in an interactive mode. "
                        + "Print all exceptions (e.g. also thrown in event processors running in other threads). "
                        + "Helpful for development purposes.")
                .build());
        options.addOption(Option.builder(OPTION_HELP).longOpt("help").desc("Print help message and exit.").build());
        options.addOption(Option.builder(OPTION_VERSION).longOpt("version").desc("Print the version information and exit.").build());
        options.addOption(Option.builder(OPTION_SYSTEM_PROPERTY).longOpt("system-property").hasArgs().numberOfArgs(2).valueSeparator('=')
                .argName("property=value").desc("Set the Java system property.").build());

        return options;
    }
}
