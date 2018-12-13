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

package org.openksavi.sponge.rpi.pi4j;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import org.openksavi.sponge.java.JPlugin;

/**
 * Pi4J plugin.
 */
public class Pi4JPlugin extends JPlugin {

    /** The default name of this plugin. */
    public static final String DEFAULT_PLUGIN_NAME = "pi4j";

    private GpioController gpio;

    private boolean shouldShutdownGpio = false;

    /**
     * Creates a new Pi4J plugin.
     */
    public Pi4JPlugin() {
        setName(DEFAULT_PLUGIN_NAME);
    }

    /**
     * Creates a new Pi4J plugin.
     *
     * @param name the plugin name.
     */
    public Pi4JPlugin(String name) {
        super(name);
    }

    /**
     * Starts up this plugin.
     */
    @Override
    public void onStartup() {
        if (gpio == null) {
            gpio = GpioFactory.getInstance();
        }
    }

    /**
     * Shuts down the plugin.
     */
    @Override
    public void onShutdown() {
        if (gpio != null && shouldShutdownGpio) {
            gpio.shutdown();
        }
    }

    public GpioController getGpio() {
        return gpio;
    }

    public void setGpio(GpioController gpio) {
        this.gpio = gpio;
    }

    public boolean isShouldShutdownGpio() {
        return shouldShutdownGpio;
    }

    public void setShouldShutdownGpio(boolean shouldShutdownGpio) {
        this.shouldShutdownGpio = shouldShutdownGpio;
    }
}
