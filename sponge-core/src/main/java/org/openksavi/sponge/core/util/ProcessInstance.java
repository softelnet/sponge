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

import java.util.concurrent.TimeUnit;

/**
 * A process instance.
 */
public class ProcessInstance {

    private Process process;

    private ProcessConfiguration processConfiguration;

    private String output;

    public ProcessInstance(Process process, ProcessConfiguration processConfiguration, String output) {
        this.process = process;
        this.processConfiguration = processConfiguration;
        this.output = output;
    }

    public Process getProcess() {
        return process;
    }

    public ProcessConfiguration getProcessConfiguration() {
        return processConfiguration;
    }

    public String getOutput() {
        return output;
    }

    public boolean tryWaitFor() {
        if (processConfiguration.getWaitSeconds() > 0) {
            try {
                return process.waitFor(processConfiguration.getWaitSeconds(), TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                throw SpongeUtils.wrapException(e);
            }
        }

        return false;
    }
}
