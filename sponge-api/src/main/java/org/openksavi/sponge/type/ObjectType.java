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
 * An object. This type requires a class name (typically a Java class name) as a constructor parameter.
 *
 * @param <T> object type.
 */
public class ObjectType<T> extends DataType<T> {

    /** The class name. */
    private String className;

    protected ObjectType() {
        this(null);
    }

    public ObjectType(String className) {
        super(DataTypeKind.OBJECT);

        this.className = className;
    }

    @Override
    public ObjectType<T> withFormat(String format) {
        return (ObjectType<T>) super.withFormat(format);
    }

    @Override
    public ObjectType<T> withFeatures(Map<String, Object> features) {
        return (ObjectType<T>) super.withFeatures(features);
    }

    @Override
    public ObjectType<T> withFeature(String name, Object value) {
        return (ObjectType<T>) super.withFeature(name, value);
    }

    @Override
    public ObjectType<T> withDefaultValue(T value) {
        return (ObjectType<T>) super.withDefaultValue(value);
    }

    @Override
    public ObjectType<T> withNullable(boolean nullable) {
        return (ObjectType<T>) super.withNullable(nullable);
    }

    @Override
    public ObjectType<T> withNullable() {
        return (ObjectType<T>) super.withNullable();
    }

    public String getClassName() {
        return className;
    }
}
