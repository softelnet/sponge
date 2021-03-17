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
import org.openksavi.sponge.type.value.OutputStreamValue;

/**
 * A stream type. Supports only output streams. A value of this type has to be an instance of
 * {@linkplain org.openksavi.sponge.type.value.OutputStreamValue}.
 */
public class OutputStreamType extends DataType<OutputStreamValue> {

    public OutputStreamType() {
        this(null);
    }

    public OutputStreamType(String name) {
        super(DataTypeKind.OUTPUT_STREAM, name);
    }

    @Override
    public OutputStreamType withName(String name) {
        return (OutputStreamType) super.withName(name);
    }

    @Override
    public OutputStreamType withLabel(String label) {
        return (OutputStreamType) super.withLabel(label);
    }

    @Override
    public OutputStreamType withDescription(String description) {
        return (OutputStreamType) super.withDescription(description);
    }

    @Override
    public OutputStreamType withAnnotated(boolean annotated) {
        return (OutputStreamType) super.withAnnotated(annotated);
    }

    @Override
    public OutputStreamType withAnnotated() {
        return (OutputStreamType) super.withAnnotated();
    }

    @Override
    public OutputStreamType withFormat(String format) {
        return (OutputStreamType) super.withFormat(format);
    }

    @Override
    public OutputStreamType withFeatures(Map<String, Object> features) {
        return (OutputStreamType) super.withFeatures(features);
    }

    @Override
    public OutputStreamType withFeature(String name, Object value) {
        return (OutputStreamType) super.withFeature(name, value);
    }

    @Override
    public OutputStreamType withDefaultValue(OutputStreamValue value) {
        return (OutputStreamType) super.withDefaultValue(value);
    }

    @Override
    public OutputStreamType withNullable(boolean nullable) {
        return (OutputStreamType) super.withNullable(nullable);
    }

    @Override
    public OutputStreamType withNullable() {
        return (OutputStreamType) super.withNullable();
    }

    @Override
    public OutputStreamType withReadOnly(boolean readOnly) {
        return (OutputStreamType) super.withReadOnly(readOnly);
    }

    @Override
    public OutputStreamType withReadOnly() {
        return (OutputStreamType) super.withReadOnly();
    }

    @Override
    public OutputStreamType withOptional() {
        return (OutputStreamType) super.withOptional();
    }

    @Override
    public OutputStreamType withProvided(ProvidedMeta provided) {
        return (OutputStreamType) super.withProvided(provided);
    }
}
