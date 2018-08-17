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

public class ObjectType extends Type {

    private String className;

    protected ObjectType() {
        this(null);
    }

    public ObjectType(String className) {
        super(TypeKind.OBJECT);

        this.className = className;
    }

    @Override
    public ObjectType format(String format) {
        return (ObjectType) super.format(format);
    }

    @Override
    public ObjectType tags(String... tags) {
        return (ObjectType) super.tags(tags);
    }

    @Override
    public ObjectType tag(String tag) {
        return (ObjectType) super.tag(tag);
    }

    @Override
    public ObjectType features(Map<String, Object> features) {
        return (ObjectType) super.features(features);
    }

    @Override
    public ObjectType feature(String name, Object value) {
        return (ObjectType) super.feature(name, value);
    }

    public String getClassName() {
        return className;
    }
}