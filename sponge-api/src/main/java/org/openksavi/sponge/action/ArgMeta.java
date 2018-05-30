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

import org.openksavi.sponge.Type;

/**
 * Action argument metadata (name, data type and display name).
 */
public class ArgMeta {

    /** An argument name. */
    private String name;

    /** An argument data type. */
    private Type type;

    /** Is this argument required? */
    private boolean required;

    /** An argument display name. */
    private String displayName;

    /** An argument description. */
    private String description;

    public ArgMeta(String name, Type type, boolean required, String displayName, String description) {
        this.name = name;
        this.type = type;
        this.required = required;
        this.displayName = displayName;
        this.description = description;
    }

    public ArgMeta(String name, Type type, boolean required, String displayName) {
        this(name, type, required, displayName, null);
    }

    public ArgMeta(String name, Type type, boolean required) {
        this(name, type, required, null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
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
}
