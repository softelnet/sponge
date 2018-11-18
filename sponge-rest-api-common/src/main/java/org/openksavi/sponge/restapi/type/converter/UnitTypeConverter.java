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

import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;

/**
 * A unit type converter.
 *
 * @param <T> type.
 */
@SuppressWarnings("rawtypes")
public interface UnitTypeConverter<T, D extends DataType> {

    /** The data type kind. */
    DataTypeKind getTypeKind();

    /**
     * Marshals the value as type.
     *
     * @param converter the type converter.
     * @param type the type.
     * @param value the value. It will never be {@code null} here.
     * @param T instance type.
     * @param D data type.
     * @return the converted value.
     */
    Object marshal(TypeConverter converter, D type, T value);

    /**
     * Unmarshals the value as type.
     *
     * @param converter the type converter.
     * @param type the type.
     * @param value the value. It will never be {@code null} here.
     * @param T instance type.
     * @param D data type.
     * @return the converted value.
     */
    T unmarshal(TypeConverter converter, D type, Object value);
}
