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

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.security.SecurityProvider;
import org.openksavi.sponge.remoteapi.server.security.spring.SimpleSpringInMemorySecurityProvider;

@Configuration(proxyBeanMethods = false)
public class SpongeRemoteConfiguration {

    @Bean
    public RemoteApiServerPlugin spongeRemoteApiPlugin(SecurityProvider securityProvider) {
        RemoteApiServerPlugin plugin = new RemoteApiServerPlugin();

        // Use the servlet configuration.
        plugin.getSettings().setComponentId("servlet");

        plugin.getSettings().setAllowAnonymous(true);
        plugin.getSettings().setIncludeDetailedErrorMessage(false);
        plugin.getSettings().setPrettyPrint(true);

        plugin.setSecurityProvider(securityProvider);

        return plugin;
    }

    @Bean
    public GrpcApiServerPlugin spongeGrpcApiPlugin() {
        return new GrpcApiServerPlugin();
    }

    // TODO Should be injectable
    @Bean
    @ConditionalOnMissingBean(SecurityProvider.class)
    public SecurityProvider spongeSecurityProvider() {
        return new SimpleSpringInMemorySecurityProvider();
    }
}
