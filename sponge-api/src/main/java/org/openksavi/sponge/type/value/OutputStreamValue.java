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

package org.openksavi.sponge.type.value;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An output stream value.
 */
public class OutputStreamValue {

    /** The output stream producer. */
    private OutputStreamProducer outputProducer;

    /** The stream content type. */
    private String contentType;

    /** The stream headers. */
    private Map<String, Object> headers = new LinkedHashMap<>();

    protected OutputStreamValue() {
    }

    public OutputStreamValue(OutputStreamProducer outputProducer) {
        this.outputProducer = outputProducer;
    }

    public OutputStreamProducer getOutputProducer() {
        return outputProducer;
    }

    public void setOutputProducer(OutputStreamProducer outputProducer) {
        this.outputProducer = outputProducer;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    public OutputStreamValue withContentType(String contentType) {
        setContentType(contentType);
        return this;
    }

    public OutputStreamValue withHeaders(Map<String, Object> headers) {
        this.headers.putAll(headers);
        return this;
    }

    public OutputStreamValue withHeader(String name, Object value) {
        headers.put(name, value);
        return this;
    }
}
