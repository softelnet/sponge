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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public static final String OPTION_LANG = "l";

    public static final String OPTION_QUIET = "q";

    public static final String OPTION_STACK_TRACE = "t";

    public static final String OPTION_DEBUG = "d";

    public static final String OPTION_HELP = "h";

    public static final String OPTION_VERSION = "v";

    public static final String OPTION_SYSTEM_PROPERTY = "D";

    public static final Map<String, String> LONG_OPT_MAP = createLongOptMap();

    public static final Options OPTIONS = StandaloneConstants.createOptions();

    public static final String INTERACTIVE_LANG_KB_NAME = "_interactiveLang";

    private StandaloneConstants() {
        //
    }

    private static Options createOptions() {
        Options options = new Options();

        options.addOption(Option.builder(OPTION_CONFIG).longOpt(LONG_OPT_MAP.get(OPTION_CONFIG)).hasArg().argName("file")
                .desc("Use the given Sponge XML configuration file. Only one configuration file may be provided.").build());

        options.addOption(Option.builder(OPTION_KNOWLEDGE_BASE).longOpt(LONG_OPT_MAP.get(OPTION_KNOWLEDGE_BASE)).hasArg()
                .argName("[name=]files]")
                .desc("Use given knowledge base by setting its name (optional) and files (comma-separated). "
                        + "When no name is provided, a default name 'kb' will be used. "
                        + "This option may be used more than once to provide many knowledge bases. Each of them could use many files.")
                .build());

        options.addOption(Option.builder(OPTION_SPRING).longOpt(LONG_OPT_MAP.get(OPTION_SPRING)).hasArg().argName("file")
                .desc("Use given Spring configuration file. "
                        + "This option may be used more than once to provide many Spring configuration files.")
                .build());

        options.addOption(Option.builder(OPTION_CAMEL).longOpt(LONG_OPT_MAP.get(OPTION_CAMEL))
                .desc("Create an Apache Camel context. Works only if one or more 'spring' options are present.").build());

        options.addOption(Option.builder(OPTION_INTERACTIVE).longOpt(LONG_OPT_MAP.get(OPTION_INTERACTIVE)).hasArg().argName("[name]")
                .optionalArg(true)
                .desc("Run in an interactive mode by connecting to a knowledge base interpreter. "
                        + "You may provide the name of one of the loaded knowledge bases, otherwise "
                        + "the first loaded knowledge base will be chosen.")
                .build());

        options.addOption(
                Option.builder(OPTION_LANG).longOpt(LONG_OPT_MAP.get(OPTION_LANG)).hasArg().argName("language")
                        .desc("Run the given script language intepreter in an interactive mode as a new, empty knowledge base. "
                                + "Supported languages: python, groovy, ruby, javascript. Applicable only in an interactive mode.")
                        .build());

        options.addOption(Option.builder(OPTION_QUIET).longOpt(LONG_OPT_MAP.get(OPTION_QUIET))
                .desc("Supresses logging to the console. Applicable only in a non interactive mode.").build());

        options.addOption(Option.builder(OPTION_STACK_TRACE).longOpt(LONG_OPT_MAP.get(OPTION_STACK_TRACE))
                .desc("Print exception stack traces to the console.").build());

        options.addOption(Option.builder(OPTION_DEBUG).longOpt(LONG_OPT_MAP.get(OPTION_DEBUG))
                .desc("Enable more debugging info. " + "Print all logs to the console (including exception stack traces). "
                        + "Applicable only in an interactive mode. Options '" + LONG_OPT_MAP.get(OPTION_DEBUG) + "' and '"
                        + LONG_OPT_MAP.get(OPTION_QUIET) + "' can't be used both. ")
                .build());

        options.addOption(Option.builder(OPTION_HELP).longOpt(LONG_OPT_MAP.get(OPTION_HELP)).desc("Print help message and exit.").build());

        options.addOption(Option.builder(OPTION_VERSION).longOpt(LONG_OPT_MAP.get(OPTION_VERSION))
                .desc("Print the version information and exit.").build());

        options.addOption(Option.builder(OPTION_SYSTEM_PROPERTY).longOpt(LONG_OPT_MAP.get(OPTION_SYSTEM_PROPERTY)).hasArgs().numberOfArgs(2)
                .valueSeparator('=').argName("property=value").desc("Set the Java system property.").build());

        return options;
    }

    private static Map<String, String> createLongOptMap() {
        Map<String, String> longOptMap = new LinkedHashMap<>();

        longOptMap.put(OPTION_CONFIG, "config");
        longOptMap.put(OPTION_KNOWLEDGE_BASE, "knowledge-base");
        longOptMap.put(OPTION_SPRING, "spring");
        longOptMap.put(OPTION_CAMEL, "camel");
        longOptMap.put(OPTION_INTERACTIVE, "interactive");
        longOptMap.put(OPTION_LANG, "lang");
        longOptMap.put(OPTION_QUIET, "quiet");
        longOptMap.put(OPTION_STACK_TRACE, "stack-trace");
        longOptMap.put(OPTION_DEBUG, "debug");
        longOptMap.put(OPTION_HELP, "help");
        longOptMap.put(OPTION_VERSION, "version");
        longOptMap.put(OPTION_SYSTEM_PROPERTY, "system-property");

        return Collections.unmodifiableMap(longOptMap);
    }
}
