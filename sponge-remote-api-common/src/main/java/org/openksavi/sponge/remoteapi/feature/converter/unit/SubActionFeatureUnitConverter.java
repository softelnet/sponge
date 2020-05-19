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

package org.openksavi.sponge.remoteapi.feature.converter.unit;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.features.Features;
import org.openksavi.sponge.features.model.SubAction;
import org.openksavi.sponge.features.model.SubActionArg;
import org.openksavi.sponge.remoteapi.feature.converter.BaseUnitFeatureConverter;
import org.openksavi.sponge.remoteapi.feature.converter.FeatureConverter;
import org.openksavi.sponge.remoteapi.feature.converter.FeaturesUtils;

public class SubActionFeatureUnitConverter extends BaseUnitFeatureConverter {

    public SubActionFeatureUnitConverter() {
        super(Features.SUB_ACTION_ACTIVATE_ACTION, Features.SUB_ACTION_CREATE_ACTION, Features.SUB_ACTION_DELETE_ACTION,
                Features.SUB_ACTION_READ_ACTION, Features.SUB_ACTION_UPDATE_ACTION);
    }

    @Override
    public Object marshal(FeatureConverter converter, Object value) {
        Validate.isInstanceOf(SubAction.class, value, "Sub-action has to be an instance of %s", SubAction.class);

        SubAction subAction = ((SubAction) value).clone();

        subAction.setFeatures(FeaturesUtils.marshal(converter, subAction.getFeatures()));

        if (subAction.getArgs() != null) {
            for (SubActionArg arg : subAction.getArgs()) {
                arg.setFeatures(FeaturesUtils.marshal(converter, arg.getFeatures()));
            }
        }

        if (subAction.getResult() != null) {
            subAction.getResult().setFeatures(FeaturesUtils.marshal(converter, subAction.getResult().getFeatures()));
        }

        return subAction;
    }

    @Override
    public Object unmarshal(FeatureConverter converter, Object value) {
        SubAction subAction = converter.getObjectMapper().convertValue(value, SubAction.class);

        subAction.setFeatures(FeaturesUtils.unmarshal(converter, subAction.getFeatures()));

        if (subAction.getArgs() != null) {
            for (SubActionArg arg : subAction.getArgs()) {
                arg.setFeatures(FeaturesUtils.unmarshal(converter, arg.getFeatures()));
            }
        }

        if (subAction.getResult() != null) {
            subAction.getResult().setFeatures(FeaturesUtils.unmarshal(converter, subAction.getResult().getFeatures()));
        }

        return subAction;
    }
}
