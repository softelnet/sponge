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
    public ObjectType<T> format(String format) {
        return (ObjectType<T>) super.format(format);
    }

    @Override
    public ObjectType<T> features(Map<String, Object> features) {
        return (ObjectType<T>) super.features(features);
    }

    @Override
    public ObjectType<T> feature(String name, Object value) {
        return (ObjectType<T>) super.feature(name, value);
    }

    @Override
    public ObjectType<T> defaultValue(T value) {
        return (ObjectType<T>) super.defaultValue(value);
    }

    @Override
    public ObjectType<T> nullable(boolean nullable) {
        return (ObjectType<T>) super.nullable(nullable);
    }

    public String getClassName() {
        return className;
    }
}
