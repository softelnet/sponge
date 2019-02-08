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

import java.util.Map;

/**
 * A void type that may be used to specify that an action returns no meaningful result.
 */
public class VoidType extends DataType<Void> {

    public VoidType() {
        super(DataTypeKind.VOID);
    }

    @Override
    public VoidType withFormat(String format) {
        return (VoidType) super.withFormat(format);
    }

    @Override
    public VoidType withFeatures(Map<String, Object> features) {
        return (VoidType) super.withFeatures(features);
    }

    @Override
    public VoidType withFeature(String name, Object value) {
        return (VoidType) super.withFeature(name, value);
    }

    @Override
    public VoidType withDefaultValue(Void value) {
        return (VoidType) super.withDefaultValue(value);
    }

    @Override
    public VoidType withNullable(boolean nullable) {
        return (VoidType) super.withNullable(nullable);
    }

    @Override
    public VoidType withNullable() {
        return (VoidType) super.withNullable();
    }
}
