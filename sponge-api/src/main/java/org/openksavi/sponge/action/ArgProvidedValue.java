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

import java.util.List;
import java.util.stream.Collectors;

import org.openksavi.sponge.type.value.AnnotatedValue;

/**
 * A provided argument value and a possible value set.
 */
public class ArgProvidedValue<T> {

    /** The value. */
    private T value;

    /** If the value is present this flag is {@code true}. */
    private boolean valuePresent = false;

    /**
     * The possible value set with optional annotations. For example it may be a list of string values to choose from. If there is no value
     * set for this argument, this property is {@code null}.
     */
    private List<AnnotatedValue<T>> annotatedValueSet;

    public ArgProvidedValue() {
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

    /**
     * The utility getter for the possible value set without annotations.
     *
     * @return the value set.
     */
    public List<T> getValueSet() {
        return annotatedValueSet != null ? annotatedValueSet.stream()
                .map(annotatedValue -> annotatedValue != null ? annotatedValue.getValue() : null).collect(Collectors.toList()) : null;
    }

    public ArgProvidedValue<T> withValue(T value) {
        setValue(value);
        setValuePresent(true);
        return this;
    }

    public ArgProvidedValue<T> withAnnotatedValueSet(List<AnnotatedValue<T>> annotatedValueSet) {
        setAnnotatedValueSet(annotatedValueSet);
        return this;
    }

    public ArgProvidedValue<T> withValueSet(List<T> valueSet) {
        return withAnnotatedValueSet(
                valueSet != null ? valueSet.stream().map(value -> new AnnotatedValue<>(value)).collect(Collectors.toList()) : null);
    }
}
