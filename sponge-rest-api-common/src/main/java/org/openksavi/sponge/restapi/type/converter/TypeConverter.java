/*
 * Copyright 2016-2018 The Sponge authors.
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

package org.openksavi.sponge.restapi.type.converter;

import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;

/**
 * A type converter.
 */
@SuppressWarnings("rawtypes")
public interface TypeConverter {

    /**
     * Marshals the value as type.
     *
     * @param type the type.
     * @param value the value.
     * @param T instance type.
     * @param D data type.
     * @return the converted value.
     */
    <T, D extends DataType<T>> Object marshal(D type, T value);

    /**
     * Unmarshals the value as type.
     *
     * @param type the type.
     * @param value the value.
     * @param T instance type.
     * @param D data type.
     * @return the converted value.
     */
    <T, D extends DataType<T>> T unmarshal(D type, Object value);

    /**
     * Registers the unit type converter.
     *
     * @param unitConverter the unit type converter.
     */
    void register(UnitTypeConverter unitConverter);

    /**
     * Registers the unit type converters.
     *
     * @param unitConverters the unit type converters.
     */
    void registerAll(List<UnitTypeConverter> unitConverters);

    /**
     * Unregisters a unit type converter for the type kind.
     *
     * @param typeKind the type kind.
     * @return the previously registered unit type converter.
     */
    UnitTypeConverter unregister(DataTypeKind typeKind);

    /**
     * Returns the JSON object mapper.
     *
     * @return the JSON object mapper.
     */
    ObjectMapper getObjectMapper();
}
