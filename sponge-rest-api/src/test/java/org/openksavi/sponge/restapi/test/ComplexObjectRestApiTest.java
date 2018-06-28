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
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.ObjectMapper;

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
import org.openksavi.sponge.examples.TestComplexObject;
import org.openksavi.sponge.examples.TestCompoundComplexObject;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.RestApiPlugin;
import org.openksavi.sponge.restapi.model.request.RestActionCallRequest;
import org.openksavi.sponge.restapi.model.response.RestActionCallResponse;
import org.openksavi.sponge.restapi.model.util.RestApiUtils;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { ComplexObjectRestApiTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class ComplexObjectRestApiTest {

    protected static final int PORT = SocketUtils.findAvailableTcpPort(RestApiConstants.DEFAULT_PORT);

    @Inject
    private SpongeEngine engine;

    @Configuration
    public static class TestConfig extends SpongeCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin())
                    .knowledgeBase("kb", "examples/rest-api/rest_api_complex_object.py").build();
        }

        @Bean
        public RestApiPlugin spongeRestApiPlugin() {
            RestApiPlugin plugin = new RestApiPlugin();
            plugin.getSettings().setPort(PORT);

            return plugin;
        }
    }

    protected final String getUrl() {
        return "http://localhost:" + PORT + RestApiConstants.BASE_URL + "/";
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    protected TestCompoundComplexObject createTestCompoundComplexObject() {
        TestComplexObject complexObject = new TestComplexObject();
        complexObject.setId(1l);
        complexObject.setName("TestComplexObject1");
        complexObject.setBigDecimal(new BigDecimal("1.25"));
        complexObject.setDate(Date.from(Instant.now()));

        TestCompoundComplexObject compoundObject = new TestCompoundComplexObject();
        compoundObject.setId(1l);
        compoundObject.setName("TestCompoundComplexObject1");
        compoundObject.setComplexObject(complexObject);

        return compoundObject;
    }

    @Test
    public void testRestCallComplexObject() {
        TestCompoundComplexObject compoundObject = createTestCompoundComplexObject();

        ResponseEntity<RestActionCallResponse> response = new RestTemplate().exchange(getUrl() + "call", HttpMethod.POST,
                new HttpEntity<>(new RestActionCallRequest("ComplexObjectAction", Arrays.asList(compoundObject)), createHeaders()),
                RestActionCallResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getResult() instanceof Map);

        ObjectMapper mapper = RestApiUtils.createObjectMapper();
        TestCompoundComplexObject result = mapper.convertValue(response.getBody().getResult(), TestCompoundComplexObject.class);
        assertEquals(compoundObject.getId() + 1, result.getId().longValue());
        assertEquals(compoundObject.getName(), result.getName());
        assertEquals(compoundObject.getComplexObject().getId(), result.getComplexObject().getId());
        assertEquals(compoundObject.getComplexObject().getName(), result.getComplexObject().getName());
        assertEquals(compoundObject.getComplexObject().getBigDecimal(), result.getComplexObject().getBigDecimal());
        assertEquals(compoundObject.getComplexObject().getDate(), result.getComplexObject().getDate());
    }

    @Test
    public void testRestCallComplexObjectList() {
        String actionName = "ComplexObjectListAction";
        TestCompoundComplexObject compoundObject = createTestCompoundComplexObject();

        ResponseEntity<RestActionCallResponse> response = new RestTemplate().exchange(getUrl() + "call", HttpMethod.POST,
                new HttpEntity<>(new RestActionCallRequest(actionName, Arrays.asList(Arrays.asList(compoundObject))), createHeaders()),
                RestActionCallResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getResult() instanceof List);

        @SuppressWarnings("unchecked")
        List<TestCompoundComplexObject> resultList =
                (List<TestCompoundComplexObject>) RestApiUtils.unmarshalActionResult(new ObjectMapper(),
                        engine.getActionManager().getActionAdapter(actionName).getResultMeta(), response.getBody().getResult());
        assertEquals(1, resultList.size());
        TestCompoundComplexObject result = resultList.get(0);
        assertEquals(compoundObject.getId() + 1, result.getId().longValue());
        assertEquals(compoundObject.getName(), result.getName());
        assertEquals(compoundObject.getComplexObject().getId(), result.getComplexObject().getId());
        assertEquals(compoundObject.getComplexObject().getName(), result.getComplexObject().getName());
        assertEquals(compoundObject.getComplexObject().getBigDecimal(), result.getComplexObject().getBigDecimal());
        assertEquals(compoundObject.getComplexObject().getDate(), result.getComplexObject().getDate());
    }

    @Test
    public void testRestCallComplexHierarchyObject() {
        Map<String, TestCompoundComplexObject> map = new HashMap<>();
        map.put("first", createTestCompoundComplexObject());

        ResponseEntity<RestActionCallResponse> response = new RestTemplate().exchange(getUrl() + "call", HttpMethod.POST,
                new HttpEntity<>(
                        new RestActionCallRequest("ComplexObjectHierarchyAction",
                                Arrays.asList("String", new Integer(100), Arrays.asList("a", "b", "c"),
                                        Arrays.asList(new BigDecimal("1.25"), new BigDecimal("5.5")), new String[] { "A", "B" }, map)),
                        createHeaders()),
                RestActionCallResponse.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getResult() instanceof List);
    }
}
