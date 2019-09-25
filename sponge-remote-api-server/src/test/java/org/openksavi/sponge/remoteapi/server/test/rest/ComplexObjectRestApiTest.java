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

package org.openksavi.sponge.remoteapi.server.test.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.remoteapi.server.test.RemoteApiTestUtils;
import org.openksavi.sponge.remoteapi.test.base.CompoundComplexObject;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.util.RestApiUtils;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ComplexObjectRestApiTest.TestConfig.class })
@DirtiesContext
public class ComplexObjectRestApiTest extends BasicTestTemplate {

    protected SpongeRestClient createRestClient() {
        return new DefaultSpongeRestClient(SpongeRestClientConfiguration.builder()
                .url(String.format("http://localhost:%d/%s", port, RestApiConstants.DEFAULT_PATH)).build());
    }

    @Test
    public void testRestCallComplexObject() {
        CompoundComplexObject compoundObject = RemoteApiTestUtils.createCompoundComplexObject();

        try (SpongeRestClient client = createRestClient()) {
            CompoundComplexObject result = client.call(CompoundComplexObject.class, "ComplexObjectAction", Arrays.asList(compoundObject));

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
        CompoundComplexObject compoundObject = RemoteApiTestUtils.createCompoundComplexObject();

        try (SpongeRestClient client = createRestClient()) {
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

    @SuppressWarnings("unchecked")
    @Test
    public void testRestCallComplexObjectList() {
        String actionName = "ComplexObjectListAction";
        CompoundComplexObject compoundObject = RemoteApiTestUtils.createCompoundComplexObject();

        try (SpongeRestClient client = createRestClient()) {
            Object returnValue = client.call(actionName, Arrays.asList(Arrays.asList(compoundObject)));

            assertTrue(returnValue instanceof List);

            List<CompoundComplexObject> resultList = (List<CompoundComplexObject>) returnValue;
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
        map.put("first", RemoteApiTestUtils.createCompoundComplexObject());

        try (SpongeRestClient client = createRestClient()) {
            Object returnValue =
                    client.call("ComplexObjectHierarchyAction", Arrays.asList("String", new Integer(100), Arrays.asList("a", "b", "c"),
                            Arrays.asList(new BigDecimal("1.25"), new BigDecimal("5.5")), new String[] { "A", "B" }, map));

            assertTrue(returnValue instanceof List);
        }
    }
}
