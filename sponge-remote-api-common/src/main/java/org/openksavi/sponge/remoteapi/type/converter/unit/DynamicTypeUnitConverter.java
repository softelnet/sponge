/*
 * Copyright 2016-2019 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.type.converter.unit;

import org.openksavi.sponge.remoteapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.remoteapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.DynamicType;
import org.openksavi.sponge.type.TypeType;
import org.openksavi.sponge.type.value.DynamicValue;

@SuppressWarnings("rawtypes")
public class DynamicTypeUnitConverter extends BaseUnitTypeConverter<DynamicValue, DynamicType> {

    public DynamicTypeUnitConverter() {
        super(DataTypeKind.DYNAMIC);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object marshal(TypeConverter converter, DynamicType type, DynamicValue value) {
        // Marshal the data type in the DynamicValue as well.
        return new DynamicValue<>(converter.marshal(value.getType(), value.getValue()),
                (DataType) converter.marshal(new TypeType(), value.getType()));
    }

    @SuppressWarnings("unchecked")
    @Override
    public DynamicValue unmarshal(TypeConverter converter, DynamicType type, Object value) {
        DynamicValue dynamicValue = converter.getObjectMapper().convertValue(value, DynamicValue.class);
        dynamicValue.setValue(converter.unmarshal(dynamicValue.getType(), dynamicValue.getValue()));

        // Unmarshal the data type in the DynamicValue as well.
        dynamicValue.setType((DataType) converter.unmarshal(new TypeType(), dynamicValue.getType()));

        return dynamicValue;
    }
}
