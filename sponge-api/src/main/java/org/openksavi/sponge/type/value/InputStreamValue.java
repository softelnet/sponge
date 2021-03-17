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

package org.openksavi.sponge.type.value;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An input stream value.
 */
public class InputStreamValue {

    /** The input stream. It must be closed after it is used. */
    private InputStream inputStream;

    /** The stream content type. */
    private String contentType;

    /** The stream filename. */
    private String filename;

    /** The stream headers. */
    private Map<String, Object> headers = new LinkedHashMap<>();

    protected InputStreamValue() {
    }

    public InputStreamValue(InputStream inputStream, String filename) {
        this.inputStream = inputStream;
        this.filename = filename;
    }

    public InputStreamValue(InputStream inputStream) {
        this(inputStream, null);
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public InputStreamValue withContentType(String contentType) {
        setContentType(contentType);
        return this;
    }

    public InputStreamValue withFilename(String filename) {
        setFilename(filename);
        return this;
    }

    public InputStreamValue withHeaders(Map<String, Object> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public InputStreamValue withHeader(String name, Object value) {
        headers.put(name, value);
        return this;
    }
}
