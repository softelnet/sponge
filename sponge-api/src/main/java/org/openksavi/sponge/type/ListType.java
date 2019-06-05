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

import org.openksavi.sponge.type.provided.ProvidedMeta;

/**
 * A list type. This type requires an {@code elementType} parameter, which is is a type of list elements.
 *
 * @param <E> element type.
 */
public class ListType<E> extends CollectionType<List<E>> {

    /** The list element type. */
    private DataType<?> elementType;

    /** The flag specifying if the list should contain unique values. Defaults to {@code false}. */
    private boolean unique = false;

    public ListType() {
        this((String) null);
    }

    @SuppressWarnings("unchecked")
    public ListType(String name) {
        this(name, (DataType<E>) new AnyType());
    }

    public ListType(DataType<E> elementType) {
        this(null, elementType);
    }

    public ListType(String name, DataType<E> elementType) {
        super(DataTypeKind.LIST, name);
        this.elementType = elementType;
    }

    @Override
    public ListType<E> withName(String name) {
        return (ListType<E>) super.withName(name);
    }

    @Override
    public ListType<E> withLabel(String label) {
        return (ListType<E>) super.withLabel(label);
    }

    @Override
    public ListType<E> withDescription(String description) {
        return (ListType<E>) super.withDescription(description);
    }

    @Override
    public ListType<E> withAnnotated(boolean annotated) {
        return (ListType<E>) super.withAnnotated(annotated);
    }

    @Override
    public ListType<E> withAnnotated() {
        return (ListType<E>) super.withAnnotated();
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

    @Override
    public ListType<E> withOptional() {
        return (ListType<E>) super.withOptional();
    }

    @Override
    public ListType<E> withProvided(ProvidedMeta provided) {
        return (ListType<E>) super.withProvided(provided);
    }

    public ListType<E> withElement(DataType<E> elementType) {
        this.elementType = elementType;
        return this;
    }

    public ListType<E> withUnique(boolean unique) {
        this.unique = unique;
        return this;
    }

    public ListType<E> withUnique() {
        return withUnique(true);
    }

    @SuppressWarnings("unchecked")
    public DataType<E> getElementType() {
        return (DataType<E>) elementType;
    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    @Override
    public ListType<E> clone() {
        ListType<E> cloned = (ListType<E>) super.clone();
        cloned.elementType = elementType != null ? elementType.clone() : null;

        return cloned;
    }
}
