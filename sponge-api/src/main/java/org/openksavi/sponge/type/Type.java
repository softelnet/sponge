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

package org.openksavi.sponge.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A data type. Used for example in action arguments metadata.
 */
public class Type {

    private TypeKind kind;

    private String format;

    private List<String> tags = new ArrayList<>();

    @SuppressWarnings("unused")
    private Type() {
        //
    }

    protected Type(TypeKind kind) {
        this.kind = kind;
    }

    protected Type format(String format) {
        setFormat(format);
        return this;
    }

    protected Type tags(String... tags) {
        setTags(Arrays.asList(tags));
        return this;
    }

    public TypeKind getKind() {
        return kind;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}
