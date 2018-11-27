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

import java.nio.charset.Charset;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fazecast.jSerialComm.SerialPort;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.Experimental;

@Experimental
public class SerialPortGsmModem {

    private static final Logger logger = LoggerFactory.getLogger(SerialPortGsmModem.class);

    private SerialPortGsmModemConfiguration configuration;

    private SerialPort serialPort;

    private Charset charset;

    /** The lock. */
    private Lock lock = new ReentrantLock(true);

    public SerialPortGsmModem(SerialPortGsmModemConfiguration configuration) {
        this.configuration = configuration;
    }

    public SerialPortGsmModem(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void open() {
        if (serialPort == null) {
            Validate.notNull(configuration, "Either a configuration or a serial port must be set");

            serialPort = SerialPort.getCommPort(configuration.getPortDescriptor());
        }

        charset = Charset.forName(configuration != null && configuration.getCharsetName() != null ? configuration.getCharsetName()
                : SerialPortGsmModemConstants.DEFAULT_CHARSET_NAME);

        Validate.isTrue(serialPort.openPort(), "The port %s couldn't be opened", serialPort.getSystemPortName());
    }

    public void close() {
        if (serialPort != null) {
            Validate.isTrue(serialPort.closePort(), "The port couldn't be closed");
        }
    }

    protected void validateSerialPort() {
        Validate.notNull(serialPort, "Serial port is not opened");
    }

    public SerialPort getSerialPort() {
        return serialPort;
    }

    public void setSerialPort(SerialPort serialPort) {
        this.serialPort = serialPort;
    }

    public void write(String message) {
        write(message.getBytes(charset));
    }

    public void write(byte data) {
        write(new byte[] { data });
    }

    public void write(byte[] bytes) {
        validateSerialPort();

        lock.lock();
        try {
            Validate.isTrue(serialPort.writeBytes(bytes, bytes.length) == bytes.length, "Writing bytes failure");
        } finally {
            lock.unlock();
        }
    }

    public byte[] readBytes() {
        validateSerialPort();

        byte[] bytes = new byte[serialPort.bytesAvailable()];
        Validate.isTrue(serialPort.readBytes(bytes, bytes.length) == bytes.length, "Reading bytes failure");
        return bytes;
    }

    public String readString(Charset charset) {
        String response = new String(readBytes(), charset);
        logger.debug("Read: {}", response);

        return response;
    }

    public String readString(String charsetName) {
        return readString(Charset.forName(charsetName));
    }

    public String readString() {
        return readString(charset);
    }

    public void sendAtCmd(String cmd) {
        logger.debug("Writing: {}", cmd);
        write("AT" + cmd + "\r");
    }
}
