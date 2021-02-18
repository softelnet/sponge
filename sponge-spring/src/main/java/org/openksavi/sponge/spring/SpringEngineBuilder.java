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

package org.openksavi.sponge.spring;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.core.engine.EngineBuilder;
import org.openksavi.sponge.engine.ExceptionHandler;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.kb.KnowledgeBaseFileProvider;
import org.openksavi.sponge.kb.KnowledgeBaseScript;
import org.openksavi.sponge.kb.KnowledgeBaseType;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.spi.EngineModuleProvider;
import org.openksavi.sponge.spi.EventQueueProvider;
import org.openksavi.sponge.spi.KnowledgeBaseInterpreterFactoryProvider;
import org.openksavi.sponge.spi.ProcessingUnitProvider;

/**
 * SpringEngine builder.
 */
public class SpringEngineBuilder extends EngineBuilder<SpringSpongeEngine> {

    /**
     * Spring-aware Sponge engine builder. Sets {@code knowledgeBaseFileProvider} to {@code SpringKnowledgeBaseFileProvider}.
     *
     * @param engine the engine.
     */
    public SpringEngineBuilder(SpringSpongeEngine engine) {
        super(engine);

        knowledgeBaseFileProvider(new SpringKnowledgeBaseFileProvider());

        engine.addOnStartupListener(() -> {
            engine.tryEnableProcessorBeans();
        });
    }

    /**
     * Sets {@code autoStartup} meaning that if it is {@code true} (the default value), the engine will be automatically started up in the
     * {@code afterPropertiesSet} Spring callback method. Moreover the engine will be shut down in the {@code destroy} Spring callback
     * method.
     *
     * @param autoStartup the autoStartup flag.
     * @return this builder.
     */
    public SpringEngineBuilder autoStartup(boolean autoStartup) {
        engine.setAutoStartup(autoStartup);
        return this;
    }

    public SpringEngineBuilder phase(int phase) {
        engine.setPhase(phase);
        return this;
    }

    public SpringEngineBuilder processorBeansKnowledgeBaseName(String processorBeansKnowledgeBaseName) {
        engine.setProcessorBeansKnowledgeBaseName(processorBeansKnowledgeBaseName);
        return this;
    }

    /**
     * Sets the engine name.
     *
     * @param name the engine name.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder name(String name) {
        return (SpringEngineBuilder) super.name(name);
    }

    /**
     * Sets the engine label.
     *
     * @param label the engine label.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder label(String label) {
        return (SpringEngineBuilder) super.label(label);
    }

    /**
     * Sets the engine description.
     *
     * @param description the engine description.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder description(String description) {
        return (SpringEngineBuilder) super.description(description);
    }

    /**
     * Sets the engine license.
     *
     * @param license the engine license.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder license(String license) {
        return (SpringEngineBuilder) super.license(license);
    }

    /**
     * Sets the module provider.
     *
     * @param moduleProvider the moduleProvider to set.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder moduleProvider(EngineModuleProvider moduleProvider) {
        return (SpringEngineBuilder) super.moduleProvider(moduleProvider);
    }

    /**
     * Sets the knowledge base interpreter factory providers.
     *
     * @param knowledgeBaseInterpreterFactoryProviders the knowledge base interpreter factory providers.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder knowledgeBaseInterpreterFactoryProviders(
            List<KnowledgeBaseInterpreterFactoryProvider> knowledgeBaseInterpreterFactoryProviders) {
        return (SpringEngineBuilder) super.knowledgeBaseInterpreterFactoryProviders(knowledgeBaseInterpreterFactoryProviders);
    }

    /**
     * Sets the event queue provider.
     *
     * @param eventQueueProvider the event queue provider.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder eventQueueProvider(EventQueueProvider eventQueueProvider) {
        return (SpringEngineBuilder) super.eventQueueProvider(eventQueueProvider);
    }

    /**
     * Sets the processing unit provider.
     *
     * @param processingUnitProvider the processing unit provider.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder processingUnitProvider(ProcessingUnitProvider processingUnitProvider) {
        return (SpringEngineBuilder) super.processingUnitProvider(processingUnitProvider);
    }

    /**
     * Sets the knowledge base file provider.
     *
     * @param knowledgeBaseFileProvider the knowledge base file provider.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder knowledgeBaseFileProvider(KnowledgeBaseFileProvider knowledgeBaseFileProvider) {
        return (SpringEngineBuilder) super.knowledgeBaseFileProvider(knowledgeBaseFileProvider);
    }

    /**
     * Sets the configuration filename.
     *
     * @param configFilename the configuration filename.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder config(String configFilename) {
        return (SpringEngineBuilder) super.config(configFilename);
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
    @Override
    public SpringEngineBuilder property(String name, Object value, boolean variable, boolean system) {
        return (SpringEngineBuilder) super.property(name, value, variable, system);
    }

    /**
     * Sets the property (that is neither a variable nor a system property).
     *
     * @param name the property name.
     * @param value the property value.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder property(String name, Object value) {
        return (SpringEngineBuilder) super.property(name, value);
    }

    /**
     * Sets the system property (that is not a variable).
     *
     * @param name the property name.
     * @param value the property value.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder systemProperty(String name, Object value) {
        return (SpringEngineBuilder) super.systemProperty(name, value);
    }

    /**
     * Sets the property that is also a variable.
     *
     * @param name the property name.
     * @param value the property value.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder variableProperty(String name, Object value) {
        return (SpringEngineBuilder) super.variableProperty(name, value);
    }

    /**
     * Sets the properties.
     *
     * @param simpleProperties the properties.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder properties(Map<String, Object> simpleProperties) {
        return (SpringEngineBuilder) super.properties(simpleProperties);
    }

    /**
     * Sets the system properties.
     *
     * @param systemProperties the system properties.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder systemProperties(Map<String, String> systemProperties) {
        return (SpringEngineBuilder) super.systemProperties(systemProperties);
    }

    /**
     * Sets the variable properties.
     *
     * @param variableProperties the variable properties.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder variableProperties(Map<String, String> variableProperties) {
        return (SpringEngineBuilder) super.variableProperties(variableProperties);
    }

    /**
     * Adds the plugin.
     *
     * @param plugin the plugin.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder plugin(Plugin plugin) {
        return (SpringEngineBuilder) super.plugin(plugin);
    }

    /**
     * Adds the plugins.
     *
     * @param plugins the plugins.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder plugins(Plugin... plugins) {
        return (SpringEngineBuilder) super.plugins(plugins);
    }

    /**
     * Adds the knowledge base.
     *
     * @param knowledgeBase the knowledge base.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder knowledgeBase(KnowledgeBase knowledgeBase) {
        return (SpringEngineBuilder) super.knowledgeBase(knowledgeBase);
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param type the knowledge base type.
     * @param files the knowledge base files.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder knowledgeBase(String name, KnowledgeBaseType type, String... files) {
        return (SpringEngineBuilder) super.knowledgeBase(name, type, files);
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param files the knowledge base files.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder knowledgeBase(String name, String... files) {
        return (SpringEngineBuilder) super.knowledgeBase(name, files);
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param scripts the knowledge base scripts.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder knowledgeBase(String name, KnowledgeBaseScript... scripts) {
        return (SpringEngineBuilder) super.knowledgeBase(name, scripts);
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param type the knowledge base type.
     * @param scripts the knowledge base scripts.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder knowledgeBase(String name, KnowledgeBaseType type, KnowledgeBaseScript... scripts) {
        return (SpringEngineBuilder) super.knowledgeBase(name, type, scripts);
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param scripts the knowledge base scripts.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder knowledgeBase(String name, List<KnowledgeBaseScript> scripts) {
        return (SpringEngineBuilder) super.knowledgeBase(name, scripts);
    }

    /**
     * Adds the knowledge base.
     *
     * @param name the knowledge base name.
     * @param type the knowledge base type.
     * @param scripts the knowledge base scripts.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder knowledgeBase(String name, KnowledgeBaseType type, List<KnowledgeBaseScript> scripts) {
        return (SpringEngineBuilder) super.knowledgeBase(name, type, scripts);
    }

    /**
     * Adds the String-based knowledge base.
     *
     * @param name the knowledge base name.
     * @param type the knowledge base type.
     * @param body the String-based knowledge base body.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder knowledgeBaseString(String name, KnowledgeBaseType type, String body) {
        return (SpringEngineBuilder) super.knowledgeBaseString(name, type, body);
    }

    /**
     * Sets the exception handler.
     *
     * @param exceptionHandler the new exception handler.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder exceptionHandler(ExceptionHandler exceptionHandler) {
        return (SpringEngineBuilder) super.exceptionHandler(exceptionHandler);
    }

    /**
     * Sets the default knowledge base name.
     *
     * @param defaultKnowledgeBaseName the default knowledge base name.
     * @return this builder.
     */
    @Override
    public SpringEngineBuilder defaultKnowledgeBaseName(String defaultKnowledgeBaseName) {
        return (SpringEngineBuilder) super.defaultKnowledgeBaseName(defaultKnowledgeBaseName);
    }
}
