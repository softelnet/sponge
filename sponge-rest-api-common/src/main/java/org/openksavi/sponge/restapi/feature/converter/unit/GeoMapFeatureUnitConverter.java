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

package org.openksavi.sponge.restapi.feature.converter.unit;

import org.openksavi.sponge.features.Features;
import org.openksavi.sponge.features.model.geo.GeoLayer;
import org.openksavi.sponge.features.model.geo.GeoMap;
import org.openksavi.sponge.restapi.feature.converter.BaseUnitFeatureConverter;
import org.openksavi.sponge.restapi.feature.converter.FeatureConverter;
import org.openksavi.sponge.restapi.feature.converter.FeaturesUtils;

public class GeoMapFeatureUnitConverter extends BaseUnitFeatureConverter {

    public GeoMapFeatureUnitConverter() {
        super(Features.GEO_MAP);
    }

    @Override
    public Object unmarshal(FeatureConverter converter, Object value) {
        GeoMap geoMap = converter.getObjectMapper().convertValue(value, GeoMap.class);

        if (geoMap != null) {
            geoMap.setFeatures(FeaturesUtils.unmarshal(converter, geoMap.getFeatures()));

            if (geoMap.getLayers() != null) {
                for (GeoLayer layer : geoMap.getLayers()) {
                    layer.setFeatures(FeaturesUtils.unmarshal(converter, layer.getFeatures()));
                }
            }
        }

        return geoMap;
    }
}
