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

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.SpongeException;
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
import org.openksavi.sponge.restapi.model.request.BaseRestRequest;
import org.openksavi.sponge.restapi.model.request.RestActionCallRequest;
import org.openksavi.sponge.restapi.model.request.RestGetActionsRequest;
import org.openksavi.sponge.restapi.model.request.RestGetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.RestGetVersionRequest;
import org.openksavi.sponge.restapi.model.request.RestLoginRequest;
import org.openksavi.sponge.restapi.model.request.RestLogoutRequest;
import org.openksavi.sponge.restapi.model.request.RestReloadRequest;
import org.openksavi.sponge.restapi.model.request.RestSendEventRequest;
import org.openksavi.sponge.restapi.model.response.BaseRestResponse;
import org.openksavi.sponge.restapi.model.response.RestActionCallResponse;
import org.openksavi.sponge.restapi.model.response.RestGetActionsResponse;
import org.openksavi.sponge.restapi.model.response.RestGetKnowledgeBasesResponse;
import org.openksavi.sponge.restapi.model.response.RestGetVersionResponse;
import org.openksavi.sponge.restapi.model.response.RestLoginResponse;
import org.openksavi.sponge.restapi.model.response.RestLogoutResponse;
import org.openksavi.sponge.restapi.model.response.RestReloadResponse;
import org.openksavi.sponge.restapi.model.response.RestSendEventResponse;
import org.openksavi.sponge.restapi.model.util.RestApiUtils;
import org.openksavi.sponge.restapi.security.RestApiSecurityService;
import org.openksavi.sponge.restapi.security.User;

/**
 * Default Sponge REST service.
 */
public class DefaultRestApiService implements RestApiService {

    private SpongeEngine engine;

    private RestApiSettings settings;

    private RestApiSecurityService securityService;

    private RestApiErrorResponseProvider errorResponseProvider = new DefaultRestApiErrorResponseProvider();

    private ObjectMapper mapper = RestApiUtils.createObjectMapper();

    public DefaultRestApiService() {
        //
    }

    @Override
    public RestGetVersionResponse getVersion(RestGetVersionRequest request, Exchange exchange) {
        try {
            // Privileges not checked here.

            return setupSuccessResponse(new RestGetVersionResponse(getEngine().getVersion()), request);
        } catch (Exception e) {
            getEngine().handleError("REST getVersion", e);
            return setupErrorResponse(new RestGetVersionResponse(), request, e);
        }
    }

    @Override
    public RestLoginResponse login(RestLoginRequest request, Exchange exchange) {
        try {
            Validate.notNull(request, "The request must not be null");

            throw new UnsupportedOperationException("A token-based auhentication is not supported yet");

            // User user = authenticateUser(request.getUsername(), request.getPassword(), exchange);

            // TODO Create auth token
            // TODO Token timeout and management

            // return setupSuccessResponse(new RestLoginResponse(), request);
        } catch (Exception e) {
            getEngine().handleError("REST login", e);
            return setupErrorResponse(new RestLoginResponse(), request, e);
        }
    }

    @Override
    public RestLogoutResponse logout(RestLogoutRequest request, Exchange exchange) {
        try {
            Validate.notNull(request, "The request must not be null");

            // TODO Implement.

            throw new RestApiInvalidAuthTokenException("Unknown authentication token");
        } catch (Exception e) {
            getEngine().handleError("REST logout", e);
            return setupErrorResponse(new RestLogoutResponse(), request, e);
        }
    }

    @Override
    public RestGetKnowledgeBasesResponse getKnowledgeBases(RestGetKnowledgeBasesRequest request, Exchange exchange) {
        if (request == null) {
            request = new RestGetKnowledgeBasesRequest();
        }

        try {
            User user = authenticateUser(request, exchange);

            return setupSuccessResponse(new RestGetKnowledgeBasesResponse(getEngine().getKnowledgeBaseManager().getKnowledgeBases().stream()
                    .filter(kb -> securityService.canUseKnowledgeBase(user, kb))
                    .filter(kb -> !kb.getName().equals(DefaultKnowledgeBase.NAME)).map(kb -> createRestKnowledgeBase(kb))
                    .collect(Collectors.toList())), request);
        } catch (Exception e) {
            getEngine().handleError("REST getKnowledgeBases", e);
            return setupErrorResponse(new RestGetKnowledgeBasesResponse(), request, e);
        }
    }

    @Override
    public RestGetActionsResponse getActions(RestGetActionsRequest request, Exchange exchange) {
        try {
            if (request == null) {
                request = new RestGetActionsRequest();
            }

            User user = authenticateUser(request, exchange);

            boolean actualMetadataRequired = request.getMetadataRequired() != null ? request.getMetadataRequired()
                    : RestApiConstants.REST_PARAM_ACTIONS_METADATA_REQUIRED_DEFAULT;
            String actionNameRegExp = request.getNameRegExp();

            String isPublicActionActionName = RestApiConstants.ACTION_IS_ACTION_PUBLIC;
            Predicate<ActionAdapter> isPublicByAction = action -> getEngine().getOperations().existsAction(isPublicActionActionName)
                    ? getEngine().getOperations().call(Boolean.class, isPublicActionActionName, action).booleanValue()
                    : RestApiConstants.DEFAULT_IS_ACTION_PUBLIC;

            Predicate<ActionAdapter> isPublicBySettings = action -> settings.getPublicActions() != null
                    ? settings.getPublicActions().stream()
                            .filter(qn -> action.getKnowledgeBase().getName().matches(qn.getKnowledgeBaseName())
                                    && action.getName().matches(qn.getName()))
                            .findAny().isPresent()
                    : RestApiConstants.DEFAULT_IS_ACTION_PUBLIC;

            Predicate<ActionAdapter> isSelectedByNameRegExp =
                    action -> actionNameRegExp != null ? action.getName().matches(actionNameRegExp) : true;

            return setupSuccessResponse(new RestGetActionsResponse(getEngine().getActions().stream().filter(isSelectedByNameRegExp)
                    .filter(isPublicByAction).filter(isPublicBySettings)
                    .filter(action -> actualMetadataRequired ? action.getArgsMeta() != null && action.getResultMeta() != null : true)
                    .filter(action -> !action.getKnowledgeBase().getName().equals(DefaultKnowledgeBase.NAME))
                    .filter(action -> canCallAction(user, action))
                    .map(action -> new RestActionMeta(action.getName(), action.getDisplayName(), action.getDescription(),
                            createRestKnowledgeBase(action.getKnowledgeBase()), action.getMeta(), createActionArgMetaList(action),
                            createActionResultMeta(action)))
                    .collect(Collectors.toList())), request);
        } catch (Exception e) {
            getEngine().handleError("REST getActions", e);
            return setupErrorResponse(new RestGetActionsResponse(), request, e);
        }
    }

    @Override
    public RestActionCallResponse call(RestActionCallRequest request, Exchange exchange) {
        ActionAdapter actionAdapter = null;

        try {
            Validate.notNull(request, "The request must not be null");

            User user = authenticateUser(request, exchange);

            actionAdapter = getEngine().getActionManager().getActionAdapter(request.getName());
            Validate.notNull(actionAdapter, "The action %s doesn't exist", request.getName());
            Validate.isTrue(canCallAction(user, actionAdapter), "No privileges to call action %s", request.getName());

            return setupSuccessResponse(
                    new RestActionCallResponse(request.getName(),
                            getEngine().getActionManager().callAction(request.getName(), unmarshalActionArgs(actionAdapter, request))),
                    request);
        } catch (Exception e) {
            if (actionAdapter != null) {
                getEngine().handleError(actionAdapter, e);
            } else {
                getEngine().handleError("REST call", e);
            }

            return setupErrorResponse(new RestActionCallResponse(request.getName()), request, e);
        }
    }

    protected Object[] unmarshalActionArgs(ActionAdapter actionAdapter, RestActionCallRequest request) {
        return RestApiUtils.unmarshalActionArgs(mapper, actionAdapter, request.getArgs());
    }

    @Override
    public RestSendEventResponse send(RestSendEventRequest request, Exchange exchange) {
        try {
            Validate.notNull(request, "The request must not be null");

            User user = authenticateUser(request, exchange);
            Validate.isTrue(securityService.canSendEvent(user, request.getName()), "No privileges to send the event named '%s'",
                    request.getName());
            Validate.isTrue(isEventPublic(request.getName()), "There is no public event named '%s'", request.getName());

            EventDefinition definition = getEngine().getOperations().event(request.getName());
            if (request.getAttributes() != null) {
                request.getAttributes().forEach((name, value) -> definition.set(name, value));
            }

            return setupSuccessResponse(new RestSendEventResponse(definition.send().getId()), request);
        } catch (Exception e) {
            getEngine().handleError("REST send", e);
            return setupErrorResponse(new RestSendEventResponse(), request, e);
        }
    }

    @Override
    public RestReloadResponse reload(RestReloadRequest request, Exchange exchange) {
        try {
            if (request == null) {
                request = new RestReloadRequest();
            }

            User user = authenticateUser(request, exchange);

            Validate.isTrue(user.hasRole(settings.getAdminRole()), "No privileges to reload Sponge knowledge bases");

            getEngine().reload();

            return setupSuccessResponse(new RestReloadResponse(), request);
        } catch (Exception e) {
            getEngine().handleError("REST reload", e);
            return setupErrorResponse(new RestReloadResponse(), request, e);
        }
    }

    protected List<RestActionArgMeta> createActionArgMetaList(ActionAdapter actionAdapter) {
        return actionAdapter.getArgsMeta() != null ? Arrays
                .stream(actionAdapter.getArgsMeta()).map(meta -> new RestActionArgMeta(meta.getName(),
                        meta.getType() != null ? meta.getType() : null, meta.isRequired(), meta.getDisplayName(), meta.getDescription()))
                .collect(Collectors.toList()) : null;
    }

    protected RestActionResultMeta createActionResultMeta(ActionAdapter actionAdapter) {
        ResultMeta<?> resultMeta = actionAdapter.getResultMeta();
        return resultMeta != null ? new RestActionResultMeta(resultMeta.getType() != null ? resultMeta.getType() : null,
                resultMeta.getSubtype(), resultMeta.getDisplayName()) : null;
    }

    protected User authenticateUser(BaseRestRequest request, Exchange exchange) {
        // TODO Token verification in the case of an authentication token mode.
        if (request.getAuthToken() != null) {
            throw new UnsupportedOperationException("A token-based auhentication is not supported yet");
        }

        if (request.getUsername() == null) {
            if (settings.isAllowAnonymous()) {
                return RestApiUtils.createAnonymousUser(settings.getGuestRole());
            } else {
                throw new SpongeException("Anonymous access is not allowed");
            }
        }

        return securityService.authenticateUser(request.getUsername().toLowerCase(), request.getPassword(), exchange);
    }

    protected boolean isEventPublic(String eventName) {
        boolean publicBySettings = settings.getPublicEvents() != null
                ? settings.getPublicEvents().stream().filter(name -> eventName.matches(name)).findAny().isPresent()
                : RestApiConstants.DEFAULT_IS_EVENT_PUBLIC;

        String isEventPlubliActionName = RestApiConstants.ACTION_IS_EVENT_PUBLIC;
        boolean publicByAction = getEngine().getOperations().existsAction(isEventPlubliActionName)
                ? getEngine().getOperations().call(Boolean.class, isEventPlubliActionName, eventName).booleanValue()
                : RestApiConstants.DEFAULT_IS_EVENT_PUBLIC;

        return publicBySettings && publicByAction;
    }

    protected boolean canCallAction(User user, ActionAdapter actionAdapter) {
        if (RestApiUtils.isActionPrivate(actionAdapter.getName())) {
            return false;
        }

        return securityService.canCallAction(user, actionAdapter);
    }

    protected <T extends BaseRestResponse, R extends BaseRestRequest> T setupResponse(T response, R request) {
        if (request != null && request.getId() != null) {
            response.setId(request.getId());
        }

        return response;
    }

    protected <T extends BaseRestResponse, R extends BaseRestRequest> T setupSuccessResponse(T response, R request) {
        return setupResponse(response, request);
    }

    protected <T extends BaseRestResponse, R extends BaseRestRequest> T setupErrorResponse(T response, R request, Throwable exception) {
        errorResponseProvider.applyException(this, response, exception);

        return setupResponse(response, request);
    }

    protected RestKnowledgeBaseMeta createRestKnowledgeBase(KnowledgeBase kb) {
        return new RestKnowledgeBaseMeta(kb.getName(), kb.getDisplayName(), kb.getDescription());
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

    public ObjectMapper getMapper() {
        return mapper;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
