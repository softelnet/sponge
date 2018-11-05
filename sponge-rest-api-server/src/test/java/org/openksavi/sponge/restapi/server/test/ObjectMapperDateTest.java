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

import static org.junit.Assert.assertTrue;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.restapi.test.base.ComplexObject;
import org.openksavi.sponge.restapi.test.base.CompoundComplexObject;
import org.openksavi.sponge.restapi.util.RestApiUtils;

public class ObjectMapperDateTest {

    private static final Logger logger = LoggerFactory.getLogger(ObjectMapperDateTest.class);

    @SuppressWarnings({ "unchecked" })
    @Test
    public void testObjectMapperDate() throws Exception {
        ObjectMapper mapper = RestApiUtils.createObjectMapper();
        ComplexObject complexObject = RestApiTestUtils.createComplexObject();

        String json = mapper.writeValueAsString(complexObject);
        logger.debug("JSON {}", json);

        Map<String, Object> map = mapper.readValue(json, Map.class);
        Object date = map.get("date");
        assertTrue(date instanceof String);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Test
    public void testCompoundObjectMapperDate() throws Exception {
        ObjectMapper mapper = RestApiUtils.createObjectMapper();
        CompoundComplexObject compoundComplexObject = RestApiTestUtils.createCompoundComplexObject();

        String json = mapper.writeValueAsString(compoundComplexObject);
        logger.debug("JSON {}", json);

        Map<String, Object> map = mapper.readValue(json, Map.class);
        Object date = ((Map) map.get("complexObject")).get("date");
        assertTrue(date instanceof String);
    }
}
