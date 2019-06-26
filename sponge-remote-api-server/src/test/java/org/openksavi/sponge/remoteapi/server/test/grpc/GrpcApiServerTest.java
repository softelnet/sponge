/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.server.test.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.BaseSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.client.okhttp.OkHttpSpongeRestClient;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@net.jcip.annotations.NotThreadSafe
@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { GrpcApiServerTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class GrpcApiServerTest extends GrpcApiServerBaseTest {

    @Configuration
    public static class TestConfig extends PortTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin(), spongeGrpcApiPlugin())
                    .config("examples/remote-api-server/remote_api.xml").build();
        }

        @Bean
        public RestApiServerPlugin spongeRestApiPlugin() {
            RestApiServerPlugin plugin = new RestApiServerPlugin();
            plugin.getSettings().setPort(spongeRestApiPort());
            plugin.getSettings().setPrettyPrint(true);

            return plugin;
        }

        @Bean
        public GrpcApiServerPlugin spongeGrpcApiPlugin() {
            return new GrpcApiServerPlugin();
        }
    }

    @Override
    protected BaseSpongeRestClient createRestClient(boolean useEventTypeCache) {
        return new OkHttpSpongeRestClient(
                SpongeRestClientConfiguration.builder().url(String.format("http://localhost:%d/%s", port, RestApiConstants.DEFAULT_PATH))
                        .useEventTypeCache(useEventTypeCache).build());
    }

    @Override
    protected ManagedChannel createManagedChannel() {
        // Insecure connection only for tests.
        return ManagedChannelBuilder.forTarget("dns:///localhost:" + getGrpcPort()).usePlaintext().build();
    }
}
