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

@SuppressWarnings({ "unchecked" })
public abstract class BaseUnitTypeConverter<T, D extends DataType<T>> implements UnitTypeConverter<T, D> {

    protected BaseUnitTypeConverter(DataTypeKind typeKind) {
        this.typeKind = typeKind;
    }

    protected DataTypeKind typeKind;

    @Override
    public DataTypeKind getTypeKind() {
        return typeKind;
    }

    @Override
    public Object marshal(TypeConverter converter, D type, T value) {
        return value;
    }

    @Override
    public T unmarshal(TypeConverter converter, D type, Object value) {
        return (T) value;
    }
}
