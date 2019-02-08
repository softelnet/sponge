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

/**
 * A map type. This type requires two parameters: a type of keys and a type of values in the map.
 *
 * @param <K> a map key type.
 * @param <V> a map value type.
 */
@SuppressWarnings("unchecked")
public class MapType<K, V> extends DataType<Map<K, V>> {

    /** The map key type. */
    private DataType<?> keyType;

    /** The map value type. */
    private DataType<?> valueType;

    public MapType() {
        super(DataTypeKind.MAP);
        this.keyType = new AnyType();
        this.valueType = new AnyType();
    }

    public MapType(DataType<K> keyType, DataType<V> valueType) {
        super(DataTypeKind.MAP);
        this.keyType = keyType;
        this.valueType = valueType;
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

    public DataType<K> getKeyType() {
        return (DataType<K>) keyType;
    }

    public DataType<V> getValueType() {
        return (DataType<V>) valueType;
    }
}
