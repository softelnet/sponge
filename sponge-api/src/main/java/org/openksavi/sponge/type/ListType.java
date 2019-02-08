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

package org.openksavi.sponge.type;

import java.util.List;
import java.util.Map;

/**
 * A list type. This type requires an {@code elementType} parameter, which is is a type of list elements.
 *
 * @param <E> element type.
 */
public class ListType<E> extends DataType<List<E>> {

    /** The list element type. */
    private DataType<?> elementType;

    public ListType() {
        super(DataTypeKind.LIST);
        this.elementType = new AnyType();
    }

    public ListType(DataType<E> elementType) {
        super(DataTypeKind.LIST);
        this.elementType = elementType;
    }

    @Override
    public ListType<E> withFormat(String format) {
        return (ListType<E>) super.withFormat(format);
    }

    @Override
    public ListType<E> withFeatures(Map<String, Object> features) {
        return (ListType<E>) super.withFeatures(features);
    }

    @Override
    public ListType<E> withFeature(String name, Object value) {
        return (ListType<E>) super.withFeature(name, value);
    }

    @Override
    public ListType<E> withDefaultValue(List<E> value) {
        return (ListType<E>) super.withDefaultValue(value);
    }

    @Override
    public ListType<E> withNullable(boolean nullable) {
        return (ListType<E>) super.withNullable(nullable);
    }

    @Override
    public ListType<E> withNullable() {
        return (ListType<E>) super.withNullable();
    }

    @SuppressWarnings("unchecked")
    public DataType<E> getElementType() {
        return (DataType<E>) elementType;
    }
}
