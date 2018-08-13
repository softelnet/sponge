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

public class ListType extends Type {

    private Type elementType;

    public ListType() {
        this(new AnyType());
    }

    public ListType(Type elementType) {
        super(TypeKind.LIST);

        this.elementType = elementType;
    }

    @Override
    public ListType format(String format) {
        return (ListType) super.format(format);
    }

    @Override
    public ListType tags(String... tags) {
        return (ListType) super.tags(tags);
    }

    @Override
    public ListType tag(String tag) {
        return (ListType) super.tag(tag);
    }

    @Override
    public ListType features(Map<String, Object> features) {
        return (ListType) super.features(features);
    }

    @Override
    public ListType feature(String name, Object value) {
        return (ListType) super.feature(name, value);
    }

    public Type getElementType() {
        return elementType;
    }
}
