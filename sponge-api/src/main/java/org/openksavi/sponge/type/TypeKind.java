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

package org.openksavi.sponge.type;

/**
 * Data type kind.
 */
@SuppressWarnings("rawtypes")
public enum TypeKind {
    STRING(StringType.class),
    NUMBER(NumberType.class),
    INTEGER(IntegerType.class),
    BOOLEAN(BooleanType.class),
    BINARY(BinaryType.class),
    ANY(AnyType.class),
    VOID(VoidType.class),
    OBJECT(ObjectType.class),
    LIST(ListType.class),
    MAP(MapType.class),
    ACTION(ActionType.class),;

    public static final String CODE_STRING = "STRING";

    public static final String CODE_NUMBER = "NUMBER";

    public static final String CODE_INTEGER = "INTEGER";

    public static final String CODE_BOOLEAN = "BOOLEAN";

    public static final String CODE_BINARY = "BINARY";

    public static final String CODE_ANY = "ANY";

    public static final String CODE_VOID = "VOID";

    public static final String CODE_OBJECT = "OBJECT";

    public static final String CODE_LIST = "LIST";

    public static final String CODE_MAP = "MAP";

    public static final String CODE_ACTION = "ACTION";

    private Class<? extends Type> typeClass;

    private TypeKind(Class<? extends Type> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends Type> getTypeClass() {
        return typeClass;
    }
}
