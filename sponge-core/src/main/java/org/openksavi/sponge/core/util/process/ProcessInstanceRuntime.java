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

package org.openksavi.sponge.core.util.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ProcessBuilder.Redirect;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.util.process.ErrorRedirect;
import org.openksavi.sponge.util.process.InputRedirect;
import org.openksavi.sponge.util.process.OutputRedirect;
import org.openksavi.sponge.util.process.ProcessConfiguration;
import org.openksavi.sponge.util.process.ProcessInstance;

/**
 * A process instance runtime. Not thread safe.
 */
public class ProcessInstanceRuntime {

    private static final Logger logger = LoggerFactory.getLogger(ProcessInstanceRuntime.class);

    private static final Logger processOutputLogger =
            LoggerFactory.getLogger(ProcessInstanceRuntime.class.getPackage().getName() + ".Output");

    private static final Logger processErrorLogger =
            LoggerFactory.getLogger(ProcessInstanceRuntime.class.getPackage().getName() + ".Error");

    private SpongeEngine engine;

    private ProcessConfiguration configuration;

    private ProcessInstance instance;

    private Semaphore semaphore = new Semaphore(0, true);

    private AtomicReference<String> errorLine = new AtomicReference<>(null);

    private List<Future<?>> inputStreamLineConsumerRunnableFutures = new ArrayList<>();

    private ExecutorService executor;

    private AtomicBoolean waitForReadyFinished = new AtomicBoolean(false);

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

        if (configuration.getProcessBuilderConsumer() != null) {
            configuration.getProcessBuilderConsumer().accept(builder);
        }

        if (configuration.getInputRedirect() == InputRedirect.INHERIT) {
            builder.redirectInput(Redirect.INHERIT);
        }

        if (configuration.getOutputRedirect() == OutputRedirect.INHERIT) {
            builder.redirectOutput(Redirect.INHERIT);
        }

        if (configuration.getErrorRedirect() == ErrorRedirect.INHERIT) {
            builder.redirectError(Redirect.INHERIT);
        }

        builder.redirectErrorStream(configuration.isRedirectErrorStream());

        if (configuration.getWorkingDir() != null) {
            builder.directory(new File(configuration.getWorkingDir()));
        }

        builder.environment().putAll(configuration.getEnv());

        return builder;
    }

    protected Charset getCharset() {
        return configuration.getCharset() != null ? configuration.getCharset() : Charset.defaultCharset();
    }

    protected BiConsumer<Logger, String> getOutputLoggingConsumer() {
        return configuration.getOutputLoggingConsumer() != null ? configuration.getOutputLoggingConsumer()
                : (lineLogger, line) -> lineLogger.info(line);
    }

    protected BiConsumer<Logger, String> getErrorLoggingConsumer() {
        return configuration.getErrorLoggingConsumer() != null ? configuration.getErrorLoggingConsumer()
                : (lineLogger, line) -> lineLogger.warn(line);
    }

    protected InputStreamLineConsumerRunnable createInputStreamLineConsumerRunnable(InputStream inputStream, Consumer<String> consumer,
            Logger lineLogger, BiConsumer<Logger, String> loggingConsumer) {
        return new InputStreamLineConsumerRunnable(inputStream, (line) -> {
            loggingConsumer.accept(lineLogger, line);

            if (consumer != null) {
                consumer.accept(line);
            }

            if (configuration.getWaitForPositiveLineRegexp() != null && line.matches(configuration.getWaitForPositiveLineRegexp())) {
                semaphore.release(getRequiredFullSemaphorePermits());
            } else if (configuration.getWaitForNegativeLineRegexp() != null && line.matches(configuration.getWaitForNegativeLineRegexp())) {
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
        List<InputStreamLineConsumerRunnable> runnables = new ArrayList<>();
        if (configuration.getOutputRedirect() == OutputRedirect.LOGGER) {
            runnables.add(createInputStreamLineConsumerRunnable(instance.getInternalProcess().getInputStream(),
                    configuration.getOutputLineConsumer(), processOutputLogger, getOutputLoggingConsumer()));
        }

        if (configuration.getErrorRedirect() == ErrorRedirect.LOGGER) {
            runnables.add(createInputStreamLineConsumerRunnable(instance.getInternalProcess().getErrorStream(),
                    configuration.getErrorLineConsumer(), processErrorLogger, getErrorLoggingConsumer()));
        }

        if (!runnables.isEmpty()) {
            executor = Executors.newFixedThreadPool(runnables.size());
            runnables.forEach(runnable -> inputStreamLineConsumerRunnableFutures.add(executor.submit(runnable)));
        }
    }

    protected void optionallyWaitForOutputLine() throws InterruptedException {
        if (ProcessUtils.shouldWaitForSpecificLine(configuration)) {
            // Wait for the semaphore.
            if (configuration.getWaitForLineTimeout() == null) {
                semaphore.acquire(getRequiredFullSemaphorePermits());
            } else {
                SpongeUtils.isTrue(
                        semaphore.tryAcquire(getRequiredFullSemaphorePermits(), configuration.getWaitForLineTimeout(), TimeUnit.SECONDS),
                        "Process wait timeout exceeded");
            }

            if (errorLine.get() != null) {
                // Kill the subprocess if error detected.
                if (instance.getInternalProcess().isAlive()) {
                    instance.getInternalProcess().destroy();
                }

                throw new SpongeException("Error in the subprocess: " + errorLine.get());
            }
        }
    }

    protected String readErrorStream() throws IOException {
        try (BufferedReader errors =
                new BufferedReader(new InputStreamReader(instance.getInternalProcess().getErrorStream(), getCharset()))) {
            String errorsString = errors.lines().collect(Collectors.joining("\n")).trim();

            return errorsString.isEmpty() ? null : errorsString;
        }
    }

    protected void optionallySetOutputData() {
        try {
            switch (configuration.getErrorRedirect()) {
            case STRING:
                instance.setErrorString(readErrorStream());
                break;
            case EXCEPTION:
                // Throw exception if there is any text in the error output.
                String errorString = readErrorStream();
                if (errorString != null) {
                    throw new SpongeException(getName() + " error: " + errorString);
                }
                break;
            case FILE:
                FileUtils.copyInputStreamToFile(instance.getInternalProcess().getErrorStream(), new File(configuration.getErrorFile()));
                break;
            default:
                break;
            }

            switch (configuration.getOutputRedirect()) {
            case STRING:
                instance.setOutputString(IOUtils.readLines(instance.getInternalProcess().getInputStream(), getCharset()).stream()
                        .collect(Collectors.joining("\n")));
                break;
            case BINARY:
                instance.setOutputBinary(IOUtils.toByteArray(instance.getInternalProcess().getInputStream()));
                break;
            case FILE:
                FileUtils.copyInputStreamToFile(instance.getInternalProcess().getInputStream(), new File(configuration.getOutputFile()));
                break;
            default:
                break;
            }
        } catch (IOException e) {
            throw SpongeUtils.wrapException(getName(), e);
        }
    }

    protected String getName() {
        return configuration.getName() != null ? configuration.getName() : configuration.getExecutable();
    }

    protected void optionallySetInputData() {
        try {
            switch (configuration.getInputRedirect()) {
            case STRING:
                IOUtils.write(configuration.getInputString(), instance.getInput(), getCharset());
                instance.getInput().close();
                break;
            case BINARY:
                IOUtils.write(configuration.getInputBinary(), instance.getInput());
                instance.getInput().close();
                break;
            case FILE:
                FileUtils.copyFile(new File(configuration.getInputFile()), instance.getInput());
                instance.getInput().close();
                break;
            default:
                break;
            }
        } catch (IOException e) {
            throw SpongeUtils.wrapException(getName(), e);
        }
    }

    protected void optionallyWaitForTheProcessToEnd() throws InterruptedException {
        long elapsedSeconds = Duration.between(instance.getStartTime(), Instant.now()).getSeconds();
        if (configuration.getWaitSeconds() != null) {
            if (configuration.getWaitSeconds() > elapsedSeconds) {
                instance.setWaitSecondsTimedOut(
                        !instance.getInternalProcess().waitFor(configuration.getWaitSeconds() - elapsedSeconds, TimeUnit.SECONDS));
            }
        } else {
            if (ProcessUtils.isRedirectSavingInstantly(configuration)) {
                instance.getInternalProcess().waitFor();
            }
        }
    }

    protected void startProcess(ProcessBuilder builder) {
        logger.info("Running a new subprocess: {} {}", configuration.getExecutable(),
                configuration.getArguments().stream().collect(Collectors.joining(" ")));
        if (!configuration.getEnv().isEmpty()) {
            logger.debug("The subprocess additional environment: {}", configuration.getEnv());
        }

        try {
            instance = new DefaultProcessInstance(builder.start(), configuration, this);
        } catch (IOException e) {
            throw SpongeUtils.wrapException(getName(), e);
        }
    }

    public ProcessInstance start() throws InterruptedException {
        SpongeUtils.isTrue(instance == null, "The process has already started");

        ProcessUtils.validateProcessConfiguration(configuration);

        // Set the initial state.
        initState();

        // Start subprocess.
        startProcess(createAndConfigureProcessBuilder());

        optionallySetOutputLogger();

        if (ProcessUtils.shouldWaitForReadyInstantly(configuration)) {
            waitForReady();
        }

        return instance;
    }

    public void waitForReady() throws InterruptedException {
        if (waitForReadyFinished.get()) {
            return;
        }

        // If specified, set an input string or binary.
        optionallySetInputData();

        optionallyWaitForOutputLine();

        // If specified, set an output string or binary.
        optionallySetOutputData();

        // If specified, wait for the process.
        optionallyWaitForTheProcessToEnd();

        waitForReadyFinished.set(true);
    }

    public int waitFor() throws InterruptedException {
        waitForReady();

        return instance.getInternalProcess().waitFor();
    }

    public void destroy() throws InterruptedException {
        instance.getInternalProcess().destroyForcibly().waitFor();

        // Stop thread(s) reading the standard output and the error output.
        inputStreamLineConsumerRunnableFutures.forEach(future -> future.cancel(true));

        if (executor != null) {
            SpongeUtils.shutdownExecutorService(engine, "ProcessInstanceRuntime", executor);
        }
    }
}
