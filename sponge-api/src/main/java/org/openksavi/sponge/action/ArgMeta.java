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

package org.openksavi.sponge.action;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openksavi.sponge.type.DataType;

/**
 * An action argument metadata.
 */
@SuppressWarnings("rawtypes")
public class ArgMeta<T extends DataType> {

    /** The argument name. */
    private String name;

    /** The argument data type. */
    private T type;

    /** The argument label. */
    private String label;

    /** The argument description. */
    private String description;

    /** The flag specifying if this argument is optional. Defaults to {@code false}. */
    private boolean optional = false;

    /** The provided argument specification. Defaults to {@code null}. */
    private ArgProvidedMeta provided;

    /** The features. */
    private Map<String, Object> features = new LinkedHashMap<>();

    public ArgMeta(String name, T type) {
        this.name = name;
        this.type = type;
    }

    public ArgMeta<T> withLabel(String label) {
        this.label = label;
        return this;
    }

    public ArgMeta<T> withDescription(String description) {
        this.description = description;
        return this;
    }

    public ArgMeta<T> withOptional() {
        this.optional = true;
        return this;
    }

    public ArgMeta<T> withProvided(ArgProvidedMeta provided) {
        this.provided = provided;
        return this;
    }

    public ArgMeta<T> withFeatures(Map<String, Object> features) {
        getFeatures().putAll(features);
        return this;
    }

    public ArgMeta<T> withFeature(String name, Object value) {
        getFeatures().put(name, value);
        return this;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public T getType() {
        return type;
    }

    public void setType(T type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public ArgProvidedMeta getProvided() {
        return provided;
    }

    public void setProvided(ArgProvidedMeta provided) {
        this.provided = provided;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = new LinkedHashMap<>(features);
    }
}
