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

package org.openksavi.sponge.remoteapi.client;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

import org.openksavi.sponge.action.ProvideArgsParameters;
import org.openksavi.sponge.remoteapi.client.listener.OnRequestSerializedListener;
import org.openksavi.sponge.remoteapi.client.listener.OnResponseDeserializedListener;
import org.openksavi.sponge.remoteapi.feature.converter.FeatureConverter;
import org.openksavi.sponge.remoteapi.model.RemoteActionMeta;
import org.openksavi.sponge.remoteapi.model.RemoteKnowledgeBaseMeta;
import org.openksavi.sponge.remoteapi.model.request.ActionCallNamedRequest;
import org.openksavi.sponge.remoteapi.model.request.ActionCallRequest;
import org.openksavi.sponge.remoteapi.model.request.GetActionsRequest;
import org.openksavi.sponge.remoteapi.model.request.GetEventTypesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetFeaturesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetVersionRequest;
import org.openksavi.sponge.remoteapi.model.request.IsActionActiveRequest;
import org.openksavi.sponge.remoteapi.model.request.IsActionActiveRequest.IsActionActiveEntry;
import org.openksavi.sponge.remoteapi.model.request.LoginRequest;
import org.openksavi.sponge.remoteapi.model.request.LogoutRequest;
import org.openksavi.sponge.remoteapi.model.request.ProvideActionArgsRequest;
import org.openksavi.sponge.remoteapi.model.request.ReloadRequest;
import org.openksavi.sponge.remoteapi.model.request.SendEventRequest;
import org.openksavi.sponge.remoteapi.model.request.SpongeRequest;
import org.openksavi.sponge.remoteapi.model.response.ActionCallResponse;
import org.openksavi.sponge.remoteapi.model.response.GetActionsResponse;
import org.openksavi.sponge.remoteapi.model.response.GetEventTypesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetFeaturesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetKnowledgeBasesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetVersionResponse;
import org.openksavi.sponge.remoteapi.model.response.IsActionActiveResponse;
import org.openksavi.sponge.remoteapi.model.response.LoginResponse;
import org.openksavi.sponge.remoteapi.model.response.LogoutResponse;
import org.openksavi.sponge.remoteapi.model.response.ProvideActionArgsResponse;
import org.openksavi.sponge.remoteapi.model.response.ReloadResponse;
import org.openksavi.sponge.remoteapi.model.response.ResponseError;
import org.openksavi.sponge.remoteapi.model.response.SendEventResponse;
import org.openksavi.sponge.remoteapi.model.response.SpongeResponse;
import org.openksavi.sponge.remoteapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.provided.ProvidedValue;

/**
 * A Sponge Remote API client.
 */
@SuppressWarnings("rawtypes")
public interface SpongeClient extends Closeable {

    /**
     * Returns the client configuration.
     *
     * @return the client configuration.
     */
    SpongeClientConfiguration getConfiguration();

    void addOnRequestSerializedListener(OnRequestSerializedListener listener);

    boolean removeOnRequestSerializedListener(OnRequestSerializedListener listener);

    void addOnResponseDeserializedListener(OnResponseDeserializedListener listener);

    boolean removeOnResponseDeserializedListener(OnResponseDeserializedListener listener);

    /**
     * Returns the Sponge API features by sending the {@code features} request to the server and returning the features or using the cache.
     *
     * @return the API features.
     */
    Map<String, Object> getFeatures();

    /**
     * Sends the {@code features} request to the server.
     *
     * @param request the request.
     * @param context the context.
     * @return the {@code features} response.
     */
    GetFeaturesResponse getFeatures(GetFeaturesRequest request, SpongeRequestContext context);

    /**
     * Sends the {@code features} request to the server.
     *
     * @param request the request.
     * @return the {@code features} response.
     */
    GetFeaturesResponse getFeatures(GetFeaturesRequest request);

    /**
     * Sends the {@code version} request to the server.
     *
     * @param request the request.
     * @param context the context.
     * @return the {@code version} response.
     */
    GetVersionResponse getVersion(GetVersionRequest request, SpongeRequestContext context);

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
     * @param context the context.
     * @return the {@code login} response.
     */
    LoginResponse login(LoginRequest request, SpongeRequestContext context);

    /**
     * Sends the {@code login} request to the server. See {@link #login(LoginRequest,SpongeRequestContext)}.
     *
     * @param request the request.
     * @return the {@code login} response.
     */
    LoginResponse login(LoginRequest request);

    /**
     * Sends the {@code login} request to the server. See {@link #login(LoginRequest,SpongeRequestContext)}.
     *
     * @return the optional auth token.
     */
    String login();

    /**
     * Sends the {@code logout} request to the server. Clears the auth token in the client.
     *
     * @param request the request.
     * @param context the context.
     * @return the {@code logout} response.
     */
    LogoutResponse logout(LogoutRequest request, SpongeRequestContext context);

    /**
     * Sends the {@code logout} request to the server. See {@link #logout(LogoutRequest,SpongeRequestContext)}.
     *
     * @param request the request.
     * @return the {@code logout} response.
     */
    LogoutResponse logout(LogoutRequest request);

    /**
     * Sends the {@code logout} request to the server. See {@link #logout(LogoutRequest,SpongeRequestContext)}.
     */
    void logout();

    /**
     * Sends the {@code knowledgeBases} request to the server.
     *
     * @param request the request.
     * @param context the context.
     * @return the {@code knowledgeBases} response.
     */
    GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request, SpongeRequestContext context);

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
    List<RemoteKnowledgeBaseMeta> getKnowledgeBases();

    /**
     * Sends the {@code actions} request to the server. This method may populate the action metadata cache.
     *
     * @param request the request.
     * @param context the context.
     * @return the {@code actions} response.
     */
    GetActionsResponse getActions(GetActionsRequest request, SpongeRequestContext context);

    /**
     * Sends the {@code actions} request to the server. See {@link #getActions(GetActionsRequest,SpongeRequestContext)}.
     *
     * @param request the request.
     * @return the {@code actions} response.
     */
    GetActionsResponse getActions(GetActionsRequest request);

    /**
     * Sends the {@code actions} request to the server. See {@link #getActions(GetActionsRequest,SpongeRequestContext)}.
     *
     * @param name the action name or the regular expression or {@code null} to return results for all actions.
     * @param metadataRequired if {@code true} only actions that have arguments and result metadata will be returned.
     * @return the list of available actions metadata..
     */
    List<RemoteActionMeta> getActions(String name, Boolean metadataRequired);

    /**
     * Sends the {@code actions} request to the server. See {@link #getActions(GetActionsRequest,SpongeRequestContext)}.
     *
     * @param name the action name or the regular expression or {@code null} to return results for all actions.
     * @return the list of available actions metadata..
     */
    List<RemoteActionMeta> getActions(String name);

    /**
     * Sends the {@code actions} request to the server. See {@link #getActions(GetActionsRequest,SpongeRequestContext)}.
     *
     * @return the list of available actions metadata.
     */
    List<RemoteActionMeta> getActions();

    /**
     * Returns the metadata for the specified action. This method may fetch the metadata from the server or use the action metadata cache if
     * configured.
     *
     * @param actionName the action name.
     * @param allowFetchMetadata if {@code true} (the default value), the action metadata may be fetched from the server.
     * @param context the context.
     *
     * @return the action metadata or {@code null} if there is no such action or that action has no metadata.
     */
    RemoteActionMeta getActionMeta(String actionName, boolean allowFetchMetadata, SpongeRequestContext context);

    /**
     * Returns the metadata for the specified action. See {@link #getActionMeta(String,boolean,SpongeRequestContext)}.
     *
     * @param actionName the action name.
     * @param allowFetchMetadata if {@code true} (the default value), the action metadata may be fetched from the server.
     *
     * @return the action metadata or {@code null} if there is no such action or that action has no metadata.
     */
    RemoteActionMeta getActionMeta(String actionName, boolean allowFetchMetadata);

    /**
     * Returns the metadata for the specified action. See {@link #getActionMeta(String,boolean,SpongeRequestContext)}.
     *
     * @param actionName the action name.
     *
     * @return the action metadata or {@code null} if there is no such action or that action has no metadata.
     */
    RemoteActionMeta getActionMeta(String actionName);

    /**
     * Validates the action call arguments. This method is invoked internally by the {@code call} methods. Throws exception on validation
     * failure.
     *
     * @param actionMeta the action metadata.
     * @param args the action arguments.
     */
    void validateCallArgs(RemoteActionMeta actionMeta, List<Object> args);

    /**
     * Validates the action call arguments. This method is invoked internally by the {@code call} methods. Throws exception on validation
     * failure.
     *
     * @param actionMeta the action metadata.
     * @param args the named action arguments.
     */
    void validateCallArgs(RemoteActionMeta actionMeta, Map<String, ?> args);

    /**
     * Calls the action. Marshals the arguments and unmarshals the result using a best effort strategy, i.e. when a metadata is defined.
     *
     * @param request the request.
     * @param actionMeta the action metadata that will be used for marshalling and unmarshalling. If the value is {@code null}, this method
     *        may fetch the action metadata from the server if the action metadata cache is turned off or is not populated.
     * @param allowFetchMetadata if {@code true} (the default value), the action metadata (if {@code null}) may be fetched from the server
     *        by sending an additional request. If {@code false} and the action metadata is {@code null} or is not in the cache, the
     *        marshalling of arguments and unmarshalling of the result will be suppressed.
     * @param context the context.
     *
     * @return the response.
     */
    ActionCallResponse call(ActionCallRequest request, RemoteActionMeta actionMeta, boolean allowFetchMetadata,
            SpongeRequestContext context);

    /**
     * Calls the action. For more information see {@link #call(ActionCallRequest,RemoteActionMeta,boolean,SpongeRequestContext) call}.
     *
     * @param request the request.
     * @param actionMeta the action metadata.
     * @param allowFetchMetadata the flag for fetching the metadata.
     *
     * @return the response.
     */
    ActionCallResponse call(ActionCallRequest request, RemoteActionMeta actionMeta, boolean allowFetchMetadata);

    /**
     * Calls the action. Allows fetching the action metadata. For more information see
     * {@link #call(ActionCallRequest,RemoteActionMeta,boolean,SpongeRequestContext) call}.
     *
     * @param request the request.
     * @param actionMeta the action metadata.
     *
     * @return the response.
     */
    ActionCallResponse call(ActionCallRequest request, RemoteActionMeta actionMeta);

    /**
     * Calls the action. Allows fetching the action metadata. For more information see
     * {@link #call(ActionCallRequest,RemoteActionMeta,boolean,SpongeRequestContext) call}.
     *
     * @param request the request.
     *
     * @return the response.
     */
    ActionCallResponse call(ActionCallRequest request);

    /**
     * Calls the action. For more information see {@link #call(ActionCallRequest,RemoteActionMeta,boolean,SpongeRequestContext) call}.
     *
     * @param actionName the action name.
     * @param args the action arguments.
     *
     * @return the action result.
     */
    Object call(String actionName, List<Object> args);

    /**
     * Calls the action. For more information see {@link #call(ActionCallRequest,RemoteActionMeta,boolean,SpongeRequestContext) call}.
     *
     * @param resultClass the result class.
     * @param actionName the action name.
     * @param args the action arguments.
     *
     * @return the action result.
     * @param <T> the result type.
     */
    <T> T call(Class<T> resultClass, String actionName, List<Object> args);

    /**
     * Calls the action. For more information see {@link #call(ActionCallRequest,RemoteActionMeta,boolean,SpongeRequestContext) call}.
     *
     * @param actionName the action name.
     *
     * @return the action result.
     */
    Object call(String actionName);

    /**
     * Calls the action. For more information see {@link #call(ActionCallRequest,RemoteActionMeta,boolean,SpongeRequestContext) call}.
     *
     * @param resultClass the result class.
     * @param actionName the action name.
     *
     * @return the action result.
     * @param <T> the result type.
     */
    <T> T call(Class<T> resultClass, String actionName);

    /**
     * Calls the action with named arguments. Marshals the arguments and unmarshals the result using a best effort strategy, i.e. when a
     * metadata is defined.
     *
     * @param request the request.
     * @param actionMeta the action metadata that will be used for marshalling and unmarshalling. If the value is {@code null}, this method
     *        may fetch the action metadata from the server if the action metadata cache is turned off or is not populated.
     * @param allowFetchMetadata if {@code true} (the default value), the action metadata (if {@code null}) may be fetched from the server
     *        by sending an additional request. If {@code false} and the action metadata is {@code null} or is not in the cache, the
     *        marshalling of arguments and unmarshalling of the result will be suppressed.
     * @param context the context.
     *
     * @return the response.
     */
    ActionCallResponse callNamed(ActionCallNamedRequest request, RemoteActionMeta actionMeta, boolean allowFetchMetadata,
            SpongeRequestContext context);

    /**
     * Calls the action with named arguments.
     *
     * @param request the request.
     * @param actionMeta the action metadata.
     * @param allowFetchMetadata the flag for fetching the metadata.
     *
     * @return the response.
     */
    ActionCallResponse callNamed(ActionCallNamedRequest request, RemoteActionMeta actionMeta, boolean allowFetchMetadata);

    /**
     * Calls the action with named arguments. Allows fetching the action metadata.
     *
     * @param request the request.
     * @param actionMeta the action metadata.
     *
     * @return the response.
     */
    ActionCallResponse callNamed(ActionCallNamedRequest request, RemoteActionMeta actionMeta);

    /**
     * Calls the action with named arguments. Allows fetching the action metadata.
     *
     * @param request the request.
     *
     * @return the response.
     */
    ActionCallResponse callNamed(ActionCallNamedRequest request);

    /**
     * Calls the action with named arguments.
     *
     * @param actionName the action name.
     * @param args the action arguments.
     *
     * @return the action result.
     */
    Object callNamed(String actionName, Map<String, ?> args);

    /**
     * Calls the action with named arguments.
     *
     * @param resultClass the result class.
     * @param actionName the action name.
     * @param args the action arguments.
     *
     * @return the action result.
     * @param <T> the result type.
     */
    <T> T callNamed(Class<T> resultClass, String actionName, Map<String, ?> args);

    /**
     * Sends the event to the server.
     *
     * @param request the request.
     * @param context the context.
     * @return the response.
     */
    SendEventResponse send(SendEventRequest request, SpongeRequestContext context);

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
     * Sends the event to the server.
     *
     * @param eventName the event name.
     * @param attributes the event attributes.
     * @param label the event label.
     * @return the event ID.
     */
    String send(String eventName, Map<String, Object> attributes, String label);

    /**
     * Sends the event to the server.
     *
     * @param eventName the event name.
     * @param attributes the event attributes.
     * @param label the event label.
     * @param description the event description.
     * @return the event ID.
     */
    String send(String eventName, Map<String, Object> attributes, String label, String description);

    /**
     * Sends the event to the server.
     *
     * @param eventName the event name.
     * @param attributes the event attributes.
     * @param label the event label.
     * @param description the event description.
     * @param features the event features.
     * @return the event ID.
     */
    String send(String eventName, Map<String, Object> attributes, String label, String description, Map<String, Object> features);

    /**
     * Sends the {@code isActionActive} request to the server.
     *
     * @param request the request.
     * @param context the context.
     * @return the response.
     */
    IsActionActiveResponse isActionActive(IsActionActiveRequest request, SpongeRequestContext context);

    /**
     * Sends the {@code isActionActive} request to the server.
     *
     * @param request the request.
     * @return the response.
     */
    IsActionActiveResponse isActionActive(IsActionActiveRequest request);

    /**
     * Fetches active/inactive statuses for actions specified in the entries. If none of the actions is activatable (based on the cached
     * metadata), returns positive activity statuses without connecting the server.
     *
     * @param entries the reaction entries.
     * @return the list of flags.
     */
    List<Boolean> isActionActive(List<IsActionActiveEntry> entries);

    /**
     * Sends the {@code provideActionArgs} request to the server. Fetches the provided action arguments from the server.
     *
     * @param request the request.
     * @param context the context.
     * @return the response.
     */
    ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request, SpongeRequestContext context);

    /**
     * Sends the {@code provideActionArgs} request to the server. Fetches the provided action arguments from the server.
     *
     * @param request the request.
     * @return the response.
     */
    ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request);

    /**
     * Submits action arguments to the server and/or fetches action arguments from the server.
     *
     * @param actionName the action name.
     * @param parameters the parameters.
     * @return the provided action arguments.
     */
    Map<String, ProvidedValue<?>> provideActionArgs(String actionName, ProvideArgsParameters parameters);

    /**
     * Sends the {@code eventTypes} request to the server.
     *
     * @param request the request.
     * @param context the context.
     * @return the response.
     */
    GetEventTypesResponse getEventTypes(GetEventTypesRequest request, SpongeRequestContext context);

    /**
     * Sends the {@code eventTypes} request to the server.
     *
     * @param request the request.
     * @return the response.
     */
    GetEventTypesResponse getEventTypes(GetEventTypesRequest request);

    /**
     * Returns the registered event types mathing the event name pattern.
     *
     * @param eventName the event name pattern or {@code null} to get event types.
     * @return the event types map.
     */
    Map<String, RecordType> getEventTypes(String eventName);

    /**
     * Returns the registered event types.
     *
     * @return the event types map.
     */
    default Map<String, RecordType> getEventTypes() {
        return getEventTypes((String) null);
    }

    /**
     * Returns the event type for the specified event type name.
     *
     * @param eventTypeName the event type name.
     * @param allowFetchEventType the flag that allows fetching the event type from the server.
     * @param context the context as the record type.
     * @return the event type or {@code null}.
     */
    RecordType getEventType(String eventTypeName, boolean allowFetchEventType, SpongeRequestContext context);

    /**
     * Returns the event type for the specified event type name.
     *
     * @param eventTypeName the event type name.
     * @param allowFetchEventType the flag that allows fetching the event type from the server.
     * @return the event type or {@code null}.
     */
    RecordType getEventType(String eventTypeName, boolean allowFetchEventType);

    /**
     * Returns the event type for the specified event type name. Fetches from the server if not cached.
     *
     * @param eventTypeName the event type name.
     * @return the event type or {@code null}.
     */
    RecordType getEventType(String eventTypeName);

    /**
     * Sends the {@code reload} request to the server.
     *
     * @param request the request.
     * @param context the context.
     * @return the {@code reload} response.
     */
    ReloadResponse reload(ReloadRequest request, SpongeRequestContext context);

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
     * Sends the request to the server.
     *
     * @param operationType the operationType.
     * @param request the request.
     * @param responseClass the response class.
     * @param context the context.
     * @return the response.
     * @param <T> a request type.
     * @param <R> a response type.
     */
    <T extends SpongeRequest, R extends SpongeResponse> R execute(String operationType, T request, Class<R> responseClass,
            SpongeRequestContext context);

    /**
     * Sends the request to the server.
     *
     * @param operationType the operationType.
     * @param request the request.
     * @param responseClass the response class.
     * @return the response.
     * @param <T> a request type.
     * @param <R> a response type.
     */
    <T extends SpongeRequest, R extends SpongeResponse> R execute(String operationType, T request, Class<R> responseClass);

    /**
     * Clears caches.
     */
    void clearCache();

    /**
     * Clears the action metadata cache.
     */
    void clearActionMetaCache();

    /**
     * Clears the event type cache.
     */
    void clearEventTypeCache();

    /**
     * Clears the API features cache.
     */
    void clearFeaturesCache();

    /**
     * Closes the client.
     */
    @Override
    void close();

    /**
     * Clears the session, i.e. the auth token.
     */
    void clearSession();

    Long getCurrentRequestId();

    String getCurrentAuthToken();

    TypeConverter getTypeConverter();

    void setTypeConverter(TypeConverter typeConverter);

    FeatureConverter getFeatureConverter();

    void setFeatureConverter(FeatureConverter typeConverter);

    <T extends SpongeRequest> T setupRequest(T request);

    void handleErrorResponse(String operation, ResponseError error);

    <T, X> X executeWithAuthentication(T request, String requestUsername, String requestPassword, String requestAuthToken,
            Function<T, X> onExecute, Supplier<T> onClearAuthToken);
}
