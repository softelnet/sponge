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

import org.apache.commons.lang3.ObjectUtils;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.type.provided.ProvidedMeta;
import org.openksavi.sponge.util.Descriptive;

/**
 * A data type. Used for example in action arguments metadata.
 */
public class DataType<T> implements Descriptive, Cloneable {

    /** The feature name for the format. */
    public static final String FEATURE_FORMAT = "format";

    /** The data type kind. */
    private DataTypeKind kind;

    /** The optional corresponding registered data type name. */
    private String registeredType;

    /** The data type location name. */
    private String name;

    /** The data type location label. */
    private String label;

    /** The data type location description. */
    private String description;

    /**
     * Tells if a value of this type is annotated, i.e. wrapped by an instance of
     * {@linkplain org.openksavi.sponge.type.value.AnnotatedValue}. Defaults to {@code false}.
     */
    private boolean annotated = false;

    /** The format (optional). */
    private String format;

    /** The default value (optional). */
    private T defaultValue;

    /** Tells if a value of this type may be null. The default is that a value must not be null, i.e. it is <b>not nullable</b>. */
    private boolean nullable = false;

    /** The data type features as a map of names to values. */
    private Map<String, Object> features = new LinkedHashMap<>();

    /** The flag specifying if this type is optional. Defaults to {@code false}. */
    private boolean optional = false;

    /** The provided value specification. Defaults to {@code null}. */
    private ProvidedMeta provided;

    @SuppressWarnings("unused")
    private DataType() {
        //
    }

    protected DataType(DataTypeKind kind, String name) {
        this.kind = kind;
        this.name = name;
    }

    public DataType<T> withName(String name) {
        this.name = name;
        return this;
    }

    public DataType<T> withLabel(String label) {
        this.label = label;
        return this;
    }

    public DataType<T> withDescription(String description) {
        this.description = description;
        return this;
    }

    public DataType<T> withAnnotated(boolean annotated) {
        this.annotated = annotated;
        return this;
    }

    public DataType<T> withAnnotated() {
        return withAnnotated(true);
    }

    public DataType<T> withFormat(String format) {
        setFormat(format);
        return this;
    }

    public DataType<T> withDefaultValue(T value) {
        setDefaultValue(value);
        return this;
    }

    public DataType<T> withNullable(boolean nullable) {
        setNullable(nullable);
        return this;
    }

    public DataType<T> withNullable() {
        return withNullable(true);
    }

    public DataType<T> withFeatures(Map<String, Object> features) {
        this.features.putAll(features);
        return this;
    }

    public DataType<T> withFeature(String name, Object value) {
        features.put(name, value);
        return this;
    }

    public DataType<T> withOptional() {
        optional = true;
        return this;
    }

    public DataType<T> withProvided(ProvidedMeta provided) {
        this.provided = provided;
        return this;
    }

    public DataTypeKind getKind() {
        return kind;
    }

    public String getRegisteredType() {
        return registeredType;
    }

    public void setRegisteredType(String registeredType) {
        this.registeredType = registeredType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isAnnotated() {
        return annotated;
    }

    public void setAnnotated(boolean annotated) {
        this.annotated = annotated;
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

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public ProvidedMeta getProvided() {
        return provided;
    }

    public void setProvided(ProvidedMeta provided) {
        this.provided = provided;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataType<T> clone() {
        try {
            DataType<T> cloned = (DataType<T>) super.clone();
            cloned.defaultValue = defaultValue instanceof Cloneable ? ObjectUtils.clone(defaultValue) : defaultValue;
            cloned.features = features != null ? new LinkedHashMap<>(features) : null;

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new SpongeException(e);
        }
    }
}
