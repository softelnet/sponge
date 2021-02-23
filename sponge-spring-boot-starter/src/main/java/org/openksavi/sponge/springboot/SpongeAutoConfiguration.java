/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.springboot;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.core.engine.ConfigurationConstants;
import org.openksavi.sponge.engine.ConfigurationManager;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.spring.SpringEngineBuilder;
import org.openksavi.sponge.spring.SpringPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;
import org.openksavi.sponge.springboot.SpongeProperties.EngineProperties;

@Configuration
@ConditionalOnClass(SpongeEngine.class)
@EnableConfigurationProperties(SpongeProperties.class)
public class SpongeAutoConfiguration {

    private static final String DEFAULT_CONFIG_FILE = "config/sponge.xml";

    @Autowired
    private SpongeProperties spongeProperties;

    @Bean
    @ConditionalOnMissingBean(SpongeEngine.class)
    public SpongeEngine spongeEngine(List<Plugin> plugins, List<KnowledgeBase> knowledgeBases) {
        SpringEngineBuilder builder = SpringSpongeEngine.builder();

        plugins.forEach(builder::plugin);

        KnowledgeBase bootKnowledgeBase = new BootKnowledgeBase();
        builder.knowledgeBase(bootKnowledgeBase);
        knowledgeBases.forEach(builder::knowledgeBase);

        String processorBeansKnowledgeBaseName = spongeProperties.getProcessorBeansKnowledgeBaseName();
        builder.processorBeansKnowledgeBaseName(
                processorBeansKnowledgeBaseName != null ? processorBeansKnowledgeBaseName : bootKnowledgeBase.getName());

        String spongeHome =
                spongeProperties.getHome() != null ? spongeProperties.getHome() : System.getProperty(ConfigurationConstants.PROP_HOME);
        if (spongeHome == null) {
            spongeHome = ".";
        }
        builder.property(ConfigurationConstants.PROP_HOME, spongeHome);

        String configFile = spongeProperties.getConfigFile();
        if (configFile != null) {
            builder.config(spongeProperties.getConfigFile());

            if (spongeProperties.getIgnoreConfigurationFileNotFound() != null) {
                builder.ignoreConfigurationFileNotFound(spongeProperties.getIgnoreConfigurationFileNotFound());
            }
        } else {
            builder.config(DEFAULT_CONFIG_FILE);

            // If a default config file is used, set it to optional.
            builder.ignoreConfigurationFileNotFound(spongeProperties.getIgnoreConfigurationFileNotFound() != null
                    ? spongeProperties.getIgnoreConfigurationFileNotFound() : true);

        }

        setupSpongeEngineBuilder(builder);

        SpongeEngine engine = builder.build();

        setupSpongeProperties(engine);
        setupSpongeEngine(engine, spongeProperties.getEngine());

        return engine;
    }

    @Bean
    @ConditionalOnMissingBean(SpringPlugin.class)
    public SpringPlugin springPlugin() {
        return new SpringPlugin();
    }

    protected void setupSpongeEngineBuilder(SpringEngineBuilder builder) {
        if (spongeProperties.getName() != null) {
            builder.name(spongeProperties.getName());
        }

        if (spongeProperties.getLabel() != null) {
            builder.label(spongeProperties.getLabel());
        }

        if (spongeProperties.getDescription() != null) {
            builder.description(spongeProperties.getDescription());
        }

        if (spongeProperties.getLicense() != null) {
            builder.license(spongeProperties.getLicense());
        }

        if (spongeProperties.getDefaultKnowledgeBaseName() != null) {
            builder.defaultKnowledgeBaseName(spongeProperties.getDefaultKnowledgeBaseName());
        }

        if (spongeProperties.getAutoStartup() != null) {
            builder.autoStartup(spongeProperties.getAutoStartup());
        }

        if (spongeProperties.getPhase() != null) {
            builder.phase(spongeProperties.getPhase());
        }

        if (spongeProperties.getProcessorBeansKnowledgeBaseName() != null) {
            builder.processorBeansKnowledgeBaseName(spongeProperties.getProcessorBeansKnowledgeBaseName());
        }
    }

    protected void setupSpongeProperties(SpongeEngine engine) {
        ConfigurationManager configuration = engine.getConfigurationManager();

        spongeProperties.getProperties().forEach((name, value) -> configuration.setProperty(name, value));
        spongeProperties.getSystemProperties().forEach((name, value) -> configuration.setSystemProperty(name, value));
        spongeProperties.getVariableProperties().forEach((name, value) -> configuration.setVariableProperty(name, value));
    }

    protected void setupSpongeEngine(SpongeEngine engine, EngineProperties engineProperties) {
        ConfigurationManager configuration = engine.getConfigurationManager();

        if (engineProperties.getMainProcessingUnitThreadCount() != null) {
            configuration.setMainProcessingUnitThreadCount(engineProperties.getMainProcessingUnitThreadCount());
        }

        if (engineProperties.getEventClonePolicy() != null) {
            configuration.setEventClonePolicy(engineProperties.getEventClonePolicy());
        }

        if (engineProperties.getEventQueueCapacity() != null) {
            configuration.setEventQueueCapacity(engineProperties.getEventQueueCapacity());
        }

        if (engineProperties.getDurationThreadCount() != null) {
            configuration.setDurationThreadCount(engineProperties.getDurationThreadCount());
        }

        if (engineProperties.getAsyncEventSetProcessorExecutorThreadCount() != null) {
            configuration.setAsyncEventSetProcessorExecutorThreadCount(engineProperties.getAsyncEventSetProcessorExecutorThreadCount());
        }

        if (engineProperties.getEventSetProcessorDefaultSynchronous() != null) {
            configuration.setEventSetProcessorDefaultSynchronous(engineProperties.getEventSetProcessorDefaultSynchronous());
        }

        if (engineProperties.getAutoEnable() != null) {
            configuration.setAutoEnable(engineProperties.getAutoEnable());
        }

        if (engineProperties.getExecutorShutdownTimeout() != null) {
            configuration.setExecutorShutdownTimeout(engineProperties.getExecutorShutdownTimeout());
        }
    }
}
