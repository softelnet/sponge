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

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;

/**
 * This example program shows MPD events.
 */
public class MpdEventsMain {

    public void run() {
        SpongeEngine engine = DefaultSpongeEngine.builder().config("examples/mpd/mpd_events.xml").build();

        try {
            engine.startup();
            SpongeUtils.registerShutdownHook(engine);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Main method.
     *
     * @param args arguments.
     */
    public static void main(String... args) {
        new MpdEventsMain().run();
    }
}
