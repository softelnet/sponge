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
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.OutputStreamType;
import org.openksavi.sponge.type.value.OutputStreamValue;

/**
 * Marshalling is handled by the upper layer.
 */
public class OutputStreamTypeUnitConverter extends BaseUnitTypeConverter<OutputStreamValue, OutputStreamType> {

    public OutputStreamTypeUnitConverter() {
        super(DataTypeKind.OUTPUT_STREAM);
    }

    @Override
    public OutputStreamValue unmarshal(TypeConverter converter, OutputStreamType type, Object value) {
        throw new UnsupportedOperationException("Unmarshalling of an output stream value is not supported");
    }
}
