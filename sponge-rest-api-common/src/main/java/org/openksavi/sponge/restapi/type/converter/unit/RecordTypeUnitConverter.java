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

import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.restapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.RecordType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class RecordTypeUnitConverter extends BaseUnitTypeConverter<Map<String, Object>, RecordType> {

    public RecordTypeUnitConverter() {
        super(DataTypeKind.RECORD);
    }

    @Override
    public Object marshal(TypeConverter converter, RecordType type, Map<String, Object> value) {
        Map<String, DataType> fieldMap = createFieldMap(type);

        return value.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(),
                entry -> converter.marshal(getFieldType(fieldMap, type, entry.getKey()), entry.getValue())));
    }

    @Override
    public Map<String, Object> unmarshal(TypeConverter converter, RecordType type, Object value) {
        Validate.isInstanceOf(Map.class, value, "Expected map but got %s", value.getClass());

        Map<String, DataType> fieldMap = createFieldMap(type);

        return (Map<String, Object>) ((Map) value).entrySet().stream().collect(Collectors.toMap((Map.Entry entry) -> entry.getKey(),
                (Map.Entry entry) -> converter.unmarshal(getFieldType(fieldMap, type, (String) entry.getKey()), entry.getValue())));
    }

    protected DataType<?> getFieldType(Map<String, DataType> fieldMap, RecordType type, String fieldName) {
        return Validate.notNull(fieldMap.get(fieldName), "Field %s is not defined in the record type %s", fieldName,
                type.getName() != null ? type.getName() : "");
    }

    protected Map<String, DataType> createFieldMap(RecordType type) {
        return type.getFields().stream().collect(Collectors.toMap(field -> field.getName(), field -> field));
    }
}
