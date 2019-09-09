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
 * An object. This type requires a class name (typically a Java class name) as a constructor parameter.
 *
 * @param <T> object type.
 */
public class ObjectType<T> extends DataType<T> {

    /** The feature name for the className. */
    public static final String FEATURE_CLASS_NAME = "className";

    /** The class name. */
    private String className;

    public ObjectType() {
        this(null);
    }

    public ObjectType(String name) {
        this(name, (String) null);
    }

    public ObjectType(String name, String className) {
        super(DataTypeKind.OBJECT, name);

        this.className = className;
    }

    public ObjectType(String name, Class<T> objectClass) {
        this(name, objectClass != null ? objectClass.getName() : null);
    }

    @Override
    public ObjectType<T> withName(String name) {
        return (ObjectType<T>) super.withName(name);
    }

    @Override
    public ObjectType<T> withLabel(String label) {
        return (ObjectType<T>) super.withLabel(label);
    }

    @Override
    public ObjectType<T> withDescription(String description) {
        return (ObjectType<T>) super.withDescription(description);
    }

    @Override
    public ObjectType<T> withAnnotated(boolean annotated) {
        return (ObjectType<T>) super.withAnnotated(annotated);
    }

    @Override
    public ObjectType<T> withAnnotated() {
        return (ObjectType<T>) super.withAnnotated();
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

    @Override
    public ObjectType<T> withOptional() {
        return (ObjectType<T>) super.withOptional();
    }

    @Override
    public ObjectType<T> withProvided(ProvidedMeta provided) {
        return (ObjectType<T>) super.withProvided(provided);
    }

    public ObjectType<T> withClassName(String className) {
        this.className = className;
        return this;
    }

    public String getClassName() {
        return className;
    }
}
