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

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.restapi.type.converter.BaseUnitTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.ObjectType;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ObjectTypeUnitConverter extends BaseUnitTypeConverter<Object, ObjectType> {

    private boolean useTransparentIfNotFound;

    private boolean findClass = true;

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
    public Object marshal(TypeConverter converter, ObjectType type, Object value) {
        String className = type.getClassName();

        // Use marshaler if registered.
        if (marshalers.containsKey(className)) {
            return marshalers.get(className).map(converter, value);
        }

        Class cls = getTypeValueClass(type);

        if (cls != null && cls.isInstance(value)) {
            return value;
        }

        // Class not found.
        if (type.getCompanionType() != null) {
            return converter.marshal(type.getCompanionType(), value);
        }

        if (!useTransparentIfNotFound) {
            throw new SpongeException(String.format("Unsupported object type class name %s", className));
        }

        return value;
    }

    @Override
    public Object unmarshal(TypeConverter converter, ObjectType type, Object value) {
        String className = type.getClassName();

        // Use unmarshaler if registered.
        if (unmarshalers.containsKey(className)) {
            return unmarshalers.get(className).map(converter, value);
        }

        Class cls = getTypeValueClass(type);
        if (cls != null) {
            return converter.getObjectMapper().convertValue(value, cls);
        }

        // Class not found.
        if (type.getCompanionType() != null) {
            return converter.unmarshal(type.getCompanionType(), value);
        }

        if (!useTransparentIfNotFound) {
            throw new SpongeException(String.format("Unsupported object type class name %s", type.getClassName()));
        }

        return value;
    }

    protected Class getTypeValueClass(ObjectType type) {
        return findClass ? RestApiUtils.getClass(type.getClassName()) : null;
    }

    public void addMarshaler(String className, ObjectTypeConverterMapper mapper) {
        marshalers.put(className, mapper);
    }

    public void addUnmarshaler(String className, ObjectTypeConverterMapper mapper) {
        unmarshalers.put(className, mapper);
    }

    public boolean isUseTransparentIfNotFound() {
        return useTransparentIfNotFound;
    }

    public void setUseTransparentIfNotFound(boolean useTransparentIfNotFound) {
        this.useTransparentIfNotFound = useTransparentIfNotFound;
    }

    public boolean isFindClass() {
        return findClass;
    }

    public void setFindClass(boolean findClass) {
        this.findClass = findClass;
    }
}
