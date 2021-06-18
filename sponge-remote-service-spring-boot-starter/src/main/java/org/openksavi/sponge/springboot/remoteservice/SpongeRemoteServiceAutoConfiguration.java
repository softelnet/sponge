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
package org.openksavi.sponge.springboot.remoteservice;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;

import org.openksavi.sponge.camel.CamelPlugin;
import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.grpcapi.server.util.GrpcApiServerUtils;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.RemoteApiSettings;
import org.openksavi.sponge.remoteapi.server.discovery.ServiceDiscoveryInfo;
import org.openksavi.sponge.remoteapi.server.security.AccessService;
import org.openksavi.sponge.remoteapi.server.security.DefaultRequestAuthenticationService;
import org.openksavi.sponge.remoteapi.server.security.DefaultSecurityProvider;
import org.openksavi.sponge.remoteapi.server.security.RequestAuthenticationService;
import org.openksavi.sponge.remoteapi.server.security.RoleBasedAccessService;
import org.openksavi.sponge.remoteapi.server.security.SecurityProvider;
import org.openksavi.sponge.remoteapi.server.security.SecurityService;
import org.openksavi.sponge.remoteapi.server.security.spring.SpringSecurityService;
import org.openksavi.sponge.springboot.remoteservice.SpongeRemoteServiceProperties.GrpcProperties;

@Configuration
@ConditionalOnClass({ RemoteApiServerPlugin.class, GrpcApiServerPlugin.class })
@EnableConfigurationProperties(SpongeRemoteServiceProperties.class)
@PropertySource("classpath:/org/openksavi/sponge/springboot/remoteservice/remoteservice.properties")
public class SpongeRemoteServiceAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(SpongeRemoteServiceAutoConfiguration.class);

    private static final String COMPONENT_ID = "servlet";

    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";

    @Autowired
    private SpongeRemoteServiceProperties serviceProperties;

    @Autowired
    private Environment environment;

    @Bean
    public RemoteApiServerPlugin spongeRemoteApiPlugin(SecurityProvider securityProvider) {
        RemoteApiServerPlugin plugin = new RemoteApiServerPlugin();

        // Use the servlet configuration.
        plugin.getSettings().setComponentId(COMPONENT_ID);

        setupRemoteApiServerPlugin(plugin.getSettings());

        plugin.setSecurityProvider(securityProvider);

        return plugin;
    }

    @Bean
    public GrpcApiServerPlugin spongeGrpcApiPlugin() {
        GrpcApiServerPlugin grpcPlugin = new GrpcApiServerPlugin();

        setupGrpcApiServerPlugin(grpcPlugin);

        return grpcPlugin;
    }

    @Bean
    @ConditionalOnMissingBean(SecurityProvider.class)
    public SecurityProvider spongeSecurityProvider(SecurityService securityService, AccessService accessService,
            RequestAuthenticationService requestAuthenticationService) {
        return new DefaultSecurityProvider(securityService, accessService, requestAuthenticationService);
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

    @Bean
    @ConditionalOnMissingBean(CamelPlugin.class)
    public CamelPlugin camelPlugin() {
        return new CamelPlugin();
    }

    protected void setupRemoteApiServerPlugin(RemoteApiSettings settings) {
        if (serviceProperties.getVersion() != null) {
            settings.setVersion(serviceProperties.getVersion());
        }

        if (serviceProperties.getName() != null) {
            settings.setName(serviceProperties.getName());
        }

        if (serviceProperties.getDescription() != null) {
            settings.setDescription(serviceProperties.getDescription());
        }

        if (serviceProperties.getLicense() != null) {
            settings.setLicense(serviceProperties.getLicense());
        }

        if (serviceProperties.getPrettyPrint() != null) {
            settings.setPrettyPrint(serviceProperties.getPrettyPrint());
        }

        if (serviceProperties.getPublishReload() != null) {
            settings.setPublishReload(serviceProperties.getPublishReload());
        }

        if (serviceProperties.getAllowAnonymous() != null) {
            settings.setAllowAnonymous(serviceProperties.getAllowAnonymous());
        }

        // Conform to Spring Security standards.
        settings.setAdminRole(serviceProperties.getAdminRole() != null ? serviceProperties.getAdminRole() : ROLE_ADMIN);
        settings.setAnonymousRole(serviceProperties.getAnonymousRole() != null ? serviceProperties.getAnonymousRole() : ROLE_ANONYMOUS);

        if (serviceProperties.getIncludeDetailedErrorMessage() != null) {
            settings.setIncludeDetailedErrorMessage(serviceProperties.getIncludeDetailedErrorMessage());
        }

        if (serviceProperties.getAuthTokenExpirationDuration() != null) {
            settings.setAuthTokenExpirationDuration(serviceProperties.getAuthTokenExpirationDuration());
        }

        if (serviceProperties.getIncludeResponseTimes() != null) {
            settings.setIncludeResponseTimes(serviceProperties.getIncludeResponseTimes());
        }

        if (serviceProperties.getRegisterServiceDiscovery() != null) {
            settings.setRegisterServiceDiscovery(serviceProperties.getRegisterServiceDiscovery());
        }

        if (serviceProperties.getIgnoreUnknownArgs() != null) {
            settings.setIgnoreUnknownArgs(serviceProperties.getIgnoreUnknownArgs());
        }

        if (serviceProperties.getCopyHttpRequestHeaders() != null) {
            settings.setCopyHttpRequestHeaders(serviceProperties.getCopyHttpRequestHeaders());
        }

        if (serviceProperties.getCorsEnabled() != null) {
            settings.setCorsEnabled(serviceProperties.getCorsEnabled());
        }

        if (serviceProperties.getOpenApiDocsForGetVerbOperations() != null) {
            settings.setOpenApiDocsForGetVerbOperations(serviceProperties.getOpenApiDocsForGetVerbOperations());
        }

        if (serviceProperties.getOpenApiOperationIdSuffixForGetVerbOperations() != null) {
            settings.setOpenApiOperationIdSuffixForGetVerbOperations(serviceProperties.getOpenApiOperationIdSuffixForGetVerbOperations());
        }

        if (serviceProperties.getOpenApiDocsForEndpoints() != null) {
            settings.setOpenApiDocsForEndpoints(serviceProperties.getOpenApiDocsForEndpoints());
        }

        settings.getOpenApiProperties().putAll(serviceProperties.getOpenApiProperties());

        if (serviceProperties.getDiscovery().getUrl() != null) {
            ServiceDiscoveryInfo discoveryInfo = new ServiceDiscoveryInfo();

            discoveryInfo.setUrl(serviceProperties.getDiscovery().getUrl());

            settings.setServiceDiscoveryInfo(discoveryInfo);
        }
    }

    protected void setupGrpcApiServerPlugin(GrpcApiServerPlugin grpcPlugin) {
        GrpcProperties grpc = serviceProperties.getGrpc();

        if (grpc.getPort() != null) {
            grpcPlugin.setPort(grpc.getPort());
        } else {
            Integer remoteApiPort = environment.getProperty("server.port", Integer.class);
            Validate.notNull(remoteApiPort, "The server.port property must be set explicitely for the gRPC API to calculate its port");

            if (remoteApiPort == 0) {
                logger.warn("The server.port property is set to random. Can't determine the gRPC port. Using default.");
            } else {
                grpcPlugin.setPort(GrpcApiServerUtils.calculateDefaultPortByRemoteApi(remoteApiPort));
            }
        }

        if (grpc.getAutoStart() != null) {
            grpcPlugin.setAutoStart(grpc.getAutoStart());
        }
    }
}
