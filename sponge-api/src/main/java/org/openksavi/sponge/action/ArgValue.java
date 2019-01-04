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

/**
 * An argument value and a possible value set.
 */
public class ArgValue<T> {

    /** The value. */
    private T value;

    /** If the value is present this flag is {@code true}. */
    private boolean valuePresent = false;

    /**
     * The possible value set if not {@code null}. For example it may be a list of string values to choose from. If the value set is present
     * is is not {@code null}.
     */
    private List<T> valueSet;

    /** The value set display names. */
    private List<String> valueSetDisplayNames;

    public ArgValue() {
        //
    }

    public T getValue() {
        return value;
    }

    public List<T> getValueSet() {
        return valueSet;
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

    public void setValueSet(List<T> valueSet) {
        this.valueSet = valueSet;
    }

    public List<String> getValueSetDisplayNames() {
        return valueSetDisplayNames;
    }

    public void setValueSetDisplayNames(List<String> valueSetDisplayNames) {
        this.valueSetDisplayNames = valueSetDisplayNames;
    }

    public ArgValue<T> value(T value) {
        setValue(value);
        setValuePresent(true);
        return this;
    }

    public ArgValue<T> valueSet(List<T> valueSet) {
        setValueSet(valueSet);
        return this;
    }

    public ArgValue<T> valueSet(List<T> valueSet, List<String> displayNames) {
        setValueSet(valueSet);
        setValueSetDisplayNames(displayNames);
        return this;
    }
}
