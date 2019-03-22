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
public class StreamType extends DataType<OutputStreamValue> {

    public StreamType() {
        this(null);
    }

    public StreamType(String name) {
        super(DataTypeKind.STREAM, name);
    }

    @Override
    public StreamType withName(String name) {
        return (StreamType) super.withName(name);
    }

    @Override
    public StreamType withLabel(String label) {
        return (StreamType) super.withLabel(label);
    }

    @Override
    public StreamType withDescription(String description) {
        return (StreamType) super.withDescription(description);
    }

    @Override
    public StreamType withAnnotated(boolean annotated) {
        return (StreamType) super.withAnnotated(annotated);
    }

    @Override
    public StreamType withAnnotated() {
        return (StreamType) super.withAnnotated();
    }

    @Override
    public StreamType withFormat(String format) {
        return (StreamType) super.withFormat(format);
    }

    @Override
    public StreamType withFeatures(Map<String, Object> features) {
        return (StreamType) super.withFeatures(features);
    }

    @Override
    public StreamType withFeature(String name, Object value) {
        return (StreamType) super.withFeature(name, value);
    }

    @Override
    public StreamType withDefaultValue(OutputStreamValue value) {
        return (StreamType) super.withDefaultValue(value);
    }

    @Override
    public StreamType withNullable(boolean nullable) {
        return (StreamType) super.withNullable(nullable);
    }

    @Override
    public StreamType withNullable() {
        return (StreamType) super.withNullable();
    }

    @Override
    public StreamType withOptional() {
        return (StreamType) super.withOptional();
    }

    @Override
    public StreamType withProvided(ProvidedMeta provided) {
        return (StreamType) super.withProvided(provided);
    }
}
