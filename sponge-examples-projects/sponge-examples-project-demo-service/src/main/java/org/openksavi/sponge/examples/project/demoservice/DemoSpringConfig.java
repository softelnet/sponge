/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.examples.project.demoservice;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@Configuration
public class DemoSpringConfig extends SpongeCamelConfiguration {

    @Inject
    protected Optional<CamelContext> camelContext;

    @Bean
    public SpongeEngine spongeEngine() {
        return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin()).config("sponge/sponge_demo.xml").build();
    }

    @Bean
    public RestApiServerPlugin spongeRestApiPlugin() {
        RestApiServerPlugin plugin = new RestApiServerPlugin();

        // Use the web application port.
        plugin.getSettings().setPort(null);
        plugin.getSettings().setRestComponentId("servlet");
        plugin.getSettings().setAllowAnonymous(true);
        plugin.getSettings().setIncludeDetailedErrorMessage(false);

        plugin.setCamelContext(camelContext.get());

        return plugin;
    }
}
