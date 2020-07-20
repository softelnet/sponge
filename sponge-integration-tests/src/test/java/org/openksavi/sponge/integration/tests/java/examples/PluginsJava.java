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

package org.openksavi.sponge.integration.tests.java.examples;

import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.examples.ConnectionPlugin;
import org.openksavi.sponge.examples.EchoPlugin;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge Base. Using plugins.
 */
public class PluginsJava extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("connectionName", null);
        getSponge().setVariable("echoConfig", null);
    }

    public static class PluginTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("e1");
        }

        @Override
        public void onRun(Event event) {
            ConnectionPlugin connectionPlugin = getSponge().getPlugin(ConnectionPlugin.class);
            getLogger().debug("Connection name is still: {}", connectionPlugin.getConnectionName());
            getSponge().setVariable("connectionName", connectionPlugin.getConnectionName());
        }
    }

    @Override
    public void onStartup() {
        ConnectionPlugin connectionPlugin = getSponge().getPlugin(ConnectionPlugin.class);
        getLogger().debug("Connection name: {}", connectionPlugin.getConnectionName());
        getSponge().event("e1").send();

        EchoPlugin echoPlugin = getSponge().getPlugin(EchoPlugin.class);
        getLogger().info("Echo plugin config: {}", echoPlugin.getEchoConfig());
        getSponge().setVariable("echoConfig", echoPlugin.getEchoConfig());
        for (int i = 0; i < echoPlugin.getCount(); i++) {
            getLogger().info("\tEcho from echo plugin: {}", echoPlugin.getEcho());
        }
    }
}
