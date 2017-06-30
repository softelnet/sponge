/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.core.engine;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.plugin.KnowledgeBasePluginStub;
import org.openksavi.sponge.core.util.Utils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.engine.PluginManager;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.plugin.Plugin;

/**
 * Plugin Manager.
 */
public class DefaultPluginManager extends BaseEngineModule implements PluginManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPluginManager.class);

    /** Plugin map indexed by name. */
    protected Map<String, Plugin> pluginMap = Collections.synchronizedMap(new HashMap<>());

    /**
     * Creates a new Plugin Manager.
     *
     * @param engine the engine.
     */
    public DefaultPluginManager(Engine engine) {
        super("PluginManager", engine);
    }

    /**
     * Configures plugins.
     *
     * @param configuration plugins configuration.
     */
    @Override
    public void configure(Configuration configuration) {
        Stream.of(configuration.getChildConfigurationsOf(PluginManagerConstants.CFG_PLUGINS))
                .forEach(pluginConfig -> addPlugin(createAndConfigurePlugin(pluginConfig)));
    }

    /**
     * Creates and configures a plugin.
     *
     * @param pluginConfig plugin configuration.
     * @return a plugin.
     */
    public Plugin createAndConfigurePlugin(Configuration pluginConfig) {
        String pluginName = pluginConfig.getAttribute(PluginManagerConstants.CFG_PLUGIN_NAME, null);
        if (StringUtils.isBlank(pluginName)) {
            throw new SpongeException("Plugin should have a name");
        }

        String pluginDescription = pluginConfig.getAttribute(PluginManagerConstants.CFG_PLUGIN_DESCRIPTION, null);
        if (StringUtils.isBlank(pluginDescription)) {
            pluginDescription = null;
        }

        String className = pluginConfig.getAttribute(PluginManagerConstants.CFG_PLUGIN_CLASS, null);
        if (StringUtils.isBlank(className)) {
            throw new SpongeException("Plugin configuration should specify a class: " + pluginName);
        }

        String knowledgeBaseName = pluginConfig.getAttribute(PluginManagerConstants.CFG_PLUGIN_KB_NAME, null);

        Plugin plugin = createPluginStub(pluginName, knowledgeBaseName, className);

        plugin.setDescription(pluginDescription);

        if (existsPlugin(plugin.getName())) {
            throw new SpongeException("Plugin '" + plugin.getName() + "' already exists.");
        }

        if (plugin.getName() != null && plugin.getName().equals(KnowledgeBaseConstants.VAR_ENGINE_OPERATIONS)) {
            throw new SpongeException("Invalid plugin name: " + plugin.getName());
        }

        plugin.setConfiguration(pluginConfig, true);

        return plugin;
    }

    /**
     * Checks whether a plugin with the specified name exists.
     *
     * @param name plugin name.
     * @return {@code true} if the plugin exists.
     */
    @Override
    public boolean existsPlugin(String name) {
        return pluginMap.containsKey(name);
    }

    /**
     * Adds the specified plugin.
     *
     * @param plugin plugin.
     */
    @Override
    public void addPlugin(Plugin plugin) {
        String name = plugin.getName();

        if (pluginMap.containsKey(name)) {
            throw new SpongeException("Plugin '" + name + "' already exists");
        }

        logger.debug("Adding plugin '{}'.", name);

        pluginMap.put(name, plugin);
        plugin.setEngine(engine);

        if (isRunning()) {
            initAndStartupPlugin(plugin);
            definePluginVariable(name, plugin);
        }
    }

    /**
     * Removes the specified plugin.
     *
     * @param plugin plugin.
     */
    @Override
    public void removePlugin(Plugin plugin) {
        plugin.setEngine(null);
        pluginMap.remove(plugin.getName());

        definePluginVariable(plugin.getName(), null);
    }

    /**
     * Returns plugin list.
     *
     * @return plugin list.
     */
    @Override
    public List<Plugin> getPlugins() {
        return ImmutableList.copyOf(pluginMap.values());
    }

    /**
     * Returns a plugin identified by the name.
     *
     * @param name plugin name.
     * @return plugin or {@code null} if there is no such plugin.
     */
    @Override
    public Plugin getPlugin(String name) {
        return pluginMap.get(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Plugin> T getPlugin(String name, Class<T> cls) {
        Plugin plugin = pluginMap.get(name);

        if (plugin == null) {
            return null;
        }

        if (cls.isInstance(plugin)) {
            return (T) plugin;
        }

        throw new SpongeException("Plugin '" + name + "' should be an instance of " + cls);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Plugin> T getPlugin(Class<T> cls) {
        List<T> plugins =
                pluginMap.values().stream().filter(plugin -> cls.isInstance(plugin)).map(plugin -> (T) plugin).collect(Collectors.toList());
        if (plugins.isEmpty()) {
            return null;
        } else if (plugins.size() > 1) {
            throw new SpongeException("There are more than one plugins of type " + cls);
        }

        return plugins.get(0);
    }

    /**
     * Creates a plugin stub.
     *
     * @param pluginName a plugin name.
     * @param knowledgeBaseName a knowledge base name.
     * @param pluginClassName a plugin class name.
     * @return a plugin stub.
     */
    protected Plugin createPluginStub(String pluginName, String knowledgeBaseName, String pluginClassName) {
        try {
            Plugin plugin = new KnowledgeBasePluginStub(knowledgeBaseName, pluginClassName);
            plugin.setName(pluginName);

            return plugin;
        } catch (Throwable e) {
            throw Utils.wrapException(pluginName, e);
        }
    }

    /**
     * Loads a plugin.
     *
     * @param pluginStub a plugin stub.
     * @return loaded plugin.
     */
    protected Plugin loadPlugin(KnowledgeBasePluginStub pluginStub) {
        try {
            KnowledgeBase knowledgeBase = pluginStub.getKnowledgeBaseName() != null
                    ? engine.getKnowledgeBaseManager().getKnowledgeBase(pluginStub.getKnowledgeBaseName())
                    : engine.getKnowledgeBaseManager().getDefaultKnowledgeBase();

            Plugin plugin = knowledgeBase.getInterpreter().createPluginInstance(pluginStub.getPluginClassName());
            if (pluginStub.getName() != null) {
                plugin.setName(pluginStub.getName());
            }
            if (pluginStub.getDescription() != null) {
                plugin.setDescription(pluginStub.getDescription());
            }

            plugin.setEngine(getEngine());
            plugin.setKnowledgeBase(knowledgeBase);
            plugin.setConfiguration(pluginStub.getConfiguration(), true);

            return plugin;
        } catch (Throwable e) {
            throw Utils.wrapException("loadKnowledgeBaseDefinedPlugin", e);
        }
    }

    /**
     * Starts up plugins.
     */
    @Override
    public void startup() {
        if (isRunning()) {
            return;
        }

        initAndStartupPlugins();
        definePluginVariables();

        setRunning(true);
    }

    protected void initAndStartupPlugins() {
        pluginMap.values().forEach(plugin -> initAndStartupPlugin(plugin));
    }

    protected void initAndStartupPlugin(Plugin plugin) {
        if (plugin instanceof KnowledgeBasePluginStub) {
            KnowledgeBasePluginStub stub = (KnowledgeBasePluginStub) plugin;
            plugin = loadPlugin(stub);
            replacePlugin(stub, plugin);
        } else if (plugin.getKnowledgeBase() == null) {
            plugin.setKnowledgeBase(engine.getKnowledgeBaseManager().getDefaultKnowledgeBase());
        }

        plugin.init();
        plugin.startup();
    }

    /**
     * Defines plugin variables.
     */
    protected void definePluginVariables() {
        pluginMap.values().forEach(plugin -> definePluginVariable(plugin.getName(), plugin));
    }

    protected void definePluginVariable(String name, Plugin plugin) {
        if (name != null) {
            getEngine().getKnowledgeBaseManager().setInterpreterGlobalVariable(name, plugin);
        }
    }

    protected void replacePlugin(KnowledgeBasePluginStub stub, Plugin plugin) {
        pluginMap.put(plugin.getName(), plugin);
    }

    /**
     * Shuts down plugins.
     */
    @Override
    public void shutdown() {
        if (!isRunning()) {
            return;
        }

        setRunning(false);

        pluginMap.values().forEach(plugin -> plugin.shutdown());
    }

    /**
     * Calls onBeforeReload on plugins.
     */
    @Override
    public void onBeforeReload() {
        pluginMap.values().forEach(plugin -> plugin.onBeforeReload());
    }

    /**
     * Calls onAfterReload on plugins.
     */
    @Override
    public void onAfterReload() {
        definePluginVariables();

        pluginMap.values().forEach(plugin -> plugin.onAfterReload());
    }
}
