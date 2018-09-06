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

public class BooleanType extends Type<Boolean> {

    public BooleanType() {
        super(TypeKind.BOOLEAN);
    }

    @Override
    public BooleanType format(String format) {
        return (BooleanType) super.format(format);
    }

    @Override
    public BooleanType tags(String... tags) {
        return (BooleanType) super.tags(tags);
    }

    @Override
    public BooleanType tag(String tag) {
        return (BooleanType) super.tag(tag);
    }

    @Override
    public BooleanType features(Map<String, Object> features) {
        return (BooleanType) super.features(features);
    }

    @Override
    public BooleanType feature(String name, Object value) {
        return (BooleanType) super.feature(name, value);
    }

    @Override
    public BooleanType defaultValue(Boolean value) {
        return (BooleanType) super.defaultValue(value);
    }
}
