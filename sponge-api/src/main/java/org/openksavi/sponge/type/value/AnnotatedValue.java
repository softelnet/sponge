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

package org.openksavi.sponge.type.value;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An annotated value.
 */
public class AnnotatedValue<T> {

    /** The value. */
    private T value;

    /** The optional value label. */
    private String label;

    /** The optional value description. */
    private String description;

    /** The annotated type features as a map of names to values. */
    private Map<String, Object> features = new LinkedHashMap<>();

    protected AnnotatedValue() {
        //
    }

    public AnnotatedValue(T value, String label, String description, Map<String, Object> features) {
        this.value = value;
        this.label = label;
        this.description = description;
        this.features = features;
    }

    public AnnotatedValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
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

    public Map<String, Object> getFeatures() {
        return Collections.unmodifiableMap(features);
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = new LinkedHashMap<>(features);
    }

    public AnnotatedValue<T> withLabel(String label) {
        setLabel(label);
        return this;
    }

    public AnnotatedValue<T> withDescription(String description) {
        setDescription(description);
        return this;
    }

    public AnnotatedValue<T> withFeatures(Map<String, Object> features) {
        this.features.putAll(features);
        return this;
    }

    public AnnotatedValue<T> withFeature(String name, Object value) {
        this.features.put(name, value);
        return this;
    }
}
