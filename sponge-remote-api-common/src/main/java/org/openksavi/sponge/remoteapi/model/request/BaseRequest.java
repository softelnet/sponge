/*
 * Copyright 2016-2020 The Sponge authors.
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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;

public abstract class BaseRequest<T extends RequestParams> implements SpongeRequest<T> {

    private String jsonrpc = "2.0";

    private String method;

    @JsonInclude(Include.NON_NULL)
    private T params;

    private Object id;

    protected BaseRequest(String method, T params) {
        this.method = method;
        this.params = params;
    }

    protected BaseRequest() {
    }

    @ApiModelProperty(value = "The JSON-RPC version", required = true)
    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    @Override
    @ApiModelProperty(value = "The JSON-RPC method", required = true)
    public String getMethod() {
        return method;
    }

    @Override
    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    @ApiModelProperty(value = "The JSON-RPC params", required = false)
    public T getParams() {
        return params;
    }

    @Override
    public void setParams(T params) {
        this.params = params;
    }

    @Override
    @ApiModelProperty(value = "The JSON-RPC id", required = false)
    public Object getId() {
        return id;
    }

    @Override
    public void setId(Object id) {
        this.id = id;
    }
}
