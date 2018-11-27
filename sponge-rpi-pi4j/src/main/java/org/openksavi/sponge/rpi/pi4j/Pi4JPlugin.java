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

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.hopding.jrpicam.RPiCamera;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.java.JPlugin;

/**
 * Pi4J plugin.
 */
public class Pi4JPlugin extends JPlugin {

    /** The default name of this plugin. */
    public static final String DEFAULT_PLUGIN_NAME = "pi4j";

    private GpioController gpio;

    private boolean shouldShutdownGpio = false;

    private RPiCamera camera;

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

    public RPiCamera getCamera() {
        return camera;
    }

    public void setCamera(RPiCamera camera) {
        this.camera = camera;
    }

    public byte[] takePictureAsBytes(String format) {
        try {
            return getImageBytes(camera.takeBufferedStill(), format);
        } catch (IOException | InterruptedException e) {
            throw SpongeUtils.wrapException("takePictureAsBytes", e);
        }
    }

    public byte[] getImageBytes(BufferedImage image, String format) {
        ByteArrayOutputStream baos = null;
        byte[] bytes = null;
        try {
            baos = new ByteArrayOutputStream();
            Validate.isTrue(ImageIO.write(image, format, baos), "No appropriate image writer is found");
            baos.flush();
            bytes = baos.toByteArray();
        } catch (IOException e) {
            throw SpongeUtils.wrapException("getImageBytes", e);
        } finally {
            SpongeUtils.close(baos);
        }

        return bytes;
    }
}
