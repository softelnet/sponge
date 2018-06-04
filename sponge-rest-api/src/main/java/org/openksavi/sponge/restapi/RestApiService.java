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

package org.openksavi.sponge.restapi;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.model.request.RestActionCallRequest;
import org.openksavi.sponge.restapi.model.request.RestGetActionsRequest;
import org.openksavi.sponge.restapi.model.request.RestGetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.RestGetVersionRequest;
import org.openksavi.sponge.restapi.model.request.RestReloadRequest;
import org.openksavi.sponge.restapi.model.request.RestSendEventRequest;
import org.openksavi.sponge.restapi.model.response.RestActionCallResponse;
import org.openksavi.sponge.restapi.model.response.RestGetActionsResponse;
import org.openksavi.sponge.restapi.model.response.RestGetKnowledgeBasesResponse;
import org.openksavi.sponge.restapi.model.response.RestGetVersionResponse;
import org.openksavi.sponge.restapi.model.response.RestReloadResponse;
import org.openksavi.sponge.restapi.model.response.RestSendEventResponse;
import org.openksavi.sponge.restapi.security.RestApiSecurityService;

/**
 * Sponge REST service.
 */
public interface RestApiService {

    SpongeEngine getEngine();

    void setEngine(SpongeEngine engine);

    RestApiSettings getSettings();

    void setSettings(RestApiSettings settings);

    RestApiSecurityService getSecurityService();

    void setSecurityService(RestApiSecurityService securityService);

    RestApiErrorResponseProvider getErrorResponseProvider();

    void setErrorResponseProvider(RestApiErrorResponseProvider errorResponseProvider);

    RestActionCallResponse call(RestActionCallRequest request);

    RestSendEventResponse send(RestSendEventRequest request);

    RestGetKnowledgeBasesResponse getKnowledgeBases(RestGetKnowledgeBasesRequest request);

    RestGetActionsResponse getActions(RestGetActionsRequest request);

    RestGetVersionResponse getVersion(RestGetVersionRequest request);

    RestReloadResponse reload(RestReloadRequest request);
}
