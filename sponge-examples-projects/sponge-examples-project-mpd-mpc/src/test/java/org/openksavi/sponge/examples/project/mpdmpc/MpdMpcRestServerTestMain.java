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

package org.openksavi.sponge.examples.project.mpdmpc;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.spring.SpringSpongeEngine;

public class MpdMpcRestServerTestMain {

    @Configuration
    public static class Config extends SpongeCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin()).config("kb/mpd_mpc.xml").build();
        }
    }

    protected SpongeRestClient createRestClient() {
        return new DefaultSpongeRestClient(
                SpongeRestClientConfiguration.builder().url(String.format("http://localhost:%d", RestApiConstants.DEFAULT_PORT)).build());
    }

    public void testMpdPlaylist() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        ctx.start();

        try {
            SpongeEngine engine = ctx.getBean(SpongeEngine.class);

            try (SpongeRestClient client = createRestClient()) {
                String info = client.call(String.class, "MpdSetAndPlayPlaylist", Arrays.asList(null, null, "rock", null, null, true));

                assertNotNull(info);
                assertFalse(engine.isError());
            }
        } finally {
            ctx.close();
        }
    }

    public static void main(String... args) {
        new MpdMpcRestServerTestMain().testMpdPlaylist();
    }
}
