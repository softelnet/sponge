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

import java.io.Closeable;
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
public interface SpongeRestClient extends Closeable {

    /**
     * Returns the client configuration.
     *
     * @return the client configuration.
     */
    SpongeRestClientConfiguration getConfiguration();

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
     * Sends the {@code login} request to the server. Sets the auth token in the client for further requests.
     *
     * @param request the request.
     * @return the {@code login} response.
     */
    LoginResponse login(LoginRequest request);

    /**
     * Sends the {@code login} request to the server. See {@link #login(LoginRequest)}.
     *
     * @return the optional auth token.
     */
    String login();

    /**
     * Sends the {@code logout} request to the server. Clears the auth token in the client.
     *
     * @param request the request.
     * @return the {@code logout} response.
     */
    LogoutResponse logout(LogoutRequest request);

    /**
     * Sends the {@code logout} request to the server. See {@link #logout(LogoutRequest)}.
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
     * @return the list of available knowledge bases metadata.
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
     * Sends the {@code actions} request to the server. See {@link #getActions(GetActionsRequest)}.
     *
     * @param name the action name or the regular expression or {@code null} to return results for all actions.
     * @param metadataRequired if {@code true} only actions that have arguments and result metadata will be returned.
     * @return the list of available actions metadata..
     */
    List<RestActionMeta> getActions(String name, Boolean metadataRequired);

    /**
     * Sends the {@code actions} request to the server. See {@link #getActions(GetActionsRequest)}.
     *
     * @param name the action name or the regular expression or {@code null} to return results for all actions.
     * @return the list of available actions metadata..
     */
    List<RestActionMeta> getActions(String name);

    /**
     * Sends the {@code actions} request to the server. See {@link #getActions(GetActionsRequest)}.
     *
     * @return the list of available actions metadata..
     */
    List<RestActionMeta> getActions();

    /**
     * Returns the metadata for the specified action. This method may fetch the metadata from the server or use the action metadata cache if
     * configured.
     *
     * @param actionName the action name.
     *
     * @return the action metadata or {@code null} if there is no such action or that action has no metadata.
     */
    RestActionMeta getActionMeta(String actionName);

    /**
     * Returns the metadata for the specified action. This method may fetch the metadata from the server or use the action metadata cache if
     * configured.
     *
     * @param actionName the action name.
     * @param allowFetchMetadata if {@code true} (the default value), the action metadata may be fetched from the server.
     *
     * @return the action metadata or {@code null} if there is no such action or that action has no metadata.
     */
    RestActionMeta getActionMeta(String actionName, boolean allowFetchMetadata);

    /**
     * Calls the action. Allows fetching the action metadata. For more information see
     * {@link #call(ActionCallRequest,RestActionMeta,boolean) call}.
     *
     * @param request the request.
     *
     * @return the response.
     */
    ActionCallResponse call(ActionCallRequest request);

    /**
     * Calls the action. Allows fetching the action metadata. For more information see
     * {@link #call(ActionCallRequest,RestActionMeta,boolean) call}.
     *
     * @param request the request.
     * @param actionMeta the action metadata.
     *
     * @return the response.
     */
    ActionCallResponse call(ActionCallRequest request, RestActionMeta actionMeta);

    /**
     * Calls the action. Marshals the arguments and unmarshals the result using a best effort strategy, i.e. when a metadata is defined.
     *
     * @param request the request.
     * @param actionMeta the action metadata that will be used for marshaling and unmarshaling. If the value is {@code null}, this method
     *        may fetch the action metadata from the server if the action metadata cache is turned off or is not populated.
     * @param allowFetchMetadata if {@code true} (the default value), the action metadata (if {@code null}) may be fetched from the server
     *        by sending an additional request. If {@code false} and the action metadata is {@code null} or is not in the cache, the
     *        marshaling of arguments and unmarshaling of the result will be suppressed.
     *
     * @return the response.
     */
    ActionCallResponse call(ActionCallRequest request, RestActionMeta actionMeta, boolean allowFetchMetadata);

    /**
     * Calls the action. For more information see {@link #call(ActionCallRequest,RestActionMeta,boolean) call}.
     *
     * @param actionName the action name.
     * @param args the action arguments.
     *
     * @return the action result.
     */
    Object call(String actionName, Object... args);

    /**
     * Calls the action. For more information see {@link #call(ActionCallRequest,RestActionMeta,boolean) call}.
     *
     * @param resultClass the result class.
     * @param actionName the action name.
     * @param args the action arguments.
     *
     * @return the action result.
     * @param <T> the result type.
     */
    <T> T call(Class<T> resultClass, String actionName, Object... args);

    /**
     * Sends the event to the server.
     *
     * @param request the request.
     * @return the response.
     */
    SendEventResponse send(SendEventRequest request);

    /**
     * Sends the event to the server.
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
     */
    void reload();

    /**
     * Clears the action metadata cache.
     */
    void clearCache();

    /**
     * Closes the client.
     */
    @Override
    void close();
}
