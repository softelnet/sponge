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

import org.apache.camel.Exchange;

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
import org.openksavi.sponge.restapi.server.security.RestApiAuthTokenService;
import org.openksavi.sponge.restapi.server.security.RestApiSecurityService;
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

    LoginResponse login(LoginRequest request, Exchange exchange);

    LogoutResponse logout(LogoutRequest request, Exchange exchange);

    ActionCallResponse call(ActionCallRequest request, Exchange exchange);

    SendEventResponse send(SendEventRequest request, Exchange exchange);

    GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request, Exchange exchange);

    GetActionsResponse getActions(GetActionsRequest request, Exchange exchange);

    GetVersionResponse getVersion(GetVersionRequest request, Exchange exchange);

    ReloadResponse reload(ReloadRequest request, Exchange exchange);
}