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

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.jline.reader.impl.completer.StringsCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.VersionInfo;
import org.openksavi.sponge.core.engine.CombinedExceptionHandler;
import org.openksavi.sponge.core.engine.EngineBuilder;
import org.openksavi.sponge.core.engine.LoggingExceptionHandler;
import org.openksavi.sponge.core.engine.interactive.DefaultInteractiveMode;
import org.openksavi.sponge.core.engine.interactive.InteractiveModeConstants;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.ExceptionHandler;
import org.openksavi.sponge.engine.interactive.InteractiveMode;
import org.openksavi.sponge.engine.interactive.InteractiveModeConsole;
import org.openksavi.sponge.standalone.interactive.JLineInteractiveModeConsole;

/**
 * StandaloneEngine builder.
 */
public class StandaloneEngineBuilder extends EngineBuilder<StandaloneEngine> {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneEngineBuilder.class);

    public static final String OPTION_CONFIG = "c";

    public static final String OPTION_KNOWLEDGE_BASE = "k";

    public static final String OPTION_SPRING = "s";

    public static final String OPTION_CAMEL = "m";

    public static final String OPTION_INTERACTIVE = "i";

    public static final String OPTION_PRINT_ALL_EXCEPTIONS = "e";

    public static final String OPTION_HELP = "h";

    public static final String OPTION_VERSION = "v";

    public static final String DEFAULT_KNOWLEDGE_BASE_NAME = "kb";

    public static final char ARGUMENT_VALUE_SEPARATOR = '=';

    public static final char KB_FILES_SEPARATOR = ',';

    public static final String ENV_COLUMNS = "COLUMNS";

    public static final String EXECUTABLE_NAME = "sponge";

    private final Options options = createOptions();

    private String[] commandLineArgs;

    private Supplier<InteractiveModeConsole> interactiveModeConsoleSupplier = () -> {
        JLineInteractiveModeConsole console = new JLineInteractiveModeConsole();

        console.setCompleter(new StringsCompleter(InteractiveModeConstants.EXIT_COMMAND, InteractiveModeConstants.QUIT_COMMAND));

        return console;
    };

    public StandaloneEngineBuilder(StandaloneEngine engine) {
        super(engine);
    }

    public StandaloneEngineBuilder commandLineArgs(String... commandLineArgs) {
        this.commandLineArgs = commandLineArgs;
        return this;
    }

    public StandaloneEngineBuilder interactiveModeConsoleSupplier(Supplier<InteractiveModeConsole> interactiveModeConsoleSupplier) {
        this.interactiveModeConsoleSupplier = interactiveModeConsoleSupplier;
        return this;
    }

    private Options createOptions() {
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

        return options;
    }

    /**
     * Returns a new StandaloneEngine or {@code null} if help or version option is specified so the application should exit.
     */
    @Override
    public StandaloneEngine build() {
        try {
            if (commandLineArgs == null) {
                return super.build();
            }

            CommandLineParser parser = new DefaultParser();
            CommandLine commandLine = parser.parse(options, commandLineArgs);

            if (commandLine.hasOption(OPTION_HELP)) {
                printHelp();
                return null;
            }

            if (commandLine.hasOption(OPTION_VERSION)) {
                System.out.println(getDescription());
                return null;
            }

            if (commandLine.hasOption(OPTION_CONFIG)) {
                config(commandLine.getOptionValue(OPTION_CONFIG));
            }

            if (Stream.of(commandLine.getOptions()).filter(option -> option.getOpt().equals(OPTION_CONFIG)).count() > 1) {
                throw new StandaloneInitializationException("Only one Sponge XML configuration file may be provided.");
            }

            Stream.of(commandLine.getOptions()).filter(option -> option.getOpt().equals(OPTION_KNOWLEDGE_BASE)).forEachOrdered(option -> {
                String value = option.getValue();
                if (value == null || StringUtils.isBlank(value)) {
                    throw new StandaloneInitializationException("Empty knowledge base specification.");
                }

                String[] values = StringUtils.split(value, ARGUMENT_VALUE_SEPARATOR);

                String kbName = values.length == 2 ? values[0] : DEFAULT_KNOWLEDGE_BASE_NAME;
                String kbFilesString = values.length == 2 ? values[1] : values[0];

                if (StringUtils.isBlank(kbName)) {
                    throw new StandaloneInitializationException("Empty knowledge base name.");
                }

                List<String> kbFiles = Utils.split(kbFilesString, KB_FILES_SEPARATOR);
                knowledgeBase(kbName, kbFiles.toArray(new String[kbFiles.size()]));
            });

            if (!commandLine.hasOption(OPTION_CONFIG) && !commandLine.hasOption(OPTION_KNOWLEDGE_BASE)) {
                throw new StandaloneInitializationException(
                        "An Sponge XML configuration file or a knowledge base file(s) should be provided.");
            }

            // Apply standard parameters.
            super.build();

            applyDefaultParameters();

            if (commandLine.hasOption(OPTION_INTERACTIVE)) {
                InteractiveMode interactiveMode =
                        new DefaultInteractiveMode(engine, commandLine.getOptionValue(OPTION_INTERACTIVE), interactiveModeConsoleSupplier);
                ExceptionHandler interactiveExceptionHandler = new SystemErrExceptionHandler();
                interactiveMode.setExceptionHandler(interactiveExceptionHandler);

                engine.setInteractiveMode(interactiveMode);

                if (commandLine.hasOption(OPTION_PRINT_ALL_EXCEPTIONS)) {
                    engine.setExceptionHandler(new CombinedExceptionHandler(interactiveExceptionHandler, new LoggingExceptionHandler()));
                } else {
                    StandaloneUtils.logToConsole(false);
                }
            } else {
                if (commandLine.hasOption(OPTION_PRINT_ALL_EXCEPTIONS)) {
                    throw new StandaloneInitializationException(
                            "'" + OPTION_PRINT_ALL_EXCEPTIONS + "' option may be used only if '" + OPTION_INTERACTIVE + "' is also used.");
                }
            }

            StandaloneEngineListener standaloneListener = new StandaloneEngineListener(engine);

            List<String> springConfigurationFiles =
                    Stream.of(commandLine.getOptions()).filter(option -> option.getOpt().equals(OPTION_SPRING)).map(option -> {
                        String[] values = option.getValues();
                        if (values == null || values.length == 0) {
                            throw new StandaloneInitializationException("No Spring configuration file provided.");
                        }

                        return option.getValue(0);
                    }).collect(Collectors.toList());

            if (!springConfigurationFiles.isEmpty()) {
                standaloneListener.setSpringConfigurations(springConfigurationFiles);
            }

            if (commandLine.hasOption(OPTION_CAMEL)) {
                standaloneListener.setCamel(true);
            }

            engine.addOnStartupListener(standaloneListener);
            engine.addOnShutdownListener(standaloneListener);
        } catch (ParseException e) {
            throw new StandaloneInitializationException(e.getMessage(), e);
        }

        return engine;
    }

    protected void applyDefaultParameters() {
        engine.getDefaultParameters().setMainProcessingUnitThreadCount(StandaloneConstants.MAIN_PROCESSING_UNIT_THREAD_COUNT);
        engine.getDefaultParameters()
                .setAsyncEventSetProcessorExecutorThreadCount(engine.getDefaultParameters().getMainProcessingUnitThreadCount());
        engine.getDefaultParameters().setEventQueueCapacity(StandaloneConstants.EVENT_QUEUE_CAPACITY);
    }

    public void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setSyntaxPrefix("Usage: ");
        formatter.setOptionComparator(null);

        String columns = System.getenv(ENV_COLUMNS);
        if (columns != null) {
            try {
                formatter.setWidth(Integer.parseInt(columns));
            } catch (Exception e) {
                logger.warn(e.toString());
            }
        }

        String header = "Run the " + VersionInfo.PRODUCT + " standalone command-line application.\n\n";
        String leftPadding = StringUtils.repeat(' ', formatter.getLeftPadding());
        //@formatter:off
        String footer = new StringBuilder()
                .append("\nExamples (change directory to " + VersionInfo.PRODUCT + " bin/ first):\n")
                .append(leftPadding + "./" + EXECUTABLE_NAME + " -c ../examples/script/py/hello_world.xml\n")
                .append(leftPadding + "./" + EXECUTABLE_NAME + " -k helloWorldKb=../examples/script/py/hello_world.py\n")
                .append(leftPadding + "./" + EXECUTABLE_NAME + " -k ../examples/script/py/hello_world.py\n")
                .append(leftPadding + "./" + EXECUTABLE_NAME
                        + " -k filtersKb=../examples/script/py/filters.py -k heartbeatKb=../examples/script/js/rules_heartbeat.js\n")
                .append(leftPadding + "./" + EXECUTABLE_NAME
                        + " -k ../examples/standalone/multiple_kb_files/event_processors.py"
                        + ",../examples/standalone/multiple_kb_files/example2.py\n")
                .append("\nPress CTRL+C to exit the " + VersionInfo.PRODUCT + " standalone command-line application.\n")
                .append("\nSee http://sponge.openksavi.org for more details.").toString();
        //@formatter:on
        formatter.printHelp(EXECUTABLE_NAME, header, options, footer, true);
    }
}
