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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A tile layer.
 */
public class GeoTileLayer extends GeoLayer {

    private static final long serialVersionUID = 1575569726806173109L;

    private String urlTemplate;

    private Map<String, String> options = new LinkedHashMap<>();

    private List<String> subdomains;

    public GeoTileLayer(String name) {
        super(GeoLayerType.TILE, name);
    }

    public GeoTileLayer() {
        this(null);
    }

    @Override
    public GeoTileLayer withName(String name) {
        return (GeoTileLayer) super.withName(name);
    }

    @Override
    public GeoTileLayer withLabel(String label) {
        return (GeoTileLayer) super.withLabel(label);
    }

    @Override
    public GeoTileLayer withDescription(String description) {
        return (GeoTileLayer) super.withDescription(description);
    }

    @Override
    public GeoTileLayer withFeatures(Map<String, Object> features) {
        return (GeoTileLayer) super.withFeatures(features);
    }

    @Override
    public GeoTileLayer withFeature(String name, Object value) {
        return (GeoTileLayer) super.withFeature(name, value);
    }

    public GeoTileLayer withUrlTemplate(String urlTemplate) {
        setUrlTemplate(urlTemplate);
        return this;
    }

    public GeoTileLayer withSubdomains(List<String> subdomains) {
        setSubdomains(subdomains);
        return this;
    }

    public GeoTileLayer withOptions(Map<String, String> options) {
        setOptions(options);
        return this;
    }

    public String getUrlTemplate() {
        return urlTemplate;
    }

    public void setUrlTemplate(String urlTemplate) {
        this.urlTemplate = urlTemplate;
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

    @Override
    public GeoTileLayer clone() {
        GeoTileLayer cloned = (GeoTileLayer) super.clone();
        cloned.options = options != null ? new LinkedHashMap<>(options) : null;
        cloned.subdomains = subdomains != null ? new ArrayList<>(subdomains) : null;

        return cloned;
    }
}
