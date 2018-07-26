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

public class BinaryType extends Type {

    private String mimeType;

    public BinaryType() {
        super(TypeKind.BINARY);
    }

    @Override
    public BinaryType format(String format) {
        return (BinaryType) super.format(format);
    }

    @Override
    public BinaryType tags(String... tags) {
        return (BinaryType) super.tags(tags);
    }

    @Override
    public BinaryType tag(String tag) {
        return (BinaryType) super.tag(tag);
    }

    public BinaryType mimeType(String mimeType) {
        this.mimeType = mimeType;
        return this;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }
}
