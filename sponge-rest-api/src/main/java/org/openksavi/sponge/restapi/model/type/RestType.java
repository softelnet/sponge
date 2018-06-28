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

package org.openksavi.sponge.restapi.model.type;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import org.openksavi.sponge.type.AnyType;
import org.openksavi.sponge.type.BinaryType;
import org.openksavi.sponge.type.BooleanType;
import org.openksavi.sponge.type.IntegerType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.MapType;
import org.openksavi.sponge.type.NumberType;
import org.openksavi.sponge.type.ObjectType;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.TypeKind;
import org.openksavi.sponge.type.VoidType;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "kind")
@JsonSubTypes({ @Type(value = AnyType.class, name = TypeKind.CODE_ANY), @Type(value = BinaryType.class, name = TypeKind.CODE_BINARY),
        @Type(value = BooleanType.class, name = TypeKind.CODE_BOOLEAN), @Type(value = IntegerType.class, name = TypeKind.CODE_INTEGER),
        @Type(value = ListType.class, name = TypeKind.CODE_LIST), @Type(value = MapType.class, name = TypeKind.CODE_MAP),
        @Type(value = NumberType.class, name = TypeKind.CODE_NUMBER), @Type(value = ObjectType.class, name = TypeKind.CODE_OBJECT),
        @Type(value = StringType.class, name = TypeKind.CODE_STRING), @Type(value = VoidType.class, name = TypeKind.CODE_VOID) })
public abstract class RestType {

}
