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

package org.openksavi.sponge.restapi.type.converter.unit;

import org.openksavi.sponge.restapi.feature.converter.FeaturesUtils;
import org.openksavi.sponge.restapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.TypeType;
import org.openksavi.sponge.util.DataTypeUtils;

@SuppressWarnings("rawtypes")
public class TypeTypeUnitConverter extends BaseUnitTypeConverter<DataType, TypeType> {

    public TypeTypeUnitConverter() {
        super(DataTypeKind.TYPE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataType marshal(TypeConverter converter, TypeType type, DataType value) {
        // It's very important to clone the instance because the copy will be modified.
        DataType result = value.clone();

        // Recursively marshal default values and features.
        for (DataType t : DataTypeUtils.getTypes(result)) {
            t.setDefaultValue(converter.marshal(t, t.getDefaultValue()));

            t.setFeatures(FeaturesUtils.marshal(converter.getFeatureConverter(), t.getFeatures()));
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public DataType unmarshal(TypeConverter converter, TypeType type, Object value) {
        DataType unmarshalled =
                value instanceof DataType ? (DataType) value : converter.getObjectMapper().convertValue(value, DataType.class);

        // Recursively unmarshal default values and features.
        for (DataType t : DataTypeUtils.getTypes(unmarshalled)) {
            t.setDefaultValue(converter.unmarshal(t, t.getDefaultValue()));

            t.setFeatures(FeaturesUtils.unmarshal(converter.getFeatureConverter(), t.getFeatures()));
        }

        return unmarshalled;
    }
}
