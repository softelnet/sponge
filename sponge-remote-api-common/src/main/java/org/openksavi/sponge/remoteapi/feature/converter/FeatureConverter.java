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

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A feature converter.
 */
public interface FeatureConverter {

    /**
     * Marshals the value as a feature.
     *
     * @param name the feature name.
     * @param value the value.
     * @return the converted value.
     */
    Object marshal(String name, Object value);

    /**
     * Unmarshals the value as feature.
     *
     * @param name the feature name.
     * @param value the value.
     * @return the converted value.
     */
    Object unmarshal(String name, Object value);

    /**
     * Registers the unit feature converter.
     *
     * @param unitConverter the unit feature converter.
     */
    void register(UnitFeatureConverter unitConverter);

    /**
     * Registers the unit feature converters.
     *
     * @param unitConverters the unit feature converters.
     */
    void registerAll(List<UnitFeatureConverter> unitConverters);

    /**
     * Unregisters a unit feature converter.
     *
     * @param name the feature name.
     * @return the previously registered unit feature converter.
     */
    UnitFeatureConverter unregister(String name);

    /**
     * Returns the JSON object mapper.
     *
     * @return the JSON object mapper.
     */
    ObjectMapper getObjectMapper();
}
