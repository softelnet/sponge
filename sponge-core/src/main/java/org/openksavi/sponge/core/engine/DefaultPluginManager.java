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

package org.openksavi.sponge.core.engine;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.core.plugin.KnowledgeBasePluginStub;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.PluginManager;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseConstants;
import org.openksavi.sponge.kb.KnowledgeBaseInterpreter;
import org.openksavi.sponge.plugin.Plugin;

/**
 * Plugin Manager.
 */
public class DefaultPluginManager extends BaseEngineModule implements PluginManager {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPluginManager.class);

    /** Plugin map indexed by name. */
    protected Map<String, Plugin> pluginMap = Collections.synchronizedMap(new LinkedHashMap<>());

    /**
     * Creates a new Plugin Manager.
     *
     * @param engine the engine.
     */
    public DefaultPluginManager(SpongeEngine engine) {
        super("PluginManager", engine);
    }

    /**
     * Configures plugins.
     *
     * @param configuration plugins configuration.
     */
    @Override
    public void configure(Configuration configuration) {
        configuration.getChildConfigurationsOf(PluginManagerConstants.CFG_PLUGINS)
                .forEach(pluginConfig -> addPlugin(createAndConfigurePlugin(pluginConfig)));
    }

    protected boolean isValidPluginName(String name) {
        if (name == null) {
            return true;
        }

        return StringUtils.isAlphanumeric(name) && !name.equals(KnowledgeBaseConstants.VAR_ENGINE_OPERATIONS);
    }

    /**
     * Creates and configures a plugin.
     *
     * @param pluginConfig plugin configuration.
     * @return a plugin.
     */
    protected Plugin createAndConfigurePlugin(Configuration pluginConfig) {
        String pluginName = pluginConfig.getAttribute(PluginManagerConstants.CFG_PLUGIN_NAME, null);

        Validate.isTrue(!StringUtils.isBlank(pluginName), "Plugin should have a name");

        String className = pluginConfig.getAttribute(PluginManagerConstants.CFG_PLUGIN_CLASS, null);
        Validate.isTrue(!StringUtils.isBlank(className), "Plugin '%s' configuration should specify a class", pluginName);

        String knowledgeBaseName = pluginConfig.getAttribute(PluginManagerConstants.CFG_PLUGIN_KB_NAME, null);

        Plugin plugin = createPluginStub(pluginName, knowledgeBaseName, className);

        Validate.isTrue(isValidPluginName(plugin.getName()), "Invalid plugin name '%s'", plugin.getName());

        plugin.setLabel(pluginConfig.getAttribute(PluginManagerConstants.CFG_PLUGIN_LABEL, null));
        plugin.setDescription(pluginConfig.getString(PluginManagerConstants.CFG_PLUGIN_DESCRIPTION, null));

        plugin.setConfiguration(pluginConfig.getChildConfiguration(PluginManagerConstants.CFG_PLUGIN_CONFIGURATION), true);

        return plugin;
    }

    @Override
    public boolean hasPlugin(String name) {
        return pluginMap.containsKey(name);
    }

    /**
     * Adds the specified plugin. If there is an existing plugin that has the same name and the engine is already running, an exception will
     * be thrown.
     *
     * @param plugin plugin.
     */
    @Override
    public void addPlugin(Plugin plugin) {
        String name = plugin.getName();

        Validate.isTrue(!isRunning() || !pluginMap.containsKey(name), "Plugin '%s' has already been registered", plugin.getName());

        logger.debug("Adding plugin '{}'.", name);

        pluginMap.put(name, plugin);
        plugin.setEngine(getEngine());

        if (isRunning()) {
            initPlugin(plugin);
            plugin.startup();
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
     * Returns the plugin list. The result is an immutable list that prevents from locking the entire plugin map for long-running operations
     * on the list.
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
    public <T extends Plugin> T getPlugin(Class<T> cls, String name) {
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
            throw SpongeUtils.wrapException(pluginName, e);
        }
    }

    /**
     * Loads a plugin.
     *
     * @param pluginStub a plugin stub.
     * @return loaded plugin.
     */
    protected Plugin loadPlugin(KnowledgeBasePluginStub pluginStub) {
        KnowledgeBaseInterpreter interpreter = null;

        try {
            KnowledgeBase knowledgeBase = pluginStub.getKnowledgeBaseName() != null
                    ? getEngine().getKnowledgeBaseManager().getKnowledgeBase(pluginStub.getKnowledgeBaseName())
                    : getEngine().getKnowledgeBaseManager().getDefaultKnowledgeBase();

            interpreter = knowledgeBase.getInterpreter();
            Plugin plugin = interpreter.createPluginInstance(pluginStub.getPluginClassName());
            if (pluginStub.getName() != null) {
                plugin.setName(pluginStub.getName());
            }

            if (pluginStub.getLabel() != null) {
                plugin.setLabel(pluginStub.getLabel());
            }

            if (pluginStub.getDescription() != null) {
                plugin.setDescription(pluginStub.getDescription());
            }

            plugin.setEngine(getEngine());
            plugin.setKnowledgeBase(knowledgeBase);
            plugin.setConfiguration(pluginStub.getConfiguration(), true);

            return plugin;
        } catch (Throwable e) {
            throw SpongeUtils.wrapException(interpreter, e);
        }
    }

    /**
     * Starts up plugins.
     */
    @Override
    public void doStartup() {
        initPlugins();
        startupPlugins();
        definePluginVariables();
    }

    public void startupPlugins() {
        getPlugins().forEach(Plugin::startup);
    }

    protected void initPlugins() {
        getPlugins().forEach(plugin -> initPlugin(plugin));
    }

    protected void initPlugin(Plugin plugin) {
        if (plugin instanceof KnowledgeBasePluginStub) {
            KnowledgeBasePluginStub stub = (KnowledgeBasePluginStub) plugin;
            plugin = loadPlugin(stub);
            replacePlugin(stub, plugin);
        } else if (plugin.getKnowledgeBase() == null) {
            plugin.setKnowledgeBase(getEngine().getKnowledgeBaseManager().getDefaultKnowledgeBase());
        }

        plugin.onInit();
    }

    /**
     * Defines plugin variables.
     */
    public void definePluginVariables() {
        getPlugins().forEach(plugin -> definePluginVariable(plugin.getName(), plugin));
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
    public void doShutdown() {
        Lists.reverse(getPlugins()).forEach(plugin -> plugin.shutdown());
        pluginMap.clear();
    }

    /**
     * Calls onBeforeReload on plugins.
     */
    @Override
    public void onBeforeReload() {
        getPlugins().forEach(plugin -> plugin.onBeforeReload());
    }

    /**
     * Calls onAfterReload on plugins.
     */
    @Override
    public void onAfterReload() {
        definePluginVariables();

        getPlugins().forEach(plugin -> plugin.onAfterReload());
    }
}
