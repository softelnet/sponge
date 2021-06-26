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

package org.openksavi.sponge.remoteapi.server.test.remote;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.RemoteApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.security.spring.SimpleSpringInMemorySecurityProvider;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.remoteapi.server.test.RemoteApiTestUtils;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { BaseHttpRemoteApiTest.TestConfig.class })
@DirtiesContext
public abstract class BaseHttpRemoteApiTest extends BaseRemoteApiTestTemplate {

    @Configuration
    public static class TestConfig extends PortTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRemoteApiPlugin(), spongeGrpcApiPlugin())
                    .config("examples/remote-api-server/remote_api_security.xml").build();
        }

        @Bean
        public RemoteApiServerPlugin spongeRemoteApiPlugin() {
            RemoteApiServerPlugin plugin = new RemoteApiServerPlugin();

            plugin.getSettings().setPort(spongeRemoteApiPort());
            plugin.getSettings().setIncludeDetailedErrorMessage(true);
            plugin.getSettings().setIncludeErrorLocationInErrorMessage(true);
            plugin.getSettings().setPrettyPrint(true);
            // plugin.getSettings().setPublicActions(Arrays.asList(new ProcessorQualifiedName(".*", "^(?!)Private.*")));

            plugin.getSettings().setAllowAnonymous(true);
            plugin.getSettings().setPublishReload(true);
            plugin.getSettings().setIncludeResponseTimes(true);

            plugin.setSecurityProvider(new SimpleSpringInMemorySecurityProvider());

            RemoteApiTestUtils.setupRemoteService(plugin.getSettings());

            return plugin;
        }

        @Bean
        public GrpcApiServerPlugin spongeGrpcApiPlugin() {
            return new GrpcApiServerPlugin();
        }
    }
}
