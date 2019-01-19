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
 * A provided argument specification.
 */
public class ArgProvided {

    /** The flag specifying if this argument value is provided. */
    private boolean value;

    /** The flag specifying if this argument value set is provided. */
    private boolean valueSet;

    /** The list of attribute names that this provided attribute depends on. */
    private List<String> depends = new ArrayList<>();

    /** The flag specifying if this provided argument is read only. Defaults to {@code false}. */
    private boolean readOnly = false;

    /**
     * The flag specifying if the provided value of this argument should overwrite the value set in a client code. Defaults to
     * {@code false}. This flag should be handled by a client code.
     */
    private boolean overwrite = false;

    public ArgProvided() {
    }

    public ArgProvided value() {
        value = true;
        return this;
    }

    public ArgProvided valueSet() {
        valueSet = true;
        return this;
    }

    public ArgProvided depends(String... depends) {
        this.depends.addAll(Arrays.asList(depends));
        return this;
    }

    public ArgProvided readOnly() {
        readOnly = true;
        return this;
    }

    public ArgProvided overwrite() {
        overwrite = true;
        return this;
    }

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean isValueSet() {
        return valueSet;
    }

    public void setValueSet(boolean valueSet) {
        this.valueSet = valueSet;
    }

    public List<String> getDepends() {
        return depends;
    }

    public void setDepends(List<String> depends) {
        this.depends = depends;
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
