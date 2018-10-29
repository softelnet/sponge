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

import java.util.Collection;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.restapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.TypeKind;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ListTypeUnitConverter extends BaseUnitTypeConverter<ListType> {

    public ListTypeUnitConverter() {
        super(TypeKind.LIST);
    }

    @Override
    public Object marshal(TypeConverter converter, ListType type, Object value) {
        Validate.isInstanceOf(Collection.class, value, "Expected list but got %s", value.getClass());

        return ((Collection) value).stream().map((Object element) -> converter.marshal(((ListType) type).getElementType(), element))
                .collect(Collectors.toList());
    }

    @Override
    public Object unmarshal(TypeConverter converter, ListType type, Object value) {
        Validate.isInstanceOf(Collection.class, value, "Expected list but got %s", value.getClass());

        return ((Collection) value).stream().map((Object jsonElem) -> converter.unmarshal(((ListType) type).getElementType(), jsonElem))
                .collect(Collectors.toList());
    }
}
