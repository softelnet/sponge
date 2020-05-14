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
 * A WMS layer.
 */
public class GeoWmsLayer extends GeoLayer {

    private static final long serialVersionUID = 6335447555965836764L;

    private String baseUrl;

    private List<String> layers = new ArrayList<>();

    private GeoCrs crs;

    private String format;

    private String version;

    private List<String> styles = new ArrayList<>();

    private Boolean transparent;

    private Map<String, String> otherParameters = new LinkedHashMap<>();

    public GeoWmsLayer(String name) {
        super(GeoLayerType.WMS, name);
    }

    public GeoWmsLayer() {
        this(null);
    }

    @Override
    public GeoWmsLayer withName(String name) {
        return (GeoWmsLayer) super.withName(name);
    }

    @Override
    public GeoWmsLayer withLabel(String label) {
        return (GeoWmsLayer) super.withLabel(label);
    }

    @Override
    public GeoWmsLayer withDescription(String description) {
        return (GeoWmsLayer) super.withDescription(description);
    }

    @Override
    public GeoWmsLayer withFeatures(Map<String, Object> features) {
        return (GeoWmsLayer) super.withFeatures(features);
    }

    @Override
    public GeoWmsLayer withFeature(String name, Object value) {
        return (GeoWmsLayer) super.withFeature(name, value);
    }

    public GeoWmsLayer withBaseUrl(String baseUrl) {
        setBaseUrl(baseUrl);
        return this;
    }

    public GeoWmsLayer withLayers(List<String> layers) {
        this.layers.addAll(layers);
        return this;
    }

    public GeoWmsLayer withLayer(String layer) {
        layers.add(layer);
        return this;
    }

    public GeoWmsLayer withCrs(GeoCrs crs) {
        setCrs(crs);
        return this;
    }

    public GeoWmsLayer withFormat(String format) {
        setFormat(format);
        return this;
    }

    public GeoWmsLayer withVersion(String version) {
        setVersion(version);
        return this;
    }

    public GeoWmsLayer withStyles(List<String> styles) {
        this.styles.addAll(styles);
        return this;
    }

    public GeoWmsLayer withTransparent(Boolean transparent) {
        setTransparent(transparent);
        return this;
    }

    public GeoWmsLayer withOtherParameters(Map<String, String> otherParameters) {
        this.otherParameters.putAll(otherParameters);
        return this;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public List<String> getLayers() {
        return layers;
    }

    public void setLayers(List<String> layers) {
        this.layers = layers;
    }

    public GeoCrs getCrs() {
        return crs;
    }

    public void setCrs(GeoCrs crs) {
        this.crs = crs;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getStyles() {
        return styles;
    }

    public void setStyles(List<String> styles) {
        this.styles = styles;
    }

    public Boolean getTransparent() {
        return transparent;
    }

    public void setTransparent(Boolean transparent) {
        this.transparent = transparent;
    }

    public Map<String, String> getOtherParameters() {
        return otherParameters;
    }

    public void setOtherParameters(Map<String, String> otherParameters) {
        this.otherParameters = otherParameters;
    }

    @Override
    public GeoWmsLayer clone() {
        GeoWmsLayer cloned = (GeoWmsLayer) super.clone();
        cloned.layers = layers != null ? new ArrayList<>(layers) : null;
        cloned.crs = crs != null ? crs.clone() : null;
        cloned.styles = styles != null ? new ArrayList<>(styles) : null;
        cloned.otherParameters = otherParameters != null ? new LinkedHashMap<>(otherParameters) : null;

        return cloned;
    }
}
