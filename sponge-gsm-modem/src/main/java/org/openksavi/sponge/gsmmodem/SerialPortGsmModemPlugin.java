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

import org.openksavi.sponge.Experimental;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.gsmmodem.serial.SerialPortGsmModem;
import org.openksavi.sponge.gsmmodem.serial.SerialPortGsmModemConfiguration;
import org.openksavi.sponge.gsmmodem.serial.SerialPortGsmModemConstants;
import org.openksavi.sponge.java.JPlugin;

/**
 * A GSM modem plugin that uses a serial port.
 */
@Experimental
public class SerialPortGsmModemPlugin extends JPlugin {

    /** The default name of this plugin. */
    public static final String DEFAULT_PLUGIN_NAME = "gsm";

    private SerialPortGsmModemConfiguration modemConfiguration;

    private SerialPortGsmModem modem;

    /**
     * Creates a new GSM modem plugin.
     */
    public SerialPortGsmModemPlugin() {
        setName(DEFAULT_PLUGIN_NAME);
    }

    /**
     * Creates a new GSM modem plugin.
     *
     * @param name the plugin name.
     */
    public SerialPortGsmModemPlugin(String name) {
        super(name);
    }

    /**
     * Applies the XML configuration to this plugin.
     */
    @Override
    public void onConfigure(Configuration configuration) {
        SerialPortGsmModemConfiguration.Builder builder = SerialPortGsmModemConfiguration.builder();

        builder.portDescriptor(configuration.getString(SerialPortGsmModemConstants.TAG_PORT_DESCRIPTOR, null));

        modemConfiguration = builder.build();
    }

    /**
     * Starts up this plugin.
     */
    @Override
    public void onStartup() {
        if (modem == null) {
            modem = new SerialPortGsmModem(modemConfiguration);
            modem.open();
        }
    }

    /**
     * Shuts down the plugin. Closes the modem port.
     */
    @Override
    public void onShutdown() {
        if (modem != null) {
            modem.close();
        }
    }

    public SerialPortGsmModemConfiguration getModemConfiguration() {
        return modemConfiguration;
    }

    public void setModemConfiguration(SerialPortGsmModemConfiguration modemConfiguration) {
        this.modemConfiguration = modemConfiguration;
    }

    public SerialPortGsmModem getModem() {
        return modem;
    }

    public void setModem(SerialPortGsmModem modem) {
        this.modem = modem;
    }
}
