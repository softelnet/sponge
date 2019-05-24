/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.grpcapi.server.kb;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.java.JCorrelator;

public class GrpcApiSubscribeCorrelator extends JCorrelator {

    private GrpcApiServerPlugin plugin;

    @Override
    public void onInit() {
        plugin = getSponge().getPlugin(GrpcApiServerPlugin.class);
    }

    @Override
    public void onConfigure() {
        // Listen to all events.
        withEvent(".*").withMaxInstances(1);

        // Allow concurrent pushing of events.
        withInstanceSynchronous(false);
    }

    @Override
    public void onEvent(Event event) {
        // getLogger().debug("Event: {}, {}", event.getName(), event.getId());
        plugin.getService().pushEvent(event);
    }
}
