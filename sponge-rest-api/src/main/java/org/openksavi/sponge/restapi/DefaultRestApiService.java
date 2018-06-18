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

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.core.kb.DefaultKnowledgeBase;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.EventDefinition;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.restapi.model.RestActionArgMeta;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.RestActionResultMeta;
import org.openksavi.sponge.restapi.model.RestKnowledgeBaseMeta;
import org.openksavi.sponge.restapi.model.request.RestActionCallRequest;
import org.openksavi.sponge.restapi.model.request.RestGetActionsRequest;
import org.openksavi.sponge.restapi.model.request.RestGetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.RestGetVersionRequest;
import org.openksavi.sponge.restapi.model.request.RestReloadRequest;
import org.openksavi.sponge.restapi.model.request.RestSendEventRequest;
import org.openksavi.sponge.restapi.model.response.BaseRestResponse;
import org.openksavi.sponge.restapi.model.response.RestActionCallResponse;
import org.openksavi.sponge.restapi.model.response.RestGetActionsResponse;
import org.openksavi.sponge.restapi.model.response.RestGetKnowledgeBasesResponse;
import org.openksavi.sponge.restapi.model.response.RestGetVersionResponse;
import org.openksavi.sponge.restapi.model.response.RestReloadResponse;
import org.openksavi.sponge.restapi.model.response.RestSendEventResponse;
import org.openksavi.sponge.restapi.security.RestApiSecurityService;
import org.openksavi.sponge.restapi.security.Role;
import org.openksavi.sponge.restapi.security.User;

/**
 * Default Sponge REST service.
 */
public class DefaultRestApiService implements RestApiService {

    private SpongeEngine engine;

    private RestApiSettings settings;

    private RestApiSecurityService securityService;

    private RestApiErrorResponseProvider errorResponseProvider = new DefaultRestApiErrorResponseProvider();

    public DefaultRestApiService() {
        //
    }

    @Override
    public SpongeEngine getEngine() {
        return engine;
    }

    @Override
    public void setEngine(SpongeEngine engine) {
        this.engine = engine;
    }

    @Override
    public RestApiSettings getSettings() {
        return settings;
    }

    @Override
    public void setSettings(RestApiSettings settings) {
        this.settings = settings;
    }

    @Override
    public RestApiSecurityService getSecurityService() {
        return securityService;
    }

    @Override
    public void setSecurityService(RestApiSecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public RestApiErrorResponseProvider getErrorResponseProvider() {
        return errorResponseProvider;
    }

    @Override
    public void setErrorResponseProvider(RestApiErrorResponseProvider errorResponseProvider) {
        this.errorResponseProvider = errorResponseProvider;
    }

    @Override
    public RestActionCallResponse call(RestActionCallRequest request) {
        ActionAdapter actionAdapter = null;

        try {
            Validate.notNull(request, "The request must not be null");

            actionAdapter = engine.getActionManager().getActionAdapter(request.getName());
            Validate.notNull(actionAdapter, "The action %s doesn't exist", request.getName());

            User user = securityService.authenticateUser(request.getUsername(), request.getPassword());
            Validate.isTrue(securityService.canCallAction(user, actionAdapter), "No privileges to call action %s", request.getName());

            return new RestActionCallResponse(request.getName(), engine.getActionManager().callAction(request.getName(),
                    request.getArgs() != null ? request.getArgs().toArray() : null));
        } catch (Exception e) {
            if (actionAdapter != null) {
                engine.handleError(actionAdapter, e);
            } else {
                engine.handleError("REST call", e);
            }

            return setupErrorResponse(new RestActionCallResponse(request.getName()), e);
        }
    }

    protected <T extends BaseRestResponse> T setupErrorResponse(T response, Throwable exception) {
        errorResponseProvider.applyException(response, exception);

        return response;
    }

    protected boolean isEventPublic(String eventName) {
        boolean publicBySettings = settings.getPublicEvents() != null
                ? settings.getPublicEvents().stream().filter(name -> eventName.matches(name)).findAny().isPresent()
                : RestApiConstants.DEFAULT_IS_EVENT_PUBLIC;

        String isEventPlubliActionName = RestApiConstants.ACTION_IS_EVENT_PUBLIC;
        boolean publicByAction = engine.getOperations().existsAction(isEventPlubliActionName)
                ? ((Boolean) engine.getOperations().call(isEventPlubliActionName, eventName)).booleanValue()
                : RestApiConstants.DEFAULT_IS_EVENT_PUBLIC;

        return publicBySettings && publicByAction;
    }

    @Override
    public RestSendEventResponse send(RestSendEventRequest request) {
        try {
            Validate.notNull(request, "The request must not be null");

            User user = securityService.authenticateUser(request.getUsername(), request.getPassword());
            Validate.isTrue(securityService.canSendEvent(user, request.getName()), "No privileges to send the event %s", request.getName());
            Validate.isTrue(isEventPublic(request.getName()), "There is no public event named '%s'", request.getName());

            EventDefinition definition = engine.getOperations().event(request.getName());
            if (request.getAttributes() != null) {
                request.getAttributes().forEach((name, value) -> definition.set(name, value));
            }

            return new RestSendEventResponse(definition.send().getId());
        } catch (Exception e) {
            engine.handleError("REST send", e);
            return setupErrorResponse(new RestSendEventResponse(), e);
        }
    }

    protected RestKnowledgeBaseMeta createRestKnowledgeBase(KnowledgeBase kb) {
        return new RestKnowledgeBaseMeta(kb.getName(), kb.getDisplayName(), kb.getDescription());
    }

    @Override
    public RestGetKnowledgeBasesResponse getKnowledgeBases(RestGetKnowledgeBasesRequest request) {
        if (request == null) {
            request = new RestGetKnowledgeBasesRequest();
        }

        try {
            User user = securityService.authenticateUser(request.getUsername(), request.getPassword());

            return new RestGetKnowledgeBasesResponse(engine.getKnowledgeBaseManager().getKnowledgeBases().stream()
                    .filter(kb -> securityService.canUseKnowledgeBase(user, kb))
                    .filter(kb -> !kb.getName().equals(DefaultKnowledgeBase.NAME)).map(kb -> createRestKnowledgeBase(kb))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            engine.handleError("REST getKnowledgeBases", e);
            return setupErrorResponse(new RestGetKnowledgeBasesResponse(), e);
        }
    }

    @Override
    public RestGetActionsResponse getActions(RestGetActionsRequest request) {
        try {
            if (request == null) {
                request = new RestGetActionsRequest();
            }

            User user = securityService.authenticateUser(request.getUsername(), request.getPassword());

            boolean actualMetadataRequired = request.getMetadataRequired() != null ? request.getMetadataRequired()
                    : RestApiConstants.REST_PARAM_ACTIONS_METADATA_REQUIRED_DEFAULT;

            String isPublicActionActionName = RestApiConstants.ACTION_IS_ACTION_PUBLIC;
            Predicate<ActionAdapter> isPublicByAction = action -> engine.getOperations().existsAction(isPublicActionActionName)
                    ? ((Boolean) engine.getOperations().call(isPublicActionActionName, action)).booleanValue()
                    : RestApiConstants.DEFAULT_IS_ACTION_PUBLIC;

            Predicate<ActionAdapter> isPublicBySettings = action -> settings.getPublicActions() != null
                    ? settings.getPublicActions().stream()
                            .filter(qn -> action.getKnowledgeBase().getName().matches(qn.getKnowledgeBaseName())
                                    && action.getName().matches(qn.getName()))
                            .findAny().isPresent()
                    : RestApiConstants.DEFAULT_IS_ACTION_PUBLIC;

            return new RestGetActionsResponse(engine.getActions().stream().filter(isPublicByAction).filter(isPublicBySettings)
                    .filter(action -> actualMetadataRequired ? action.getArgsMeta() != null && action.getResultMeta() != null : true)
                    .filter(action -> !action.getKnowledgeBase().getName().equals(DefaultKnowledgeBase.NAME))
                    .filter(action -> securityService.canCallAction(user, action))
                    .map(action -> new RestActionMeta(action.getName(), action.getDisplayName(), action.getDescription(),
                            createRestKnowledgeBase(action.getKnowledgeBase()), action.getMeta(), createActionArgMetaList(action),
                            createActionResultMeta(action)))
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            engine.handleError("REST getActions", e);
            return setupErrorResponse(new RestGetActionsResponse(), e);
        }
    }

    @Override
    public RestGetVersionResponse getVersion(RestGetVersionRequest request) {
        try {
            // Privileges not checked here.

            return new RestGetVersionResponse(engine.getVersion());
        } catch (Exception e) {
            engine.handleError("REST getVersion", e);
            return setupErrorResponse(new RestGetVersionResponse(), e);
        }
    }

    protected List<RestActionArgMeta> createActionArgMetaList(ActionAdapter actionAdapter) {
        return actionAdapter.getArgsMeta() != null
                ? Arrays.stream(actionAdapter.getArgsMeta()).map(meta -> new RestActionArgMeta(meta.getName(), meta.getType().getCode(),
                        meta.getSubtype(), meta.isRequired(), meta.getDisplayName(), meta.getDescription())).collect(Collectors.toList())
                : null;
    }

    protected RestActionResultMeta createActionResultMeta(ActionAdapter actionAdapter) {
        ResultMeta resultMeta = actionAdapter.getResultMeta();
        return resultMeta != null
                ? new RestActionResultMeta(resultMeta.getType().getCode(), resultMeta.getSubtype(), resultMeta.getDisplayName()) : null;
    }

    @Override
    public RestReloadResponse reload(RestReloadRequest request) {
        try {
            if (request == null) {
                request = new RestReloadRequest();
            }

            User user = securityService.authenticateUser(request.getUsername(), request.getPassword());

            Validate.isTrue(user.hasRole(Role.ADMIN), "No privileges to reload Sponge knowledge bases");

            engine.reload();

            return new RestReloadResponse();
        } catch (Exception e) {
            engine.handleError("REST reload", e);
            return setupErrorResponse(new RestReloadResponse(), e);
        }
    }
}
