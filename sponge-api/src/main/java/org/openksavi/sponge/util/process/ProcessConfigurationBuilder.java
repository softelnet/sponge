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

/**
 * A process configuration builder.
 */
public class ProcessConfigurationBuilder {

    private ProcessConfiguration configuration;

    /**
     * Creates a new process configuration builder.
     *
     * @param executable the executable.
     */
    public ProcessConfigurationBuilder(String executable) {
        configuration = new ProcessConfiguration(executable);
    }

    /**
     * Builds the process configuration.
     *
     * @return the process configuration.
     */
    public ProcessConfiguration build() {
        return configuration;
    }

    /**
     * Sets the process display name.
     *
     * @param name the process display name.
     * @return this builder.
     */
    public ProcessConfigurationBuilder name(String name) {
        configuration.setName(name);
        return this;
    }

    /**
     * Adds the process arguments.
     *
     * @param arguments the process arguments.
     * @return this builder.
     */
    public ProcessConfigurationBuilder arguments(String... arguments) {
        arguments(Arrays.asList(arguments));
        return this;
    }

    /**
     * Adds the process arguments, ignoring {@code null} elements.
     *
     * @param arguments the process arguments.
     * @return this builder.
     */
    public ProcessConfigurationBuilder arguments(List<String> arguments) {
        configuration.getArguments().addAll(arguments.stream().filter(Objects::nonNull).collect(Collectors.toList()));
        return this;
    }

    /**
     * Sets the process working directory.
     *
     * @param workingDir the process working directory. If {@code null} (the default value) then the current directory will be used.
     * @return this builder.
     */
    public ProcessConfigurationBuilder workingDir(String workingDir) {
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
    public ProcessConfigurationBuilder env(String name, String value) {
        configuration.getEnv().put(name, value);
        return this;
    }

    /**
     * Adds the environment variables.
     *
     * @param env the environment variables.
     * @return this builder.
     */
    public ProcessConfigurationBuilder env(Map<String, String> env) {
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
    public ProcessConfigurationBuilder waitSeconds(Long waitSeconds) {
        configuration.setWaitSeconds(waitSeconds);
        return this;
    }

    /**
     * Sets the input redirect type. The default value is {@code InputRedirect.PIPE}.
     *
     * @param inputRedirect the input redirect type.
     * @return this builder.
     */
    public ProcessConfigurationBuilder inputRedirect(InputRedirect inputRedirect) {
        configuration.setInputRedirect(inputRedirect);
        return this;
    }

    /**
     * Sets the standard output redirect type. The default value is {@code OutputRedirect.PIPE}.
     *
     * @param outputRedirect the standard output redirect type.
     * @return this builder.
     */
    public ProcessConfigurationBuilder outputRedirect(OutputRedirect outputRedirect) {
        configuration.setOutputRedirect(outputRedirect);
        return this;
    }

    /**
     * Sets the error output redirect type. The default value is {@code ErrorRedirect.PIPE}.
     *
     * @param errorRedirect the error output redirect type.
     * @return this builder.
     */
    public ProcessConfigurationBuilder errorRedirect(ErrorRedirect errorRedirect) {
        configuration.setErrorRedirect(errorRedirect);
        return this;
    }

    /**
     * Sets the charset of the subprocess streams used if the redirect type is STRING.
     *
     * @param charset the charset.
     * @return this builder.
     */
    public ProcessConfigurationBuilder charset(Charset charset) {
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
    public ProcessConfigurationBuilder waitForPositiveLineRegexp(String waitForPositiveLineRegexp) {
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
    public ProcessConfigurationBuilder waitForNegativeLineRegexp(String waitForNegativeLineRegexp) {
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
    public ProcessConfigurationBuilder waitForLineTimeout(Long waitForLineTimeout) {
        configuration.setWaitForLineTimeout(waitForLineTimeout);
        return this;
    }

    /**
     * The input string that will be directed to the process standard input. Applicable only if the input redirect type is STRING.
     *
     * @param inputString the input string.
     * @return this builder.
     */
    public ProcessConfigurationBuilder inputString(String inputString) {
        configuration.setInputString(inputString);
        return this;
    }

    /**
     * The input bytes that will be directed to the process standard input. Applicable only if the input redirect type is BINARY.
     *
     * @param inputBinary the input bytes.
     * @return this builder.
     */
    public ProcessConfigurationBuilder inputBinary(byte[] inputBinary) {
        configuration.setInputBinary(inputBinary);
        return this;
    }

    /**
     * The file that will be directed to the process standard input. Applicable only if the input redirect type is FILE.
     *
     * @param inputFile the input file.
     * @return this builder.
     */
    public ProcessConfigurationBuilder inputFile(String inputFile) {
        configuration.setInputFile(inputFile);
        return this;
    }

    /**
     * The file that the process standard output will be directed to. Applicable only if the output redirect type is FILE.
     *
     * @param outputFile the output file.
     * @return this builder.
     */
    public ProcessConfigurationBuilder outputFile(String outputFile) {
        configuration.setOutputFile(outputFile);
        return this;
    }

    /**
     * The file that the process error output will be directed to. Applicable only if the error redirect type is FILE.
     *
     * @param errorFile the error file.
     * @return this builder.
     */
    public ProcessConfigurationBuilder errorFile(String errorFile) {
        configuration.setErrorFile(errorFile);
        return this;
    }

    /**
     * The process standard output line consumer. Applicable only if the output redirect type is LOGGER.
     *
     * @param outputLineConsumer the line consumer.
     * @return this builder.
     */
    public ProcessConfigurationBuilder outputLineConsumer(Consumer<String> outputLineConsumer) {
        configuration.setOutputLineConsumer(outputLineConsumer);
        return this;
    }

    /**
     * The process error output line consumer. Applicable only if the error redirect type is LOGGER.
     *
     * @param errorLineConsumer the line consumer.
     * @return this builder.
     */
    public ProcessConfigurationBuilder errorLineConsumer(Consumer<String> errorLineConsumer) {
        configuration.setErrorLineConsumer(errorLineConsumer);
        return this;
    }

    /**
     * The logging consumer for the process standard output. Applicable only if the output redirect type is LOGGER. The default logging
     * level is {@code INFO}.
     *
     * @param outputLoggingConsumer the logging consumer.
     * @return this builder.
     */
    public ProcessConfigurationBuilder outputLoggingConsumer(BiConsumer<Logger, String> outputLoggingConsumer) {
        configuration.setOutputLoggingConsumer(outputLoggingConsumer);
        return this;
    }

    /**
     * The logging consumer for the process error output. Applicable only if the error redirect type is LOGGER. The default logging level is
     * {@code WARN}.
     *
     * @param errorLoggingConsumer the logging consumer.
     * @return this builder.
     */
    public ProcessConfigurationBuilder errorLoggingConsumer(BiConsumer<Logger, String> errorLoggingConsumer) {
        configuration.setErrorLoggingConsumer(errorLoggingConsumer);
        return this;
    }

    /**
     * Sets inherit I/O.
     *
     * @return this builder.
     */
    public ProcessConfigurationBuilder inheritIo() {
        return inputRedirect(InputRedirect.INHERIT).outputRedirect(OutputRedirect.INHERIT).errorRedirect(ErrorRedirect.INHERIT);
    }

    /**
     * Provides low level access to the Java ProcessBuilder consumer.
     *
     * @param processBuilderConsumer the Java ProcessBuilder consumer.
     * @return this builder.
     */
    public ProcessConfigurationBuilder processBuilderConsumer(Consumer<ProcessBuilder> processBuilderConsumer) {
        configuration.setProcessBuilderConsumer(processBuilderConsumer);
        return this;
    }

    /**
     * Merges the error output with the standard output if {@code true}. Defaults to {@code false}.
     *
     * @param redirectErrorStream redirect error stream flag.
     * @return this builder.
     */
    public ProcessConfigurationBuilder redirectErrorStream(boolean redirectErrorStream) {
        configuration.setRedirectErrorStream(redirectErrorStream);
        return this;
    }
}
