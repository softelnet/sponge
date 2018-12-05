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

import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;

/**
 * A process instance.
 */
public interface ProcessInstance {

    /**
     * Returns the underlying process.
     *
     * @return the process.
     */
    Process getInternalProcess();

    /**
     * Returns the process configuration.
     *
     * @return the process configuration.
     */
    ProcessConfiguration getConfiguration();

    /**
     * Returns the process standard output as a string.
     *
     * @return the process standard output as a string.
     */
    String getOutputString();

    /**
     * Sets the process standard output as a string.
     *
     * @param outputString the process standard output as a string.
     */
    void setOutputString(String outputString);

    /**
     * Returns the process standard output as bytes.
     *
     * @return the process standard output as bytes.
     */
    byte[] getOutputBinary();

    /**
     * Sets the process standard output as bytes.
     *
     * @param outputBinary the process standard output as bytes.
     */
    void setOutputBinary(byte[] outputBinary);

    /**
     * Returns the process error output as a string.
     *
     * @return the process error output as a string.
     */
    String getErrorString();

    /**
     * Sets the process error output as a string.
     *
     * @param errorString the process error output as a string.
     */
    void setErrorString(String errorString);

    /**
     * Returns the process start time.
     *
     * @return the process start time.
     */
    Instant getStartTime();

    /**
     * Sets the process start time.
     *
     * @param startTime the process start time.
     */
    void setStartTime(Instant startTime);

    /**
     * Returns {@code true} if the process exited before the waiting time specified by {@linkplain ProcessConfiguration#getWaitSeconds()}
     * elapsed.
     *
     * @return the timeout info.
     */
    boolean isWaitSecondsTimedOut();

    /**
     * Sets the info that tells if the process exited before the waiting time specified by
     * {@linkplain ProcessConfiguration#getWaitSeconds()} elapsed.
     *
     * @param waitSecondsTimedOut {@code true} if the timeout happened.
     */
    void setWaitSecondsTimedOut(boolean waitSecondsTimedOut);

    /**
     * Returns the process standard input.
     *
     * @return the process standard input.
     */
    OutputStream getInput();

    /**
     * Returns the process standard output.
     *
     * @return the process standard output.
     */
    InputStream getOutput();

    /**
     * Returns the process error output.
     *
     * @return the process error output.
     */
    InputStream getError();

    /**
     * Runs a new process. Should be invoked only once. Waits if necessary (in compliance with the configuration).
     *
     * <p>If the input redirect type is STREAM you should invoke manually
     * {@link org.openksavi.sponge.util.process.ProcessInstance#waitForReady() ProcessInstance.waitForReady()} after writing to and closing
     * the subprocess standard input {@link org.openksavi.sponge.util.process.ProcessInstance#getInput() ProcessInstance.getInput()}.
     *
     * @return this process instance.
     * @throws InterruptedException on interrupted.
     */
    ProcessInstance run() throws InterruptedException;

    /**
     * Waits until the process is ready. A process readiness depends on the configuration. In most cases this method is invoked
     * automatically after the start of the process and doesn't have to be invoked explicitly.
     *
     * @throws InterruptedException on interrupted.
     */
    void waitForReady() throws InterruptedException;

    /**
     * Invokes {@code waitFor()} in the underlying process. If not run previously, invokes {@code waitForReady()} first.
     *
     * @return the exit value of the subprocess.
     * @throws InterruptedException on interrupted.
     */
    int waitFor() throws InterruptedException;

    /**
     * Tests whether the subprocess is alive.
     *
     * @return {@code true} if the subprocess is alive.
     */
    boolean isAlive();

    /**
     * Kills the subprocess.
     *
     * @throws InterruptedException on interrupted.
     */
    void destroy() throws InterruptedException;

    /**
     * Returns the exit value for the subprocess.
     *
     * @return the exit value for the subprocess.
     */
    int getExitCode();
}
