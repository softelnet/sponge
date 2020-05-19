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

package org.openksavi.sponge.remoteapi.type.converter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.remoteapi.feature.converter.FeatureConverter;
import org.openksavi.sponge.remoteapi.feature.converter.FeaturesUtils;
import org.openksavi.sponge.remoteapi.util.RemoteApiUtils;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.value.AnnotatedValue;

@SuppressWarnings({ "rawtypes", "unchecked" })
public abstract class BaseTypeConverter implements TypeConverter {

    private static final Logger logger = LoggerFactory.getLogger(BaseTypeConverter.class);

    private Map<DataTypeKind, UnitTypeConverter> registry = Collections.synchronizedMap(new LinkedHashMap<>());

    private ObjectMapper objectMapper;

    private FeatureConverter featureConverter;

    protected BaseTypeConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    @Override
    public FeatureConverter getFeatureConverter() {
        return featureConverter;
    }

    @Override
    public void setFeatureConverter(FeatureConverter featureConverter) {
        this.featureConverter = featureConverter;
    }

    @Override
    public <T, D extends DataType> Object marshal(D type, T value) {
        if (value == null) {
            return null;
        }

        Validate.notNull(type, "The type must not be null");

        if (type.isAnnotated()) {
            AnnotatedValue annotatedValue;
            if (value instanceof AnnotatedValue) {
                annotatedValue = (AnnotatedValue) value;
            } else {
                // Wrap if not wrapped.
                annotatedValue = new AnnotatedValue(value);
            }

            return new AnnotatedValue<>(
                    annotatedValue.getValue() != null ? getInternalUnitConverter(type).marshal(this, type, annotatedValue.getValue())
                            : null,
                    annotatedValue.getValueLabel(), annotatedValue.getValueDescription(),
                    FeaturesUtils.marshal(featureConverter, annotatedValue.getFeatures()), annotatedValue.getTypeLabel(),
                    annotatedValue.getTypeDescription());
        }

        return getInternalUnitConverter(type).marshal(this, type, value);
    }

    @Override
    public <D extends DataType> Object unmarshal(D type, Object value) {
        if (value == null) {
            return null;
        }

        Validate.notNull(type, "The type must not be null");

        // Handle a wrapped annotated value.
        if (type.isAnnotated()) {
            AnnotatedValue annotatedValue;
            if (value instanceof AnnotatedValue) {
                annotatedValue = (AnnotatedValue) value;
            } else if (RemoteApiUtils.isAnnotatedValueMap(value)) {
                annotatedValue = objectMapper.convertValue(value, AnnotatedValue.class);
            } else {
                // Wrap if not wrapped.
                annotatedValue = new AnnotatedValue(value);
            }

            if (annotatedValue.getValue() != null) {
                annotatedValue.setValue(getInternalUnitConverter(type).unmarshal(this, type, annotatedValue.getValue()));
            }

            annotatedValue.setFeatures(FeaturesUtils.unmarshal(featureConverter, annotatedValue.getFeatures()));

            return annotatedValue;
        }

        return getInternalUnitConverter(type).unmarshal(this, type, value);
    }

    @Override
    public void register(UnitTypeConverter unitConverter) {
        logger.trace("Registering {} type converter: {}", unitConverter.getTypeKind(), unitConverter.getClass());
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

    public <T, D extends DataType> UnitTypeConverter<T, D> getInternalUnitConverter(D type) {
        return getInternalUnitConverter(type.getKind());
    }

    /**
     * Returns the internal unit converter. The returned converter should not be used to convert values directly.
     *
     * @param typeKind the data type kind.
     * @return the unit converter.
     */
    public UnitTypeConverter getInternalUnitConverter(DataTypeKind typeKind) {
        return Validate.notNull(registry.get(typeKind), "Unsupported type %s", typeKind);
    }
}
