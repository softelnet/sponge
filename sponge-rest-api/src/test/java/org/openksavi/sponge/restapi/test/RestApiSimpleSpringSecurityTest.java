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

package org.openksavi.sponge.restapi.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.SocketUtils;
import org.springframework.web.client.RestTemplate;

import org.openksavi.sponge.camel.SpongeCamelConfiguration;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.RestApiPlugin;
import org.openksavi.sponge.restapi.model.request.RestGetActionsRequest;
import org.openksavi.sponge.restapi.model.response.RestGetActionsResponse;
import org.openksavi.sponge.restapi.security.RestApiSecurityService;
import org.openksavi.sponge.restapi.security.spring.SimpleSpringInMemorySecurityService;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { RestApiSimpleSpringSecurityTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class RestApiSimpleSpringSecurityTest {

    private static final int PORT = SocketUtils.findAvailableTcpPort(1836);

    private static final String URL = "http://localhost:" + PORT + RestApiConstants.BASE_URL + "/";

    @Produce(uri = "direct:test")
    protected ProducerTemplate testProducer;

    @Inject
    protected SpongeEngine engine;

    @Configuration
    public static class TestConfig extends SpongeCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin())
                    .knowledgeBase("admin", "classpath:org/openksavi/sponge/restapi/administration_library.py")
                    .knowledgeBase("example", "examples/rest-api/rest_api.py")
                    .knowledgeBase("security", "examples/rest-api/rest_api_security.py").build();
        }

        @Bean
        public RestApiPlugin spongeRestApiPlugin() {
            RestApiPlugin plugin = new RestApiPlugin();

            plugin.getSettings().setPort(PORT);
            plugin.setSecurityService(restApiSecurityService());

            return plugin;
        }

        @Bean
        public RestApiSecurityService restApiSecurityService() {
            return new SimpleSpringInMemorySecurityService();
        }
    }

    protected void doTestRestActions(String username, String password, int actionCount) {
        RestGetActionsRequest request = new RestGetActionsRequest();
        request.setUsername(username);
        request.setPassword(password);

        ResponseEntity<RestGetActionsResponse> response = new RestTemplate().exchange(URL + "actions", HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()), RestGetActionsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getActions());
        assertEquals(actionCount, response.getBody().getActions().size());
    }

    @Test
    public void testRestActionsUser1() {
        doTestRestActions("john", "password", 5);
    }

    @Test
    public void testRestActionsUser2() {
        doTestRestActions("joe", "password", 3);
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
