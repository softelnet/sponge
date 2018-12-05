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

/**
 * A process definition.
 */
public interface ProcessDefinition {

    /**
     * Runs a new process. Should be invoked only once. Waits if necessary (in compliance with the configuration).
     *
     * <p>If the input redirect type is STREAM you should invoke manually
     * {@link org.openksavi.sponge.util.process.ProcessInstance#waitForReady() ProcessInstance.waitForReady()} after writing to and closing
     * the subprocess standard input {@link org.openksavi.sponge.util.process.ProcessInstance#getInput() ProcessInstance.getInput()}.
     *
     * @return the process instance.
     * @throws InterruptedException on interrupted.
     */
    ProcessInstance run() throws InterruptedException;

    /**
     * Returns the process without running.
     *
     * @return the process.
     */
    ProcessInstance getProcess();
}
