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
 * A binary (byte array) type. Provides an optional property {@code mimeType}.
 */
public class BinaryType extends DataType<byte[]> {

    /** The feature name for the mimeType. */
    public static final String FEATURE_MIME_TYPE = "mimeType";

    /** The binary data mime type. */
    private String mimeType;

    public BinaryType() {
        this(null);
    }

    public BinaryType(String name) {
        super(DataTypeKind.BINARY, name);
    }

    @Override
    public BinaryType withName(String name) {
        return (BinaryType) super.withName(name);
    }

    @Override
    public BinaryType withLabel(String label) {
        return (BinaryType) super.withLabel(label);
    }

    @Override
    public BinaryType withDescription(String description) {
        return (BinaryType) super.withDescription(description);
    }

    @Override
    public BinaryType withAnnotated(boolean annotated) {
        return (BinaryType) super.withAnnotated(annotated);
    }

    @Override
    public BinaryType withAnnotated() {
        return (BinaryType) super.withAnnotated();
    }

    @Override
    public BinaryType withFormat(String format) {
        return (BinaryType) super.withFormat(format);
    }

    @Override
    public BinaryType withFeatures(Map<String, Object> features) {
        return (BinaryType) super.withFeatures(features);
    }

    @Override
    public BinaryType withFeature(String name, Object value) {
        return (BinaryType) super.withFeature(name, value);
    }

    @Override
    public BinaryType withDefaultValue(byte[] value) {
        return (BinaryType) super.withDefaultValue(value);
    }

    @Override
    public BinaryType withNullable(boolean nullable) {
        return (BinaryType) super.withNullable(nullable);
    }

    @Override
    public BinaryType withNullable() {
        return (BinaryType) super.withNullable();
    }

    @Override
    public BinaryType withOptional() {
        return (BinaryType) super.withOptional();
    }

    @Override
    public BinaryType withProvided(ProvidedMeta provided) {
        return (BinaryType) super.withProvided(provided);
    }

    public BinaryType withMimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
