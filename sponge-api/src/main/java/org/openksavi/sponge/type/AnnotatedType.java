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

package org.openksavi.sponge.type;

import java.util.Map;

import org.openksavi.sponge.type.value.AnnotatedValue;

/**
 * An annotated type. This type requires a {@code valueType} parameter, which is is a type of an annotated value. A value of this type has
 * to be an instance of {@linkplain org.openksavi.sponge.type.value.AnnotatedValue}.
 *
 * @param <T> valueType type.
 */
public class AnnotatedType<T> extends DataType<AnnotatedValue<T>> {

    /** The annotated value type. */
    private DataType<?> valueType;

    public AnnotatedType() {
        super(DataTypeKind.ANNOTATED);
        this.valueType = new AnyType();
    }

    public AnnotatedType(DataType<T> valueType) {
        super(DataTypeKind.ANNOTATED);
        this.valueType = valueType;
    }

    @Override
    public AnnotatedType<T> withFormat(String format) {
        return (AnnotatedType<T>) super.withFormat(format);
    }

    @Override
    public AnnotatedType<T> withFeatures(Map<String, Object> features) {
        return (AnnotatedType<T>) super.withFeatures(features);
    }

    @Override
    public AnnotatedType<T> withFeature(String name, Object value) {
        return (AnnotatedType<T>) super.withFeature(name, value);
    }

    @Override
    public AnnotatedType<T> withDefaultValue(AnnotatedValue<T> value) {
        return (AnnotatedType<T>) super.withDefaultValue(value);
    }

    @Override
    public AnnotatedType<T> withNullable(boolean nullable) {
        return (AnnotatedType<T>) super.withNullable(nullable);
    }

    @SuppressWarnings("unchecked")
    public DataType<T> getValueType() {
        return (DataType<T>) valueType;
    }
}
