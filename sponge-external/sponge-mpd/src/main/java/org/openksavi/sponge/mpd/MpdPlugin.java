/*
 * Copyright 2016-2018 The Sponge authors.
 *
 * This file is part of Sponge MPD Support.
 *
 * Sponge MPD Support is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Sponge MPD Support is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openksavi.sponge.mpd;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bff.javampd.server.MPD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.java.JPlugin;
import org.openksavi.sponge.mpd.event.MpdEvent;
import org.openksavi.sponge.mpd.event.MpdEventCategory;

/**
 * MPD (Music Player Daemon) plugin.
 */
public class MpdPlugin extends JPlugin {

    private static final Logger logger = LoggerFactory.getLogger(MpdPlugin.class);

    /** The default name of this plugin. */
    public static final String DEFAULT_PLUGIN_NAME = "mpd";

    /** The MPD server hostname. */
    private String hostname;

    /** The MPD server port. */
    private Integer port;

    /** The MPD server password. */
    private String password;

    /** The MPD server timeout. */
    private Integer timeout;

    /** The MPD-based Sponge event name prefix. */
    private String eventNamePrefix = MpdConstants.DEFAULT_EVENT_NAME_PREFIX;

    /** The auto connect flag. If {@code true} (the default value), the plugin connects to the MPD server on startup. */
    private boolean autoConnect = MpdConstants.DEFAULT_AUTO_CONNECT;

    /** The representation of a connection to a MPD server. */
    private MPD server;

    /** The MPD event listener manager. */
    private MpdListenerManager listenerManager = new MpdListenerManager(this);

    /** The lock. */
    private Lock lock = new ReentrantLock(true);

    /**
     * Creates a new MPD plugin.
     */
    public MpdPlugin() {
        setName(DEFAULT_PLUGIN_NAME);
    }

    /**
     * Creates a new MPD plugin.
     *
     * @param name the plugin name.
     */
    public MpdPlugin(String name) {
        super(name);
    }

    /**
     * Applies the XML configuration to this plugin.
     */
    @Override
    public void onConfigure(Configuration configuration) {
        hostname = configuration.getString(MpdConstants.TAG_HOSTNAME, hostname);
        port = configuration.getInteger(MpdConstants.TAG_PORT, port);
        password = configuration.getString(MpdConstants.TAG_PASSWORD, password);
        timeout = configuration.getInteger(MpdConstants.TAG_TIMEOUT, timeout);
        autoConnect = configuration.getBoolean(MpdConstants.TAG_AUTO_CONNECT, autoConnect);
    }

    /**
     * Starts up this plugin.
     */
    @Override
    public void onStartup() {
        if (autoConnect) {
            connect();
        }
    }

    /**
     * Connects to the MPD server.
     */
    public void connect() {
        lock.lock();
        try {
            if (server == null) {
                logger.info("Connecting to the MPD server hostname={}, port={}", hostname, port);
                server = createMpd();
                server.getMonitor().start();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Disconnects from the MPD server.
     */
    public void disconnect() {
        lock.lock();
        try {
            if (server != null && !server.isClosed()) {
                if (!server.getMonitor().isDone()) {
                    server.getMonitor().stop();
                }

                server.close();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Creates the MPD server representation.
     *
     * @return the MPD server representation.
     */
    protected MPD createMpd() {
        MPD.Builder builder = new MPD.Builder();

        if (hostname != null) {
            builder.server(hostname);
        }
        if (port != null) {
            builder.port(port);
        }
        if (password != null) {
            builder.password(password);
        }
        if (timeout != null) {
            builder.timeout(timeout);
        }

        return builder.build();
    }

    /**
     * Sends a new MPD-based Sponge event to the engine.
     *
     * @param category the MPD event category.
     * @param source the source MPD event.
     * @param <T> source event type.
     */
    protected <T> void sendEvent(MpdEventCategory category, T source) {
        getEngineOperations().event(
                new MpdEvent<T>(eventNamePrefix + category.getBasename(), getEngine().getDefaultParameters().getEventClonePolicy(), source))
                .send();
    }

    /**
     * Shuts down the plugin.
     */
    @Override
    public void onShutdown() {
        disconnect();
    }

    /**
     * Returns the MPD server hostname.
     *
     * @return the MPD server hostname.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * Sets the MPD server hostname.
     *
     * @param hostname the MPD server hostname.
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * Returns the MPD server port.
     *
     * @return the MPD server port.
     */
    public Integer getPort() {
        return port;
    }

    /**
     * Sets the MPD server port.
     *
     * @param port the MPD server port.
     */
    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * Returns the MPD server password.
     *
     * @return the MPD server password.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets the MPD server password.
     *
     * @param password the MPD server password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Returns the MPD server timeout.
     *
     * @return the MPD server timeout.
     */
    public Integer getTimeout() {
        return timeout;
    }

    /**
     * Sets the MPD server timeout.
     *
     * @param timeout the MPD server timeout.
     */
    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    /**
     * Returns the MPD-based Sponge event name prefix.
     *
     * @return the MPD-based Sponge event name prefix.
     */
    public String getEventNamePrefix() {
        return eventNamePrefix;
    }

    /**
     * Sets the MPD-based Sponge event name prefix.
     *
     * @param eventNamePrefix the MPD-based Sponge event name prefix.
     */
    public void setEventNamePrefix(String eventNamePrefix) {
        this.eventNamePrefix = eventNamePrefix;
    }

    /**
     * Returns the auto connect flag.
     *
     * @return {@code true} if the plugin is to connect to the MPD server on startup.
     */
    public boolean isAutoConnect() {
        return autoConnect;
    }

    /**
     * Sets the auto connect flag.
     *
     * @param autoConnect {@code true} if the plugin is to connect to the MPD server on startup.
     */
    public void setAutoConnect(boolean autoConnect) {
        this.autoConnect = autoConnect;
    }

    /**
     * Returns the MPD server representation.
     *
     * @return the MPD server representation.
     */
    public MPD getServer() {
        return server;
    }

    /**
     * Sets the MPD server representation.
     *
     * @param server the MPD server representation.
     */
    public void setServer(MPD server) {
        this.server = server;
    }

    /**
     * Returns the MPD event listener manager.
     *
     * @return the MPD event listener manager.
     */
    public MpdListenerManager getListenerManager() {
        return listenerManager;
    }

    /**
     * Registers MPD event listeners by MPD event categories.
     *
     * @param categories the MPD event categories.
     */
    public void registerListeners(MpdEventCategory... categories) {
        listenerManager.registerListeners(categories);
    }

    /**
     * Registers all MPD event listeners.
     */
    public void registerAllListeners() {
        listenerManager.registerAllListeners();
    }

    /**
     * Registers the MPD event listener for the MPD event category.
     *
     * @param category the MPD event category.
     */
    public void registerListener(MpdEventCategory category) {
        listenerManager.registerListener(category);
    }

    /**
     * Removes the MPD event listener for the MPD event category.
     *
     * @param category the MPD event category.
     */
    public void removeListener(MpdEventCategory category) {
        listenerManager.removeListener(category);
    }
}
