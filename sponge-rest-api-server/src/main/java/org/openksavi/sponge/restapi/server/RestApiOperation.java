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

package org.openksavi.sponge.restapi.server;

import java.util.function.BiFunction;

import org.apache.camel.Exchange;

import org.openksavi.sponge.restapi.model.request.SpongeRequest;
import org.openksavi.sponge.restapi.model.response.SpongeResponse;

/**
 * A REST API operation.
 */
public class RestApiOperation<I extends SpongeRequest, O extends SpongeResponse> {

    private String type;

    private String description;

    private Class<I> requestClass;

    private String requestDescription;

    private Class<O> responseClass;

    private String responseDescription;

    private BiFunction<I, Exchange, O> operationHandler;

    public RestApiOperation(String type, String description, Class<I> requestClass, String requestDescription, Class<O> responseClass,
            String responseDescription, BiFunction<I, Exchange, O> operationHandler) {
        this.type = type;
        this.description = description;
        this.requestClass = requestClass;
        this.requestDescription = requestDescription;
        this.responseClass = responseClass;
        this.responseDescription = responseDescription;
        this.operationHandler = operationHandler;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Class<I> getRequestClass() {
        return requestClass;
    }

    public void setRequestClass(Class<I> requestClass) {
        this.requestClass = requestClass;
    }

    public String getRequestDescription() {
        return requestDescription;
    }

    public void setRequestDescription(String requestDescription) {
        this.requestDescription = requestDescription;
    }

    public Class<O> getResponseClass() {
        return responseClass;
    }

    public void setResponseClass(Class<O> responseClass) {
        this.responseClass = responseClass;
    }

    public String getResponseDescription() {
        return responseDescription;
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public BiFunction<I, Exchange, O> getOperationHandler() {
        return operationHandler;
    }

    public void setOperationHandler(BiFunction<I, Exchange, O> operationHandler) {
        this.operationHandler = operationHandler;
    }
}
