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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ArgProvidedValue;
import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.core.kb.DefaultKnowledgeBase;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.EventDefinition;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.restapi.model.RestActionArgMeta;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.RestActionResultMeta;
import org.openksavi.sponge.restapi.model.RestKnowledgeBaseMeta;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.GetActionsRequest;
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
import org.openksavi.sponge.restapi.server.security.User;
import org.openksavi.sponge.restapi.server.util.RestApiServerUtils;
import org.openksavi.sponge.restapi.type.converter.DefaultTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.type.converter.unit.TypeTypeUnitConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.type.TypeType;

/**
 * Default Sponge REST service.
 */
public class DefaultRestApiService implements RestApiService {

    private SpongeEngine engine;

    private RestApiSettings settings;

    private RestApiSecurityService securityService;

    private RestApiAuthTokenService authTokenService;

    private RestApiErrorResponseProvider errorResponseProvider = new DefaultRestApiErrorResponseProvider();

    private TypeConverter typeConverter;

    private TypeTypeUnitConverter defaultTypeTypeUnitConverter = new TypeTypeUnitConverter();

    public DefaultRestApiService() {
        //
    }

    @Override
    public void init() {
        ObjectMapper mapper = RestApiUtils.createObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, settings.isPrettyPrint());

        typeConverter = new DefaultTypeConverter(mapper);
    }

    @Override
    public void destroy() {
        //
    }

    @Override
    public GetVersionResponse getVersion(GetVersionRequest request, Exchange exchange) {
        try {
            // Privileges not checked here.
            if (request != null) {
                authenticateRequest(request, exchange);
            }

            return setupSuccessResponse(new GetVersionResponse(getEngine().getVersion()), request);
        } catch (Exception e) {
            getEngine().handleError("REST getVersion", e);
            return setupErrorResponse(new GetVersionResponse(), request, e);
        }
    }

    @Override
    public LoginResponse login(LoginRequest request, Exchange exchange) {
        try {
            Validate.notNull(request, "The request must not be null");
            Validate.notNull(request.getUsername(), "The username must not be null");

            User user = authenticateUser(request.getUsername(), request.getPassword(), exchange);
            String authToken = authTokenService != null ? authTokenService.createAuthToken(user, exchange) : null;

            return setupSuccessResponse(new LoginResponse(authToken), request);
        } catch (Exception e) {
            getEngine().handleError("REST login", e);
            return setupErrorResponse(new LoginResponse(), request, e);
        }
    }

    @Override
    public LogoutResponse logout(LogoutRequest request, Exchange exchange) {
        try {
            Validate.notNull(request, "The request must not be null");

            authenticateRequest(request, exchange);

            if (request.getAuthToken() != null) {
                getSafeAuthTokenService().removeAuthToken(request.getAuthToken(), exchange);
            }

            return setupSuccessResponse(new LogoutResponse(), request);
        } catch (Exception e) {
            getEngine().handleError("REST logout", e);
            return setupErrorResponse(new LogoutResponse(), request, e);
        }
    }

    @Override
    public GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request, Exchange exchange) {
        if (request == null) {
            request = new GetKnowledgeBasesRequest();
        }

        try {
            User user = authenticateRequest(request, exchange);

            return setupSuccessResponse(new GetKnowledgeBasesResponse(getEngine().getKnowledgeBaseManager().getKnowledgeBases().stream()
                    .filter(kb -> securityService.canUseKnowledgeBase(user, kb))
                    .filter(kb -> !kb.getName().equals(DefaultKnowledgeBase.NAME)).map(kb -> createRestKnowledgeBase(kb))
                    .collect(Collectors.toList())), request);
        } catch (Exception e) {
            getEngine().handleError("REST getKnowledgeBases", e);
            return setupErrorResponse(new GetKnowledgeBasesResponse(), request, e);
        }
    }

    @Override
    public GetActionsResponse getActions(GetActionsRequest request, Exchange exchange) {
        try {
            if (request == null) {
                request = new GetActionsRequest();
            }

            User user = authenticateRequest(request, exchange);

            boolean actualMetadataRequired = request.getMetadataRequired() != null ? request.getMetadataRequired()
                    : RestApiServerConstants.REST_PARAM_ACTIONS_METADATA_REQUIRED_DEFAULT;
            String actionNameRegExp = request.getName();

            String isPublicActionActionName = RestApiServerConstants.ACTION_IS_ACTION_PUBLIC;
            Predicate<ActionAdapter> isPublicByAction = action -> getEngine().getOperations().existsAction(isPublicActionActionName)
                    ? getEngine().getOperations().call(Boolean.class, isPublicActionActionName, Arrays.asList(action)).booleanValue()
                    : RestApiServerConstants.DEFAULT_IS_ACTION_PUBLIC;

            Predicate<ActionAdapter> isPublicBySettings = action -> settings.getPublicActions() != null
                    ? settings.getPublicActions().stream()
                            .filter(qn -> action.getKnowledgeBase().getName().matches(qn.getKnowledgeBaseName())
                                    && action.getName().matches(qn.getName()))
                            .findAny().isPresent()
                    : RestApiServerConstants.DEFAULT_IS_ACTION_PUBLIC;

            Predicate<ActionAdapter> isSelectedByNameRegExp =
                    action -> actionNameRegExp != null ? action.getName().matches(actionNameRegExp) : true;

            return setupSuccessResponse(
                    new GetActionsResponse(getEngine().getActions().stream().filter(isSelectedByNameRegExp).filter(isPublicByAction)
                            .filter(isPublicBySettings)
                            .filter(action -> actualMetadataRequired ? action.getArgsMeta() != null && action.getResultMeta() != null
                                    : true)
                            .filter(action -> !action.getKnowledgeBase().getName().equals(DefaultKnowledgeBase.NAME))
                            .filter(action -> canCallAction(user, action))
                            .map(action -> new RestActionMeta(action.getName(), action.getLabel(), action.getDescription(),
                                    createRestKnowledgeBase(action.getKnowledgeBase()), getEngine().getCategory(action.getCategory()),
                                    action.getFeatures(), createActionArgMetaList(action), createActionResultMeta(action),
                                    action.getQualifiedVersion()))
                            .map(action -> marshalActionMeta(action)).collect(Collectors.toList())),
                    request);
        } catch (Exception e) {
            getEngine().handleError("REST getActions", e);
            return setupErrorResponse(new GetActionsResponse(), request, e);
        }
    }

    protected RestActionMeta marshalActionMeta(RestActionMeta actionMeta) {
        if (actionMeta != null) {
            if (actionMeta.getArgsMeta() != null) {
                actionMeta.getArgsMeta().forEach(
                        argMeta -> argMeta.setType(defaultTypeTypeUnitConverter.marshal(typeConverter, new TypeType(), argMeta.getType())));
            }

            if (actionMeta.getResultMeta() != null) {
                actionMeta.getResultMeta()
                        .setType(defaultTypeTypeUnitConverter.marshal(typeConverter, new TypeType(), actionMeta.getResultMeta().getType()));
            }
        }

        return actionMeta;
    }

    protected ActionAdapter getActionAdapterForRequest(String actionName, ProcessorQualifiedVersion qualifiedVersion, User user) {
        ActionAdapter actionAdapter = getEngine().getActionManager().getActionAdapter(actionName);

        Validate.notNull(actionAdapter, "The action %s doesn't exist", actionName);
        Validate.isTrue(canCallAction(user, actionAdapter), "No privileges to call action %s", actionName);

        if (qualifiedVersion != null && !qualifiedVersion.equals(actionAdapter.getQualifiedVersion())) {
            throw new RestApiIncorrectKnowledgeBaseVersionServerException(
                    String.format("The expected action qualified version (%s) differs from the actual (%s)", qualifiedVersion.toString(),
                            actionAdapter.getQualifiedVersion().toString()));
        }

        return actionAdapter;
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request, Exchange exchange) {
        ActionAdapter actionAdapter = null;

        try {
            Validate.notNull(request, "The request must not be null");
            User user = authenticateRequest(request, exchange);
            actionAdapter = getActionAdapterForRequest(request.getName(), request.getQualifiedVersion(), user);

            Object actionResult =
                    getEngine().getActionManager().callAction(request.getName(), unmarshalActionArgs(actionAdapter, request, exchange));

            return setupSuccessResponse(new ActionCallResponse(marshalActionResult(actionAdapter, actionResult, exchange)), request);
        } catch (Exception e) {
            if (actionAdapter != null) {
                getEngine().handleError(actionAdapter, e);
            } else {
                getEngine().handleError("REST call", e);
            }

            return setupErrorResponse(new ActionCallResponse(), request, e);
        }
    }

    protected List<Object> unmarshalActionArgs(ActionAdapter actionAdapter, ActionCallRequest request, Exchange exchange) {
        return RestApiServerUtils.unmarshalActionCallArgs(typeConverter, actionAdapter, request.getArgs(), exchange);
    }

    protected Object marshalActionResult(ActionAdapter actionAdapter, Object result, Exchange exchange) {
        return RestApiServerUtils.marshalActionCallResult(typeConverter, actionAdapter, result, exchange);
    }

    @Override
    public SendEventResponse send(SendEventRequest request, Exchange exchange) {
        try {
            Validate.notNull(request, "The request must not be null");

            User user = authenticateRequest(request, exchange);
            Validate.isTrue(securityService.canSendEvent(user, request.getName()), "No privileges to send the event named '%s'",
                    request.getName());
            Validate.isTrue(isEventPublic(request.getName()), "There is no public event named '%s'", request.getName());

            EventDefinition definition = getEngine().getOperations().event(request.getName());
            if (request.getAttributes() != null) {
                request.getAttributes().forEach((name, value) -> definition.set(name, value));
            }

            return setupSuccessResponse(new SendEventResponse(definition.send().getId()), request);
        } catch (Exception e) {
            getEngine().handleError("REST send", e);
            return setupErrorResponse(new SendEventResponse(), request, e);
        }
    }

    @Override
    public ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request, Exchange exchange) {
        ActionAdapter actionAdapter = null;

        try {
            Validate.notNull(request, "The request must not be null");
            User user = authenticateRequest(request, exchange);
            actionAdapter = getActionAdapterForRequest(request.getName(), request.getQualifiedVersion(), user);

            Map<String,
                    ArgProvidedValue<?>> provided = getEngine().getOperations().provideActionArgs(actionAdapter.getName(),
                            request.getArgNames(),
                            RestApiServerUtils.unmarshalProvideActionArgs(typeConverter, actionAdapter, request.getCurrent(), exchange));
            RestApiServerUtils.marshalProvidedActionArgValues(typeConverter, actionAdapter, provided);

            return setupSuccessResponse(new ProvideActionArgsResponse(provided), request);
        } catch (Exception e) {
            if (actionAdapter != null) {
                getEngine().handleError(actionAdapter, e);
            } else {
                getEngine().handleError("REST provideActionArgs", e);
            }

            return setupErrorResponse(new ProvideActionArgsResponse(), request, e);
        }
    }

    @Override
    public ReloadResponse reload(ReloadRequest request, Exchange exchange) {
        try {
            if (request == null) {
                request = new ReloadRequest();
            }

            User user = authenticateRequest(request, exchange);

            Validate.isTrue(user.hasRole(settings.getAdminRole()), "No privileges to reload Sponge knowledge bases");

            getEngine().reload();

            return setupSuccessResponse(new ReloadResponse(), request);
        } catch (Exception e) {
            getEngine().handleError("REST reload", e);
            return setupErrorResponse(new ReloadResponse(), request, e);
        }
    }

    protected List<RestActionArgMeta> createActionArgMetaList(ActionAdapter actionAdapter) {
        return actionAdapter
                .getArgsMeta() != null
                        ? actionAdapter.getArgsMeta().stream()
                                .map(meta -> new RestActionArgMeta(meta.getName(), meta.getType() != null ? meta.getType() : null,
                                        meta.getLabel(), meta.getDescription(), meta.isOptional(), meta.getProvided()))
                                .collect(Collectors.toList())
                        : null;
    }

    protected RestActionResultMeta createActionResultMeta(ActionAdapter actionAdapter) {
        ResultMeta<?> resultMeta = actionAdapter.getResultMeta();
        return resultMeta != null ? new RestActionResultMeta(resultMeta.getType(), resultMeta.getLabel()) : null;
    }

    protected RestApiAuthTokenService getSafeAuthTokenService() {
        return Validate.notNull(authTokenService, "Auth token service not configured");
    }

    /**
     * Throws exception if the request can't be successfully authenticated.
     *
     * @param request the request.
     * @param exchange the exchange.
     * @return the user.
     */
    protected User authenticateRequest(SpongeRequest request, Exchange exchange) {
        if (request.getAuthToken() != null) {
            Validate.isTrue(request.getUsername() == null, "No username is allowed when using a token-based auhentication");
            Validate.isTrue(request.getPassword() == null, "No password is allowed when using a token-based auhentication");

            String username = getSafeAuthTokenService().validateAuthToken(request.getAuthToken(), exchange);
            return securityService.getUser(username);
        } else {
            if (request.getUsername() == null) {
                if (settings.isAllowAnonymous()) {
                    return RestApiServerUtils.createAnonymousUser(settings.getAnonymousRole());
                } else {
                    throw new SpongeException("Anonymous access is not allowed");
                }
            }

            return authenticateUser(request.getUsername(), request.getPassword(), exchange);
        }
    }

    protected User authenticateUser(String username, String password, Exchange exchange) {
        return securityService.authenticateUser(username != null ? username.toLowerCase() : null, password, exchange);
    }

    protected boolean isEventPublic(String eventName) {
        boolean publicBySettings = settings.getPublicEvents() != null
                ? settings.getPublicEvents().stream().filter(name -> eventName.matches(name)).findAny().isPresent()
                : RestApiServerConstants.DEFAULT_IS_EVENT_PUBLIC;

        String isEventPlubliActionName = RestApiServerConstants.ACTION_IS_EVENT_PUBLIC;
        boolean publicByAction = getEngine().getOperations().existsAction(isEventPlubliActionName)
                ? getEngine().getOperations().call(Boolean.class, isEventPlubliActionName, Arrays.asList(eventName)).booleanValue()
                : RestApiServerConstants.DEFAULT_IS_EVENT_PUBLIC;

        return publicBySettings && publicByAction;
    }

    protected boolean canCallAction(User user, ActionAdapter actionAdapter) {
        if (RestApiServerUtils.isActionPrivate(actionAdapter.getName())) {
            return false;
        }

        return securityService.canCallAction(user, actionAdapter);
    }

    protected <T extends SpongeResponse, R extends SpongeRequest> T setupResponse(T response, R request) {
        if (request != null && request.getId() != null) {
            response.setId(request.getId());
        }

        return response;
    }

    protected <T extends SpongeResponse, R extends SpongeRequest> T setupSuccessResponse(T response, R request) {
        return setupResponse(response, request);
    }

    protected <T extends SpongeResponse, R extends SpongeRequest> T setupErrorResponse(T response, R request, Throwable exception) {
        errorResponseProvider.applyException(this, response, exception);

        return setupResponse(response, request);
    }

    protected RestKnowledgeBaseMeta createRestKnowledgeBase(KnowledgeBase kb) {
        return new RestKnowledgeBaseMeta(kb.getName(), kb.getLabel(), kb.getDescription(), kb.getVersion());
    }

    @Override
    public SpongeResponse createGenericErrorResponse(Throwable e, Exchange exchange) {
        Validate.notNull(e, "Exception should be not null");
        SpongeResponse response = new SpongeResponse();

        errorResponseProvider.applyException(this, response, e);

        return response;
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
    public TypeConverter getTypeConverter() {
        return typeConverter;
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
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
    public RestApiAuthTokenService getAuthTokenService() {
        return authTokenService;
    }

    @Override
    public void setAuthTokenService(RestApiAuthTokenService authTokenService) {
        this.authTokenService = authTokenService;
    }

    @Override
    public RestApiErrorResponseProvider getErrorResponseProvider() {
        return errorResponseProvider;
    }

    @Override
    public void setErrorResponseProvider(RestApiErrorResponseProvider errorResponseProvider) {
        this.errorResponseProvider = errorResponseProvider;
    }
}
