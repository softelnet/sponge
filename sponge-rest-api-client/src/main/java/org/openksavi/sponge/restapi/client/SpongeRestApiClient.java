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

import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.RestKnowledgeBaseMeta;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.GetActionsRequest;
import org.openksavi.sponge.restapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.request.LoginRequest;
import org.openksavi.sponge.restapi.model.request.LogoutRequest;
import org.openksavi.sponge.restapi.model.request.ReloadRequest;
import org.openksavi.sponge.restapi.model.request.SendEventRequest;
import org.openksavi.sponge.restapi.model.response.ActionCallResponse;
import org.openksavi.sponge.restapi.model.response.GetActionsResponse;
import org.openksavi.sponge.restapi.model.response.GetKnowledgeBasesResponse;
import org.openksavi.sponge.restapi.model.response.GetVersionResponse;
import org.openksavi.sponge.restapi.model.response.LoginResponse;
import org.openksavi.sponge.restapi.model.response.LogoutResponse;
import org.openksavi.sponge.restapi.model.response.ReloadResponse;
import org.openksavi.sponge.restapi.model.response.SendEventResponse;

/**
 * A Sponge REST API client.
 */
public interface SpongeRestApiClient {

    RestApiClientConfiguration getConfiguration();

    GetVersionResponse getVersion(GetVersionRequest request);

    String getVersion();

    LoginResponse login(LoginRequest request);

    String login();

    LogoutResponse logout(LogoutRequest request);

    void logout();

    GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request);

    List<RestKnowledgeBaseMeta> getKnowledgeBases();

    GetActionsResponse getActions(GetActionsRequest request);

    List<RestActionMeta> getActions(String nameRegExp, Boolean metadataRequired);

    List<RestActionMeta> getActions(String nameRegExp);

    List<RestActionMeta> getActions();

    RestActionMeta getActionMeta(String actionName);

    /**
     * Calls the action.
     *
     * Fetches the action result metadata from the server every call. Unmarshals the result using a best effort strategy, i.e. when a result
     * metadata is defined for this action (on the server side). If you want to disable fetching metadata and unmarshalling the result, use
     * {@code callWithNoMeta}.
     *
     * @param request the request.
     *
     * @return the response.
     */
    ActionCallResponse call(ActionCallRequest request);

    Object call(String actionName, Object... args);

    <T> T call(Class<T> resultClass, String actionName, Object... args);

    /**
     * Calls the action. Unmarshals the result if {@code actionMeta} is not {@code null}.
     *
     * @param actionMeta the action metadata.
     * @param request the request.
     *
     * @return the response.
     */
    ActionCallResponse callWithMeta(RestActionMeta actionMeta, ActionCallRequest request);

    Object callWithMeta(RestActionMeta actionMeta, Object... args);

    <T> T callWithMeta(Class<T> resultClass, RestActionMeta actionMeta, Object... args);

    Object callWithNoMeta(String actionName, Object... args);

    <T> T callWithNoMeta(Class<T> resultClass, String actionName, Object... args);

    SendEventResponse send(SendEventRequest request);

    String send(String eventName, Map<String, Object> attributes);

    ReloadResponse reload(ReloadRequest request);

    void reload();
}
