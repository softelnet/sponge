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

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.RestConfiguration;
import org.openksavi.sponge.restapi.model.RestAction;
import org.openksavi.sponge.restapi.model.RestActionsResult;
import org.openksavi.sponge.restapi.model.RestCallResult;
import org.openksavi.sponge.restapi.model.RestEvent;
import org.openksavi.sponge.restapi.model.RestSendResult;
import org.openksavi.sponge.restapi.model.RestVersion;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { RestApiTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class RestApiTest {

    private static final String URL = "http://localhost:" + RestApiConstants.DEFAULT_PORT + "/sponge/";

    @Produce(uri = "direct:test")
    protected ProducerTemplate testProducer;

    @Inject
    protected SpongeEngine engine;

    @Configuration
    @Import(RestConfiguration.class)
    public static class TestConfig extends CamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().knowledgeBase("kb", "examples/rest-api/rest_api.py").build();
        }
    }

    @Test
    public void testRestVersion() {
        ResponseEntity<RestVersion> response = new RestTemplate().getForEntity(URL + "version", RestVersion.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(engine.getVersion(), response.getBody().getVersion());
    }

    @Test
    public void testRestActions() {
        ResponseEntity<RestActionsResult> response = new RestTemplate().getForEntity(URL + "actions", RestActionsResult.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(engine.getActions().size(), response.getBody().getActions().size());
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    @Test
    public void testRestCall() {
        String actionName = "UpperCase";
        String arg1 = "test1";

        ResponseEntity<RestCallResult> response = new RestTemplate().exchange(URL + "call", HttpMethod.POST,
                new HttpEntity<>(new RestAction(actionName, Arrays.asList(arg1)), createHeaders()), RestCallResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(actionName, response.getBody().getActionName());
        assertTrue(response.getBody().getResult() instanceof String);
        assertEquals(arg1.toUpperCase(), response.getBody().getResult());

        await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "actionCalled").get());
        assertFalse(engine.isError());
    }

    @Test
    public void testRestSend() {
        String eventName = "alarm";
        Map<String, Object> attributes = SpongeUtils.immutableMapOf("attr1", "Test");

        // TODO send as const
        ResponseEntity<RestSendResult> response = new RestTemplate().exchange(URL + "send", HttpMethod.POST,
                new HttpEntity<>(new RestEvent(eventName, attributes), createHeaders()), RestSendResult.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().getEventId());

        await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "eventSent").get());
        assertFalse(engine.isError());
    }
}
