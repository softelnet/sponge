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

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.java.JKnowledgeBase;
import org.openksavi.sponge.java.JPlugin;
import org.openksavi.sponge.java.JTrigger;

/**
 * Sponge Knowledge Base. Defining plugins in a Kotlin knowledge base.
 */
public class PluginsKb extends JKnowledgeBase {

    @Override
    public void onInit() {
        // Variables for assertions only
        getSponge().setVariable("valueBefore", null);
        getSponge().setVariable("valueAfter", null);
    }

    /** Example plugin defined in the Kotlin knowledge base. */
    public static class JavaKbPlugin extends JPlugin {

        private Object storedValue;

        @Override
        public void onConfigure(Configuration configuration) {
            storedValue = configuration.getString("storedValue", "default");
        }

        @Override
        public void onInit() {
            getLogger().debug("Initializing {}", getName());
        }

        @Override
        public void onStartup() {
            getLogger().debug("Starting up {}", getName());
        }
    }

    public static class PluginTrigger extends JTrigger {

        @Override
        public void onConfigure() {
            withEvent("e1");
        }

        @Override
        public void onRun(Event event) {
            JavaKbPlugin javaKbPlugin = getSponge().getPlugin(JavaKbPlugin.class);
            Object valueBefore = javaKbPlugin.storedValue;
            getLogger().info("Plugin stored value: {}", valueBefore);
            getSponge().setVariable("valueBefore", valueBefore);
            javaKbPlugin.storedValue = event.get("value");
            Object valueAfter = javaKbPlugin.storedValue;
            getLogger().info("New stored value: {}", valueAfter);
            getSponge().setVariable("valueAfter", valueAfter);
        }
    }

    @Override
    public void onStartup() {
        getSponge().event("e1").set("value", "Value B").send();
    }
}
