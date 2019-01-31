/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.restapi.type.converter.unit;

import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.restapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.AnnotatedType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.value.AnnotatedValue;

public class AnnotatedTypeUnitConverter<T> extends BaseUnitTypeConverter<AnnotatedValue<T>, AnnotatedType<T>> {

    public AnnotatedTypeUnitConverter() {
        super(DataTypeKind.ANNOTATED);
    }

    @Override
    public Object marshal(TypeConverter converter, AnnotatedType<T> type, AnnotatedValue<T> value) {
        return new AnnotatedValue<>(converter.marshal(type.getValueType(), value.getValue()), value.getLabel(), value.getDescription(),
                value.getFeatures());
    }

    @SuppressWarnings("unchecked")
    @Override
    public AnnotatedValue<T> unmarshal(TypeConverter converter, AnnotatedType<T> type, Object value) {
        Validate.isInstanceOf(Map.class, value, "Expected map but got %s", value.getClass());

        AnnotatedValue<T> annotatedValue = converter.getObjectMapper().convertValue(value, AnnotatedValue.class);
        annotatedValue.setValue(converter.unmarshal(type.getValueType(), annotatedValue.getValue()));

        return annotatedValue;
    }
}
