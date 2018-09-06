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

public class ListType<E> extends Type<List<E>> {

    private Type<?> elementType;

    public ListType() {
        super(TypeKind.LIST);
        this.elementType = new AnyType();
    }

    public ListType(Type<E> elementType) {
        super(TypeKind.LIST);
        this.elementType = elementType;
    }

    @Override
    public ListType<E> format(String format) {
        return (ListType<E>) super.format(format);
    }

    @Override
    public ListType<E> tags(String... tags) {
        return (ListType<E>) super.tags(tags);
    }

    @Override
    public ListType<E> tag(String tag) {
        return (ListType<E>) super.tag(tag);
    }

    @Override
    public ListType<E> features(Map<String, Object> features) {
        return (ListType<E>) super.features(features);
    }

    @Override
    public ListType<E> feature(String name, Object value) {
        return (ListType<E>) super.feature(name, value);
    }

    @Override
    public ListType<E> defaultValue(List<E> value) {
        return (ListType<E>) super.defaultValue(value);
    }

    @Override
    public ListType<E> nullable(boolean nullable) {
        return (ListType<E>) super.nullable(nullable);
    }

    @SuppressWarnings("unchecked")
    public Type<E> getElementType() {
        return (Type<E>) elementType;
    }
}
