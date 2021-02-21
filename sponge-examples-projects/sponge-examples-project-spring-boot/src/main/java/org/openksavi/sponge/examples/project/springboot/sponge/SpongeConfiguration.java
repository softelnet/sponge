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
package org.openksavi.sponge.examples.project.springboot.sponge;

import java.util.List;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.camel.CamelPlugin;
import org.openksavi.sponge.core.engine.ConfigurationConstants;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.plugin.Plugin;
import org.openksavi.sponge.spring.SpringEngineBuilder;
import org.openksavi.sponge.spring.SpringPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@Configuration(proxyBeanMethods = false)
public class SpongeConfiguration {

    @Bean
    @ConditionalOnMissingBean(SpongeEngine.class)
    public SpongeEngine spongeEngine(List<Plugin> plugins, List<KnowledgeBase> knowledgeBases) {
        // TODO Handle Spring Boot property.
        String spongeHome = System.getProperty(ConfigurationConstants.PROP_HOME);
        if (spongeHome == null) {
            spongeHome = ".";
        }

        SpringEngineBuilder builder =  SpringSpongeEngine.builder();

        plugins.forEach(builder::plugin);

        KnowledgeBase bootKnowledgeBase = new BootKnowledgeBase();
        builder.knowledgeBase(bootKnowledgeBase);
        knowledgeBases.forEach(builder::knowledgeBase);
        builder.processorBeansKnowledgeBaseName(bootKnowledgeBase.getName());

        builder.property(ConfigurationConstants.PROP_HOME, spongeHome)
            .config("config/sponge.xml");

        return builder.build();
    }

    @Bean
    @ConditionalOnMissingBean(SpringPlugin.class)
    public SpringPlugin springPlugin() {
        return new SpringPlugin();
    }

    @Bean
    @ConditionalOnMissingBean(CamelPlugin.class)
    public CamelPlugin camelPlugin() {
        return new CamelPlugin();
    }
}
