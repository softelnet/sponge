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

import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.value.AnnotatedValue;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class BaseTypeConverter implements TypeConverter {

    private static final Logger logger = LoggerFactory.getLogger(BaseTypeConverter.class);

    private Map<DataTypeKind, UnitTypeConverter> registry = Collections.synchronizedMap(new LinkedHashMap<>());

    private ObjectMapper objectMapper;

    protected BaseTypeConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public <T, D extends DataType> Object marshal(D type, T value) {
        if (value == null) {
            return null;
        }

        Validate.notNull(type, "The type must not be null");

        if (type.isAnnotated()) {
            AnnotatedValue<T> annotatedValue = (AnnotatedValue) value;
            return new AnnotatedValue<>(getUnitConverter(type).marshal(this, type, annotatedValue.getValue()),
                    annotatedValue.getValueLabel(), annotatedValue.getValueDescription(), annotatedValue.getFeatures(),
                    annotatedValue.getTypeLabel(), annotatedValue.getTypeDescription());
        }

        return getUnitConverter(type).marshal(this, type, value);
    }

    @Override
    public <T, D extends DataType> T unmarshal(D type, Object value) {
        if (value == null) {
            return null;
        }

        Validate.notNull(type, "The type must not be null");

        if (type.isAnnotated()) {
            Validate.isInstanceOf(Map.class, value, "Expected an annotated value as a map but got %s", value.getClass());

            AnnotatedValue annotatedValue = objectMapper.convertValue(value, AnnotatedValue.class);
            annotatedValue.setValue(getUnitConverter(type).unmarshal(this, type, annotatedValue.getValue()));

            return (T) annotatedValue;
        }

        return (T) getUnitConverter(type).unmarshal(this, type, value);
    }

    @Override
    public void register(UnitTypeConverter unitConverter) {
        logger.trace("Registering {} converter: {}", unitConverter.getTypeKind(), unitConverter.getClass());
        registry.put(unitConverter.getTypeKind(), unitConverter);
    }

    @Override
    public void registerAll(List<UnitTypeConverter> unitConverters) {
        unitConverters.forEach(converter -> register(converter));
    }

    @Override
    public UnitTypeConverter unregister(DataTypeKind typeKind) {
        return registry.remove(typeKind);
    }

    protected <T, D extends DataType> UnitTypeConverter<T, D> getUnitConverter(D type) {
        return Validate.notNull(registry.get(type.getKind()), "Unsupported type %s", type.getKind());
    }
}
