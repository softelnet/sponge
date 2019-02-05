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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openksavi.sponge.SpongeException;

/**
 * A data type. Used for example in action arguments metadata.
 */
public class DataType<T> implements Cloneable {

    /** The data type kind. */
    private DataTypeKind kind;

    /** The format (optional). */
    private String format;

    /** The default value (optional). */
    private T defaultValue;

    /** Tells if a value of this type may be null. The default is that a value must not be null, i.e. it is <b>not nullable</b>. */
    private boolean nullable = false;

    /** The data type features as a map of names to values. */
    private Map<String, Object> features = new LinkedHashMap<>();

    @SuppressWarnings("unused")
    private DataType() {
        //
    }

    protected DataType(DataTypeKind kind) {
        this.kind = kind;
    }

    public DataType<T> format(String format) {
        setFormat(format);
        return this;
    }

    public DataType<T> defaultValue(T value) {
        setDefaultValue(value);
        return this;
    }

    public DataType<T> nullable(boolean nullable) {
        setNullable(nullable);
        return this;
    }

    public DataType<T> nullable() {
        return nullable(true);
    }

    public DataType<T> features(Map<String, Object> features) {
        this.features.putAll(features);
        return this;
    }

    public DataType<T> feature(String name, Object value) {
        features.put(name, value);
        return this;
    }

    public DataTypeKind getKind() {
        return kind;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(T defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public Map<String, Object> getFeatures() {
        return Collections.unmodifiableMap(features);
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = new LinkedHashMap<>(features);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataType<T> clone() {
        try {
            return (DataType<T>) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }
}
