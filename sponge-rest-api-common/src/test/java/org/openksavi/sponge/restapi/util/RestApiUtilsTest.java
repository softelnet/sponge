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

package org.openksavi.sponge.restapi.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.type.value.AnnotatedValue;

public class RestApiUtilsTest {

    @Test
    public void testObfuscatePassword() {
        assertEquals("{\"username\":\"test\",\"password\":\"***\"}",
                RestApiUtils.obfuscatePassword("{\"username\":\"test\",\"password\":\"secret\"}"));

        assertEquals("{\"username\":\"test\",\"password\":\"***\"}",
                RestApiUtils.obfuscatePassword("{\"username\":\"test\",\"password\":\"\"}"));

        assertEquals("{\"username\":null,\"password\":null}", RestApiUtils.obfuscatePassword("{\"username\":null,\"password\":null}"));
        assertEquals("{\"username\":null,\"password\":null,\"object\":{\"name\":\"value\"}}",
                RestApiUtils.obfuscatePassword("{\"username\":null,\"password\":null,\"object\":{\"name\":\"value\"}}"));
        assertEquals(
                "{\"id\":null,\"username\":\"test\",\"password\":\"***\",\"authToken\":null,"
                        + "\"name\":\"TestAction\",\"args\":[\"TEST\",null],\"version\":null}",
                RestApiUtils.obfuscatePassword("{\"id\":null,\"username\":\"test\",\"password\":\"password\",\"authToken\":null,"
                        + "\"name\":\"TestAction\",\"args\":[\"TEST\",null],\"version\":null}"));

        assertEquals("{\"passwordRetype\":\"***\"}", RestApiUtils.obfuscatePassword("{\"passwordRetype\":\"secret!!!\"}"));
        assertEquals("{\"PASSwordRetype\":\"***\"}", RestApiUtils.obfuscatePassword("{\"PASSwordRetype\":\"secret!!!\"}"));
        assertEquals("{\"a\":\"b\"}", RestApiUtils.obfuscatePassword("{\"a\":\"b\"}"));
    }

    @Test
    public void testIsAnnotatedValueMap() {
        Set<String> fieldNames = Arrays.asList(AnnotatedValue.class.getDeclaredFields()).stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers())).map(Field::getName).collect(Collectors.toSet());

        Map<String, Object> exampleMap = new LinkedHashMap<>();
        fieldNames.forEach(name -> exampleMap.put(name, null));
        assertTrue(RestApiUtils.isAnnotatedValueMap(exampleMap));

        assertFalse(RestApiUtils.isAnnotatedValueMap(SpongeUtils.immutableMapOf("value", null)));
    }
}
