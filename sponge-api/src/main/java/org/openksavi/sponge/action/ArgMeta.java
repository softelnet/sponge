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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    /** The argument display name. */
    private String displayName;

    /** The argument description. */
    private String description;

    /** The flag specifying if this argument is optional. Defaults to {@code false}. */
    private boolean optional = false;

    /** The flag specifying if this argument is provided. Defaults to {@code false}. */
    private boolean provided = false;

    /** The list of attribute names that this attribute depends on. Available only if the attribute is provided. */
    private List<String> depends = new ArrayList<>();

    /**
     * The flag specifying if this argument is read only. Defaults to {@code false}. Available only if the attribute is provided.
     */
    private boolean readOnly = false;

    /**
     * The flag specifying if the provided value of this argument should overwrite the value set in a client code. Defaults to
     * {@code false}. Available only if the attribute is provided. This flag should be handled by a client code.
     */
    private boolean overwrite = false;

    public ArgMeta(String name, T type) {
        this.name = name;
        this.type = type;
    }

    public ArgMeta<T> displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ArgMeta<T> description(String description) {
        this.description = description;
        return this;
    }

    public ArgMeta<T> optional() {
        this.optional = true;
        return this;
    }

    public ArgMeta<T> provided() {
        this.provided = true;
        return this;
    }

    public ArgMeta<T> depends(String... depends) {
        this.depends.addAll(Arrays.asList(depends));
        return this;
    }

    public ArgMeta<T> readOnly() {
        this.readOnly = true;
        return this;
    }

    public ArgMeta<T> overwrite() {
        this.overwrite = true;
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

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getLabel() {
        return displayName != null ? displayName : name;
    }

    public boolean isProvided() {
        return provided;
    }

    public void setProvided(boolean provided) {
        this.provided = provided;
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
