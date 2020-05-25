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

package org.openksavi.sponge.remoteapi.model.request;

import java.util.LinkedHashMap;
import java.util.Map;

import io.swagger.annotations.ApiModel;

@ApiModel(value = "GenericRequest", description = "A generic request")
public class GenericRequest extends BaseRequest<Map<String, Object>> {

    public GenericRequest() {
    }

    @Override
    public Map<String, Object> createParams() {
        return new LinkedHashMap<>();
    }

    @Override
    public RequestHeader getHeader() {
        throw new UnsupportedOperationException("getHeader() is not supported in the generic request");
    }

    @Override
    public void setHeader(RequestHeader header) {
        throw new UnsupportedOperationException("setHeader() is not supported in the generic request");
    }
}
