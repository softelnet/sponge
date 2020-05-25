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

package org.openksavi.sponge.remoteapi.server;

import java.util.Comparator;
import java.util.Map;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.remoteapi.model.RemoteActionMeta;
import org.openksavi.sponge.remoteapi.model.request.ActionCallRequest;
import org.openksavi.sponge.remoteapi.model.request.GetActionsRequest;
import org.openksavi.sponge.remoteapi.model.request.GetEventTypesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetFeaturesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetVersionRequest;
import org.openksavi.sponge.remoteapi.model.request.IsActionActiveRequest;
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
import org.openksavi.sponge.remoteapi.model.response.SendEventResponse;
import org.openksavi.sponge.remoteapi.model.response.SpongeResponse;
import org.openksavi.sponge.remoteapi.server.listener.OnSessionCloseListener;
import org.openksavi.sponge.remoteapi.server.listener.OnSessionOpenListener;
import org.openksavi.sponge.remoteapi.server.security.AccessService;
import org.openksavi.sponge.remoteapi.server.security.AuthTokenService;
import org.openksavi.sponge.remoteapi.server.security.RequestAuthenticationService;
import org.openksavi.sponge.remoteapi.server.security.SecurityService;
import org.openksavi.sponge.remoteapi.server.security.UserContext;
import org.openksavi.sponge.remoteapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.util.HasEngine;
import org.openksavi.sponge.util.Initializable;

/**
 * Sponge Remote API service.
 */
@SuppressWarnings("rawtypes")
public interface RemoteApiService extends HasEngine, Initializable {

    RemoteApiSettings getSettings();

    void setSettings(RemoteApiSettings settings);

    String getApiVersion();

    SecurityService getSecurityService();

    void setSecurityService(SecurityService securityService);

    RequestAuthenticationService getRequestAuthenticationService();

    void setRequestAuthenticationService(RequestAuthenticationService requestAuthenticationService);

    AccessService getAccessService();

    void setAccessService(AccessService accessService);

    AuthTokenService getAuthTokenService();

    void setAuthTokenService(AuthTokenService authTokenService);

    ErrorResponseProvider getErrorResponseProvider();

    void setErrorResponseProvider(ErrorResponseProvider errorResponseProvider);

    LoginResponse login(LoginRequest request);

    LogoutResponse logout(LogoutRequest request);

    ActionCallResponse call(ActionCallRequest request);

    SendEventResponse send(SendEventRequest request);

    IsActionActiveResponse isActionActive(IsActionActiveRequest request);

    ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request);

    GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request);

    GetActionsResponse getActions(GetActionsRequest request);

    GetVersionResponse getVersion(GetVersionRequest request);

    GetFeaturesResponse getFeatures(GetFeaturesRequest request);

    GetEventTypesResponse getEventTypes(GetEventTypesRequest request);

    ReloadResponse reload(ReloadRequest request);

    Map<String, Object> getFeatures();

    SpongeResponse createErrorResponse(Throwable e);

    TypeConverter getTypeConverter();

    Comparator<RemoteActionMeta> getActionsOrderComparator();

    void setActionsOrderComparator(Comparator<RemoteActionMeta> actionsOrderComparator);

    /**
     * Returns a thread local session.
     *
     * @return the session.
     */
    RemoteApiSession getSession();

    /**
     * Opens the thread local session.
     *
     * @param session the session.
     */
    void openSession(RemoteApiSession session);

    /**
     * Closes the thread local session.
     */
    void closeSession();

    DataType marshalDataType(DataType type);

    RemoteActionMeta marshalActionMeta(RemoteActionMeta actionMeta);

    UserContext authenticateRequest(SpongeRequest request);

    /**
     * Sends a new event. The attributes and features maps should be unmarshalled first.
     *
     * @param eventName the event name.
     * @param attributes the event attributes map.
     * @param label the event label.
     * @param description the event description.
     * @param features the event the event description..
     * @param userContext the user context.
     * @return the sent event.
     */
    Event sendEvent(String eventName, Map<String, Object> attributes, String label, String description, Map<String, Object> features,
            UserContext userContext);

    /**
     * Sends a new event. The attributes map should be unmarshalled first.
     *
     * @param eventName the event name.
     * @param attributes the event attributes map.
     * @param label the event label.
     * @param description the event description.
     * @param userContext the user context.
     * @return the sent event.
     */
    Event sendEvent(String eventName, Map<String, Object> attributes, String label, String description, UserContext userContext);

    /**
     * Sends a new event. The attributes map should be unmarshalled first.
     *
     * @param eventName the event name.
     * @param attributes the event attributes map.
     * @param label the event label.
     * @param userContext the user context.
     * @return the sent event.
     */
    Event sendEvent(String eventName, Map<String, Object> attributes, String label, UserContext userContext);

    /**
     * Sends a new event. The attributes map should be unmarshalled first.
     *
     * @param eventName the event name.
     * @param attributes the event attributes map.
     * @param userContext the user context.
     * @return the sent event.
     */
    Event sendEvent(String eventName, Map<String, Object> attributes, UserContext userContext);

    boolean canCallAction(UserContext userContext, ActionAdapter actionAdapter);

    boolean canSendEvent(UserContext userContext, String eventName);

    boolean canSubscribeEvent(UserContext userContext, String eventName);

    void setFeature(String name, Object value);

    /**
     * Returns the API feature. Throws exception if not found.
     *
     * @param name the feature name.
     * @return the feature value.
     * @param <T> feature.
     */
    <T> T getFeature(String name);

    /**
     * Returns the API feature. Throws exception if not found.
     *
     * @param cls the feature class.
     * @param name the feature name.
     *
     * @return the feature value.
     * @param <T> feature.
     */
    <T> T getFeature(Class<T> cls, String name);

    /**
     * Returns the API feature or {@code defaultValue} if not found.
     *
     * @param name the feature name.
     * @param defaultValue the default value.
     *
     * @return the feature value.
     * @param <T> feature.
     */
    <T> T getFeature(String name, T defaultValue);

    /**
     * Returns the API feature or {@code defaultValue} if not found.
     *
     * @param cls the feature class.
     * @param name the feature name.
     * @param defaultValue default value.
     *
     * @return the feature value.
     * @param <T> feature.
     */
    <T> T getFeature(Class<T> cls, String name, T defaultValue);

    /**
     * Sets the on session open listener. The listener is invoked after a request is authenticated.
     *
     * @param onSessionOpenListener the listener.
     */
    void setOnSessionOpenListener(OnSessionOpenListener onSessionOpenListener);

    /**
     * Sets the on session close listener. The listener is invoked before closiong the session.
     *
     * @param onSessionCloseListener the listener.
     */
    void setOnSessionCloseListener(OnSessionCloseListener onSessionCloseListener);
}
