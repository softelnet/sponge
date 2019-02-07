/*
 * Copyright 2016-2019 The Sponge authors.
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

import org.openksavi.sponge.type.value.DynamicValue;

/**
 * An dynamic type representing dynamically typed values. A value of this type has to be an instance of
 * {@linkplain org.openksavi.sponge.type.value.DynamicValue}.
 */
@SuppressWarnings("rawtypes")
public class DynamicType extends DataType<DynamicValue> {

    public DynamicType() {
        super(DataTypeKind.DYNAMIC);
    }

    @Override
    public DynamicType withFormat(String format) {
        return (DynamicType) super.withFormat(format);
    }

    @Override
    public DynamicType withFeatures(Map<String, Object> features) {
        return (DynamicType) super.withFeatures(features);
    }

    @Override
    public DynamicType withFeature(String name, Object value) {
        return (DynamicType) super.withFeature(name, value);
    }

    @Override
    public DynamicType withDefaultValue(DynamicValue value) {
        return (DynamicType) super.withDefaultValue(value);
    }

    @Override
    public DynamicType withNullable(boolean nullable) {
        return (DynamicType) super.withNullable(nullable);
    }
}
