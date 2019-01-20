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

import org.openksavi.sponge.util.LabeledValue;

/**
 * An argument value and a possible value set.
 */
public class ArgValue<T> {

    /** The value. */
    private T value;

    /** If the value is present this flag is {@code true}. */
    private boolean valuePresent = false;

    /**
     * The possible value set with optional labels. For example it may be a list of string values to choose from. If there is no value set
     * for this argument, this property is {@code null}.
     */
    private List<LabeledValue<T>> labeledValueSet;

    public ArgValue() {
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

    public List<LabeledValue<T>> getLabeledValueSet() {
        return labeledValueSet;
    }

    public void setLabeledValueSet(List<LabeledValue<T>> labeledValueSet) {
        this.labeledValueSet = labeledValueSet;
    }

    /**
     * The utility getter for the possible value set without labels.
     *
     * @return the value set.
     */
    public List<T> getValueSet() {
        return labeledValueSet != null ? labeledValueSet.stream().map(labeledValue -> labeledValue != null ? labeledValue.getValue() : null)
                .collect(Collectors.toList()) : null;
    }

    public ArgValue<T> value(T value) {
        setValue(value);
        setValuePresent(true);
        return this;
    }

    public ArgValue<T> labeledValueSet(List<LabeledValue<T>> valueSet) {
        setLabeledValueSet(valueSet);
        return this;
    }

    public ArgValue<T> valueSet(List<T> valueSet) {
        setLabeledValueSet(
                valueSet != null ? valueSet.stream().map(value -> new LabeledValue<>(value, null)).collect(Collectors.toList()) : null);
        return this;
    }
}
