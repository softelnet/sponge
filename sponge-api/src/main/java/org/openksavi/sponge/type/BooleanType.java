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
 * A boolean type.
 */
public class BooleanType extends DataType<Boolean> {

    public BooleanType() {
        super(DataTypeKind.BOOLEAN);
    }

    @Override
    public BooleanType withFormat(String format) {
        return (BooleanType) super.withFormat(format);
    }

    @Override
    public BooleanType withFeatures(Map<String, Object> features) {
        return (BooleanType) super.withFeatures(features);
    }

    @Override
    public BooleanType withFeature(String name, Object value) {
        return (BooleanType) super.withFeature(name, value);
    }

    @Override
    public BooleanType withDefaultValue(Boolean value) {
        return (BooleanType) super.withDefaultValue(value);
    }

    @Override
    public BooleanType withNullable(boolean nullable) {
        return (BooleanType) super.withNullable(nullable);
    }
}
