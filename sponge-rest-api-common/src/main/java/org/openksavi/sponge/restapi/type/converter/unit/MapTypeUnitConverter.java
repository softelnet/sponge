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

package org.openksavi.sponge.restapi.type.converter.unit;

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.restapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.MapType;
import org.openksavi.sponge.type.TypeKind;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class MapTypeUnitConverter extends BaseUnitTypeConverter<MapType> {

    public MapTypeUnitConverter() {
        super(TypeKind.MAP);
    }

    @Override
    public Object marshal(TypeConverter converter, MapType type, Object value) {
        Validate.isInstanceOf(Map.class, value, "Expected map but got %s", value.getClass());

        return ((Map) value).entrySet().stream()
                .collect(Collectors.toMap((Map.Entry entry) -> converter.marshal(((MapType) type).getKeyType(), entry.getKey()),
                        (Map.Entry entry) -> converter.marshal(((MapType) type).getValueType(), entry.getValue())));
    }

    @Override
    public Object unmarshal(TypeConverter converter, MapType type, Object value) {
        Validate.isInstanceOf(Map.class, value, "Expected map but got %s", value.getClass());

        return ((Map) value).entrySet().stream()
                .collect(Collectors.toMap((Map.Entry entry) -> converter.unmarshal(((MapType) type).getKeyType(), entry.getKey()),
                        (Map.Entry entry) -> converter.unmarshal(((MapType) type).getValueType(), entry.getValue())));
    }
}
