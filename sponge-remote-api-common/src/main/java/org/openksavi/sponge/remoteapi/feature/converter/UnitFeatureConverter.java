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

package org.openksavi.sponge.remoteapi.feature.converter;

import java.util.List;

/**
 * A unit feature converter.
 */
public interface UnitFeatureConverter {

    /**
     * Returns the feature names.
     *
     * @return the feature names.
     */
    List<String> getNames();

    /**
     * Marshals the value as a feature.
     *
     * @param converter the type converter.
     * @param value the value. It will never be {@code null} here.
     * @return the converted value.
     */
    Object marshal(FeatureConverter converter, Object value);

    /**
     * Unmarshals the value as a feature.
     *
     * @param converter the type converter.
     * @param value the value. It will never be {@code null} here.
     * @return the converted value.
     */
    Object unmarshal(FeatureConverter converter, Object value);

}
