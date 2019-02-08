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
 * A string type.
 */
public class StringType extends DataType<String> {

    /** The minimum length (optional). */
    private Integer minLength;

    /** The maximum length (optional). */
    private Integer maxLength;

    public StringType() {
        super(DataTypeKind.STRING);
    }

    @Override
    public StringType withFormat(String format) {
        return (StringType) super.withFormat(format);
    }

    @Override
    public StringType withFeatures(Map<String, Object> features) {
        return (StringType) super.withFeatures(features);
    }

    @Override
    public StringType withFeature(String name, Object value) {
        return (StringType) super.withFeature(name, value);
    }

    @Override
    public StringType withDefaultValue(String value) {
        return (StringType) super.withDefaultValue(value);
    }

    @Override
    public StringType withNullable(boolean nullable) {
        return (StringType) super.withNullable(nullable);
    }

    @Override
    public StringType withNullable() {
        return (StringType) super.withNullable();
    }

    public StringType withMinLength(Integer minLength) {
        setMinLength(minLength);
        return this;
    }

    public StringType withMaxLength(Integer maxLength) {
        setMaxLength(maxLength);
        return this;
    }

    public Integer getMinLength() {
        return minLength;
    }

    public void setMinLength(Integer minLength) {
        this.minLength = minLength;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(Integer maxLength) {
        this.maxLength = maxLength;
    }
}
