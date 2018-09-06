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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.ProcessConfiguration.RedirectType;
import org.openksavi.sponge.engine.SpongeEngine;

/**
 * A process instance runtime. Not thread safe.
 */
public class ProcessInstanceRuntime {

    private static final Logger logger = LoggerFactory.getLogger(ProcessInstanceRuntime.class);

    private SpongeEngine engine;

    private ProcessConfiguration configuration;

    private ProcessInstance instance;

    /** The process output consumer. May be {@code null}. Applicable only if the redirect type is LOGGER. */
    private Consumer<String> outputConsumer;

    private Semaphore semaphore = new Semaphore(0, true);

    private AtomicReference<String> errorLine = new AtomicReference<>(null);

    private List<Future<?>> inputStreamLineConsumerRunnableFutures = new ArrayList<>();

    private ExecutorService executor;

    public ProcessInstanceRuntime(SpongeEngine engine, ProcessConfiguration configuration) {
        this.engine = engine;
        this.configuration = configuration;
    }

    public ProcessConfiguration getConfiguration() {
        return configuration;
    }

    public ProcessInstance getInstance() {
        return instance;
    }

    public Consumer<String> getOutputConsumer() {
        return outputConsumer;
    }

    public void setOutputConsumer(Consumer<String> outputConsumer) {
        this.outputConsumer = outputConsumer;
    }

    protected void validate() {
        SpongeUtils.isTrue(outputConsumer == null || configuration.getRedirectType() == RedirectType.LOGGER,
                "If the output consumer is provided, the redirect type must be LOGGER");

        SpongeUtils.isTrue(!shouldWaitForOutputLine() || configuration.getRedirectType() == RedirectType.LOGGER,
                "If the waiting for an output line is set, the redirect type must be LOGGER");
    }

    protected void initState() {
        semaphore.drainPermits();
        errorLine.set(null);
        inputStreamLineConsumerRunnableFutures.clear();
        executor = null;
    }

    protected ProcessBuilder createAndConfigureProcessBuilder() {
        // Configure the process builder.
        List<String> commands = new ArrayList<>();
        commands.add(configuration.getExecutable());
        commands.addAll(configuration.getArguments());

        ProcessBuilder builder = new ProcessBuilder(commands);
        if (configuration.getRedirectType() == RedirectType.INHERIT) {
            builder.inheritIO();
        }

        if (configuration.getWorkingDir() != null) {
            builder.directory(new File(configuration.getWorkingDir()));
        }

        builder.environment().putAll(configuration.getEnv());

        return builder;
    }

    protected Charset getCharset() {
        return configuration.getCharset() != null ? configuration.getCharset() : Charset.defaultCharset();
    }

    protected boolean shouldWaitForOutputLine() {
        return configuration.getWaitForOutputLineRegexp() != null || configuration.getWaitForErrorOutputLineRegexp() != null;
    }

    protected InputStreamLineConsumerRunnable createInputStreamLineConsumerRunnable(InputStream inputStream, Consumer<String> logConsumer) {
        return new InputStreamLineConsumerRunnable(inputStream, (line) -> {
            logConsumer.accept(line);

            if (outputConsumer != null) {
                outputConsumer.accept(line);
            }

            if (configuration.getWaitForOutputLineRegexp() != null && line.matches(configuration.getWaitForOutputLineRegexp())) {
                semaphore.release(getRequiredFullSemaphorePermits());
            } else if (configuration.getWaitForErrorOutputLineRegexp() != null
                    && line.matches(configuration.getWaitForErrorOutputLineRegexp())) {
                errorLine.set(line);
                semaphore.release(getRequiredFullSemaphorePermits());
            }
        }, () -> {
            semaphore.release();
        }, getCharset());
    }

    protected int getRequiredFullSemaphorePermits() {
        return inputStreamLineConsumerRunnableFutures.size();
    }

    protected void optionallySetOutputLogger() {
        if (configuration.getRedirectType() == RedirectType.LOGGER) {
            List<InputStreamLineConsumerRunnable> runnables =
                    Arrays.asList(createInputStreamLineConsumerRunnable(instance.getProcess().getInputStream(), logger::info),
                            createInputStreamLineConsumerRunnable(instance.getProcess().getErrorStream(), logger::warn));

            executor = Executors.newFixedThreadPool(runnables.size());
            runnables.forEach(runnable -> inputStreamLineConsumerRunnableFutures.add(executor.submit(runnable)));
        }
    }

    protected void optionallyWaitForOutputLine() {
        if (shouldWaitForOutputLine()) {
            // Wait for the semaphore.
            try {
                if (configuration.getWaitForOutputLineTimeout() == null) {
                    semaphore.acquire(getRequiredFullSemaphorePermits());
                } else {
                    SpongeUtils.isTrue(semaphore.tryAcquire(getRequiredFullSemaphorePermits(), configuration.getWaitForOutputLineTimeout(),
                            TimeUnit.SECONDS), "Process wait timeout exceeded");
                }
            } catch (InterruptedException e) {
                throw SpongeUtils.wrapException(e);
            }

            if (errorLine.get() != null) {
                // Kill the subprocess if error detected.
                if (instance.getProcess().isAlive()) {
                    instance.getProcess().destroy();
                }

                throw new SpongeException("Error in the subprocess: " + errorLine.get());
            }
        }
    }

    protected void optionallySetOutputData() {
        if (configuration.getRedirectType() == RedirectType.STRING || configuration.getRedirectType() == RedirectType.BINARY) {
            try (BufferedReader errors = new BufferedReader(new InputStreamReader(instance.getProcess().getErrorStream(), getCharset()))) {
                String errorsString = errors.lines().collect(Collectors.joining("\n"));
                if (!errorsString.isEmpty()) {
                    throw new SpongeException(configuration.getName() + " error: " + errorsString);
                }
            } catch (IOException e) {
                throw SpongeUtils.wrapException(configuration.getName(), e);
            }
        }

        if (configuration.getRedirectType() == RedirectType.STRING) {
            try (BufferedReader output = new BufferedReader(new InputStreamReader(instance.getProcess().getInputStream(), getCharset()))) {
                instance.setOutputString(output.lines().collect(Collectors.joining("\n")));
            } catch (IOException e) {
                throw SpongeUtils.wrapException(configuration.getName(), e);
            }
        } else if (configuration.getRedirectType() == RedirectType.BINARY) {
            try {
                instance.setOutputBinary(IOUtils.toByteArray(instance.getProcess().getInputStream()));
            } catch (IOException e) {
                throw SpongeUtils.wrapException(configuration.getName(), e);
            }
        }
    }

    protected void optionallyWaitForTheProcessToEnd() {
        long elapsedSeconds = Duration.between(instance.getStartTime(), Instant.now()).getSeconds();
        if (configuration.getWaitSeconds() != null && configuration.getWaitSeconds() > elapsedSeconds) {
            try {
                instance.setFinished(instance.getProcess().waitFor(configuration.getWaitSeconds() - elapsedSeconds, TimeUnit.SECONDS));
            } catch (InterruptedException e) {
                throw SpongeUtils.wrapException(e);
            }
        }
    }

    protected void startProcess(ProcessBuilder builder) {
        logger.info("Starting a new subprocess: {} {}", configuration.getExecutable(), configuration.getArguments());
        if (!configuration.getEnv().isEmpty()) {
            logger.debug("The subprocess additional environment: {}", configuration.getEnv());
        }

        try {
            instance = new ProcessInstance(builder.start(), configuration, this);
        } catch (IOException e) {
            throw SpongeUtils.wrapException(configuration.getName(), e);
        }
    }

    /**
     * Starts a new process. Waits the specified time if necessary. Should be invoked only once.
     *
     * @return a new process instance.
     */
    public ProcessInstance start() {
        SpongeUtils.isTrue(instance == null, "The process has already started");

        validate();

        // Set the initial state.
        initState();

        // Start subprocess.
        startProcess(createAndConfigureProcessBuilder());

        optionallySetOutputLogger();

        optionallyWaitForOutputLine();

        // If specified, set the output string or binary.
        optionallySetOutputData();

        // If specified, wait for the process.
        optionallyWaitForTheProcessToEnd();

        return getInstance();
    }

    public void destroy() {
        instance.getProcess().destroyForcibly();

        // Stop thread(s) reading the standard output and the error output.
        inputStreamLineConsumerRunnableFutures.forEach(future -> future.cancel(true));

        if (executor != null) {
            SpongeUtils.shutdownExecutorService(engine, "ProcessInstanceRuntime", executor);
        }
    }
}