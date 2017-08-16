/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.signal;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Support for handling Operating System signals. This class wraps {@code sun.misc.Signal} and {@code sun.misc.SignalHandler}.
 */
@SuppressWarnings("restriction")
public class SystemSignal implements SignalHandler {

    /** Signal name for SIGHUP. */
    public static final String SIGHUP = "HUP";

    /** Signal name for SIGINT. */
    public static final String SIGINT = "INT";

    /** Signal name for SIGQUIT. */
    public static final String SIGQUIT = "QUIT";

    /** Signal name for SIGILL. */
    public static final String SIGILL = "ILL";

    /** Signal name for SIGABRT. */
    public static final String SIGABRT = "ABRT";

    /** Signal name for SIGKILL. */
    public static final String SIGKILL = "KILL";

    /** Signal name for SIGALRM. */
    public static final String SIGALRM = "ALRM";

    /** Signal name for SIGTERM. */
    public static final String SIGTERM = "TERM";

    /** Signal name for SIGSTOP. */
    public static final String SIGSTOP = "STOP";

    /** Object representing a signal. */
    private Signal signal;

    /** Signal listener. */
    private SystemSignalListener listener;

    /**
     * Creates a new instance of the signal supporting class.
     *
     * @param signalName signal name.
     * @param listener signal listener.
     */
    protected SystemSignal(String signalName, SystemSignalListener listener) {
        this.listener = listener;
        signal = new Signal(signalName);

        Signal.handle(signal, this);
    }

    /**
     * Returns signal name.
     *
     * @return signal name.
     */
    public String getName() {
        return signal.getName();
    }

    /**
     * Handles a signal by calling a listener method.
     *
     * @param signal a signal.
     */
    @Override
    public void handle(Signal signal) {
        if (listener != null) {
            listener.signalReceived(this);
        }
    }

    /**
     * Returns the full name of the signal.
     *
     * @return full name of the signal.
     */
    @Override
    public String toString() {
        return signal.toString();
    }

    /**
     * Checks if the name of the handled signal is equal to {@code signalName}.
     *
     * @param signalName signal name.
     * @return {@code true} if the name of the handled signal is equal to {@code signalName}.
     */
    public boolean isSignal(String signalName) {
        return signal.getName().equals(signalName);
    }

    /**
     * Sets the listener for the signal named {@code signalName}.
     *
     * @param signalName signal name.
     * @param listener signal listener.
     * @return newly created instance of {@code SystemSignal}.
     */
    public static SystemSignal setSystemSignalListener(String signalName, SystemSignalListener listener) {
        return new SystemSignal(signalName, listener);
    }
}
