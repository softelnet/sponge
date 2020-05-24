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

package org.openksavi.sponge.remoteapi.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import io.swagger.annotations.ApiModelProperty;

/**
 * A base response.
 */
@SuppressWarnings("rawtypes")
public abstract class BaseResponse<T extends ResponseResult> implements SpongeResponse<T> {

    private String jsonrpc = "2.0";

    @JsonInclude(Include.NON_NULL)
    private T result;

    @JsonInclude(Include.NON_NULL)
    private ResponseError error;

    private Object id;

    protected BaseResponse(T result) {
        this.result = result;
    }

    protected BaseResponse() {
    }

    @ApiModelProperty(value = "The JSON-RPC version", required = true)
    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    @Override
    @ApiModelProperty(value = "The result", required = false)
    public T getResult() {
        return result;
    }

    @Override
    public void setResult(T result) {
        this.result = result;
    }

    @Override
    @ApiModelProperty(value = "The error", required = false)
    public ResponseError getError() {
        return error;
    }

    @Override
    public void setError(ResponseError error) {
        this.error = error;
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
