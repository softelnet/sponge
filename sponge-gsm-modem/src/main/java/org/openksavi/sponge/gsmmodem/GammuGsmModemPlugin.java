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

package org.openksavi.sponge.gsmmodem;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JPlugin;
import org.openksavi.sponge.util.process.ProcessConfiguration;
import org.openksavi.sponge.util.process.ProcessInstance;

/**
 * A GSM modem plugin that uses Gammu.
 */
public class GammuGsmModemPlugin extends JPlugin {

    /** The default name of this plugin. */
    public static final String DEFAULT_PLUGIN_NAME = "gsm";

    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new GSM modem plugin.
     */
    public GammuGsmModemPlugin() {
        setName(DEFAULT_PLUGIN_NAME);
    }

    /**
     * Creates a new GSM modem plugin.
     *
     * @param name the plugin name.
     */
    public GammuGsmModemPlugin(String name) {
        super(name);
    }

    public void sendSms(String number, String message) {
        lock.lock();
        try {
            String unicode = canEncodeGsm(message) ? null : "-unicode";
            ProcessInstance process = getEngineOperations().process(ProcessConfiguration.builder("gammu")
                    .arguments("sendsms", "TEXT", number, unicode).inputAsString(message).outputAsString()).run();

            if (process.waitFor() != 0) {
                throw new SpongeException(String.format("Exit code %d: %s", process.getExitCode(), process.getOutputString()));
            }
        } catch (InterruptedException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            lock.unlock();
        }
    }

    public boolean canEncodeGsm(String text) {
        return GsmModemUtils.canEncodeGsm(text);
    }
}
