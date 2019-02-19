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

package org.openksavi.sponge.restapi.type.converter;

import java.util.Arrays;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.openksavi.sponge.restapi.type.converter.unit.AnnotatedTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.AnyTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.BinaryTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.BooleanTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.DateTimeTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.DynamicTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.IntegerTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.ListTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.MapTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.NumberTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.ObjectTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.RecordTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.StringTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.TypeTypeUnitConverter;
import org.openksavi.sponge.restapi.type.converter.unit.VoidTypeUnitConverter;

/**
 * A default type converter.
 */
public class DefaultTypeConverter extends BaseTypeConverter {

    @SuppressWarnings("rawtypes")
    public DefaultTypeConverter(ObjectMapper objectMapper) {
        super(objectMapper);

        // Register default unit converters.
        registerAll(Arrays.asList(new AnnotatedTypeUnitConverter(), new AnyTypeUnitConverter(), new BinaryTypeUnitConverter(),
                new BooleanTypeUnitConverter(), new DateTimeTypeUnitConverter(), new DynamicTypeUnitConverter(),
                new IntegerTypeUnitConverter(), new ListTypeUnitConverter(), new MapTypeUnitConverter(), new NumberTypeUnitConverter(),
                new ObjectTypeUnitConverter(), new RecordTypeUnitConverter(), new StringTypeUnitConverter(), new TypeTypeUnitConverter(),
                new VoidTypeUnitConverter()));
    }
}
