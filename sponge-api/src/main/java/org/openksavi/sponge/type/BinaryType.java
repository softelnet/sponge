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
 * A binary (byte array) type. Provides an optional property {@code mimeType}.
 */
public class BinaryType extends DataType<byte[]> {

    /** The binary data mime type. */
    private String mimeType;

    public BinaryType() {
        super(DataTypeKind.BINARY);
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
