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

package org.openksavi.sponge.restapi.feature.converter;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Features utils.
 */
public abstract class FeaturesUtils {

    protected FeaturesUtils() {
        //
    }

    public static Map<String, Object> marshal(FeatureConverter converter, Map<String, Object> features) {
        if (converter == null) {
            return features;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        if (features != null) {
            for (String featureName : features.keySet()) {
                result.put(featureName, converter.marshal(featureName, features.get(featureName)));
            }
        }

        return result;
    }

    public static Map<String, Object> unmarshal(FeatureConverter converter, Map<String, Object> features) {
        if (converter == null) {
            return features;
        }

        Map<String, Object> result = new LinkedHashMap<>();
        if (features != null) {
            for (String featureName : features.keySet()) {
                result.put(featureName, converter.unmarshal(featureName, features.get(featureName)));
            }
        }

        return result;
    }
}
