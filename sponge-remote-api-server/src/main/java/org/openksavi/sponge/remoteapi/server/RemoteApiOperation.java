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

package org.openksavi.sponge.remoteapi.server;

import org.openksavi.sponge.remoteapi.model.request.SpongeRequest;
import org.openksavi.sponge.remoteapi.model.response.SpongeResponse;

/**
 * Defines a Remote API method.
 */
@SuppressWarnings("rawtypes")
public class RemoteApiOperation<I extends SpongeRequest<P>, P, O extends SpongeResponse> {

    private String method;

    private String operationId;

    private String description;

    private Class<I> requestClass;

    private Class<P> requestParamsClass;

    private String requestDescription;

    private Class<O> responseClass;

    private String responseDescription;

    private RemoteApiMethodHandler<I, O> handler;

    private boolean supportsFormDataMultiPart = false;

    public RemoteApiOperation(String method, String operationId, String description, Class<I> requestClass, Class<P> requestParamsClass,
            String requestDescription, Class<O> responseClass, String responseDescription, RemoteApiMethodHandler<I, O> handler,
            boolean supportsFormDataMultiPart) {
        this.method = method;
        this.operationId = operationId;
        this.description = description;
        this.requestClass = requestClass;
        this.requestParamsClass = requestParamsClass;
        this.requestDescription = requestDescription;
        this.responseClass = responseClass;
        this.responseDescription = responseDescription;
        this.handler = handler;
        this.supportsFormDataMultiPart = supportsFormDataMultiPart;
    }

    public RemoteApiOperation(String method, String operationId, String description, Class<I> requestClass, Class<P> requestParamsClass,
            String requestDescription, Class<O> responseClass, String responseDescription, RemoteApiMethodHandler<I, O> handler) {
        this(method, operationId, description, requestClass, requestParamsClass, requestDescription, responseClass, responseDescription,
                handler, false);
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
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

    public Class<P> getRequestParamsClass() {
        return requestParamsClass;
    }

    public void setRequestParamsClass(Class<P> requestParamsClass) {
        this.requestParamsClass = requestParamsClass;
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

    public RemoteApiMethodHandler<I, O> getHandler() {
        return handler;
    }

    public void setHandler(RemoteApiMethodHandler<I, O> handler) {
        this.handler = handler;
    }

    public boolean isSupportsFormDataMultiPart() {
        return supportsFormDataMultiPart;
    }

    public void setSupportsFormDataMultiPart(boolean supportsFormDataMultiPart) {
        this.supportsFormDataMultiPart = supportsFormDataMultiPart;
    }
}
