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

package org.openksavi.sponge.restapi.server.test;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.core.util.SslConfiguration;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { BaseHttpsRestApiTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public abstract class BaseHttpsRestApiTest extends BaseRestApiTestTemplate {

    @Configuration
    public static class TestConfig extends PortTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin())
                    .knowledgeBase("kb", "examples/rest-api-server/rest_api.py").build();
        }

        @Bean
        public RestApiServerPlugin spongeRestApiPlugin() {
            RestApiServerPlugin plugin = new RestApiServerPlugin();

            plugin.getSettings().setPort(spongeRestApiPort());

            SslConfiguration sslConfiguration = new SslConfiguration();
            sslConfiguration.setKeyStore("keystore/rest_api_selfsigned.jks");
            sslConfiguration.setKeyStorePassword("sponge");
            sslConfiguration.setKeyPassword("sponge");
            plugin.getSettings().setSslConfiguration(sslConfiguration);

            return plugin;
        }
    }
}
