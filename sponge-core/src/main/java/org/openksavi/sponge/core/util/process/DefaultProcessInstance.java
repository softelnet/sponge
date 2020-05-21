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
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.util.process.ProcessConfiguration;
import org.openksavi.sponge.util.process.ProcessInstance;

/**
 * A default process instance.
 */
public class DefaultProcessInstance implements ProcessInstance {

    private ProcessInstanceRuntime runtime;

    private Process internalProcess;

    private String outputString;

    private byte[] outputBinary;

    private String errorString;

    private Instant startTime;

    private boolean waitSecondsTimedOut = false;

    public DefaultProcessInstance(SpongeEngine engine, ProcessConfiguration configuration) {
        runtime = new ProcessInstanceRuntime(engine, configuration, this);
    }

    @Override
    public ProcessInstance run() throws InterruptedException {
        Validate.isTrue(internalProcess == null, "The process has already started");

        runtime.start();
        runtime.waitFor();

        return this;
    }

    @Override
    public ProcessInstance runAsync() throws InterruptedException {
        Validate.isTrue(internalProcess == null, "The process has already started");

        runtime.start();

        return this;
    }

    private void validateStarted() {
        Validate.notNull(internalProcess, "The process is not started");
    }

    @Override
    public Process getInternalProcess() {
        return internalProcess;
    }

    public void setInternalProcess(Process internalProcess) {
        this.internalProcess = internalProcess;
    }

    @Override
    public ProcessConfiguration getConfiguration() {
        return runtime.getConfiguration();
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
        validateStarted();

        runtime.waitForReady();
    }

    @Override
    public int waitFor() throws InterruptedException {
        validateStarted();

        return runtime.waitFor();
    }

    @Override
    public boolean waitFor(long timeout, TimeUnit unit) throws InterruptedException {
        validateStarted();

        return runtime.waitFor(timeout, unit);
    }

    @Override
    public void destroy() throws InterruptedException {
        if (runtime != null) {
            runtime.destroy();
        }
    }

    @Override
    public boolean destroy(long timeout, TimeUnit unit) throws InterruptedException {
        return runtime != null ? runtime.destroy(timeout, unit) : true;
    }

    @Override
    public boolean isAlive() {
        validateStarted();

        return internalProcess.isAlive();
    }

    @Override
    public int getExitCode() {
        validateStarted();

        return internalProcess.exitValue();
    }

    @Override
    public String getOutputString() {
        validateStarted();

        return outputString;
    }

    @Override
    public void setOutputString(String outputString) {
        this.outputString = outputString;
    }

    @Override
    public byte[] getOutputBinary() {
        validateStarted();

        return outputBinary;
    }

    @Override
    public void setOutputBinary(byte[] outputBinary) {
        this.outputBinary = outputBinary;
    }

    @Override
    public String getErrorString() {
        validateStarted();

        return errorString;
    }

    @Override
    public void setErrorString(String errorString) {
        this.errorString = errorString;
    }

    @Override
    public OutputStream getInput() {
        validateStarted();

        return internalProcess.getOutputStream();
    }

    @Override
    public InputStream getOutput() {
        validateStarted();

        return internalProcess.getInputStream();
    }

    @Override
    public InputStream getError() {
        validateStarted();

        return internalProcess.getErrorStream();
    }
}
