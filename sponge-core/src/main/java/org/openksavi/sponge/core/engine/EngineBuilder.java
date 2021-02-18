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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openksavi.sponge.config.PropertyEntry;
import org.openksavi.sponge.core.kb.DefaultScriptKnowledgeBase;
import org.openksavi.sponge.core.kb.FileKnowledgeBaseScript;
import org.openksavi.sponge.core.kb.StringKnowledgeBaseScript;
import org.openksavi.sponge.engine.EngineParameters;
import org.openksavi.sponge.engine.ExceptionHandler;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseFileProvider;
import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.kb.ScriptKnowledgeBase;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.spi.EngineModuleProvider;
import org.openksavi.sponge.spi.EventQueueProvider;
import org.openksavi.sponge.spi.KnowledgeBaseInterpreterFactoryProvider;
import org.openksavi.sponge.spi.ProcessingUnitProvider;

/**
 * Engine builder. It provides the most common settings for the engine.
 */
public class EngineBuilder<T extends BaseSpongeEngine> {

    /** The engine. */
    protected T engine;

    /** Plugins added manually before the config file. */
    protected List<Plugin> preConfigPlugins = new ArrayList<>();

    /** Plugins added manually after the config file. */
    protected List<Plugin> postConfigPlugins = new ArrayList<>();

    /** Additional plugins list reference - pre or post config. */
    protected AtomicReference<List<Plugin>> additionalPlugins = new AtomicReference<>(preConfigPlugins);

    /** Knowledge bases added manually before the config file. */
    protected List<KnowledgeBase> preConfigKnowledgeBases = new ArrayList<>();

    /** Knowledge bases added manually after the config file. */
    protected List<KnowledgeBase> postConfigKnowledgeBases = new ArrayList<>();

    /** Additional knowledge bases list reference - pre or post config. */
    protected AtomicReference<List<KnowledgeBase>> additionalKnowledgeBases = new AtomicReference<>(preConfigKnowledgeBases);

    /** Properties. */
    protected Map<String, PropertyEntry> propertyEntries = new LinkedHashMap<>();

    private boolean initialized = false;

    /**
     * Creates a new Engine Builder.
     *
     * @param engine the engine.
     */
    public EngineBuilder(T engine) {
        this.engine = engine;
    }

    /**
     * Sets the engine name.
     *
     * @param name the engine name.
     * @return this builder.
     */
    public EngineBuilder<T> name(String name) {
        engine.setName(name);
        return this;
    }

    /**
     * Sets the engine label.
     *
     * @param label the engine label.
     * @return this builder.
     */
    public EngineBuilder<T> label(String label) {
        engine.setLabel(label);
        return this;
    }

    /**
     * Sets the engine description.
     *
     * @param description the engine description.
     * @return this builder.
     */
    public EngineBuilder<T> description(String description) {
        engine.setDescription(description);
        return this;
    }

    /**
     * Sets the engine license.
     *
     * @param license the engine license.
     * @return this builder.
     */
    public EngineBuilder<T> license(String license) {
        engine.setLicense(license);
        return this;
    }

    /**
     * Sets the module provider.
     *
     * @param moduleProvider the moduleProvider to set.
     * @return this builder.
     */
    public EngineBuilder<T> moduleProvider(EngineModuleProvider moduleProvider) {
        engine.setModuleProvider(moduleProvider);
        return this;
    }

    /**
     * Sets the knowledge base interpreter factory providers.
     *
     * @param knowledgeBaseInterpreterFactoryProviders the knowledge base interpreter factory providers.
     * @return this builder.
     */
    public EngineBuilder<T> knowledgeBaseInterpreterFactoryProviders(
            List<KnowledgeBaseInterpreterFactoryProvider> knowledgeBaseInterpreterFactoryProviders) {
        engine.setKnowledgeBaseInterpreterFactoryProviders(knowledgeBaseInterpreterFactoryProviders);
        return this;
    }

    /**
     * Sets the event queue provider.
     *
     * @param eventQueueProvider the event queue provider.
     * @return this builder.
     */
    public EngineBuilder<T> eventQueueProvider(EventQueueProvider eventQueueProvider) {
        engine.setEventQueueProvider(eventQueueProvider);
        return this;
    }

    /**
     * Sets the processing unit provider.
     *
     * @param processingUnitProvider the processing unit provider.
     * @return this builder.
     */
    public EngineBuilder<T> processingUnitProvider(ProcessingUnitProvider processingUnitProvider) {
        engine.setProcessingUnitProvider(processingUnitProvider);
        return this;
    }

    /**
     * Sets the knowledge base file provider.
     *
     * @param knowledgeBaseFileProvider the knowledge base file provider.
     * @return this builder.
     */
    public EngineBuilder<T> knowledgeBaseFileProvider(KnowledgeBaseFileProvider knowledgeBaseFileProvider) {
        engine.setKnowledgeBaseFileProvider(knowledgeBaseFileProvider);
        return this;
    }

    /**
     * Sets the configuration filename.
     *
     * @param configFilename the configuration filename.
     * @return this builder.
     */
    public EngineBuilder<T> config(String configFilename) {
        engine.setConfigurationFilename(configFilename);

        // The rest of plugins added manually will be added after the plugins defined in the configuration file.
        additionalPlugins.set(postConfigPlugins);

        // The rest of knowledge bases added manually will be added after the knowledge bases defined in the configuration file.
        additionalKnowledgeBases.set(postConfigKnowledgeBases);

        return this;
    }

    /**
     * Sets the property.
     *
     * @param name the property name.
     * @param value the property value.
     * @param variable should the property be used as a variable.
     * @param system is the property a system property.
     * @return this builder.
     */
    public EngineBuilder<T> property(String name, Object value, boolean variable, boolean system) {
        propertyEntries.put(name, new GenericPropertyEntry(value, variable, system));
        return this;
    }

    /**
     * Sets the property (that is neither a variable nor a system property).
     *
     * @param name the property name.
     * @param value the property value.
     * @return this builder.
     */
    public EngineBuilder<T> property(String name, Object value) {
        return property(name, value, false, false);
    }

    /**
     * Sets the system property (that is not a variable).
     *
     * @param name the property name.
     * @param value the property value.
     * @return this builder.
     */
    public EngineBuilder<T> systemProperty(String name, Object value) {
        return property(name, value, false, true);
    }

    /**
     * Sets the property that is also a variable.
     *
     * @param name the property name.
     * @param value the property value.
     * @return this builder.
     */
    public EngineBuilder<T> variableProperty(String name, Object value) {
        return property(name, value, true, false);
    }

    /**
     * Sets the properties.
     *
     * @param simpleProperties the properties.
     * @return this builder.
     */
    public EngineBuilder<T> properties(Map<String, Object> simpleProperties) {
        simpleProperties.forEach((name, value) -> property(name, value));
        return this;
    }

    /**
     * Sets the system properties.
     *
     * @param systemProperties the system properties.
     * @return this builder.
     */
    public EngineBuilder<T> systemProperties(Map<String, String> systemProperties) {
        systemProperties.forEach((name, value) -> systemProperty(name, value));
        return this;
    }

    /**
     * Sets the variable properties.
     *
     * @param variableProperties the variable properties.
     * @return this builder.
     */
    public EngineBuilder<T> variableProperties(Map<String, String> variableProperties) {
        variableProperties.forEach((name, value) -> variableProperty(name, value));
        return this;
    }

    /**
     * Adds the plugin.
     *
     * @param plugin the plugin.
     * @return this builder.
     */
    public EngineBuilder<T> plugin(Plugin plugin) {
        additionalPlugins.get().add(plugin);
        return this;
    }

    /**
     * Adds the plugins.
     *
     * @param plugins the plugins.
     * @return this builder.
     */
    public EngineBuilder<T> plugins(Plugin... plugins) {
        Stream.of(plugins).forEachOrdered(plugin -> plugin(plugin));
        return this;
    }

    /**
     * Adds the knowledge base.
     *
     * @param knowledgeBase the knowledge base.
     * @return this builder.
     */
    public EngineBuilder<T> knowledgeBase(KnowledgeBase knowledgeBase) {
        additionalKnowledgeBases.get().add(knowledgeBase);
        return this;
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param type the knowledge base type.
     * @param files the knowledge base files.
     * @return this builder.
     */
    public EngineBuilder<T> knowledgeBase(String name, KnowledgeBaseType type, String... files) {
        return knowledgeBase(name, type, Stream.of(files).map(file -> new FileKnowledgeBaseScript(file)).collect(Collectors.toList()));
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param files the knowledge base files.
     * @return this builder.
     */
    public EngineBuilder<T> knowledgeBase(String name, String... files) {
        return knowledgeBase(name, Stream.of(files).map(file -> new FileKnowledgeBaseScript(file)).collect(Collectors.toList()));
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param scripts the knowledge base scripts.
     * @return this builder.
     */
    public EngineBuilder<T> knowledgeBase(String name, KnowledgeBaseScript... scripts) {
        return knowledgeBase(name, null, Stream.of(scripts).collect(Collectors.toList()));
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param type the knowledge base type.
     * @param scripts the knowledge base scripts.
     * @return this builder.
     */
    public EngineBuilder<T> knowledgeBase(String name, KnowledgeBaseType type, KnowledgeBaseScript... scripts) {
        return knowledgeBase(name, type, Stream.of(scripts).collect(Collectors.toList()));
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param scripts the knowledge base scripts.
     * @return this builder.
     */
    public EngineBuilder<T> knowledgeBase(String name, List<KnowledgeBaseScript> scripts) {
        return knowledgeBase(name, null, scripts);
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param type the knowledge base type.
     * @param scripts the knowledge base scripts.
     * @return this builder.
     */
    public EngineBuilder<T> knowledgeBase(String name, KnowledgeBaseType type, List<KnowledgeBaseScript> scripts) {
        ScriptKnowledgeBase knowledgeBase = new DefaultScriptKnowledgeBase(name, type);
        scripts.forEach(script -> knowledgeBase.addScript(script));
        knowledgeBase(knowledgeBase);

        return this;
    }

    /**
     * Adds the String-based knowledge base.
     *
     * @param name the knowledge base name.
     * @param type the knowledge base type.
     * @param body the String-based knowledge base body.
     * @return this builder.
     */
    public EngineBuilder<T> knowledgeBaseString(String name, KnowledgeBaseType type, String body) {
        return knowledgeBase(name, type, Stream.of(new StringKnowledgeBaseScript(body)).collect(Collectors.toList()));
    }

    /**
     * Sets the exception handler.
     *
     * @param exceptionHandler the new exception handler.
     * @return this builder.
     */
    public EngineBuilder<T> exceptionHandler(ExceptionHandler exceptionHandler) {
        engine.setExceptionHandler(exceptionHandler);

        return this;
    }

    /**
     * Sets the default knowledge base name.
     *
     * @param defaultKnowledgeBaseName the default knowledge base name.
     * @return this builder.
     */
    public EngineBuilder<T> defaultKnowledgeBaseName(String defaultKnowledgeBaseName) {
        engine.setDefaultKnowledgeBaseName(defaultKnowledgeBaseName);

        return this;
    }

    /**
     * Returns the engine default parameters. This method allows changing the values of default parameters.
     *
     * @return the engine default parameters
     */
    public EngineParameters getEngineDefaultParameters() {
        return engine.getDefaultParameters();
    }

    public EngineBuilder<T> init() {
        if (!initialized) {
            engine.init();

            initialized = true;
        }

        return this;
    }

    /**
     * Build the engine.
     *
     * @return the engine.
     */
    public T build() {
        init();

        if (propertyEntries != null) {
            engine.getConfigurationManager().setPropertyEntries(propertyEntries);
        }

        engine.getConfigurationManager().addPreConfigPlugins(preConfigPlugins);
        engine.getConfigurationManager().addPostConfigPlugins(postConfigPlugins);

        engine.getConfigurationManager().addPreConfigKnowledgeBases(preConfigKnowledgeBases);
        engine.getConfigurationManager().addPostConfigKnowledgeBases(postConfigKnowledgeBases);

        return engine;
    }

    /**
     * Returns the engine info.
     *
     * @return the engine info.
     */
    public String getInfo() {
        return engine.getInfo();
    }
}
