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

import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.features.Features;
import org.openksavi.sponge.restapi.feature.converter.BaseUnitFeatureConverter;
import org.openksavi.sponge.restapi.feature.converter.FeatureConverter;

public class SubActionFeaturesUnitConverter extends BaseUnitFeatureConverter {

    private SubActionFeatureUnitConverter elementConverter = new SubActionFeatureUnitConverter();

    public SubActionFeaturesUnitConverter() {
        super(Features.CONTEXT_ACTIONS);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object marshal(FeatureConverter converter, Object value) {
        Validate.isInstanceOf(List.class, value, "The %s feature has to be a list of sub-actions", Features.CONTEXT_ACTIONS);

        return ((List) value).stream().map(subAction -> elementConverter.marshal(converter, subAction)).collect(Collectors.toList());
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Object unmarshal(FeatureConverter converter, Object value) {
        Validate.isInstanceOf(List.class, value, "The %s feature has to be a list of sub-actions", Features.CONTEXT_ACTIONS);

        return ((List) value).stream().map(subAction -> elementConverter.unmarshal(converter, subAction)).collect(Collectors.toList());
    }
}
