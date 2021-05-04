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

package org.openksavi.sponge.app.demoservice;

import java.util.Optional;

import javax.inject.Inject;

import org.apache.camel.CamelContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.core.engine.ConfigurationConstants;
import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.security.spring.SimpleSpringInMemorySecurityProvider;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@Configuration
public class DemoSpringConfig extends SpongeCamelConfiguration {

    @Inject
    protected Optional<CamelContext> camelContext;

    @Bean
    public SpringSpongeEngine spongeEngine() {
        String spongeHome = System.getProperty(ConfigurationConstants.PROP_HOME);
        if (StringUtils.isBlank(spongeHome)) {
            spongeHome = "sponge";
        }

        return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRemoteApiPlugin(), spongeGrpcApiPlugin())
                .config(spongeHome + "/sponge.xml").build();
    }

    @Bean
    public RemoteApiServerPlugin spongeRemoteApiPlugin() {
        RemoteApiServerPlugin plugin = new RemoteApiServerPlugin();

        // Use the servlet configuration.
        plugin.getSettings().setComponentId("servlet");

        plugin.getSettings().setAllowAnonymous(true);
        plugin.getSettings().setPublishReload(true);
        plugin.getSettings().setIncludeDetailedErrorMessage(false);
        plugin.getSettings().setPrettyPrint(true);
        plugin.getSettings().setCorsEnabled(true);

        plugin.setSecurityProvider(new SimpleSpringInMemorySecurityProvider());

        return plugin;
    }

    @Bean
    public GrpcApiServerPlugin spongeGrpcApiPlugin() {
        return new GrpcApiServerPlugin();
    }
}
