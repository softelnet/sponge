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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestApiClient;
import org.openksavi.sponge.restapi.client.RestApiClientConfiguration;
import org.openksavi.sponge.restapi.client.SpongeRestApiClient;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.restapi.test.base.CompoundComplexObject;
import org.openksavi.sponge.restapi.type.converter.DefaultTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@net.jcip.annotations.NotThreadSafe
@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { ComplexObjectRestApiTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class ComplexObjectRestApiTest {

    @Inject
    private SpongeEngine engine;

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

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

            return plugin;
        }
    }

    protected SpongeRestApiClient createRestApiClient() {
        return new DefaultSpongeRestApiClient(RestApiClientConfiguration.builder()
                .url(String.format("http://localhost:%d/%s", port, RestApiConstants.DEFAULT_PATH)).build());
    }

    @Test
    public void testRestCallComplexObject() {
        CompoundComplexObject compoundObject = RestApiTestUtils.createCompoundComplexObject();

        try (SpongeRestApiClient client = createRestApiClient()) {
            CompoundComplexObject result = client.call(CompoundComplexObject.class, "ComplexObjectAction", compoundObject);

            assertEquals(compoundObject.getId() + 1, result.getId().longValue());
            assertEquals(compoundObject.getName(), result.getName());
            assertEquals(compoundObject.getComplexObject().getId(), result.getComplexObject().getId());
            assertEquals(compoundObject.getComplexObject().getName(), result.getComplexObject().getName());
            assertEquals(compoundObject.getComplexObject().getBigDecimal(), result.getComplexObject().getBigDecimal());
            assertEquals(compoundObject.getComplexObject().getDate(), result.getComplexObject().getDate());
        }
    }

    @Test
    public void testRestCallComplexObjectNoClass() {
        CompoundComplexObject compoundObject = RestApiTestUtils.createCompoundComplexObject();

        try (SpongeRestApiClient client = createRestApiClient()) {
            CompoundComplexObject result = (CompoundComplexObject) client.call("ComplexObjectAction", compoundObject);

            assertEquals(compoundObject.getId() + 1, result.getId().longValue());
            assertEquals(compoundObject.getName(), result.getName());
            assertEquals(compoundObject.getComplexObject().getId(), result.getComplexObject().getId());
            assertEquals(compoundObject.getComplexObject().getName(), result.getComplexObject().getName());
            assertEquals(compoundObject.getComplexObject().getBigDecimal(), result.getComplexObject().getBigDecimal());
            assertEquals(compoundObject.getComplexObject().getDate(), result.getComplexObject().getDate());
        }
    }

    @Test
    public void testRestCallComplexObjectNoMeta() {
        String actionName = "ComplexObjectAction";
        CompoundComplexObject compoundObject = RestApiTestUtils.createCompoundComplexObject();

        try (SpongeRestApiClient client = createRestApiClient()) {
            Object value = client.call(new ActionCallRequest(actionName, Arrays.asList(compoundObject)), null, false).getResult();

            assertTrue(value instanceof Map);

            ObjectMapper mapper = RestApiUtils.createObjectMapper();
            CompoundComplexObject result = mapper.convertValue(value, CompoundComplexObject.class);

            assertEquals(compoundObject.getId() + 1, result.getId().longValue());
            assertEquals(compoundObject.getName(), result.getName());
            assertEquals(compoundObject.getComplexObject().getId(), result.getComplexObject().getId());
            assertEquals(compoundObject.getComplexObject().getName(), result.getComplexObject().getName());
            assertEquals(compoundObject.getComplexObject().getBigDecimal(), result.getComplexObject().getBigDecimal());
            assertEquals(compoundObject.getComplexObject().getDate(), result.getComplexObject().getDate());
        }
    }

    @Test
    public void testRestCallComplexObjectList() {
        String actionName = "ComplexObjectListAction";
        CompoundComplexObject compoundObject = RestApiTestUtils.createCompoundComplexObject();

        try (SpongeRestApiClient client = createRestApiClient()) {
            Object returnValue = client.call(actionName, Arrays.asList(compoundObject));

            assertTrue(returnValue instanceof List);

            TypeConverter typeConverter = new DefaultTypeConverter(new ObjectMapper());
            @SuppressWarnings("unchecked")
            List<CompoundComplexObject> resultList = (List<CompoundComplexObject>) typeConverter
                    .unmarshal(engine.getActionManager().getActionAdapter(actionName).getResultMeta().getType(), returnValue);// RestApiUtils.unmarshalActionResult(new
                                                                                                                              // DefaultTypeConverter(new
            assertEquals(1, resultList.size());
            CompoundComplexObject result = resultList.get(0);
            assertEquals(compoundObject.getId() + 1, result.getId().longValue());
            assertEquals(compoundObject.getName(), result.getName());
            assertEquals(compoundObject.getComplexObject().getId(), result.getComplexObject().getId());
            assertEquals(compoundObject.getComplexObject().getName(), result.getComplexObject().getName());
            assertEquals(compoundObject.getComplexObject().getBigDecimal(), result.getComplexObject().getBigDecimal());
            assertEquals(compoundObject.getComplexObject().getDate(), result.getComplexObject().getDate());
        }
    }

    @Test
    public void testRestCallComplexHierarchyObject() {
        Map<String, CompoundComplexObject> map = new HashMap<>();
        map.put("first", RestApiTestUtils.createCompoundComplexObject());

        try (SpongeRestApiClient client = createRestApiClient()) {
            Object returnValue = client.call("ComplexObjectHierarchyAction", "String", new Integer(100), Arrays.asList("a", "b", "c"),
                    Arrays.asList(new BigDecimal("1.25"), new BigDecimal("5.5")), new String[] { "A", "B" }, map);

            assertTrue(returnValue instanceof List);
        }
    }
}
