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
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
import org.jline.reader.impl.completer.StringsCompleter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.VersionInfo;
import org.openksavi.sponge.core.engine.CombinedExceptionHandler;
import org.openksavi.sponge.core.engine.EngineBuilder;
import org.openksavi.sponge.core.engine.LoggingExceptionHandler;
import org.openksavi.sponge.core.engine.SystemErrExceptionHandler;
import org.openksavi.sponge.core.engine.interactive.DefaultInteractiveMode;
import org.openksavi.sponge.core.engine.interactive.InteractiveModeConstants;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.ExceptionHandler;
import org.openksavi.sponge.engine.interactive.InteractiveMode;
import org.openksavi.sponge.engine.interactive.InteractiveModeConsole;
import org.openksavi.sponge.logging.LoggingUtils;
import org.openksavi.sponge.spring.SpringKnowledgeBaseFileProvider;
import org.openksavi.sponge.standalone.interactive.JLineInteractiveModeConsole;

/**
 * StandaloneEngine builder.
 */
public class StandaloneEngineBuilder extends EngineBuilder<StandaloneSpongeEngine> {

    private static final Logger logger = LoggerFactory.getLogger(StandaloneEngineBuilder.class);

    public static final String DEFAULT_KNOWLEDGE_BASE_NAME = "kb";

    public static final char ARGUMENT_VALUE_SEPARATOR = '=';

    public static final char KB_FILES_SEPARATOR = ',';

    public static final String ENV_COLUMNS = "COLUMNS";

    public static final String EXECUTABLE_NAME = "sponge";

    private String[] commandLineArgs;

    private Supplier<InteractiveModeConsole> interactiveModeConsoleSupplier = () -> {
        JLineInteractiveModeConsole console = new JLineInteractiveModeConsole();

        console.setCompleter(new StringsCompleter(InteractiveModeConstants.EXIT_COMMAND, InteractiveModeConstants.QUIT_COMMAND));

        return console;
    };

    public StandaloneEngineBuilder(StandaloneSpongeEngine engine) {
        super(engine);

        knowledgeBaseFileProvider(new SpringKnowledgeBaseFileProvider());
    }

    public StandaloneEngineBuilder commandLineArgs(String... commandLineArgs) {
        this.commandLineArgs = commandLineArgs;
        return this;
    }

    public StandaloneEngineBuilder interactiveModeConsoleSupplier(Supplier<InteractiveModeConsole> interactiveModeConsoleSupplier) {
        this.interactiveModeConsoleSupplier = interactiveModeConsoleSupplier;
        return this;
    }

    /**
     * Returns a new StandaloneEngine or {@code null} if help or version option is specified so the application should exit.
     */
    @Override
    public StandaloneSpongeEngine build() {
        try {
            CommandLine commandLine = StandaloneUtils.parseCommandLine(commandLineArgs);

            if (commandLine.hasOption(StandaloneConstants.OPTION_HELP)) {
                printHelp();
                return null;
            }

            if (commandLine.hasOption(StandaloneConstants.OPTION_VERSION)) {
                System.out.println(getInfo());
                return null;
            }

            if (commandLine.hasOption(StandaloneConstants.OPTION_SYSTEM_PROPERTY)) {
                StandaloneUtils.setSystemProperties(commandLine.getOptionProperties(StandaloneConstants.OPTION_SYSTEM_PROPERTY));
            }

            if (commandLine.hasOption(StandaloneConstants.OPTION_CONFIG)) {
                config(commandLine.getOptionValue(StandaloneConstants.OPTION_CONFIG));
            }

            if (Stream.of(commandLine.getOptions()).filter(option -> option.getOpt().equals(StandaloneConstants.OPTION_CONFIG))
                    .count() > 1) {
                throw new StandaloneInitializationException("Only one Sponge XML configuration file may be provided.");
            }

            Stream.of(commandLine.getOptions()).filter(option -> option.getOpt().equals(StandaloneConstants.OPTION_KNOWLEDGE_BASE))
                    .forEachOrdered(option -> {
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

                        List<String> kbFiles = SpongeUtils.split(kbFilesString, KB_FILES_SEPARATOR);
                        knowledgeBase(kbName, kbFiles.toArray(new String[kbFiles.size()]));
                    });

            if (!commandLine.hasOption(StandaloneConstants.OPTION_CONFIG)
                    && !commandLine.hasOption(StandaloneConstants.OPTION_KNOWLEDGE_BASE)) {
                throw new StandaloneInitializationException(
                        "A Sponge XML configuration file or a knowledge base file(s) should be provided.");
            }

            // Apply standard parameters.
            super.build();

            applyDefaultParameters();

            boolean optionDebug = commandLine.hasOption(StandaloneConstants.OPTION_DEBUG);
            boolean optionPrintStackTrace = commandLine.hasOption(StandaloneConstants.OPTION_STACK_TRACE);
            boolean optionQuiet = commandLine.hasOption(StandaloneConstants.OPTION_QUIET);

            if (optionDebug && optionQuiet) {
                throw new StandaloneInitializationException(
                        "Options '" + StandaloneConstants.LONG_OPT_MAP.get(StandaloneConstants.OPTION_DEBUG) + "' and '"
                                + StandaloneConstants.LONG_OPT_MAP.get(StandaloneConstants.OPTION_QUIET) + "' can't be used both.");
            }

            if (commandLine.hasOption(StandaloneConstants.OPTION_INTERACTIVE)) {
                InteractiveMode interactiveMode = new DefaultInteractiveMode(engine,
                        commandLine.getOptionValue(StandaloneConstants.OPTION_INTERACTIVE), interactiveModeConsoleSupplier);

                ExceptionHandler systemErrExceptionHandler =
                        new SystemErrExceptionHandler(optionDebug || optionPrintStackTrace, !optionDebug);

                interactiveMode.setExceptionHandler(systemErrExceptionHandler);
                engine.setInteractiveMode(interactiveMode);

                if (!optionDebug) {
                    LoggingUtils.logToConsole(false);
                }
            } else {
                if (optionQuiet) {
                    engine.setExceptionHandler(new CombinedExceptionHandler(new SystemErrExceptionHandler(optionPrintStackTrace, true),
                            new LoggingExceptionHandler()));
                    LoggingUtils.logToConsole(false);
                }
            }

            StandaloneEngineListener standaloneListener = new StandaloneEngineListener(engine);

            StandaloneSettings settings = new StandaloneSettings();

            List<String> springConfigurationFiles = Stream.of(commandLine.getOptions())
                    .filter(option -> option.getOpt().equals(StandaloneConstants.OPTION_SPRING)).map(option -> {
                        String[] values = option.getValues();
                        if (values == null || values.length == 0) {
                            throw new StandaloneInitializationException("No Spring configuration file provided.");
                        }

                        return option.getValue(0);
                    }).collect(Collectors.toList());

            if (!springConfigurationFiles.isEmpty()) {
                settings.setSpringConfigurationFiles(springConfigurationFiles);
            }

            if (commandLine.hasOption(StandaloneConstants.OPTION_CAMEL)) {
                settings.setCamel(true);
            }

            engine.setArgs(commandLine.getArgList());

            engine.getOperations().setVariable(StandaloneSettings.VARIABLE_NAME, settings);

            // Add a default standalone plugin.
            engine.getPluginManager().addPlugin(new StandalonePlugin());

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
                .append(leftPadding + "./" + EXECUTABLE_NAME + " -c ../examples/script/py/triggers_hello_world.xml\n")
                .append(leftPadding + "./" + EXECUTABLE_NAME + " -k helloWorldKb=../examples/script/py/triggers_hello_world.py\n")
                .append(leftPadding + "./" + EXECUTABLE_NAME + " -k ../examples/script/py/triggers_hello_world.py\n")
                .append(leftPadding + "./" + EXECUTABLE_NAME
                        + " -k filtersKb=../examples/script/py/filters.py -k heartbeatKb=../examples/script/js/rules_heartbeat.js\n")
                .append(leftPadding + "./" + EXECUTABLE_NAME
                        + " -k ../examples/standalone/multiple_kb_files/event_processors.py"
                        + ",../examples/standalone/multiple_kb_files/example2.py\n")
                .append("\nPress CTRL+C to exit the " + VersionInfo.PRODUCT + " standalone command-line application.\n")
                .append("\nSee https://sponge.openksavi.org for more details.").toString();
        //@formatter:on
        formatter.printHelp(EXECUTABLE_NAME, header, StandaloneConstants.OPTIONS, footer, true);
    }
}
