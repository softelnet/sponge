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

package org.openksavi.sponge.restapi.model.request;

import io.swagger.annotations.ApiModelProperty;

/**
 * A request with a body.
 */
public abstract class BodySpongeRequest<T extends RequestBody> extends SpongeRequest {

    private T body;

    protected BodySpongeRequest(T body) {
        this.body = body;
    }

    @ApiModelProperty(value = "The request body", required = true)
    public T getBody() {
        return body;
    }

    public void setBody(T body) {
        this.body = body;
    }

    public abstract T createBody();
}
