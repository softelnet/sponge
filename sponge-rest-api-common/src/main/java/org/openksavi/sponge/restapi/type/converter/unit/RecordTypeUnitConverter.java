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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.restapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.RecordType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class RecordTypeUnitConverter extends BaseUnitTypeConverter<Map<String, Object>, RecordType> {

    public RecordTypeUnitConverter() {
        super(DataTypeKind.RECORD);
    }

    @Override
    public Object marshal(TypeConverter converter, RecordType type, Map<String, Object> value) {
        Map<String, Object> marshalled = new LinkedHashMap<>(value.size());
        value.entrySet().stream()
                .forEach(entry -> marshalled.put(entry.getKey(), converter.marshal(type.getFieldType(entry.getKey()), entry.getValue())));

        return marshalled;
    }

    @Override
    public Map<String, Object> unmarshal(TypeConverter converter, RecordType type, Object value) {
        Validate.isInstanceOf(Map.class, value, "Expected map but got %s", value.getClass());

        Map<String, Object> unmarshalled = new LinkedHashMap<>(((Map) value).size());
        ((Map<String, Object>) value).entrySet().stream().forEach(entry -> unmarshalled.put(entry.getKey(),
                converter.unmarshal(type.getFieldType((String) entry.getKey()), entry.getValue())));

        return unmarshalled;
    }
}
