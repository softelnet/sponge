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

package org.openksavi.sponge.engine;

import java.util.List;

import org.openksavi.sponge.config.Configurable;
import org.openksavi.sponge.plugin.Plugin;

/**
 * Plugin Manager.
 */
public interface PluginManager extends Configurable, EngineModule {

    /**
     * Checks whether a plugin with the specified name exists.
     *
     * @param name plugin name.
     * @return {@code true} if the plugin exists.
     */
    boolean existsPlugin(String name);

    /**
     * Adds the specified plugin.
     *
     * @param plugin plugin.
     */
    void addPlugin(Plugin plugin);

    /**
     * Removes the specified plugin.
     *
     * @param plugin plugin.
     */
    void removePlugin(Plugin plugin);

    /**
     * Returns plugin list.
     *
     * @return plugin list.
     */
    List<Plugin> getPlugins();

    /**
     * Returns a plugin identified by the name.
     *
     * @param name plugin name.
     * @return plugin or {@code null} if there is no such plugin.
     */
    Plugin getPlugin(String name);

    /**
     * Returns the plugin that has the specified name and type or {@code null} if there is no such plugin.
     *
     * @param cls plugin class.
     * @param name plugin name.
     * @return plugin.
     * @param <T> Plugin type.
     */
    <T extends Plugin> T getPlugin(Class<T> cls, String name);

    /**
     * Returns the plugin that has the specified type or {@code null} if there is no such plugin.
     *
     * @param cls plugin class.
     * @return plugin.
     * @param <T> Plugin type.
     */
    <T extends Plugin> T getPlugin(Class<T> cls);

    /**
     * Calls onBeforeReload on plugins.
     */
    void onBeforeReload();

    /**
     * Calls onAfterReload on plugins.
     */
    void onAfterReload();
}
