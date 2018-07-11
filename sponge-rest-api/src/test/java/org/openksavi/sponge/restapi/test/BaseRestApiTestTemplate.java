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
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.SocketUtils;
import org.springframework.web.client.RestTemplate;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.request.RestActionCallRequest;
import org.openksavi.sponge.restapi.model.request.RestGetActionsRequest;
import org.openksavi.sponge.restapi.model.request.RestGetVersionRequest;
import org.openksavi.sponge.restapi.model.request.RestSendEventRequest;
import org.openksavi.sponge.restapi.model.response.RestActionCallResponse;
import org.openksavi.sponge.restapi.model.response.RestGetActionsResponse;
import org.openksavi.sponge.restapi.model.response.RestGetVersionResponse;
import org.openksavi.sponge.restapi.model.response.RestSendEventResponse;
import org.openksavi.sponge.restapi.model.util.RestApiUtils;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.TypeKind;

public abstract class BaseRestApiTestTemplate {

    protected static final int PORT = SocketUtils.findAvailableTcpPort(RestApiConstants.DEFAULT_PORT);

    @Produce(uri = "direct:test")
    protected ProducerTemplate testProducer;

    @Inject
    protected SpongeEngine engine;

    protected abstract String getProtocol();

    protected final String getUrl() {
        return getProtocol() + "://localhost:" + PORT + RestApiConstants.BASE_URL + "/";
    }

    protected abstract RestTemplate createRestTemplate();

    protected RestTemplate setupRestTemplate(RestTemplate restTemplate) {
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(RestApiUtils.createObjectMapper())));

        return restTemplate;
    }

    @Test
    public void testRestVersion() {
        ResponseEntity<RestGetVersionResponse> response = createRestTemplate().exchange(getUrl() + "version", HttpMethod.POST,
                new HttpEntity<>(new RestGetVersionRequest(), createHeaders()), RestGetVersionResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(engine.getVersion(), response.getBody().getVersion());
    }

    @Test
    public void testRestVersionWithId() {
        RestGetVersionRequest request = new RestGetVersionRequest();
        request.setId("5");
        ResponseEntity<RestGetVersionResponse> response = createRestTemplate().exchange(getUrl() + "version", HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()), RestGetVersionResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(engine.getVersion(), response.getBody().getVersion());
        assertEquals(response.getBody().getId(), request.getId());
    }

    @Test
    public void testRestActions() {
        ResponseEntity<RestGetActionsResponse> response = createRestTemplate().exchange(getUrl() + "actions", HttpMethod.POST,
                new HttpEntity<>(new RestGetActionsRequest(), createHeaders()), RestGetActionsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getActions().size());
    }

    @Test
    public void testRestActionsParamArgMetadataRequiredTrue() {
        RestGetActionsRequest request = new RestGetActionsRequest();
        request.setMetadataRequired(true);
        ResponseEntity<RestGetActionsResponse> response = createRestTemplate().exchange(getUrl() + "actions", HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()), RestGetActionsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(3, response.getBody().getActions().size());
    }

    @Test
    public void testRestActionsParamArgMetadataRequiredFalse() {
        RestGetActionsRequest request = new RestGetActionsRequest();
        request.setMetadataRequired(false);

        ResponseEntity<RestGetActionsResponse> response = createRestTemplate().exchange(getUrl() + "actions", HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()), RestGetActionsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(4, response.getBody().getActions().size());
        RestActionMeta meta =
                response.getBody().getActions().stream().filter(action -> action.getName().equals("UpperCase")).findFirst().get();
        assertEquals(TypeKind.STRING, meta.getArgsMeta().get(0).getType().getKind());
        assertTrue(meta.getArgsMeta().get(0).getType() instanceof StringType);
    }

    @Test
    public void testRestActionsNameRegExp() {
        RestGetActionsRequest request = new RestGetActionsRequest();
        request.setNameRegExp(".*Case");

        ResponseEntity<RestGetActionsResponse> response = createRestTemplate().exchange(getUrl() + "actions", HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()), RestGetActionsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getActions().size());
        assertTrue(response.getBody().getActions().stream().allMatch(action -> action.getName().matches(request.getNameRegExp())));
    }

    @Test
    public void testRestActionsNameExact() {
        RestGetActionsRequest request = new RestGetActionsRequest();
        request.setNameRegExp("UpperCase");

        ResponseEntity<RestGetActionsResponse> response = createRestTemplate().exchange(getUrl() + "actions", HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()), RestGetActionsResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getActions().size());
        assertEquals(response.getBody().getActions().get(0).getName(), request.getNameRegExp());
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

        ResponseEntity<RestActionCallResponse> response = createRestTemplate().exchange(getUrl() + "call", HttpMethod.POST,
                new HttpEntity<>(new RestActionCallRequest(actionName, Arrays.asList(arg1)), createHeaders()),
                RestActionCallResponse.class);

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

        ResponseEntity<RestSendEventResponse> response = createRestTemplate().exchange(getUrl() + "send", HttpMethod.POST,
                new HttpEntity<>(new RestSendEventRequest(eventName, attributes), createHeaders()), RestSendEventResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(null, response.getBody().getErrorMessage());
        assertNotNull(response.getBody().getEventId());

        await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "eventSent").get());
        assertFalse(engine.isError());
    }
}
