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
 * A data type kind.
 */
@SuppressWarnings("rawtypes")
public enum DataTypeKind {
    ANNOTATED(AnnotatedType.class),
    ANY(AnyType.class),
    BINARY(BinaryType.class),
    BOOLEAN(BooleanType.class),
    DYNAMIC(DynamicType.class),
    INTEGER(IntegerType.class),
    LIST(ListType.class),
    MAP(MapType.class),
    NUMBER(NumberType.class),
    OBJECT(ObjectType.class),
    STRING(StringType.class),
    TYPE(TypeType.class),
    VOID(VoidType.class);

    public static final String CODE_ANNOTATED = "ANNOTATED";

    public static final String CODE_ANY = "ANY";

    public static final String CODE_BINARY = "BINARY";

    public static final String CODE_BOOLEAN = "BOOLEAN";

    public static final String CODE_DYNAMIC = "DYNAMIC";

    public static final String CODE_INTEGER = "INTEGER";

    public static final String CODE_LIST = "LIST";

    public static final String CODE_MAP = "MAP";

    public static final String CODE_NUMBER = "NUMBER";

    public static final String CODE_OBJECT = "OBJECT";

    public static final String CODE_STRING = "STRING";

    public static final String CODE_TYPE = "TYPE";

    public static final String CODE_VOID = "VOID";

    private Class<? extends DataType> typeClass;

    private DataTypeKind(Class<? extends DataType> typeClass) {
        this.typeClass = typeClass;
    }

    public Class<? extends DataType> getTypeClass() {
        return typeClass;
    }
}
