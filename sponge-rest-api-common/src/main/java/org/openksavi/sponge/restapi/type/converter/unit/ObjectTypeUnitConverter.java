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

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.restapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.ObjectType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ObjectTypeUnitConverter<O> extends BaseUnitTypeConverter<O, ObjectType<O>> {

    public ObjectTypeUnitConverter() {
        super(DataTypeKind.OBJECT);
    }

    @Override
    public O unmarshal(TypeConverter converter, ObjectType type, Object value) {
        Validate.isInstanceOf(ObjectType.class, type, "Object type %s doesn't match %s", type.getClass(), ObjectType.class);
        String className = ((ObjectType) type).getClassName();

        return (O) converter.getObjectMapper().convertValue(value,
                Validate.notNull(RestApiUtils.getClass(className), "Class % not found", className));
    }
}
