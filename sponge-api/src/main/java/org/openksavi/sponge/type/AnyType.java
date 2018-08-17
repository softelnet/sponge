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

public class AnyType extends Type {

    public AnyType() {
        super(TypeKind.ANY);
    }

    @Override
    public AnyType format(String format) {
        return (AnyType) super.format(format);
    }

    @Override
    public AnyType tags(String... tags) {
        return (AnyType) super.tags(tags);
    }

    @Override
    public AnyType tag(String tag) {
        return (AnyType) super.tag(tag);
    }

    @Override
    public AnyType features(Map<String, Object> features) {
        return (AnyType) super.features(features);
    }

    @Override
    public AnyType feature(String name, Object value) {
        return (AnyType) super.feature(name, value);
    }
}