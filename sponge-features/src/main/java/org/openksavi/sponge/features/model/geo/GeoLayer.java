/*
 * Copyright 2016-2020 The Sponge authors.
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

package org.openksavi.sponge.features.model.geo;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.openksavi.sponge.util.Descriptive;
import org.openksavi.sponge.util.HasFeatures;

/**
 * A map layer.
 */
public abstract class GeoLayer implements Descriptive, HasFeatures, Serializable {

    private static final long serialVersionUID = -4882052436310260925L;

    private GeoLayerType type;

    private String name;

    private String label;

    private String description;

    private Map<String, Object> features = new LinkedHashMap<>();

    protected GeoLayer(GeoLayerType type, String name) {
        this.type = type;
        this.name = name;
    }

    protected GeoLayer(GeoLayerType type) {
        this(type, null);
    }

    public GeoLayer withName(String name) {
        setName(name);
        return this;
    }

    public GeoLayer withLabel(String label) {
        setLabel(label);
        return this;
    }

    public GeoLayer withDescription(String description) {
        setDescription(description);
        return this;
    }

    public GeoLayer withFeatures(Map<String, Object> features) {
        this.features.putAll(features);
        return this;
    }

    public GeoLayer withFeature(String name, Object value) {
        features.put(name, value);
        return this;
    }

    public GeoLayerType getType() {
        return type;
    }

    public void setType(GeoLayerType type) {
        this.type = type;
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
        this.features = features;
    }
}
