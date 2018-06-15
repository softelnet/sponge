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
 * Action result metadata (data type and display name).
 */
public class ResultMeta {

    /** A result data type. */
    private Type type;

    /** An result subtype. */
    private String subtype;

    /** A result display name. */
    private String displayName;

    /** A result description. */
    private String description;

    public ResultMeta(Type type) {
        this.type = type;
    }

    public ResultMeta subtype(String subtype) {
        this.subtype = subtype;
        return this;
    }

    public ResultMeta displayName(String displayName) {
        this.displayName = displayName;
        return this;
    }

    public ResultMeta description(String description) {
        this.description = description;
        return this;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
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
