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

package org.openksavi.sponge.action;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A provided argument metadata.
 */
public class ArgProvidedMeta {

    /** The flag specifying if this argument value is provided. */
    private boolean value;

    /** The metadata specifying if this argument value set is provided. Defaults to {@code null}. */
    private ValueSetMeta valueSet;

    /** The list of attribute names that this provided attribute depends on. */
    private List<String> dependencies = new ArrayList<>();

    /** The flag specifying if this provided argument is read only. Defaults to {@code false}. */
    private boolean readOnly = false;

    /**
     * The flag specifying if the provided value of this argument should overwrite the value set in a client code. Defaults to
     * {@code false}. This flag should be handled by a client code.
     */
    private boolean overwrite = false;

    public ArgProvidedMeta() {
    }

    public ArgProvidedMeta withValue() {
        value = true;
        return this;
    }

    public ArgProvidedMeta withValueSet() {
        return withValueSet(new ValueSetMeta());
    }

    public ArgProvidedMeta withValueSet(ValueSetMeta valueSet) {
        this.valueSet = valueSet;
        return this;
    }

    public ArgProvidedMeta withDependencies(List<String> dependencies) {
        this.dependencies.addAll(dependencies);
        return this;
    }

    public ArgProvidedMeta withDependency(String dependency) {
        return withDependencies(Arrays.asList(dependency));
    }

    public ArgProvidedMeta withReadOnly() {
        readOnly = true;
        return this;
    }

    public ArgProvidedMeta withOverwrite() {
        overwrite = true;
        return this;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public ValueSetMeta getValueSet() {
        return valueSet;
    }

    public void setValueSet(ValueSetMeta valueSet) {
        this.valueSet = valueSet;
    }

    public boolean hasValueSet() {
        return valueSet != null;
    }

    public List<String> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<String> dependencies) {
        this.dependencies = new ArrayList<>(dependencies);
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
}
