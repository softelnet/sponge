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

import java.util.Map;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.features.Features;
import org.openksavi.sponge.features.model.ui.IconInfo;
import org.openksavi.sponge.restapi.feature.converter.BaseUnitFeatureConverter;
import org.openksavi.sponge.restapi.feature.converter.FeatureConverter;

public class IconFeatureUnitConverter extends BaseUnitFeatureConverter {

    public IconFeatureUnitConverter() {
        super(Features.ICON);
    }

    @Override
    public Object unmarshal(FeatureConverter converter, Object value) {
        if (value instanceof Map) {
            return converter.getObjectMapper().convertValue(value, IconInfo.class);
        } else if (value instanceof String) {
            return new IconInfo().withName((String) value);
        } else {
            throw new SpongeException(String.format("Unsupported icon type: %s", value.getClass()));
        }
    }
}
