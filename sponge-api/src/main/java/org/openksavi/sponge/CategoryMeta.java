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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.util.Descriptive;
import org.openksavi.sponge.util.HasFeatures;
import org.openksavi.sponge.util.ProcessorPredicate;

/**
 * A category metadata.
 */
public class CategoryMeta implements Descriptive, HasFeatures {

    /** The category name. */
    private String name;

    /** The category label. */
    private String label;

    /** The category description. */
    private String description;

    /** The category features. */
    private Map<String, Object> features = new LinkedHashMap<>();

    /** The predicate for assigning processors to this category. */
    private ProcessorPredicate predicate = (processor) -> false;

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

    @Override
    public Map<String, Object> getFeatures() {
        return features;
    }

    @Override
    public void setFeatures(Map<String, Object> features) {
        Validate.notNull(features, "The category features cannot be null");
        this.features = new LinkedHashMap<>(features);
    }

    public ProcessorPredicate getPredicate() {
        return predicate;
    }

    public void setPredicate(ProcessorPredicate predicate) {
        this.predicate = predicate;
    }

    public CategoryMeta withLabel(String label) {
        setLabel(label);
        return this;
    }

    public CategoryMeta withDescription(String description) {
        setDescription(description);
        return this;
    }

    public CategoryMeta withFeatures(Map<String, Object> features) {
        getFeatures().putAll(features);
        return this;
    }

    public CategoryMeta withFeature(String name, Object value) {
        getFeatures().put(name, value);
        return this;
    }

    public CategoryMeta withPredicate(ProcessorPredicate predicate) {
        setPredicate(predicate);
        return this;
    }
}
