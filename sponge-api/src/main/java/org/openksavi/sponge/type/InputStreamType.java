/*
 * Copyright 2016-2021 The Sponge authors.
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
import org.openksavi.sponge.type.value.InputStreamValue;

/**
 * An input stream type. A value of this type has to be an instance of {@linkplain org.openksavi.sponge.type.value.InputStreamValue}.
 */
public class InputStreamType extends DataType<InputStreamValue> {

    public InputStreamType() {
        this(null);
    }

    public InputStreamType(String name) {
        super(DataTypeKind.INPUT_STREAM, name);
    }

    @Override
    public InputStreamType withName(String name) {
        return (InputStreamType) super.withName(name);
    }

    @Override
    public InputStreamType withLabel(String label) {
        return (InputStreamType) super.withLabel(label);
    }

    @Override
    public InputStreamType withDescription(String description) {
        return (InputStreamType) super.withDescription(description);
    }

    @Override
    public InputStreamType withAnnotated(boolean annotated) {
        return (InputStreamType) super.withAnnotated(annotated);
    }

    @Override
    public InputStreamType withAnnotated() {
        return (InputStreamType) super.withAnnotated();
    }

    @Override
    public InputStreamType withFormat(String format) {
        return (InputStreamType) super.withFormat(format);
    }

    @Override
    public InputStreamType withFeatures(Map<String, Object> features) {
        return (InputStreamType) super.withFeatures(features);
    }

    @Override
    public InputStreamType withFeature(String name, Object value) {
        return (InputStreamType) super.withFeature(name, value);
    }

    @Override
    public InputStreamType withDefaultValue(InputStreamValue value) {
        return (InputStreamType) super.withDefaultValue(value);
    }

    @Override
    public InputStreamType withNullable(boolean nullable) {
        return (InputStreamType) super.withNullable(nullable);
    }

    @Override
    public InputStreamType withNullable() {
        return (InputStreamType) super.withNullable();
    }

    @Override
    public InputStreamType withReadOnly(boolean readOnly) {
        return (InputStreamType) super.withReadOnly(readOnly);
    }

    @Override
    public InputStreamType withReadOnly() {
        return (InputStreamType) super.withReadOnly();
    }

    @Override
    public InputStreamType withOptional() {
        return (InputStreamType) super.withOptional();
    }

    @Override
    public InputStreamType withProvided(ProvidedMeta provided) {
        return (InputStreamType) super.withProvided(provided);
    }
}
