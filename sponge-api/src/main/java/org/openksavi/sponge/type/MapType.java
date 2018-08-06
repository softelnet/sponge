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

public class MapType extends Type {

    private Type keyType;

    private Type valueType;

    public MapType() {
        this(new AnyType(), new AnyType());
    }

    public MapType(Type keyType, Type valueType) {
        super(TypeKind.MAP);

        this.keyType = keyType;
        this.valueType = valueType;
    }

    @Override
    public MapType format(String format) {
        return (MapType) super.format(format);
    }

    @Override
    public MapType tags(String... tags) {
        return (MapType) super.tags(tags);
    }

    @Override
    public MapType tag(String tag) {
        return (MapType) super.tag(tag);
    }

    @Override
    public MapType features(Map<String, String> features) {
        return (MapType) super.features(features);
    }

    @Override
    public MapType feature(String name, String value) {
        return (MapType) super.feature(name, value);
    }

    public Type getKeyType() {
        return keyType;
    }

    public Type getValueType() {
        return valueType;
    }
}
