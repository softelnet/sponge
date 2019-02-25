/*
 * Copyright 2016-2019 The Sponge authors.
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
 * A type representing a data type. A value of this type has to be an instance of {@linkplain org.openksavi.sponge.type.DataType}.
 */
@SuppressWarnings("rawtypes")
public class TypeType extends DataType<DataType> {

    public TypeType() {
        this(null);
    }

    public TypeType(String name) {
        super(DataTypeKind.TYPE, name);
    }

    @Override
    public TypeType withName(String name) {
        return (TypeType) super.withName(name);
    }

    @Override
    public TypeType withLabel(String label) {
        return (TypeType) super.withLabel(label);
    }

    @Override
    public TypeType withDescription(String description) {
        return (TypeType) super.withDescription(description);
    }

    @Override
    public TypeType withAnnotated(boolean annotated) {
        return (TypeType) super.withAnnotated(annotated);
    }

    @Override
    public TypeType withAnnotated() {
        return (TypeType) super.withAnnotated();
    }

    @Override
    public TypeType withFormat(String format) {
        return (TypeType) super.withFormat(format);
    }

    @Override
    public TypeType withFeatures(Map<String, Object> features) {
        return (TypeType) super.withFeatures(features);
    }

    @Override
    public TypeType withFeature(String name, Object value) {
        return (TypeType) super.withFeature(name, value);
    }

    @Override
    public TypeType withDefaultValue(DataType value) {
        return (TypeType) super.withDefaultValue(value);
    }

    @Override
    public TypeType withNullable(boolean nullable) {
        return (TypeType) super.withNullable(nullable);
    }

    @Override
    public TypeType withNullable() {
        return (TypeType) super.withNullable();
    }

    @Override
    public TypeType withOptional() {
        return (TypeType) super.withOptional();
    }

    @Override
    public TypeType withProvided(ProvidedMeta provided) {
        return (TypeType) super.withProvided(provided);
    }
}
