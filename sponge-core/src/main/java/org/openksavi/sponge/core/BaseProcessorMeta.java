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

package org.openksavi.sponge.core;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.ProcessorMeta;

/**
 * A base processor metadata.
 */
public abstract class BaseProcessorMeta implements ProcessorMeta {

    /** Name. */
    private String name;

    /** Label. */
    private String label;

    /** Description. */
    private String description;

    /** The processor version. */
    private Integer version;

    /** The processor features. */
    private Map<String, Object> features = new LinkedHashMap<>();

    /** The processor category name. */
    private String category;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String getLabel() {
        return label;
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
    public Integer getVersion() {
        return version;
    }

    @Override
    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public Map<String, Object> getFeatures() {
        return features;
    }

    @Override
    public void setFeatures(Map<String, Object> features) {
        Validate.notNull(features, "The processor features cannot be null");
        this.features = new LinkedHashMap<>(features);
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public void setCategory(String category) {
        this.category = category;
    }

    public BaseProcessorMeta withName(String name) {
        setName(name);
        return this;
    }

    public BaseProcessorMeta withLabel(String label) {
        setLabel(label);
        return this;
    }

    public BaseProcessorMeta withDescription(String description) {
        setDescription(description);
        return this;
    }

    public BaseProcessorMeta withVersion(Integer version) {
        setVersion(version);
        return this;
    }

    public BaseProcessorMeta withFeatures(Map<String, Object> features) {
        getFeatures().putAll(features);
        return this;
    }

    public BaseProcessorMeta withFeature(String name, Object value) {
        getFeatures().put(name, value);
        return this;
    }

    public BaseProcessorMeta withCategory(String category) {
        setCategory(category);
        return this;
    }
}
