/*
 * Copyright 2016-2017 The Sponge authors.
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

package org.openksavi.sponge.plugin;

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.engine.EngineModule;
import org.openksavi.sponge.kb.KnowledgeBase;

/**
 * A plugin.
 */
public interface Plugin extends EngineModule {

    /**
     * Sets a plugin configuration.
     *
     * @param configuration a plugin configuration.
     * @param configure whether this configuration should be applied immediately.
     */
    void setConfiguration(Configuration configuration, boolean configure);

    /**
     * A callback method that applies a configuration.
     *
     * @param configuration configuration.
     */
    void onConfigure(Configuration configuration);

    /**
     * Returns a plugin configuration.
     *
     * @return a plugin configuration.
     */
    Configuration getConfiguration();

    /**
     * Initializes the plugin.
     */
    void onInit();

    /**
     * On startup callback method.
     */
    void onStartup();

    /**
     * On shutdown callback method.
     */
    void onShutdown();

    /**
     * Before reload callback method.
     */
    void onBeforeReload();

    /**
     * After reload callback method.
     */
    void onAfterReload();

    /**
     * Returns the knowledge base associated with this plugin.
     *
     * @return the knowledge base.
     */
    KnowledgeBase getKnowledgeBase();

    /**
     * Sets the knowledge base associated with this plugin.
     *
     * @param knowledgeBase the knowledge base.
     */
    void setKnowledgeBase(KnowledgeBase knowledgeBase);
}
