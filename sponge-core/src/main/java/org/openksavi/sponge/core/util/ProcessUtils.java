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
import java.util.List;
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

    public static final String TAG_PROCESS_WAIT_SECONDS = "waitSeconds";

    public static final String TAG_PROCESS_REDIRECT_TYPE = "redirectType";

    public static final String TAG_PROCESS_CHARSET = "charset";

    public static final String TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_REGEXP = "waitForOutputLineRegexp";

    public static final String TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_TIMEOUT = "waitForOutputLineTimeout";

    public static ProcessConfiguration createProcessConfiguration(Configuration configuration) {
        ProcessConfiguration processConfiguration = new ProcessConfiguration();

        processConfiguration.executable(configuration.getString(TAG_PROCESS_EXECUTABLE, processConfiguration.getExecutable()));
        processConfiguration.arguments(Arrays.stream(configuration.getConfigurationsAt(TAG_PROCESS_ARGUMENT)).map(Configuration::getValue)
                .collect(Collectors.toList()));
        processConfiguration.workingDir(configuration.getString(TAG_PROCESS_WORKING_DIR, processConfiguration.getWorkingDir()));
        processConfiguration.waitSeconds(configuration.getLong(TAG_PROCESS_WAIT_SECONDS, processConfiguration.getWaitSeconds()));

        String redirectTypeString = configuration.getString(TAG_PROCESS_REDIRECT_TYPE, null);
        processConfiguration.redirectType(
                redirectTypeString != null ? RedirectType.valueOf(redirectTypeString) : processConfiguration.getRedirectType());

        String charsetString = configuration.getString(TAG_PROCESS_CHARSET, null);
        processConfiguration.charset(charsetString != null ? Charset.forName(charsetString) : processConfiguration.getCharset());

        processConfiguration.waitForOutputLineRegexp(
                configuration.getString(TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_REGEXP, processConfiguration.getWaitForOutputLineRegexp()));
        processConfiguration.waitForOutputLineTimeout(
                configuration.getLong(TAG_PROCESS_WAIT_FOR_OUTPUT_LINE_TIMEOUT, processConfiguration.getWaitForOutputLineTimeout()));

        return processConfiguration;
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

        Charset finalCharset = processConfiguration.getCharset() != null ? processConfiguration.getCharset() : Charset.defaultCharset();

        // Start the process.
        ProcessInstance processInstance = null;
        try {
            logger.debug("Process environment: {}", builder.environment());

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
                logger.info("{} output:\n{}", processConfiguration.getName(), processInstance.getOutput());

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
