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

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map;

import org.bff.javampd.admin.MPDChangeListener;
import org.bff.javampd.output.OutputChangeListener;
import org.bff.javampd.player.BitrateChangeListener;
import org.bff.javampd.player.PlayerBasicChangeListener;
import org.bff.javampd.player.TrackPositionChangeListener;
import org.bff.javampd.player.VolumeChangeListener;
import org.bff.javampd.playlist.PlaylistBasicChangeListener;
import org.bff.javampd.server.ConnectionChangeListener;
import org.bff.javampd.server.ErrorListener;

import org.openksavi.sponge.mpd.event.MpdEventCategory;

/**
 * MPD event listener manager.
 */
public class MpdListenerManager {

    /** The MPD plugin. */
    private MpdPlugin plugin;

    /** The listener map by category. */
    private Map<MpdEventCategory, Object> listenerMap = Collections.synchronizedMap(new LinkedHashMap<>());

    public MpdListenerManager(MpdPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Registers the MPD event listener for the MPD event category.
     *
     * @param category the MPD event category.
     */
    public void registerListener(MpdEventCategory category) {
        if (listenerMap.get(category) != null) {
            return;
        }

        Object listener;
        switch (category) {
        case BITRATE_CHANGE:
            listener = registerBitrateChangeListener();
            break;
        case CHANGE:
            listener = registerMPDChangeListener();
            break;
        case CONNECTION_CHANGE:
            listener = registerConnectionChangeListener();
            break;
        case ERROR:
            listener = registerErrorListener();
            break;
        case OUTPUT_CHANGE:
            listener = registerOutputChangeListener();
            break;
        case PLAYER_CHANGE:
            listener = registerPlayerChangeListener();
            break;
        case PLAYLIST_CHANGE:
            listener = registerPlaylistChangeListener();
            break;
        case TRACK_POSITION_CHANGE:
            listener = registerTrackPositionChangeListener();
            break;
        case VOLUME_CHANGE:
            listener = registerVolumeChangeListener();
            break;
        default:
            throw new IllegalArgumentException("Unsupported MPD event category " + category.getBasename());
        }

        listenerMap.put(category, listener);
    }

    /**
     * Registers all MPD event listeners.
     */
    public void registerAllListeners() {
        EnumSet.allOf(MpdEventCategory.class).stream().forEach(category -> registerListener(category));
    }

    /**
     * Registers MPD event listeners by MPD event categories.
     *
     * @param categories the MPD event categories.
     */
    public void registerListeners(MpdEventCategory... categories) {
        Arrays.stream(categories).forEach(category -> registerListener(category));
    }

    public BitrateChangeListener registerBitrateChangeListener() {
        BitrateChangeListener listener = event -> plugin.sendEvent(MpdEventCategory.BITRATE_CHANGE, event);
        plugin.getServer().getMonitor().addBitrateChangeListener(listener);

        return listener;
    }

    public MPDChangeListener registerMPDChangeListener() {
        MPDChangeListener listener = event -> plugin.sendEvent(MpdEventCategory.CHANGE, event);
        plugin.getServer().getAdmin().addMPDChangeListener(listener);

        return listener;
    }

    public ErrorListener registerErrorListener() {
        ErrorListener listener = event -> plugin.sendEvent(MpdEventCategory.ERROR, event);
        plugin.getServer().getMonitor().addErrorListener(listener);

        return listener;
    }

    public ConnectionChangeListener registerConnectionChangeListener() {
        ConnectionChangeListener listener = event -> plugin.sendEvent(MpdEventCategory.CONNECTION_CHANGE, event);
        plugin.getServer().getMonitor().addConnectionChangeListener(listener);

        return listener;
    }

    public OutputChangeListener registerOutputChangeListener() {
        OutputChangeListener listener = event -> plugin.sendEvent(MpdEventCategory.OUTPUT_CHANGE, event);
        plugin.getServer().getMonitor().addOutputChangeListener(listener);

        return listener;
    }

    public PlayerBasicChangeListener registerPlayerChangeListener() {
        PlayerBasicChangeListener listener = event -> plugin.sendEvent(MpdEventCategory.PLAYER_CHANGE, event);
        plugin.getServer().getMonitor().addPlayerChangeListener(listener);

        return listener;
    }

    public VolumeChangeListener registerVolumeChangeListener() {
        VolumeChangeListener listener = event -> plugin.sendEvent(MpdEventCategory.VOLUME_CHANGE, event);
        plugin.getServer().getMonitor().addVolumeChangeListener(listener);

        return listener;
    }

    public PlaylistBasicChangeListener registerPlaylistChangeListener() {
        PlaylistBasicChangeListener listener = event -> plugin.sendEvent(MpdEventCategory.PLAYLIST_CHANGE, event);
        plugin.getServer().getMonitor().addPlaylistChangeListener(listener);

        return listener;
    }

    public TrackPositionChangeListener registerTrackPositionChangeListener() {
        TrackPositionChangeListener listener = event -> plugin.sendEvent(MpdEventCategory.TRACK_POSITION_CHANGE, event);
        plugin.getServer().getMonitor().addTrackPositionChangeListener(listener);

        return listener;
    }

    /**
     * Removes the MPD event listener for the MPD event category.
     *
     * @param category the MPD event category.
     */
    public void removeListener(MpdEventCategory category) {
        switch (category) {
        case BITRATE_CHANGE:
            removeBitrateChangeListener();
            break;
        case CHANGE:
            removeMPDChangeListener();
            break;
        case CONNECTION_CHANGE:
            removeConnectionChangeListener();
            break;
        case ERROR:
            removeErrorListener();
            break;
        case OUTPUT_CHANGE:
            removeOutputChangeListener();
            break;
        case PLAYER_CHANGE:
            removePlayerChangeListener();
            break;
        case PLAYLIST_CHANGE:
            removePlaylistChangeListener();
            break;
        case TRACK_POSITION_CHANGE:
            removeTrackPositionChangeListener();
            break;
        case VOLUME_CHANGE:
            removeVolumeChangeListener();
            break;
        default:
            throw new IllegalArgumentException("Unsupported MPD event category " + category.getBasename());
        }

        listenerMap.remove(category);
    }

    public void removeBitrateChangeListener() {
        BitrateChangeListener listener = (BitrateChangeListener) listenerMap.get(MpdEventCategory.BITRATE_CHANGE);
        if (listener != null) {
            plugin.getServer().getMonitor().removeBitrateChangeListener(listener);
        }
    }

    public void removeMPDChangeListener() {
        MPDChangeListener listener = (MPDChangeListener) listenerMap.get(MpdEventCategory.CHANGE);
        if (listener != null) {
            plugin.getServer().getAdmin().removeMPDChangeListener(listener);
        }
    }

    public void removeErrorListener() {
        ErrorListener listener = (ErrorListener) listenerMap.get(MpdEventCategory.ERROR);
        if (listener != null) {
            plugin.getServer().getMonitor().removeErrorListener(listener);
        }
    }

    public void removeConnectionChangeListener() {
        ConnectionChangeListener listener = (ConnectionChangeListener) listenerMap.get(MpdEventCategory.CONNECTION_CHANGE);
        if (listener != null) {
            plugin.getServer().getMonitor().removeConnectionChangeListener(listener);
        }
    }

    public void removeOutputChangeListener() {
        OutputChangeListener listener = (OutputChangeListener) listenerMap.get(MpdEventCategory.OUTPUT_CHANGE);
        if (listener != null) {
            plugin.getServer().getMonitor().removeOutputChangeListener(listener);
        }
    }

    public void removePlayerChangeListener() {
        PlayerBasicChangeListener listener = (PlayerBasicChangeListener) listenerMap.get(MpdEventCategory.PLAYER_CHANGE);
        if (listener != null) {
            plugin.getServer().getMonitor().removePlayerChangeListener(listener);
        }
    }

    public void removeVolumeChangeListener() {
        VolumeChangeListener listener = (VolumeChangeListener) listenerMap.get(MpdEventCategory.VOLUME_CHANGE);
        if (listener != null) {
            plugin.getServer().getMonitor().removeVolumeChangeListener(listener);
        }
    }

    public void removePlaylistChangeListener() {
        PlaylistBasicChangeListener listener = (PlaylistBasicChangeListener) listenerMap.get(MpdEventCategory.PLAYLIST_CHANGE);
        if (listener != null) {
            plugin.getServer().getMonitor().removePlaylistChangeListener(listener);
        }
    }

    public void removeTrackPositionChangeListener() {
        TrackPositionChangeListener listener = (TrackPositionChangeListener) listenerMap.get(MpdEventCategory.TRACK_POSITION_CHANGE);
        if (listener != null) {
            plugin.getServer().getMonitor().removeTrackPositionChangeListener(listener);
        }
    }
}
