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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openksavi.sponge.config.PropertyEntry;
import org.openksavi.sponge.core.kb.DefaultScriptKnowledgeBase;
import org.openksavi.sponge.core.kb.FileKnowledgeBaseScript;
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
 * Engine builder.
 */
public class EngineBuilder<T extends BaseEngine> {

    protected T engine;

    protected List<Plugin> plugins = new ArrayList<>();

    protected List<KnowledgeBase> knowledgeBases = new ArrayList<>();

    protected Map<String, PropertyEntry> propertyEntries = new LinkedHashMap<>();

    public EngineBuilder(T engine) {
        this.engine = engine;
    }

    public EngineBuilder<T> name(String name) {
        engine.setName(name);
        return this;
    }

    public EngineBuilder<T> moduleProvider(EngineModuleProvider moduleProvider) {
        engine.setModuleProvider(moduleProvider);
        return this;
    }

    public EngineBuilder<T> knowledgeBaseInterpreterFactoryProviders(
            List<KnowledgeBaseInterpreterFactoryProvider> knowledgeBaseInterpreterFactoryProviders) {
        engine.setKnowledgeBaseInterpreterFactoryProviders(knowledgeBaseInterpreterFactoryProviders);
        return this;
    }

    public EngineBuilder<T> eventQueueProvider(EventQueueProvider eventQueueProvider) {
        engine.setEventQueueProvider(eventQueueProvider);
        return this;
    }

    public EngineBuilder<T> processingUnitProvider(ProcessingUnitProvider processingUnitProvider) {
        engine.setProcessingUnitProvider(processingUnitProvider);
        return this;
    }

    public EngineBuilder<T> knowledgeBaseFileProvider(KnowledgeBaseFileProvider knowledgeBaseFileProvider) {
        engine.setKnowledgeBaseFileProvider(knowledgeBaseFileProvider);
        return this;
    }

    public EngineBuilder<T> config(String configFilename) {
        engine.setConfigurationFilename(configFilename);
        return this;
    }

    public EngineBuilder<T> property(String name, Object value, boolean variable, boolean system) {
        propertyEntries.put(name, new GenericPropertyEntry(value, variable, system));
        return this;
    }

    public EngineBuilder<T> property(String name, Object value) {
        return property(name, value, false, false);
    }

    public EngineBuilder<T> systemProperty(String name, Object value) {
        return property(name, value, false, true);
    }

    public EngineBuilder<T> variableProperty(String name, Object value) {
        return property(name, value, true, false);
    }

    public EngineBuilder<T> properties(Map<String, Object> simpleProperties) {
        simpleProperties.forEach((name, value) -> property(name, value));
        return this;
    }

    public EngineBuilder<T> systemProperties(Map<String, String> systemProperties) {
        systemProperties.forEach((name, value) -> systemProperty(name, value));
        return this;
    }

    public EngineBuilder<T> variableProperties(Map<String, String> variableProperties) {
        variableProperties.forEach((name, value) -> variableProperty(name, value));
        return this;
    }

    public EngineBuilder<T> plugin(Plugin plugin) {
        plugins.add(plugin);
        return this;
    }

    public EngineBuilder<T> plugins(Plugin... plugins) {
        Stream.of(plugins).forEach(plugin -> plugin(plugin));
        return this;
    }

    public EngineBuilder<T> knowledgeBase(KnowledgeBase knowledgeBase) {
        knowledgeBases.add(knowledgeBase);
        return this;
    }

    public EngineBuilder<T> knowledgeBase(String name, KnowledgeBaseType type, String... files) {
        return knowledgeBase(name, type, Stream.of(files).map(file -> new FileKnowledgeBaseScript(file)).collect(Collectors.toList()));
    }

    public EngineBuilder<T> knowledgeBase(String name, String... files) {
        return knowledgeBase(name, Stream.of(files).map(file -> new FileKnowledgeBaseScript(file)).collect(Collectors.toList()));
    }

    public EngineBuilder<T> knowledgeBase(String name, List<KnowledgeBaseScript> scripts) {
        return knowledgeBase(name, null, scripts);
    }

    public EngineBuilder<T> knowledgeBase(String name, KnowledgeBaseType type, List<KnowledgeBaseScript> scripts) {
        ScriptKnowledgeBase knowledgeBase = new DefaultScriptKnowledgeBase(name, type);
        scripts.forEach(script -> knowledgeBase.addScript(script));
        knowledgeBases.add(knowledgeBase);

        return this;
    }

    public EngineBuilder<T> exceptionHandler(ExceptionHandler exceptionHandler) {
        engine.setExceptionHandler(exceptionHandler);

        return this;
    }

    public EngineParameters getEngineDefaultParameters() {
        return engine.getDefaultParameters();
    }

    public T build() {
        engine.init();

        if (propertyEntries != null) {
            engine.getConfigurationManager().setPropertyEntries(propertyEntries);
        }

        plugins.forEach(plugin -> {
            engine.getPluginManager().addPlugin(plugin);

        });

        knowledgeBases.forEach(knowledgeBase -> engine.getKnowledgeBaseManager().addKnowledgeBase(knowledgeBase));

        return engine;
    }

    public String getDescription() {
        return engine.getDescription();
    }
}
