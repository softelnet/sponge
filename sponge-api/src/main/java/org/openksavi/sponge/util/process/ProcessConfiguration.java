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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.slf4j.Logger;

import org.openksavi.sponge.SpongeException;

/**
 * A process configuration. See the {@link org.openksavi.sponge.util.process.ProcessConfigurationBuilder API}.
 */
public class ProcessConfiguration implements Cloneable {

    private String name;

    private String executable;

    private List<String> arguments = new ArrayList<>();

    private String workingDir;

    private Map<String, String> env = new LinkedHashMap<>();

    private Long waitSeconds;

    private InputRedirect inputRedirect = InputRedirect.PIPE;

    private OutputRedirect outputRedirect = OutputRedirect.PIPE;

    private ErrorRedirect errorRedirect = ErrorRedirect.PIPE;

    private Charset charset;

    private String waitForPositiveLineRegexp;

    private String waitForNegativeLineRegexp;

    private Long waitForLineTimeout;

    private String inputString;

    private byte[] inputBinary;

    private String inputFile;

    private String outputFile;

    private String errorFile;

    private Consumer<String> outputLineConsumer;

    private Consumer<String> errorLineConsumer;

    private BiConsumer<Logger, String> outputLoggingConsumer;

    private BiConsumer<Logger, String> errorLoggingConsumer;

    private Consumer<ProcessBuilder> processBuilderConsumer;

    public boolean redirectErrorStream = false;

    public boolean exceptionOnExitCode = false;

    public ProcessConfiguration(String executable) {
        this.executable = executable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExecutable() {
        return executable;
    }

    public void setExecutable(String executable) {
        this.executable = executable;
    }

    public List<String> getArguments() {
        return arguments;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public Map<String, String> getEnv() {
        return env;
    }

    public Long getWaitSeconds() {
        return waitSeconds;
    }

    public void setWaitSeconds(Long waitSeconds) {
        this.waitSeconds = waitSeconds;
    }

    public InputRedirect getInputRedirect() {
        return inputRedirect;
    }

    public void setInputRedirect(InputRedirect inputRedirect) {
        this.inputRedirect = inputRedirect;
    }

    public OutputRedirect getOutputRedirect() {
        return outputRedirect;
    }

    public void setOutputRedirect(OutputRedirect outputRedirect) {
        this.outputRedirect = outputRedirect;
    }

    public ErrorRedirect getErrorRedirect() {
        return errorRedirect;
    }

    public void setErrorRedirect(ErrorRedirect errorRedirect) {
        this.errorRedirect = errorRedirect;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public String getWaitForPositiveLineRegexp() {
        return waitForPositiveLineRegexp;
    }

    public void setWaitForPositiveLineRegexp(String waitForPositiveLineRegexp) {
        this.waitForPositiveLineRegexp = waitForPositiveLineRegexp;
    }

    public String getWaitForNegativeLineRegexp() {
        return waitForNegativeLineRegexp;
    }

    public void setWaitForNegativeLineRegexp(String waitForNegativeLineRegexp) {
        this.waitForNegativeLineRegexp = waitForNegativeLineRegexp;
    }

    public Long getWaitForLineTimeout() {
        return waitForLineTimeout;
    }

    public void setWaitForLineTimeout(Long waitForLineTimeout) {
        this.waitForLineTimeout = waitForLineTimeout;
    }

    public String getInputString() {
        return inputString;
    }

    public void setInputString(String inputString) {
        this.inputString = inputString;
    }

    public byte[] getInputBinary() {
        return inputBinary;
    }

    public void setInputBinary(byte[] inputBinary) {
        this.inputBinary = inputBinary;
    }

    public String getInputFile() {
        return inputFile;
    }

    public void setInputFile(String inputFile) {
        this.inputFile = inputFile;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public String getErrorFile() {
        return errorFile;
    }

    public void setErrorFile(String errorFile) {
        this.errorFile = errorFile;
    }

    public Consumer<String> getOutputLineConsumer() {
        return outputLineConsumer;
    }

    public void setOutputLineConsumer(Consumer<String> outputLineConsumer) {
        this.outputLineConsumer = outputLineConsumer;
    }

    public Consumer<String> getErrorLineConsumer() {
        return errorLineConsumer;
    }

    public void setErrorLineConsumer(Consumer<String> errorLineConsumer) {
        this.errorLineConsumer = errorLineConsumer;
    }

    public BiConsumer<Logger, String> getOutputLoggingConsumer() {
        return outputLoggingConsumer;
    }

    public void setOutputLoggingConsumer(BiConsumer<Logger, String> outputLoggingConsumer) {
        this.outputLoggingConsumer = outputLoggingConsumer;
    }

    public BiConsumer<Logger, String> getErrorLoggingConsumer() {
        return errorLoggingConsumer;
    }

    public void setErrorLoggingConsumer(BiConsumer<Logger, String> errorLoggingConsumer) {
        this.errorLoggingConsumer = errorLoggingConsumer;
    }

    public Consumer<ProcessBuilder> getProcessBuilderConsumer() {
        return processBuilderConsumer;
    }

    public void setProcessBuilderConsumer(Consumer<ProcessBuilder> processBuilderConsumer) {
        this.processBuilderConsumer = processBuilderConsumer;
    }

    public boolean isRedirectErrorStream() {
        return redirectErrorStream;
    }

    public void setRedirectErrorStream(boolean redirectErrorStream) {
        this.redirectErrorStream = redirectErrorStream;
    }

    public boolean isExceptionOnExitCode() {
        return exceptionOnExitCode;
    }

    public void setExceptionOnExitCode(boolean exceptionOnExitCode) {
        this.exceptionOnExitCode = exceptionOnExitCode;
    }

    @Override
    public ProcessConfiguration clone() {
        try {
            ProcessConfiguration result = (ProcessConfiguration) super.clone();

            result.arguments = new ArrayList<>(arguments);
            result.env = new LinkedHashMap<>(env);

            return result;
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }

    /**
     * Returns a new process configuration builder.
     *
     * @param executable the executable filename.
     * @param arguments the optional arguments.
     * @return the builder.
     */
    public static ProcessConfigurationBuilder builder(String executable, String... arguments) {
        return new ProcessConfigurationBuilder(executable).arguments(arguments);
    }
}
