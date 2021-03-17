/*
 * Copyright 2016-2021 The Sponge authors.
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
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.InputStreamType;
import org.openksavi.sponge.type.value.InputStreamValue;

/**
 * Marshalling/unmarshalling handled by the upper layer.
 */
public class InputStreamTypeUnitConverter extends BaseUnitTypeConverter<InputStreamValue, InputStreamType> {

    public InputStreamTypeUnitConverter() {
        super(DataTypeKind.INPUT_STREAM);
    }
}
