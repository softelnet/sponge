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

package org.openksavi.sponge.util.process;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;

import org.openksavi.sponge.engine.SpongeEngine;

/**
 * A process instance builder.
 */
public class ProcessInstanceBuilder {

    private SpongeEngine engine;

    private ProcessConfiguration configuration;

    /**
     * Creates a new process instance builder.
     *
     * @param engine the engine.
     * @param executable the executable.
     */
    public ProcessInstanceBuilder(SpongeEngine engine, String executable) {
        this(engine, new ProcessConfiguration(executable));
    }

    public ProcessInstanceBuilder(SpongeEngine engine, ProcessConfiguration configuration) {
        this.engine = engine;
        this.configuration = configuration;
    }

    /**
     * Returns the process configuration.
     *
     * @return the process configuration.
     */
    public ProcessConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * Builds the process instance but doesn't run it.
     *
     * @return the process instance.
     */
    public ProcessInstance build() {
        return engine.createProcessInstance(configuration);
    }

    /**
     * Runs a new process synchronously. Should be invoked only once.
     *
     * <p>If the input redirect type is STREAM you should invoke {@link org.openksavi.sponge.util.process.ProcessInstance#runAsync()
     * ProcessInstance.runAsync()}.
     *
     * @return the process instance.
     * @throws InterruptedException on interrupted.
     */
    public ProcessInstance run() throws InterruptedException {
        return build().run();
    }

    /**
     * Runs a new process asynchronously. Should be invoked only once. Waits if necessary (in compliance with the configuration).
     *
     * <p>If the input redirect type is STREAM you should invoke manually
     * {@link org.openksavi.sponge.util.process.ProcessInstanceBuilder#waitForReady() ProcessInstanceBuilder.waitForReady()} after writing
     * to and closing the subprocess standard input {@link org.openksavi.sponge.util.process.ProcessInstance#getInput()
     * ProcessInstance.getInput()}.
     *
     * @return the process instance.
     * @throws InterruptedException on interrupted.
     */
    public ProcessInstance runAsync() throws InterruptedException {
        return build().runAsync();
    }

    /**
     * Sets the process name.
     *
     * @param name the process name.
     * @return this builder.
     */
    public ProcessInstanceBuilder name(String name) {
        configuration.setName(name);
        return this;
    }

    /**
     * Adds the process arguments.
     *
     * @param arguments the process arguments.
     * @return this builder.
     */
    public ProcessInstanceBuilder arguments(String... arguments) {
        arguments(Arrays.asList(arguments));
        return this;
    }

    /**
     * Adds the process arguments, ignoring {@code null} elements.
     *
     * @param arguments the process arguments.
     * @return this builder.
     */
    public ProcessInstanceBuilder arguments(List<String> arguments) {
        configuration.getArguments().addAll(arguments.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        return this;
    }

    /**
     * Sets the process working directory.
     *
     * @param workingDir the process working directory. If {@code null} (the default value) then the current directory will be used.
     * @return this builder.
     */
    public ProcessInstanceBuilder workingDir(String workingDir) {
        configuration.setWorkingDir(workingDir);
        return this;
    }

    /**
     * Adds the environment variable.
     *
     * @param name the environment variable name.
     * @param value the environment variable value.
     * @return this builder.
     */
    public ProcessInstanceBuilder env(String name, String value) {
        configuration.getEnv().put(name, value);
        return this;
    }

    /**
     * Adds the environment variables.
     *
     * @param env the environment variables.
     * @return this builder.
     */
    public ProcessInstanceBuilder env(Map<String, String> env) {
        configuration.getEnv().putAll(env);
        return this;
    }

    /**
     * Sets the maximum number of seconds to wait after the start of the process. The thread that started the process will be blocked until
     * the time elapses or the subprocess exits.
     *
     * @param waitSeconds the maximum number of seconds to wait or {@code null} (the default value) if the thread shouldn't wait.
     * @return this builder.
     */
    public ProcessInstanceBuilder waitSeconds(Long waitSeconds) {
        configuration.setWaitSeconds(waitSeconds);
        return this;
    }

    /**
     * Sets the input redirect type. The default value is {@code InputRedirect.PIPE}.
     *
     * @param inputRedirect the input redirect type.
     * @return this builder.
     */
    public ProcessInstanceBuilder inputRedirect(InputRedirect inputRedirect) {
        configuration.setInputRedirect(inputRedirect);
        return this;
    }

    /**
     * Sets the standard output redirect type. The default value is {@code OutputRedirect.PIPE}.
     *
     * @param outputRedirect the standard output redirect type.
     * @return this builder.
     */
    public ProcessInstanceBuilder outputRedirect(OutputRedirect outputRedirect) {
        configuration.setOutputRedirect(outputRedirect);
        return this;
    }

    /**
     * Sets the error output redirect type. The default value is {@code ErrorRedirect.PIPE}.
     *
     * @param errorRedirect the error output redirect type.
     * @return this builder.
     */
    public ProcessInstanceBuilder errorRedirect(ErrorRedirect errorRedirect) {
        configuration.setErrorRedirect(errorRedirect);
        return this;
    }

    /**
     * Sets the charset of the subprocess streams used if the redirect type is STRING.
     *
     * @param charset the charset.
     * @return this builder.
     */
    public ProcessInstanceBuilder charset(Charset charset) {
        configuration.setCharset(charset);
        return this;
    }

    /**
     * Sets the Java regular expression of a line from the process output stream. The thread that started the process will wait (blocking)
     * for such line (or {@code waitForNegativeLineRegexp} if set).
     *
     * @param waitForPositiveLineRegexp the Java regular expression or {@code null} if the thread shouldn't wait for a specific line.
     * @return this builder.
     */
    public ProcessInstanceBuilder waitForPositiveLineRegexp(String waitForPositiveLineRegexp) {
        configuration.setWaitForPositiveLineRegexp(waitForPositiveLineRegexp);
        return this;
    }

    /**
     * Sets the Java regular expression of a line from the process output stream that signals an error and should cause throwing an
     * exception.
     *
     * @param waitForNegativeLineRegexp the Java regular expression or {@code null} if the thread shouldn't look for a specific error line.
     * @return this builder.
     */
    public ProcessInstanceBuilder waitForNegativeLineRegexp(String waitForNegativeLineRegexp) {
        configuration.setWaitForNegativeLineRegexp(waitForNegativeLineRegexp);
        return this;
    }

    /**
     * Sets the timeout for waiting for a specific line from the process output stream (in seconds). If the timeout is exceeded, the
     * exception will be thrown.
     *
     * @param waitForLineTimeout the timeout for waiting for a specific line or {@code null} if the thread could wait indefinitely.
     * @return this builder.
     */
    public ProcessInstanceBuilder waitForLineTimeout(Long waitForLineTimeout) {
        configuration.setWaitForLineTimeout(waitForLineTimeout);
        return this;
    }

    /**
     * The logging consumer for the process standard output. Applicable only if the output redirect type is CONSUMER. The default logging
     * level is {@code INFO}.
     *
     * @param outputLoggingConsumer the logging consumer.
     * @return this builder.
     */
    public ProcessInstanceBuilder outputLoggingConsumer(BiConsumer<Logger, String> outputLoggingConsumer) {
        configuration.setOutputLoggingConsumer(outputLoggingConsumer);
        return this;
    }

    /**
     * Sets the logging consumer for the process standard output to none. Applicable only if the output redirect type is CONSUMER.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder outputLoggingConsumerNone() {
        return outputLoggingConsumer((logger, line) -> {
        });
    }

    /**
     * The logging consumer for the process error output. Applicable only if the error redirect type is CONSUMER. The default logging level
     * is {@code WARN}.
     *
     * @param errorLoggingConsumer the logging consumer.
     * @return this builder.
     */
    public ProcessInstanceBuilder errorLoggingConsumer(BiConsumer<Logger, String> errorLoggingConsumer) {
        configuration.setErrorLoggingConsumer(errorLoggingConsumer);
        return this;
    }

    /**
     * Sets inherit I/O.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder inheritIo() {
        return inputRedirect(InputRedirect.INHERIT).outputRedirect(OutputRedirect.INHERIT).errorRedirect(ErrorRedirect.INHERIT);
    }

    /**
     * Provides low level access to the Java ProcessBuilder consumer.
     *
     * @param processBuilderConsumer the Java ProcessBuilder consumer.
     * @return this builder.
     */
    public ProcessInstanceBuilder processBuilderConsumer(Consumer<ProcessBuilder> processBuilderConsumer) {
        configuration.setProcessBuilderConsumer(processBuilderConsumer);
        return this;
    }

    /**
     * Merges the error output with the standard output if {@code true}. Defaults to {@code false}.
     *
     * @param redirectErrorStream redirect error stream flag.
     * @return this builder.
     */
    public ProcessInstanceBuilder redirectErrorStream(boolean redirectErrorStream) {
        configuration.setRedirectErrorStream(redirectErrorStream);
        return this;
    }

    /**
     * If {@code true}, throws exception if the process exit code is not {@code 0}. Defaults to {@code true}.
     *
     * @param exceptionOnExitCode exception on exit code flag.
     * @return this builder.
     */
    public ProcessInstanceBuilder exceptionOnExitCode(boolean exceptionOnExitCode) {
        configuration.setExceptionOnExitCode(exceptionOnExitCode);
        return this;
    }

    /**
     * A convenience method to set the input redirect type {@link org.openksavi.sponge.util.process.InputRedirect#INHERIT INHERIT}.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder inputAsInherit() {
        return inputRedirect(InputRedirect.INHERIT);
    }

    /**
     * A convenience method to set the input redirect type {@link org.openksavi.sponge.util.process.InputRedirect#STRING STRING}.
     *
     * @param inputString the input string.
     * @return this builder.
     */
    public ProcessInstanceBuilder inputAsString(String inputString) {
        configuration.setInputRedirect(InputRedirect.STRING);
        configuration.setInputString(inputString);
        return this;
    }

    /**
     * A convenience method to set the input redirect type {@link org.openksavi.sponge.util.process.InputRedirect#BINARY BINARY}.
     *
     * @param inputBinary the input binary.
     * @return this builder.
     */
    public ProcessInstanceBuilder inputAsBinary(byte[] inputBinary) {
        configuration.setInputRedirect(InputRedirect.BINARY);
        configuration.setInputBinary(inputBinary);
        return this;
    }

    /**
     * A convenience method to set the input redirect type {@link org.openksavi.sponge.util.process.InputRedirect#FILE FILE}.
     *
     * @param inputFile the input filename.
     * @return this builder.
     */
    public ProcessInstanceBuilder inputAsFile(String inputFile) {
        configuration.setInputRedirect(InputRedirect.FILE);
        configuration.setInputFile(inputFile);
        return this;
    }

    /**
     * A convenience method to set the input redirect type {@link org.openksavi.sponge.util.process.InputRedirect#STREAM STREAM}.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder inputAsStream() {
        return inputRedirect(InputRedirect.STREAM);
    }

    /**
     * A convenience method to set the output redirect type {@link org.openksavi.sponge.util.process.OutputRedirect#INHERIT INHERIT}.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder outputAsInherit() {
        return outputRedirect(OutputRedirect.INHERIT);
    }

    /**
     * A convenience method to set the output redirect type {@link org.openksavi.sponge.util.process.OutputRedirect#STRING STRING}.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder outputAsString() {
        return outputRedirect(OutputRedirect.STRING);
    }

    /**
     * A convenience method to set the output redirect type {@link org.openksavi.sponge.util.process.OutputRedirect#BINARY BINARY}.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder outputAsBinary() {
        return outputRedirect(OutputRedirect.BINARY);
    }

    /**
     * A convenience method to set the output redirect type {@link org.openksavi.sponge.util.process.OutputRedirect#FILE FILE}.
     *
     * @param outputFile the output filename.
     * @return this builder.
     */
    public ProcessInstanceBuilder outputAsFile(String outputFile) {
        configuration.setOutputRedirect(OutputRedirect.FILE);
        configuration.setOutputFile(outputFile);
        return this;
    }

    /**
     * A convenience method to set the output redirect type {@link org.openksavi.sponge.util.process.OutputRedirect#CONSUMER CONSUMER}.
     *
     * @param outputLineConsumer the line consumer.
     * @return this builder.
     */
    public ProcessInstanceBuilder outputAsConsumer(Consumer<String> outputLineConsumer) {
        configuration.setOutputRedirect(OutputRedirect.CONSUMER);
        configuration.setOutputLineConsumer(outputLineConsumer);
        return this;
    }

    /**
     * A convenience method to set the output redirect type {@link org.openksavi.sponge.util.process.OutputRedirect#CONSUMER CONSUMER}.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder outputAsConsumer() {
        return outputRedirect(OutputRedirect.CONSUMER);
    }

    /**
     * A convenience method to set the process error redirect type {@link org.openksavi.sponge.util.process.ErrorRedirect#INHERIT INHERIT}.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder errorAsInherit() {
        return errorRedirect(ErrorRedirect.INHERIT);
    }

    /**
     * A convenience method to set the process error redirect type {@link org.openksavi.sponge.util.process.ErrorRedirect#STRING STRING}.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder errorAsString() {
        return errorRedirect(ErrorRedirect.STRING);
    }

    /**
     * A convenience method to set the process error redirect type {@link org.openksavi.sponge.util.process.ErrorRedirect#FILE FILE}.
     *
     * @param errorFile the error file.
     * @return this builder.
     */
    public ProcessInstanceBuilder errorAsFile(String errorFile) {
        configuration.setErrorRedirect(ErrorRedirect.FILE);
        configuration.setErrorFile(errorFile);
        return this;
    }

    /**
     * A convenience method to set the process error redirect type {@link org.openksavi.sponge.util.process.ErrorRedirect#EXCEPTION
     * EXCEPTION}.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder errorAsException() {
        return errorRedirect(ErrorRedirect.EXCEPTION);
    }

    /**
     * A convenience method to set the process error redirect type {@link org.openksavi.sponge.util.process.ErrorRedirect#CONSUMER
     * CONSUMER}.
     *
     * @return this builder.
     */
    public ProcessInstanceBuilder errorAsConsumer() {
        return errorRedirect(ErrorRedirect.CONSUMER);
    }

    /**
     * A convenience method to set the process error redirect type {@link org.openksavi.sponge.util.process.ErrorRedirect#CONSUMER
     * CONSUMER}.
     *
     * @param errorLineConsumer the line consumer.
     * @return this builder.
     */
    public ProcessInstanceBuilder errorAsConsumer(Consumer<String> errorLineConsumer) {
        configuration.setErrorRedirect(ErrorRedirect.CONSUMER);
        configuration.setErrorLineConsumer(errorLineConsumer);
        return this;
    }
}
