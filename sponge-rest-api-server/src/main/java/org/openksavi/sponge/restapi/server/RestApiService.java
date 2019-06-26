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

package org.openksavi.sponge.restapi.server;

import java.util.Comparator;
import java.util.Map;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.GetActionsRequest;
import org.openksavi.sponge.restapi.model.request.GetEventTypesRequest;
import org.openksavi.sponge.restapi.model.request.GetFeaturesRequest;
import org.openksavi.sponge.restapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.request.LoginRequest;
import org.openksavi.sponge.restapi.model.request.LogoutRequest;
import org.openksavi.sponge.restapi.model.request.ProvideActionArgsRequest;
import org.openksavi.sponge.restapi.model.request.ReloadRequest;
import org.openksavi.sponge.restapi.model.request.SendEventRequest;
import org.openksavi.sponge.restapi.model.request.SpongeRequest;
import org.openksavi.sponge.restapi.model.response.ActionCallResponse;
import org.openksavi.sponge.restapi.model.response.GetActionsResponse;
import org.openksavi.sponge.restapi.model.response.GetEventTypesResponse;
import org.openksavi.sponge.restapi.model.response.GetFeaturesResponse;
import org.openksavi.sponge.restapi.model.response.GetKnowledgeBasesResponse;
import org.openksavi.sponge.restapi.model.response.GetVersionResponse;
import org.openksavi.sponge.restapi.model.response.LoginResponse;
import org.openksavi.sponge.restapi.model.response.LogoutResponse;
import org.openksavi.sponge.restapi.model.response.ProvideActionArgsResponse;
import org.openksavi.sponge.restapi.model.response.ReloadResponse;
import org.openksavi.sponge.restapi.model.response.SendEventResponse;
import org.openksavi.sponge.restapi.model.response.SpongeResponse;
import org.openksavi.sponge.restapi.server.security.RestApiAuthTokenService;
import org.openksavi.sponge.restapi.server.security.RestApiSecurityService;
import org.openksavi.sponge.restapi.server.security.UserContext;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.util.HasEngine;
import org.openksavi.sponge.util.Initializable;

/**
 * Sponge REST API service.
 */
public interface RestApiService extends HasEngine, Initializable {

    RestApiSettings getSettings();

    void setSettings(RestApiSettings settings);

    RestApiSecurityService getSecurityService();

    void setSecurityService(RestApiSecurityService securityService);

    RestApiAuthTokenService getAuthTokenService();

    void setAuthTokenService(RestApiAuthTokenService authTokenService);

    RestApiErrorResponseProvider getErrorResponseProvider();

    void setErrorResponseProvider(RestApiErrorResponseProvider errorResponseProvider);

    LoginResponse login(LoginRequest request);

    LogoutResponse logout(LogoutRequest request);

    ActionCallResponse call(ActionCallRequest request);

    SendEventResponse send(SendEventRequest request);

    ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request);

    GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request);

    GetActionsResponse getActions(GetActionsRequest request);

    GetVersionResponse getVersion(GetVersionRequest request);

    GetFeaturesResponse getFeatures(GetFeaturesRequest request);

    GetEventTypesResponse getEventTypes(GetEventTypesRequest request);

    ReloadResponse reload(ReloadRequest request);

    SpongeResponse createGenericErrorResponse(Throwable e);

    TypeConverter getTypeConverter();

    Comparator<RestActionMeta> getActionsOrderComparator();

    void setActionsOrderComparator(Comparator<RestActionMeta> actionsOrderComparator);

    /**
     * Returns a thread local session.
     *
     * @return the session.
     */
    RestApiSession getSession();

    /**
     * Opens the thread local session.
     *
     * @param session the session.
     */
    void openSession(RestApiSession session);

    /**
     * Closes the thread local session.
     */
    void closeSession();

    @SuppressWarnings("rawtypes")
    DataType marshalDataType(DataType type);

    RestActionMeta marshalActionMeta(RestActionMeta actionMeta);

    UserContext authenticateRequest(SpongeRequest request);

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

    Map<String, Object> getFeatures();

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
}
