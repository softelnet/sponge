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

package org.openksavi.sponge.restapi.client;

import java.util.List;
import java.util.Map;

import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.request.RestActionCallRequest;
import org.openksavi.sponge.restapi.model.request.RestGetActionsRequest;
import org.openksavi.sponge.restapi.model.request.RestGetVersionRequest;
import org.openksavi.sponge.restapi.model.request.RestSendEventRequest;
import org.openksavi.sponge.restapi.model.response.RestActionCallResponse;
import org.openksavi.sponge.restapi.model.response.RestGetActionsResponse;
import org.openksavi.sponge.restapi.model.response.RestGetVersionResponse;
import org.openksavi.sponge.restapi.model.response.RestSendEventResponse;

/**
 * A Sponge REST API client.
 */
public interface SpongeRestApiClient {

    RestApiClientConfiguration getConfiguration();

    RestGetVersionResponse getVersion(RestGetVersionRequest request);

    String getVersion();

    RestGetActionsResponse getActions(RestGetActionsRequest request);

    List<RestActionMeta> getActions(Boolean metadataRequired, String nameRegExp);

    List<RestActionMeta> getActions();

    ResultMeta<?> getActionResultMeta(String actionName);

    /**
     * Calls the action.
     *
     * Fetches the action result metadata from the server every call. Unmarshals the result using best effort strategy, i.e. when the result
     * metadata is present. If you want to disable fetching metadata and unmarshalling the result, use {@code callWithMeta(request, null)}.
     *
     * @param request the request.
     *
     * @return the response.
     */
    RestActionCallResponse call(RestActionCallRequest request);

    Object call(String actionName, Object... args);

    <T> T call(Class<T> resultClass, String actionName, Object... args);

    /**
     * Calls the action. Unmarshals the result if {@code resultMeta} is not {@code null}.
     *
     * @param request the request.
     * @param resultMeta the result metadata.
     *
     * @return the response.
     */
    RestActionCallResponse callWithMeta(RestActionCallRequest request, ResultMeta<?> resultMeta);

    Object callWithMeta(ResultMeta<?> resultMeta, String actionName, Object... args);

    <T> T callWithMeta(ResultMeta<?> resultMeta, Class<T> resultClass, String actionName, Object... args);

    RestSendEventResponse send(RestSendEventRequest request);

    String send(String eventName, Map<String, Object> attributes);
}
