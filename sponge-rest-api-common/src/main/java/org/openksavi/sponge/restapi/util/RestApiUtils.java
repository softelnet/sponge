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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.apache.commons.lang3.ClassUtils;

import org.openksavi.sponge.restapi.model.RestType;
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

        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        // Ensure a proper Type inheritance hierarchy deserialization from JSON to Java.
        mapper.addMixIn(Type.class, RestType.class);

        return mapper;
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

    public static boolean isHttpSuccess(int code) {
        return 200 <= code && code <= 299;
    }

    public static String obfuscatePassword(String request) {
        return request != null ? request.replaceAll("\"password\":\".*\"", "\"password\":\"***\"") : null;
    }
}
