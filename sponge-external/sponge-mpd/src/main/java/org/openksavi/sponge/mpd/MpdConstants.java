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

/**
 * MPD constants.
 */
public final class MpdConstants {

    /** The default MPD-based Sponge event name prefix. */
    public static final String DEFAULT_EVENT_NAME_PREFIX = "mpd";

    /** The default value of an auto connect flag. */
    public static final boolean DEFAULT_AUTO_CONNECT = true;

    /** The default value of an auto start monitor flag. */
    public static final boolean DEFAULT_AUTO_START_MONITOR = true;

    public static final String TAG_HOSTNAME = "hostname";

    public static final String TAG_PORT = "port";

    public static final String TAG_PASSWORD = "password";

    public static final String TAG_TIMEOUT = "timeout";

    public static final String TAG_AUTO_CONNECT = "autoConnect";

    public static final String TAG_AUTO_START_MONITOR = "autoStartMonitor";

    public static final long DEFAULT_MONITOR_TIMEOUT = 5;

    private MpdConstants() {
        //
    }
}
