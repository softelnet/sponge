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

package org.openksavi.sponge.restapi.type.converter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.type.Type;
import org.openksavi.sponge.type.TypeKind;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class BaseTypeConverter implements TypeConverter {

    private static final Logger logger = LoggerFactory.getLogger(BaseTypeConverter.class);

    private Map<TypeKind, UnitTypeConverter> registry = Collections.synchronizedMap(new LinkedHashMap<>());

    private ObjectMapper objectMapper;

    protected BaseTypeConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public Object marshal(Type type, Object value) {
        return value != null ? getUnitConverter(type).marshal(this, type, value) : null;
    }

    @Override
    public Object unmarshal(Type type, Object value) {
        return value != null ? getUnitConverter(type).unmarshal(this, type, value) : null;
    }

    @Override
    public void register(UnitTypeConverter unitConverter) {
        logger.debug("Registering {} converter: {}", unitConverter.getTypeKind(), unitConverter.getClass());
        registry.put(unitConverter.getTypeKind(), unitConverter);
    }

    @Override
    public void registerAll(List<UnitTypeConverter> unitConverters) {
        unitConverters.forEach(converter -> register(converter));
    }

    @Override
    public UnitTypeConverter unregister(TypeKind typeKind) {
        return registry.remove(typeKind);
    }

    protected <T extends Type> UnitTypeConverter<T> getUnitConverter(T type) {
        return Validate.notNull(registry.get(type.getKind()), "Unsupported type {}", type.getKind());
    }
}
