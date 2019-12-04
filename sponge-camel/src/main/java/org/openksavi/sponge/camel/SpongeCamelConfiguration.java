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

package org.openksavi.sponge.camel;

import org.apache.camel.CamelContext;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.spring.SpringPlugin;

@Configuration
public abstract class SpongeCamelConfiguration extends CamelConfiguration {

    @Override
    protected void setupCamelContext(CamelContext camelContext) throws Exception {
        super.setupCamelContext(camelContext);

        camelContext.addStartupListener(camelPlugin());
    }

    /**
     * Spring plugin that may be bound to the engine.
     *
     * @return a Spring plugin.
     */
    @Bean
    public SpringPlugin springPlugin() {
        return new SpringPlugin();
    }

    /**
     * Camel plugin that may be bound to the engine.
     *
     * @return a Camel plugin.
     */
    @Bean
    public CamelPlugin camelPlugin() {
        CamelPlugin plugin = new CamelPlugin();
        plugin.setWaitForContextFullyStarted(true);

        return plugin;
    }
}
