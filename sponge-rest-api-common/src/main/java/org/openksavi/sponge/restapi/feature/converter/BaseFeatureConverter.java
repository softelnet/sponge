/*
 * Copyright 2016-2020 The Sponge authors.
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

package org.openksavi.sponge.restapi.feature.converter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseFeatureConverter implements FeatureConverter {

    private static final Logger logger = LoggerFactory.getLogger(BaseFeatureConverter.class);

    private Map<String, UnitFeatureConverter> registry = Collections.synchronizedMap(new LinkedHashMap<>());

    private ObjectMapper objectMapper;

    protected BaseFeatureConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public Object marshal(String name, Object value) {
        if (name == null || value == null) {
            return value;
        }

        UnitFeatureConverter unitConverter = registry.get(name);
        return unitConverter != null ? unitConverter.marshal(this, value) : value;
    }

    @Override
    public Object unmarshal(String name, Object value) {
        if (name == null || value == null) {
            return value;
        }

        UnitFeatureConverter unitConverter = registry.get(name);
        return unitConverter != null ? unitConverter.unmarshal(this, value) : value;
    }

    @Override
    public void register(UnitFeatureConverter unitConverter) {
        logger.trace("Registering {} feature(s) converter: {}", unitConverter.getNames(), unitConverter.getClass());
        unitConverter.getNames().forEach(name -> registry.put(name, unitConverter));
    }

    @Override
    public void registerAll(List<UnitFeatureConverter> unitConverters) {
        unitConverters.forEach(converter -> register(converter));
    }

    @Override
    public UnitFeatureConverter unregister(String name) {
        return registry.remove(name);
    }
}
