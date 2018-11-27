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

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

import org.openksavi.sponge.util.process.ProcessConfiguration;
import org.openksavi.sponge.util.process.ProcessInstance;

/**
 * A default process instance.
 */
public class DefaultProcessInstance implements ProcessInstance {

    private Process internalProcess;

    private ProcessConfiguration configuration;

    private String outputString;

    private byte[] outputBinary;

    private String errorString;

    private Instant startTime;

    private boolean waitSecondsTimedOut = false;

    private ProcessInstanceRuntime runtime;

    public DefaultProcessInstance(Process internalProcess, ProcessConfiguration configuration, ProcessInstanceRuntime runtime) {
        this.internalProcess = internalProcess;
        this.configuration = configuration;
        this.runtime = runtime;
        startTime = Instant.now();
    }

    @Override
    public Process getInternalProcess() {
        return internalProcess;
    }

    @Override
    public ProcessConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public Instant getStartTime() {
        return startTime;
    }

    @Override
    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    @Override
    public boolean isWaitSecondsTimedOut() {
        return waitSecondsTimedOut;
    }

    @Override
    public void setWaitSecondsTimedOut(boolean waitSecondsTimedOut) {
        this.waitSecondsTimedOut = waitSecondsTimedOut;
    }

    public ProcessInstanceRuntime getRuntime() {
        return runtime;
    }

    @Override
    public void waitForReady() throws InterruptedException {
        runtime.waitForReady();
    }

    @Override
    public int waitFor() throws InterruptedException {
        return runtime.waitFor();
    }

    @Override
    public void destroy() throws InterruptedException {
        if (runtime != null) {
            runtime.destroy();
        }
    }

    @Override
    public boolean isAlive() {
        return internalProcess.isAlive();
    }

    @Override
    public int getExitCode() {
        return internalProcess.exitValue();
    }

    @Override
    public String getOutputString() {
        return outputString;
    }

    @Override
    public void setOutputString(String outputString) {
        this.outputString = outputString;
    }

    @Override
    public byte[] getOutputBinary() {
        return outputBinary;
    }

    @Override
    public void setOutputBinary(byte[] outputBinary) {
        this.outputBinary = outputBinary;
    }

    @Override
    public String getErrorString() {
        return errorString;
    }

    @Override
    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    @Override
    public OutputStream getInput() {
        return internalProcess.getOutputStream();
    }

    @Override
    public InputStream getOutput() {
        return internalProcess.getInputStream();
    }

    @Override
    public InputStream getError() {
        return internalProcess.getErrorStream();
    }
}
