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

    /**
     * Returns the client configuration.
     *
     * @return the client configuration.
     */
    RestApiClientConfiguration getConfiguration();

    /**
     * Sends the {@code version} request to the server.
     *
     * @param request the request.
     * @return the {@code version} response.
     */
    GetVersionResponse getVersion(GetVersionRequest request);

    /**
     * Sends the {@code version} request to the server.
     *
     * @return the Sponge version.
     */
    String getVersion();

    /**
     * Sends the {@code login} request to the server.
     *
     * @param request the request.
     * @return the {@code login} response.
     */
    LoginResponse login(LoginRequest request);

    /**
     * Sends the {@code login} request to the server.
     *
     * @return the optional auth token.
     */
    String login();

    /**
     * Sends the {@code logout} request to the server.
     *
     * @param request the request.
     * @return the {@code logout} response.
     */
    LogoutResponse logout(LogoutRequest request);

    /**
     * Sends the {@code logout} request to the server.
     */
    void logout();

    /**
     * Sends the {@code knowledgeBases} request to the server.
     *
     * @param request the request.
     * @return the {@code knowledgeBases} response.
     */
    GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request);

    /**
     * Sends the {@code knowledgeBases} request to the server.
     *
     * @return the list of knowledge bases metadata.
     */
    List<RestKnowledgeBaseMeta> getKnowledgeBases();

    /**
     * Sends the {@code actions} request to the server. This method may populate the action metadata cache.
     *
     * @param request the request.
     * @return the {@code actions} response.
     */
    GetActionsResponse getActions(GetActionsRequest request);

    /**
     * Sends the {@code actions} request to the server. This method may populate the action metadata cache.
     *
     * @param nameRegExp the action name regular expression or {@code null} to return results for all actions.
     * @param metadataRequired if {@code true} only actions that have arguments and result metadata will be returned.
     * @return the list of actions metadata.
     */
    List<RestActionMeta> getActions(String nameRegExp, Boolean metadataRequired);

    /**
     * Sends the {@code actions} request to the server. This method may populate the action metadata cache.
     *
     * @param nameRegExp the action name regular expression or {@code null} to return results for all actions.
     * @return the list of actions metadata.
     */
    List<RestActionMeta> getActions(String nameRegExp);

    /**
     * Sends the {@code actions} request to the server. This method may populate the action metadata cache.
     *
     * @return the list of actions metadata.
     */
    List<RestActionMeta> getActions();

    /**
     * Returns the metadata for a specified action. This method may use action metadata cache if configured.
     *
     * @param actionName the action name.
     * @return the action metadata or {@code null} if there is no such action or that action has no metadata.
     */
    RestActionMeta getActionMeta(String actionName);

    /**
     * Calls the action. This method may use action metadata cache if configured or fetch the action result metadata from the server every
     * call.
     *
     * Unmarshals the result using a best effort strategy, i.e. when a result metadata is defined for this action (on the server side). If
     * you want to disable fetching metadata and unmarshalling the result, use action metadata cache or {@code callWithNoMeta}.
     *
     * @param request the request.
     *
     * @return the response.
     */
    ActionCallResponse call(ActionCallRequest request);

    /**
     * Calls the action. For more information see {@link #call(ActionCallRequest) call}.
     *
     * @param actionName the action name.
     * @param args the action arguments.
     *
     * @return the action result.
     */
    Object call(String actionName, Object... args);

    /**
     * Calls the action. For more information see {@link #call(ActionCallRequest) call}.
     *
     * @param resultClass the result class.
     * @param actionName the action name.
     * @param args the action arguments.
     *
     * @return the action result.
     */
    <T> T call(Class<T> resultClass, String actionName, Object... args);

    /**
     * Calls the action. Unmarshals the result according to the action result type if {@code actionMeta} is not {@code null}.
     *
     * @param actionMeta the action metadata.
     * @param request the request.
     *
     * @return the response.
     */
    ActionCallResponse callWithMeta(RestActionMeta actionMeta, ActionCallRequest request);

    /**
     * Calls the action. Unmarshals the result according to the action result type if {@code actionMeta} is not {@code null}.
     *
     * @param actionMeta the action metadata.
     * @param args the action arguments.
     *
     * @return the action result.
     */
    Object callWithMeta(RestActionMeta actionMeta, Object... args);

    /**
     * Calls the action. Unmarshals the result according to the action result type if {@code actionMeta} is not {@code null}.
     *
     * @param resultClass the result class.
     * @param actionMeta the action metadata.
     * @param args the action arguments.
     *
     * @return the action result.
     */
    <T> T callWithMeta(Class<T> resultClass, RestActionMeta actionMeta, Object... args);

    /**
     * Calls the action with no metadata.
     *
     * @param actionName the action name.
     * @param args the action arguments.
     *
     * @return the action result.
     */
    Object callWithNoMeta(String actionName, Object... args);

    /**
     * Calls the action with no metadata.
     *
     * @param resultClass the result class.
     * @param actionName the action name.
     * @param args the action arguments.
     *
     * @return the action result.
     */
    <T> T callWithNoMeta(Class<T> resultClass, String actionName, Object... args);

    /**
     * Send the event to the server.
     *
     * @param request the request.
     * @return the response.
     */
    SendEventResponse send(SendEventRequest request);

    /**
     * Send the event to the server.
     *
     * @param eventName the event name.
     * @param attributes the event attributes.
     * @return the event ID.
     */
    String send(String eventName, Map<String, Object> attributes);

    /**
     * Sends the {@code reload} request to the server.
     *
     * @param request the request.
     * @return the {@code reload} response.
     */
    ReloadResponse reload(ReloadRequest request);

    /**
     * Sends the {@code reload} request to the server.
     *
     * @return the {@code reload} response.
     */
    void reload();

    /**
     * Clears the action metadata cache.
     */
    void clearCache();
}
