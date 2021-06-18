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

package org.openksavi.sponge.remoteapi.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModelProperty;

import org.openksavi.sponge.remoteapi.JsonRpcConstants;

/**
 * A Remote API response.
 */
@SuppressWarnings("rawtypes")
public interface SpongeResponse<T extends ResponseResult> {

    @ApiModelProperty(value = "The JSON-RPC version", required = true, allowableValues = JsonRpcConstants.VERSION)
    @JsonProperty(defaultValue = JsonRpcConstants.VERSION)
    String getJsonrpc();

    @ApiModelProperty(value = "The result", required = false)
    T getResult();

    void setResult(T result);

    @ApiModelProperty(value = "The error", required = false)
    ResponseError getError();

    void setError(ResponseError error);

    @ApiModelProperty(value = "The JSON-RPC id", required = false)
    Object getId();

    void setId(Object id);
}
