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

package org.openksavi.sponge.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.util.ProcessConfiguration.RedirectType;
import org.openksavi.sponge.engine.SpongeEngine;

/**
 * This class defines a set of process utility methods.
 */
public abstract class ProcessUtils {

    private static final Logger logger = LoggerFactory.getLogger(ProcessUtils.class);

    public static final String TAG_PROCESS_EXECUTABLE = "executable";

    public static final String TAG_PROCESS_ARGUMENT = "argument";

    public static final String TAG_PROCESS_WORKING_DIR = "workingDir";

    public static final String TAG_PROCESS_ENV = "env";

    public static final String TAG_PROCESS_WAIT_SECONDS = "waitSeconds";

    public static final String TAG_PROCESS_REDIRECT_TYPE = "redirectType";

    public static final String TAG_PROCESS_CHARSET = "charset";

    public static final String TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_REGEXP = "waitForOutputLineRegexp";

    public static final String TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_TIMEOUT = "waitForOutputLineTimeout";

    public static final String ATTR_PROCESS_ENV_NAME = "name";

    public static ProcessConfiguration.Builder createProcessConfigurationBuilder(Configuration configuration) {
        Map<String, String> env = new LinkedHashMap<>();
        Arrays.stream(configuration.getConfigurationsAt(TAG_PROCESS_ENV)).forEach(c -> {
            env.put(Validate.notNull(c.getAttribute(ATTR_PROCESS_ENV_NAME, null), "The environment variable must have a name"),
                    c.getValue());
        });

        ProcessConfiguration.Builder builder = ProcessConfiguration.builder(configuration.getString(TAG_PROCESS_EXECUTABLE, null))
                .arguments(Arrays.stream(configuration.getConfigurationsAt(TAG_PROCESS_ARGUMENT)).map(Configuration::getValue)
                        .collect(Collectors.toList()))
                .workingDir(configuration.getString(TAG_PROCESS_WORKING_DIR, null)).env(env)
                .waitSeconds(configuration.getLong(TAG_PROCESS_WAIT_SECONDS, null));

        String redirectTypeString = configuration.getString(TAG_PROCESS_REDIRECT_TYPE, null);
        if (redirectTypeString != null) {
            builder.redirectType(RedirectType.valueOf(redirectTypeString));
        }

        String charsetString = configuration.getString(TAG_PROCESS_CHARSET, null);
        if (charsetString != null) {
            builder.charset(Charset.forName(charsetString));
        }

        builder.waitForOutputLineRegexp(configuration.getString(TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_REGEXP, null))
                .waitForOutputLineTimeout(configuration.getLong(TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_TIMEOUT, null));

        return builder;
    }

    public static ProcessInstance startProcess(SpongeEngine engine, ProcessConfiguration processConfiguration) {
        if (processConfiguration.getWaitForOutputLineRegexp() != null) {
            return startProcessAndWaitForOutputLine(engine, processConfiguration);
        } else {
            return startProcessWithOutputLineConsumer(engine, processConfiguration, null);
        }
    }

    public static ProcessInstance startProcessAndWaitForOutputLine(SpongeEngine engine, ProcessConfiguration processConfiguration) {
        final Semaphore semaphore = new Semaphore(0);

        ProcessInstance processInstance = startProcessWithOutputLineConsumer(engine, processConfiguration, (line) -> {
            if (line.matches(processConfiguration.getWaitForOutputLineRegexp())) {
                semaphore.release();
            }
        });

        try {
            if (processConfiguration.getWaitForOutputLineTimeout() != null) {
                Validate.isTrue(semaphore.tryAcquire(processConfiguration.getWaitForOutputLineTimeout(), TimeUnit.SECONDS),
                        "Process wait timeout exceeded");
            } else {
                semaphore.acquire();
            }
        } catch (InterruptedException e) {
            throw SpongeUtils.wrapException(e);
        }

        return processInstance;
    }

    /**
     * Starts a new process. Waits the specified time if necessary.
     *
     * @param engine the engine.
     * @param processConfiguration the process configuration,
     * @param outputConsumer the process output consumer. May be {@code null}. Applicable only if the redirect type is LOGGER.
     * @return a new process instance.
     */
    public static ProcessInstance startProcessWithOutputLineConsumer(SpongeEngine engine, ProcessConfiguration processConfiguration,
            Consumer<String> outputConsumer) {
        if (outputConsumer != null) {
            Validate.isTrue(processConfiguration.getRedirectType() == RedirectType.LOGGER,
                    "If the output consumer is provided, the redirect type must be LOGGER");
        }

        // Configure the process.
        List<String> commands = new ArrayList<>();
        commands.add(processConfiguration.getExecutable());
        commands.addAll(processConfiguration.getArguments());

        ProcessBuilder builder = new ProcessBuilder(commands);
        if (processConfiguration.getRedirectType() == RedirectType.INHERIT) {
            builder.inheritIO();
        }

        if (processConfiguration.getWorkingDir() != null) {
            builder.directory(new File(processConfiguration.getWorkingDir()));
        }

        builder.environment().putAll(processConfiguration.getEnv());

        logger.debug("Starting a new subprocess: {} {}", processConfiguration.getExecutable(), processConfiguration.getArguments());
        if (!processConfiguration.getEnv().isEmpty()) {
            logger.debug("The subprocess additional environment: {}", processConfiguration.getEnv());
        }

        Charset finalCharset = processConfiguration.getCharset() != null ? processConfiguration.getCharset() : Charset.defaultCharset();

        // Start the process.
        ProcessInstance processInstance = null;
        try {
            processInstance = new ProcessInstance(builder.start(), processConfiguration);

            if (processConfiguration.getRedirectType() == RedirectType.LOGGER) {
                SpongeUtils.executeConcurrentlyOnce(engine,
                        new InputStreamLineConsumerRunnable(processInstance.getProcess().getInputStream(), (line) -> {
                            logger.info(line);

                            if (outputConsumer != null) {
                                outputConsumer.accept(line);
                            }
                        }, finalCharset));
                SpongeUtils.executeConcurrentlyOnce(engine,
                        new InputStreamLineConsumerRunnable(processInstance.getProcess().getErrorStream(), logger::warn, finalCharset));
            }
        } catch (IOException e) {
            throw SpongeUtils.wrapException(processConfiguration.getName(), e);
        }

        // If specified, set the output string.
        if (processConfiguration.getRedirectType() == RedirectType.STRING) {
            try (BufferedReader output =
                    new BufferedReader(new InputStreamReader(processInstance.getProcess().getInputStream(), finalCharset));
                    BufferedReader errors =
                            new BufferedReader(new InputStreamReader(processInstance.getProcess().getErrorStream(), finalCharset))) {
                processInstance.setOutput(output.lines().collect(Collectors.joining("\n")));
                logger.debug("{} output: {}", processConfiguration.getName(), processInstance.getOutput());

                String errorsString = errors.lines().collect(Collectors.joining("\n"));
                if (!errorsString.isEmpty()) {
                    throw new SpongeException(processConfiguration.getName() + " error: " + errorsString);
                }
            } catch (IOException e) {
                throw SpongeUtils.wrapException(processConfiguration.getName(), e);
            }
        }

        // If specified, wait for the process.
        long elapsedSeconds = Duration.between(processInstance.getStartTime(), Instant.now()).getSeconds();
        if (processConfiguration.getWaitSeconds() != null && processConfiguration.getWaitSeconds() > elapsedSeconds) {
            try {
                processInstance.setFinished(
                        processInstance.getProcess().waitFor(processConfiguration.getWaitSeconds() - elapsedSeconds, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                throw SpongeUtils.wrapException(e);
            }
        }

        return processInstance;
    }

    protected ProcessUtils() {
        //
    }
}
