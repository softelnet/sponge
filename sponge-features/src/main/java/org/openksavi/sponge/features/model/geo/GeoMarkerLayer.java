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

import java.util.Map;

/**
 * A marker layer.
 */
public class GeoMarkerLayer extends GeoLayer {

    public GeoMarkerLayer(String name) {
        super(GeoLayerType.MARKER, name);
    }

    public GeoMarkerLayer() {
        this(null);
    }

    @Override
    public GeoMarkerLayer withName(String name) {
        return (GeoMarkerLayer) super.withName(name);
    }

    @Override
    public GeoMarkerLayer withLabel(String label) {
        return (GeoMarkerLayer) super.withLabel(label);
    }

    @Override
    public GeoMarkerLayer withDescription(String description) {
        return (GeoMarkerLayer) super.withDescription(description);
    }

    @Override
    public GeoMarkerLayer withFeatures(Map<String, Object> features) {
        return (GeoMarkerLayer) super.withFeatures(features);
    }

    @Override
    public GeoMarkerLayer withFeature(String name, Object value) {
        return (GeoMarkerLayer) super.withFeature(name, value);
    }
}
