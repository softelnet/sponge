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

package org.openksavi.sponge.type.provided;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openksavi.sponge.type.value.AnnotatedValue;

/**
 * A provided object value and a possible value set.
 */
@SuppressWarnings("rawtypes")
public class ProvidedValue<T> {

    /** The value. */
    private T value;

    /** If the value is present this flag is {@code true}. */
    private boolean valuePresent = false;

    /**
     * The possible value set (with optional annotations). For example it may be a list of string values to choose from. If there is no
     * value set, this property is {@code null}.
     */
    private List<AnnotatedValue<T>> annotatedValueSet;

    /**
     * The possible element value set (with optional annotations) for a list type. For example it may be a list of string values to multiple
     * choice. Applicable only for list types. If there is no element value set, this property is {@code null}.
     */
    private List<AnnotatedValue> annotatedElementValueSet;

    /** The optional provided features. Note that these features are not the same as those provided in annotated values. */
    private Map<String, Object> features = new LinkedHashMap<>();

    public ProvidedValue() {
        //
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public boolean isValuePresent() {
        return valuePresent;
    }

    public void setValuePresent(boolean valuePresent) {
        this.valuePresent = valuePresent;
    }

    public List<AnnotatedValue<T>> getAnnotatedValueSet() {
        return annotatedValueSet;
    }

    public void setAnnotatedValueSet(List<AnnotatedValue<T>> annotatedValueSet) {
        this.annotatedValueSet = annotatedValueSet;
    }

    public List<AnnotatedValue> getAnnotatedElementValueSet() {
        return annotatedElementValueSet;
    }

    public <E> void setAnnotatedElementValueSet(List<AnnotatedValue> annotatedElementValueSet) {
        this.annotatedElementValueSet = annotatedElementValueSet;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }

    public ProvidedValue<T> withValue(T value) {
        setValue(value);
        setValuePresent(true);
        return this;
    }

    public ProvidedValue<T> withAnnotatedValueSet(List<AnnotatedValue<T>> annotatedValueSet) {
        setAnnotatedValueSet(annotatedValueSet);
        return this;
    }

    public ProvidedValue<T> withValueSet(List<T> valueSet) {
        return withAnnotatedValueSet(
                valueSet != null ? valueSet.stream().map(value -> new AnnotatedValue<>(value)).collect(Collectors.toList()) : null);
    }

    public <E> ProvidedValue<T> withAnnotatedElementValueSet(List<AnnotatedValue<E>> annotatedElementValueSet) {
        setAnnotatedElementValueSet(annotatedElementValueSet.stream().collect(Collectors.toList()));
        return this;
    }

    public <E> ProvidedValue<T> withElementValueSet(List<E> elementValueSet) {
        return withAnnotatedElementValueSet(
                elementValueSet.stream().map(value -> new AnnotatedValue<>(value)).collect(Collectors.toList()));
    }

    public ProvidedValue<T> withFeatures(Map<String, Object> features) {
        this.features.putAll(features);
        return this;
    }

    public ProvidedValue<T> withFeature(String name, Object value) {
        features.put(name, value);
        return this;
    }
}
