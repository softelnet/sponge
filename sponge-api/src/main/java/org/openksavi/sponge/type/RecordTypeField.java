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

package org.openksavi.sponge.type;

import java.util.LinkedHashMap;
import java.util.Map;

import org.openksavi.sponge.util.Descriptive;

/**
 * A record type field.
 */
public class RecordTypeField implements Descriptive {

    /** The field name. */
    private String name;

    /** The field label. */
    private String label;

    /** The field description. */
    private String description;

    /** The field type. */
    private DataType<?> type;

    /** The features as a map of names to values. */
    private Map<String, Object> features = new LinkedHashMap<>();

    protected RecordTypeField() {
    }

    public RecordTypeField(String name, DataType<?> type) {
        this.name = name;
        this.type = type;
    }

    public RecordTypeField withLabel(String label) {
        setLabel(label);
        return this;
    }

    public RecordTypeField withDescription(String description) {
        setDescription(description);
        return this;
    }

    public RecordTypeField withFeatures(Map<String, Object> features) {
        this.features.putAll(features);
        return this;
    }

    public RecordTypeField withFeature(String name, Object value) {
        features.put(name, value);
        return this;
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

    public DataType<?> getType() {
        return type;
    }

    public void setType(DataType<?> type) {
        this.type = type;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }
}
