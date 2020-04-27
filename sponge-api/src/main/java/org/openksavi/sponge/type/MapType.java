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

import java.util.Map;

import org.openksavi.sponge.type.provided.ProvidedMeta;

/**
 * A map type. This type requires two parameters: a type of keys and a type of values in the map.
 *
 * @param <K> a map key type.
 * @param <V> a map value type.
 */
@SuppressWarnings("unchecked")
public class MapType<K, V> extends CollectionType<Map<K, V>> {

    /** The map key type. */
    private DataType<?> keyType;

    /** The map value type. */
    private DataType<?> valueType;

    public MapType() {
        this(null);
    }

    public MapType(String name) {
        this(name, (DataType<K>) new AnyType(), (DataType<V>) new AnyType());
    }

    public MapType(DataType<K> keyType, DataType<V> valueType) {
        this(null, keyType, valueType);
    }

    public MapType(String name, DataType<K> keyType, DataType<V> valueType) {
        super(DataTypeKind.MAP, name);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public MapType<K, V> withName(String name) {
        return (MapType<K, V>) super.withName(name);
    }

    @Override
    public MapType<K, V> withLabel(String label) {
        return (MapType<K, V>) super.withLabel(label);
    }

    @Override
    public MapType<K, V> withDescription(String description) {
        return (MapType<K, V>) super.withDescription(description);
    }

    @Override
    public MapType<K, V> withAnnotated(boolean annotated) {
        return (MapType<K, V>) super.withAnnotated(annotated);
    }

    @Override
    public MapType<K, V> withAnnotated() {
        return (MapType<K, V>) super.withAnnotated();
    }

    @Override
    public MapType<K, V> withFormat(String format) {
        return (MapType<K, V>) super.withFormat(format);
    }

    @Override
    public MapType<K, V> withFeatures(Map<String, Object> features) {
        return (MapType<K, V>) super.withFeatures(features);
    }

    @Override
    public MapType<K, V> withFeature(String name, Object value) {
        return (MapType<K, V>) super.withFeature(name, value);
    }

    @Override
    public MapType<K, V> withDefaultValue(Map<K, V> value) {
        return (MapType<K, V>) super.withDefaultValue(value);
    }

    @Override
    public MapType<K, V> withNullable(boolean nullable) {
        return (MapType<K, V>) super.withNullable(nullable);
    }

    @Override
    public MapType<K, V> withNullable() {
        return (MapType<K, V>) super.withNullable();
    }

    @Override
    public MapType<K, V> withReadOnly(boolean readOnly) {
        return (MapType<K, V>) super.withReadOnly(readOnly);
    }

    @Override
    public MapType<K, V> withReadOnly() {
        return (MapType<K, V>) super.withReadOnly();
    }

    @Override
    public MapType<K, V> withOptional() {
        return (MapType<K, V>) super.withOptional();
    }

    @Override
    public MapType<K, V> withProvided(ProvidedMeta provided) {
        return (MapType<K, V>) super.withProvided(provided);
    }

    public MapType<K, V> withKey(DataType<K> keyType) {
        this.keyType = keyType;
        return this;
    }

    public MapType<K, V> withValue(DataType<V> valueType) {
        this.valueType = valueType;
        return this;
    }

    public DataType<K> getKeyType() {
        return (DataType<K>) keyType;
    }

    public DataType<V> getValueType() {
        return (DataType<V>) valueType;
    }

    @Override
    public MapType<K, V> clone() {
        MapType<K, V> cloned = (MapType<K, V>) super.clone();
        cloned.keyType = keyType != null ? keyType.clone() : null;
        cloned.valueType = valueType != null ? valueType.clone() : null;

        return cloned;
    }
}
