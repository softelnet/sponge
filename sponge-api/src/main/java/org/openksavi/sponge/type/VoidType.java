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
 * A void type that may be used to specify that an action returns no meaningful result.
 */
public class VoidType extends DataType<Void> {

    public VoidType() {
        this(null);
    }

    public VoidType(String name) {
        super(DataTypeKind.VOID, name);
    }

    @Override
    public VoidType withName(String name) {
        return (VoidType) super.withName(name);
    }

    @Override
    public VoidType withLabel(String label) {
        return (VoidType) super.withLabel(label);
    }

    @Override
    public VoidType withDescription(String description) {
        return (VoidType) super.withDescription(description);
    }

    @Override
    public VoidType withAnnotated(boolean annotated) {
        return (VoidType) super.withAnnotated(annotated);
    }

    @Override
    public VoidType withAnnotated() {
        return (VoidType) super.withAnnotated();
    }

    @Override
    public VoidType withFormat(String format) {
        return (VoidType) super.withFormat(format);
    }

    @Override
    public VoidType withFeatures(Map<String, Object> features) {
        return (VoidType) super.withFeatures(features);
    }

    @Override
    public VoidType withFeature(String name, Object value) {
        return (VoidType) super.withFeature(name, value);
    }

    @Override
    public VoidType withDefaultValue(Void value) {
        return (VoidType) super.withDefaultValue(value);
    }

    @Override
    public VoidType withNullable(boolean nullable) {
        return (VoidType) super.withNullable(nullable);
    }

    @Override
    public VoidType withNullable() {
        return (VoidType) super.withNullable();
    }

    @Override
    public VoidType withReadOnly(boolean readOnly) {
        return (VoidType) super.withReadOnly(readOnly);
    }

    @Override
    public VoidType withReadOnly() {
        return (VoidType) super.withReadOnly();
    }

    @Override
    public VoidType withOptional() {
        return (VoidType) super.withOptional();
    }

    @Override
    public VoidType withProvided(ProvidedMeta provided) {
        return (VoidType) super.withProvided(provided);
    }
}
