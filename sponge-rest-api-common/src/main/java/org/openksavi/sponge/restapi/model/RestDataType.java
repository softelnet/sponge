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

package org.openksavi.sponge.restapi.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import io.swagger.annotations.ApiModel;

import org.openksavi.sponge.type.AnyType;
import org.openksavi.sponge.type.BinaryType;
import org.openksavi.sponge.type.BooleanType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.DateTimeType;
import org.openksavi.sponge.type.DynamicType;
import org.openksavi.sponge.type.IntegerType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.MapType;
import org.openksavi.sponge.type.NumberType;
import org.openksavi.sponge.type.ObjectType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.StreamType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.TypeType;
import org.openksavi.sponge.type.VoidType;

@ApiModel(value = "DataType", description = "A data type")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "kind")
@JsonSubTypes({ @Type(value = AnyType.class, name = DataTypeKind.CODE_ANY),
        @Type(value = BinaryType.class, name = DataTypeKind.CODE_BINARY),
        @Type(value = BooleanType.class, name = DataTypeKind.CODE_BOOLEAN),
        @Type(value = DateTimeType.class, name = DataTypeKind.CODE_DATE_TIME),
        @Type(value = DynamicType.class, name = DataTypeKind.CODE_DYNAMIC),
        @Type(value = IntegerType.class, name = DataTypeKind.CODE_INTEGER), @Type(value = ListType.class, name = DataTypeKind.CODE_LIST),
        @Type(value = MapType.class, name = DataTypeKind.CODE_MAP), @Type(value = NumberType.class, name = DataTypeKind.CODE_NUMBER),
        @Type(value = ObjectType.class, name = DataTypeKind.CODE_OBJECT), @Type(value = RecordType.class, name = DataTypeKind.CODE_RECORD),
        @Type(value = StreamType.class, name = DataTypeKind.CODE_STREAM), @Type(value = StringType.class, name = DataTypeKind.CODE_STRING),
        @Type(value = TypeType.class, name = DataTypeKind.CODE_TYPE), @Type(value = VoidType.class, name = DataTypeKind.CODE_VOID) })
public abstract class RestDataType {
}
