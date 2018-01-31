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

package org.openksavi.sponge.mpd.event;

/**
 * A MPD-based Sponge event category.
 */
public enum MpdEventCategory {
    CHANGE("Change"),
    ERROR("Error"),
    BITRATE_CHANGE("BitrateChange"),
    CONNECTION_CHANGE("ConnectionChange"),
    OUTPUT_CHANGE("OutputChange"),
    PLAYER_CHANGE("PlayerChange"),
    VOLUME_CHANGE("VolumeChange"),
    PLAYLIST_CHANGE("PlaylistChange"),
    TRACK_POSITION_CHANGE("TrackPositionChange");

    /** The MPD-based Sponge event basename. */
    private String basename;

    private MpdEventCategory(String basename) {
        this.basename = basename;
    }

    /**
     * Returns the MPD-based Sponge event basename.
     *
     * @return the MPD-based Sponge event basename.
     */
    public String getBasename() {
        return basename;
    }
}
