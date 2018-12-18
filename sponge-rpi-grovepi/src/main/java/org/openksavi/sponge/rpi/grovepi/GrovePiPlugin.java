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

package org.openksavi.sponge.rpi.grovepi;

import java.io.IOException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.iot.raspberry.grovepi.GrovePi;
import org.iot.raspberry.grovepi.pi4j.GrovePi4J;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JPlugin;

/**
 * GrovePi plugin. GrovePi is an open source platform for connecting Grove Sensors to the Raspberry Pi. It uses
 * https://github.com/DexterInd/GrovePi/tree/master/Software/Java8.
 */
public class GrovePiPlugin extends JPlugin {

    /** The default name of this plugin. */
    public static final String DEFAULT_PLUGIN_NAME = "grovepi";

    /** https://forum.predix.io/questions/19383/ioexception-error-writing-to-i2cdevice-on-i2cbus-g.html */
    private static final String I2C_ERROR = "Error writing to I2CDevice on I2CBus '1' ('/dev/i2c-1') at address 0x4. Got '-20001'.";

    private GrovePi device;

    private Lock lock = new ReentrantLock(true);

    private int errorRetryCount = 3;

    /**
     * Creates a new GrovePi plugin.
     */
    public GrovePiPlugin() {
        setName(DEFAULT_PLUGIN_NAME);
    }

    /**
     * Creates a new GrovePi plugin.
     *
     * @param name the plugin name.
     */
    public GrovePiPlugin(String name) {
        super(name);
    }

    /**
     * Starts up this plugin.
     */
    @Override
    public void onStartup() {
        try {
            device = new GrovePi4J();
        } catch (IOException e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    /**
     * Shuts down the plugin.
     */
    @Override
    public void onShutdown() {
        if (device != null) {
            device.close();
        }
    }

    public GrovePi getDevice() {
        return device;
    }

    protected boolean isRetryError(Throwable e) {
        IOException ioException =
                (IOException) ExceptionUtils.getThrowableList(e).stream().filter(IOException.class::isInstance).findFirst().orElse(null);

        return ioException != null && I2C_ERROR.equals(ioException.getMessage());
    }

    public <T> T get(Supplier<T> command) {
        lock.lock();
        try {
            Throwable exception = null;

            for (int i = 0; i < errorRetryCount; i++) {
                try {
                    return command.get();
                } catch (Throwable e) {
                    exception = e;

                    if (!isRetryError(e)) {
                        break;
                    }
                }
            }

            throw SpongeUtils.wrapException(GrovePiPlugin.class.getSimpleName(), exception);
        } finally {
            lock.unlock();
        }
    }

    public void set(Runnable command) {
        lock.lock();
        try {
            Throwable exception = null;

            for (int i = 0; i < errorRetryCount; i++) {
                try {
                    command.run();

                    return;
                } catch (Throwable e) {
                    exception = e;

                    if (!isRetryError(e)) {
                        break;
                    }
                }
            }

            throw SpongeUtils.wrapException(GrovePiPlugin.class.getSimpleName(), exception);
        } finally {
            lock.unlock();
        }
    }
}
