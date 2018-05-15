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

package org.openksavi.sponge;

import java.util.Arrays;

/**
 * A data type. Used for example in action arguments metadata.
 */
public enum Type {
    STRING("string"), NUMBER("number"), BYTES("bytes"), OBJECT("object"), VOID("void");

    private String code;

    private Type(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static Type fromCode(String code) {
        return Arrays.stream(Type.values()).filter(type -> type.getCode().equals(code)).findFirst().orElse(null);
    }
}
