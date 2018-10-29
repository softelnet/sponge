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
import java.sql.Date;
import java.time.Instant;
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
import org.openksavi.sponge.examples.TestComplexObject;
import org.openksavi.sponge.examples.TestCompoundComplexObject;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestApiClient;
import org.openksavi.sponge.restapi.client.RestApiClientConfiguration;
import org.openksavi.sponge.restapi.client.SpongeRestApiClient;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
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
                    .knowledgeBase("kb", "examples/rest-api-server/rest_api_complex_object.py").build();
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

        TestCompoundComplexObject result =
                createRestApiClient().call(TestCompoundComplexObject.class, "ComplexObjectAction", compoundObject);

        assertEquals(compoundObject.getId() + 1, result.getId().longValue());
        assertEquals(compoundObject.getName(), result.getName());
        assertEquals(compoundObject.getComplexObject().getId(), result.getComplexObject().getId());
        assertEquals(compoundObject.getComplexObject().getName(), result.getComplexObject().getName());
        assertEquals(compoundObject.getComplexObject().getBigDecimal(), result.getComplexObject().getBigDecimal());
        assertEquals(compoundObject.getComplexObject().getDate(), result.getComplexObject().getDate());
    }

    @Test
    public void testRestCallComplexObjectNoClass() {
        TestCompoundComplexObject compoundObject = createTestCompoundComplexObject();

        TestCompoundComplexObject result = (TestCompoundComplexObject) createRestApiClient().call("ComplexObjectAction", compoundObject);

        assertEquals(compoundObject.getId() + 1, result.getId().longValue());
        assertEquals(compoundObject.getName(), result.getName());
        assertEquals(compoundObject.getComplexObject().getId(), result.getComplexObject().getId());
        assertEquals(compoundObject.getComplexObject().getName(), result.getComplexObject().getName());
        assertEquals(compoundObject.getComplexObject().getBigDecimal(), result.getComplexObject().getBigDecimal());
        assertEquals(compoundObject.getComplexObject().getDate(), result.getComplexObject().getDate());
    }

    @Test
    public void testRestCallComplexObjectNoMeta() {
        String actionName = "ComplexObjectAction";
        TestCompoundComplexObject compoundObject = createTestCompoundComplexObject();

        Object value = createRestApiClient().callWithNoMeta(actionName, compoundObject);

        assertTrue(value instanceof Map);

        ObjectMapper mapper = RestApiUtils.createObjectMapper();
        TestCompoundComplexObject result = mapper.convertValue(value, TestCompoundComplexObject.class);

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

        Object returnValue = createRestApiClient().call(actionName, Arrays.asList(compoundObject));

        assertTrue(returnValue instanceof List);

        TypeConverter typeConverter = new DefaultTypeConverter(new ObjectMapper());
        @SuppressWarnings("unchecked")
        List<TestCompoundComplexObject> resultList = (List<TestCompoundComplexObject>) typeConverter
                .unmarshal(engine.getActionManager().getActionAdapter(actionName).getResultMeta().getType(), returnValue);// RestApiUtils.unmarshalActionResult(new
                                                                                                                          // DefaultTypeConverter(new
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

        Object returnValue = createRestApiClient().call("ComplexObjectHierarchyAction", "String", new Integer(100),
                Arrays.asList("a", "b", "c"), Arrays.asList(new BigDecimal("1.25"), new BigDecimal("5.5")), new String[] { "A", "B" }, map);

        assertTrue(returnValue instanceof List);
    }
}
