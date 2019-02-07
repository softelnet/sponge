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

package org.openksavi.sponge;

import org.openksavi.sponge.util.Descriptive;

/**
 * A category metadata.
 */
public class CategoryMeta implements Descriptive {

    /** The category name. */
    private String name;

    /** The category label. */
    private String label;

    /** The category description. */
    private String description;

    @SuppressWarnings("unused")
    private CategoryMeta() {
    }

    public CategoryMeta(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryMeta withLabel(String label) {
        setLabel(label);
        return this;
    }

    public CategoryMeta withDescription(String description) {
        setDescription(description);
        return this;
    }
}
