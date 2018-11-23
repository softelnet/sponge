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

package org.openksavi.sponge.mpd;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.SocketUtils;

import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.restapi.server.security.SimpleInMemorySecurityService;
import org.openksavi.sponge.spring.SpringSpongeEngine;

public class MpdRestServerTestMain {

    protected static final int PORT = SocketUtils.findAvailableTcpPort(RestApiConstants.DEFAULT_PORT);

    @Configuration
    public static class TestConfig extends SpongeCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin())
                    .config("examples/mpd/mpd_rest_server/mpd_rest_server_test.xml").build();
        }

        @Bean
        public RestApiServerPlugin spongeRestApiPlugin() {
            RestApiServerPlugin plugin = new RestApiServerPlugin();
            plugin.getSettings().setPort(PORT);

            plugin.setSecurityService(new SimpleInMemorySecurityService());

            return plugin;
        }
    }

    protected SpongeRestClient createRestClient() {
        return new DefaultSpongeRestClient(SpongeRestClientConfiguration.builder()
                .url(String.format("http://localhost:%d/%s", PORT, RestApiConstants.DEFAULT_PATH)).build());
    }

    public void testMpdPlaylist() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(TestConfig.class);
        ctx.start();

        try {
            SpongeEngine engine = ctx.getBean(SpongeEngine.class);

            try (SpongeRestClient client = createRestClient()) {
                String info = client.call(String.class, "MpdSetAndPlayPlaylist", null, null, "rock", null, null, false);

                assertNotNull(info);
                assertFalse(engine.isError());
            }
        } finally {
            ctx.close();
        }
    }

    public static void main(String... args) {
        new MpdRestServerTestMain().testMpdPlaylist();
    }
}
