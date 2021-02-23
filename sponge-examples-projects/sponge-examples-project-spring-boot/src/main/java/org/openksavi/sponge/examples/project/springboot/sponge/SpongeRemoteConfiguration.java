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
import org.springframework.security.authentication.AuthenticationManager;

import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.security.AccessService;
import org.openksavi.sponge.remoteapi.server.security.DefaultRequestAuthenticationService;
import org.openksavi.sponge.remoteapi.server.security.DefaultSecurityProvider;
import org.openksavi.sponge.remoteapi.server.security.RequestAuthenticationService;
import org.openksavi.sponge.remoteapi.server.security.RoleBasedAccessService;
import org.openksavi.sponge.remoteapi.server.security.SecurityProvider;
import org.openksavi.sponge.remoteapi.server.security.SecurityService;
import org.openksavi.sponge.remoteapi.server.security.spring.SpringSecurityService;

@Configuration
public class SpongeRemoteConfiguration {

    @Bean
    public RemoteApiServerPlugin spongeRemoteApiPlugin(SecurityProvider securityProvider) {
        RemoteApiServerPlugin plugin = new RemoteApiServerPlugin();

        // Use the servlet configuration.
        plugin.getSettings().setComponentId("servlet");

        plugin.getSettings().setAllowAnonymous(true);
        plugin.getSettings().setIncludeDetailedErrorMessage(false);
        plugin.getSettings().setPrettyPrint(true);

        // Conform to Spring Security standards.
        plugin.getSettings().setAdminRole("ROLE_ADMIN");
        plugin.getSettings().setAnonymousRole("ROLE_ANONYMOUS");

        plugin.setSecurityProvider(securityProvider);

        return plugin;
    }

    @Bean
    public GrpcApiServerPlugin spongeGrpcApiPlugin() {
        return new GrpcApiServerPlugin();
    }

    @Bean
    @ConditionalOnMissingBean(SecurityProvider.class)
    public SecurityProvider spongeSecurityProvider(SecurityService securityService, AccessService accessService,
            RequestAuthenticationService requestAuthenticationService) {
        return new DefaultSecurityProvider(securityService,  accessService, requestAuthenticationService);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityService.class)
    public SecurityService spongeSecurityService(AuthenticationManager authenticationManager) {
        return new SpringSecurityService(authenticationManager);
    }

    @Bean
    @ConditionalOnMissingBean(AccessService.class)
    public AccessService spongeAccessService() {
        return new RoleBasedAccessService();
    }

    @Bean
    @ConditionalOnMissingBean(RequestAuthenticationService.class)
    public RequestAuthenticationService spongeRequestAuthenticationService() {
        return new DefaultRequestAuthenticationService();
    }
}
