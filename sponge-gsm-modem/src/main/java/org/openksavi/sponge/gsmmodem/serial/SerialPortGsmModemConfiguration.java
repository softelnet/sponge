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

package org.openksavi.sponge.gsmmodem.serial;

import org.apache.commons.lang3.Validate;

/**
 * A Serial port GSM modem configuration.
 */
public class SerialPortGsmModemConfiguration {

    private String portDescriptor;

    private String charsetName = SerialPortGsmModemConstants.DEFAULT_CHARSET_NAME;

    protected SerialPortGsmModemConfiguration() {
        //
    }

    public String getPortDescriptor() {
        return portDescriptor;
    }

    public void setPortDescriptor(String portDescriptor) {
        this.portDescriptor = portDescriptor;
    }

    public String getCharsetName() {
        return charsetName;
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    /**
     * A GSM modem configuration builder.
     *
     * @return the builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private SerialPortGsmModemConfiguration configuration = new SerialPortGsmModemConfiguration();

        /**
         * Builds the client.
         *
         * @return the client.
         */
        public SerialPortGsmModemConfiguration build() {
            Validate.notNull(configuration.getPortDescriptor(), "Port descriptor not set");

            return configuration;
        }

        /**
         * Sets the port descriptor.
         *
         * @param portDescriptor the port descriptor.
         * @return the builder.
         */
        public Builder portDescriptor(String portDescriptor) {
            configuration.setPortDescriptor(portDescriptor);
            return this;
        }

        /**
         * Sets the charset name.
         *
         * @param charsetName charset name.
         * @return the builder.
         */
        public Builder charsetName(String charsetName) {
            configuration.setCharsetName(charsetName);
            return this;
        }
    }
}
