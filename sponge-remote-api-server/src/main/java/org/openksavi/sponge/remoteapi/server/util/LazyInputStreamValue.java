/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.server.util;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.fileupload.FileItemStream;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.type.value.InputStreamValue;

public class LazyInputStreamValue extends InputStreamValue {

    private String name;

    private FormDataMultiPartContext context;

    private boolean consumed = false;

    public LazyInputStreamValue(String name, FormDataMultiPartContext context) {
        this.name = name;
        this.context = context;
    }

    @Override
    public String getContentType() {
        ensureCurrentItem();
        return super.getContentType();
    }

    @Override
    public InputStream getInputStream() {
        ensureCurrentItem();
        return super.getInputStream();
    }

    @Override
    public String getFilename() {
        ensureCurrentItem();
        return super.getFilename();
    }

    @Override
    public Map<String, Object> getHeaders() {
        ensureCurrentItem();
        return super.getHeaders();
    }

    public boolean hasNext() {
        return context.hasNextItem(name);
    }

    public LazyInputStreamValue next() {
        return new LazyInputStreamValue(name, context);
    }

    private void applyItem(FileItemStream item) {
        try {
            setInputStream(item.openStream());
            setFilename(item.getName());
            setContentType(item.getContentType());

            if (item.getHeaders() != null) {
                Iterator<String> namesIterator = item.getHeaders().getHeaderNames();
                while (namesIterator.hasNext()) {
                    String name = namesIterator.next();
                    getHeaders().put(name, item.getHeaders().getHeader(name));
                }
            }
        } catch (Exception e) {
            throw SpongeUtils.wrapException(e);
        }
    }

    private void ensureCurrentItem() {
        if (consumed) {
            return;
        }

        if (!hasNext()) {
            throw new SpongeException("There is no more files for " + name);
        }

        consumed = true;

        applyItem(context.nextItem(name));
    }
}
