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

@SuppressWarnings("unchecked")
public class MapType<K, V> extends Type<Map<K, V>> {

    private Type<?> keyType;

    private Type<?> valueType;

    public MapType() {
        super(TypeKind.MAP);
        this.keyType = new AnyType();
        this.valueType = new AnyType();
    }

    public MapType(Type<K> keyType, Type<V> valueType) {
        super(TypeKind.MAP);
        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public MapType<K, V> format(String format) {
        return (MapType<K, V>) super.format(format);
    }

    @Override
    public MapType<K, V> features(Map<String, Object> features) {
        return (MapType<K, V>) super.features(features);
    }

    @Override
    public MapType<K, V> feature(String name, Object value) {
        return (MapType<K, V>) super.feature(name, value);
    }

    @Override
    public MapType<K, V> defaultValue(Map<K, V> value) {
        return (MapType<K, V>) super.defaultValue(value);
    }

    @Override
    public MapType<K, V> nullable(boolean nullable) {
        return (MapType<K, V>) super.nullable(nullable);
    }

    public Type<K> getKeyType() {
        return (Type<K>) keyType;
    }

    public Type<V> getValueType() {
        return (Type<V>) valueType;
    }
}
