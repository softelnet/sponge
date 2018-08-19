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

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.restapi.model.RestType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.MapType;
import org.openksavi.sponge.type.ObjectType;
import org.openksavi.sponge.type.Type;

/**
 * A set of common REST API utility methods.
 */
public abstract class RestApiUtils {

    private RestApiUtils() {
        //
    }

    public static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper().registerModule(new Jdk8Module()).registerModule(new JavaTimeModule());

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Ensure a proper Type inheritance hierarchy deserialization from JSON to Java.
        mapper.addMixIn(Type.class, RestType.class);

        return mapper;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Object unmarshalValue(ObjectMapper mapper, Type type, Object jsonValue, String valueName) {
        if (jsonValue == null || type == null) {
            return null;
        }

        // Note that metadata not null checks have been performed while enabling the action.
        switch (type.getKind()) {
        case BINARY:
            return mapper.convertValue(jsonValue, byte[].class);
        case OBJECT:
            Validate.isInstanceOf(ObjectType.class, type, "Object type %s doesn't match %s", type.getClass(), ObjectType.class);
            String className = ((ObjectType) type).getClassName();
            return mapper.convertValue(jsonValue, Validate.notNull(getClass(className), "Class % not found", className));
        case LIST:
            Validate.isInstanceOf(Collection.class, jsonValue, "Non list type in the %s: %s", valueName, jsonValue.getClass());

            return ((Collection) jsonValue).stream()
                    .map((Object jsonElem) -> unmarshalValue(mapper, ((ListType) type).getElementType(), jsonElem, valueName))
                    .collect(Collectors.toList());
        case MAP:
            Validate.isInstanceOf(Map.class, jsonValue, "Non map type in the %s: %s", valueName, jsonValue.getClass());

            return ((Map) jsonValue).entrySet().stream()
                    .collect(Collectors.toMap(
                            (Map.Entry entry) -> unmarshalValue(mapper, ((MapType) type).getKeyType(), entry.getKey(), valueName),
                            (Map.Entry entry) -> unmarshalValue(mapper, ((MapType) type).getValueType(), entry.getValue(), valueName)));
        default:
            return jsonValue;
        }
    }

    public static Object unmarshalActionResult(ObjectMapper mapper, ResultMeta<?> resultMeta, Object jsonResult) {
        return unmarshalValue(mapper, resultMeta.getType(), jsonResult, String.format("result of the action"));
    }

    /**
     * Finds a class by the name.
     *
     * @param className the class name.
     * @return the class or {@code null} if not found.
     */
    public static Class<?> getClass(String className) {
        try {
            return ClassUtils.getClass(className);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}
