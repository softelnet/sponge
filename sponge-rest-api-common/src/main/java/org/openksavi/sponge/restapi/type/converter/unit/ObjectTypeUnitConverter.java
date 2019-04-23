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

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.restapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.ObjectType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ObjectTypeUnitConverter<O> extends BaseUnitTypeConverter<O, ObjectType<O>> {

    private boolean useTransparentIfNotFound;

    private Map<String, ObjectTypeConverterMapper> marshalers = new LinkedHashMap<>();

    private Map<String, ObjectTypeConverterMapper> unmarshalers = new LinkedHashMap<>();

    public ObjectTypeUnitConverter(boolean useTransparentIfNotFound) {
        super(DataTypeKind.OBJECT);

        this.useTransparentIfNotFound = useTransparentIfNotFound;
    }

    public ObjectTypeUnitConverter() {
        this(true);
    }

    @Override
    public Object marshal(TypeConverter converter, ObjectType<O> type, O value) {
        // Use marshaler if registered.
        if (marshalers.containsKey(type.getClassName())) {
            return marshalers.get(type.getClassName()).map(converter, value);
        }

        if (!useTransparentIfNotFound) {
            throw new SpongeException(String.format("Unsupported object type class name %s", type.getClassName()));
        }

        return value;
    }

    @Override
    public O unmarshal(TypeConverter converter, ObjectType type, Object value) {
        String className = type.getClassName();

        // Use unmarshaler if registered.
        if (unmarshalers.containsKey(type.getClassName())) {
            return (O) unmarshalers.get(type.getClassName()).map(converter, value);
        }

        if (!useTransparentIfNotFound) {
            throw new SpongeException(String.format("Unsupported object type class name %s", type.getClassName()));
        }

        return (O) converter.getObjectMapper().convertValue(value,
                Validate.notNull(RestApiUtils.getClass(className), "Class %s not found", className));
    }

    public void addMarshaler(String className, ObjectTypeConverterMapper mapper) {
        marshalers.put(className, mapper);
    }

    public void addUnmarshaler(String className, ObjectTypeConverterMapper mapper) {
        unmarshalers.put(className, mapper);
    }
}
