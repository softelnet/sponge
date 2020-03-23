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

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.openksavi.sponge.util.HasFeatures;

/**
 * An annotated value. The class is not thread safe.
 */
public class AnnotatedValue<T> implements DecoratedValue<T>, HasFeatures {

    public static Set<String> FIELDS =
            new LinkedHashSet<>(Arrays.asList("value", "valueLabel", "valueDescription", "features", "typeLabel", "typeDescription"));

    /** The value. */
    private T value;

    /** The optional value label. */
    private String valueLabel;

    /** The optional value description. */
    private String valueDescription;

    /** The features as a map of names to values. */
    private Map<String, Object> features = new LinkedHashMap<>();

    /** The optional type label. */
    private String typeLabel;

    /** The optional type description. */
    private String typeDescription;

    protected AnnotatedValue() {
        //
    }

    public AnnotatedValue(T value, String valueLabel, String valueDescription, Map<String, Object> features, String typeLabel,
            String typeDescription) {
        this.value = value;
        this.valueLabel = valueLabel;
        this.valueDescription = valueDescription;
        this.features = features;
        this.typeLabel = typeLabel;
        this.typeDescription = typeDescription;
    }

    public AnnotatedValue(T value) {
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public void setValue(T value) {
        this.value = value;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }

    public String getValueDescription() {
        return valueDescription;
    }

    public void setValueDescription(String valueDescription) {
        this.valueDescription = valueDescription;
    }

    @Override
    public Map<String, Object> getFeatures() {
        return Collections.unmodifiableMap(features);
    }

    @Override
    public void setFeatures(Map<String, Object> features) {
        this.features = new LinkedHashMap<>(features);
    }

    public String getTypeLabel() {
        return typeLabel;
    }

    public void setTypeLabel(String typeLabel) {
        this.typeLabel = typeLabel;
    }

    public String getTypeDescription() {
        return typeDescription;
    }

    public void setTypeDescription(String typeDescription) {
        this.typeDescription = typeDescription;
    }

    public AnnotatedValue<T> withValueLabel(String valueLabel) {
        setValueLabel(valueLabel);
        return this;
    }

    public AnnotatedValue<T> withValueDescription(String valueDescription) {
        setValueDescription(valueDescription);
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

    public AnnotatedValue<T> withTypeLabel(String typeLabel) {
        setTypeLabel(typeLabel);
        return this;
    }

    public AnnotatedValue<T> withTypeDescription(String typeDescription) {
        setTypeDescription(typeDescription);
        return this;
    }
}
