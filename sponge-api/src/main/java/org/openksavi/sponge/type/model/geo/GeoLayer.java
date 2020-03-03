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

package org.openksavi.sponge.type.model.geo;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openksavi.sponge.util.Descriptive;

/**
 * A map layer.
 */
public class GeoLayer implements Descriptive {

    private String urlTemplate;

    private String name;

    private String label;

    private String description;

    private Map<String, String> options = new LinkedHashMap<>();

    private List<String> subdomains;

    private Map<String, Object> features = new LinkedHashMap<>();

    public GeoLayer(String urlTemplate) {
        this.urlTemplate = urlTemplate;
    }

    public GeoLayer() {
    }

    public GeoLayer withUrlTemplate(String urlTemplate) {
        setUrlTemplate(urlTemplate);
        return this;
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

    public GeoLayer withSubdomains(List<String> subdomains) {
        setSubdomains(subdomains);
        return this;
    }

    public GeoLayer withOptions(Map<String, String> options) {
        setOptions(options);
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

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
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

    public List<String> getSubdomains() {
        return subdomains;
    }

    public void setSubdomains(List<String> subdomains) {
        this.subdomains = subdomains;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    public Map<String, Object> getFeatures() {
        return features;
    }

    public void setFeatures(Map<String, Object> features) {
        this.features = features;
    }
}
