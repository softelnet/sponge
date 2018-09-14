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

import java.time.Instant;

/**
 * A process instance.
 */
public class ProcessInstance {

    private Process process;

    private ProcessConfiguration configuration;

    private String outputString;

    private byte[] outputBinary;

    private Instant startTime;

    private boolean finished = false;

    private ProcessInstanceRuntime runtime;

    public ProcessInstance(Process process, ProcessConfiguration configuration, ProcessInstanceRuntime runtime) {
        this.process = process;
        this.configuration = configuration;
        this.runtime = runtime;
        startTime = Instant.now();
    }

    public Process getProcess() {
        return process;
    }

    public ProcessConfiguration getConfiguration() {
        return configuration;
    }

    public String getOutputString() {
        return outputString;
    }

    public void setOutputString(String outputString) {
        this.outputString = outputString;
    }

    public byte[] getOutputBinary() {
        return outputBinary;
    }

    public void setOutputBinary(byte[] outputBinary) {
        this.outputBinary = outputBinary != null ? outputBinary.clone() : null;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void setStartTime(Instant startTime) {
        this.startTime = startTime;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public ProcessInstanceRuntime getRuntime() {
        return runtime;
    }

    public void destroy() {
        if (runtime != null) {
            runtime.destroy();
        }
    }
}
