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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.CategoryMeta;
import org.openksavi.sponge.ProcessorAdapter;
import org.openksavi.sponge.ProcessorQualifiedVersion;
import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.ActionAdapter;
import org.openksavi.sponge.action.ActionMeta;
import org.openksavi.sponge.action.IsActionActiveContext;
import org.openksavi.sponge.action.ProvideArgsParameters;
import org.openksavi.sponge.core.kb.DefaultKnowledgeBase;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventDefinition;
import org.openksavi.sponge.kb.KnowledgeBase;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.RestCategoryMeta;
import org.openksavi.sponge.restapi.model.RestKnowledgeBaseMeta;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.GetActionsRequest;
import org.openksavi.sponge.restapi.model.request.GetEventTypesRequest;
import org.openksavi.sponge.restapi.model.request.GetFeaturesRequest;
import org.openksavi.sponge.restapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.request.IsActionActiveRequest;
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
import org.openksavi.sponge.restapi.model.response.IsActionActiveResponse;
import org.openksavi.sponge.restapi.model.response.LoginResponse;
import org.openksavi.sponge.restapi.model.response.LogoutResponse;
import org.openksavi.sponge.restapi.model.response.ProvideActionArgsResponse;
import org.openksavi.sponge.restapi.model.response.ReloadResponse;
import org.openksavi.sponge.restapi.model.response.SendEventResponse;
import org.openksavi.sponge.restapi.model.response.SpongeResponse;
import org.openksavi.sponge.restapi.server.security.RestApiAuthTokenService;
import org.openksavi.sponge.restapi.server.security.RestApiSecurityService;
import org.openksavi.sponge.restapi.server.security.UserAuthentication;
import org.openksavi.sponge.restapi.server.security.UserContext;
import org.openksavi.sponge.restapi.server.util.RestApiServerUtils;
import org.openksavi.sponge.restapi.type.converter.DefaultTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.TypeType;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.util.SpongeApiUtils;

/**
 * Default Sponge REST service.
 */
public class DefaultRestApiService implements RestApiService {

    private static final Logger logger = LoggerFactory.getLogger(DefaultRestApiService.class);

    private SpongeEngine engine;

    private RestApiSettings settings;

    private RestApiSecurityService securityService;

    private RestApiAuthTokenService authTokenService;

    private RestApiErrorResponseProvider errorResponseProvider = new DefaultRestApiErrorResponseProvider();

    private TypeConverter typeConverter;

    private Comparator<RestActionMeta> actionsOrderComparator = RestApiServerUtils.createActionsOrderComparator();

    private ThreadLocal<RestApiSession> session = new ThreadLocal<>();

    private Map<String, Object> features = Collections.synchronizedMap(new LinkedHashMap<>());

    public DefaultRestApiService() {
        setupDefaultFeatures();
    }

    protected void setupDefaultFeatures() {
        setFeature(RestApiConstants.REMOTE_API_FEATURE_GRPC_ENABLED, false);
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
    public GetVersionResponse getVersion(GetVersionRequest request) {
        try {
            // Privileges checked only if the request is provided.
            if (request != null) {
                authenticateRequest(request);
            }

            return setupSuccessResponse(new GetVersionResponse(getEngine().getVersion()), request);
        } catch (Throwable e) {
            return handleError("REST getVersion", e, new GetVersionResponse(), request);
        }
    }

    @Override
    public GetFeaturesResponse getFeatures(GetFeaturesRequest request) {
        try {
            // Privileges checked only if the request is provided.
            if (request != null) {
                authenticateRequest(request);
            }

            return setupSuccessResponse(new GetFeaturesResponse(features), request);
        } catch (Throwable e) {
            return handleError("REST getFeatures", e, new GetFeaturesResponse(), request);
        }
    }

    @Override
    public Map<String, Object> getFeatures() {
        return features;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Validate.notNull(request, "The request must not be null");
            Validate.notNull(request.getHeader().getUsername(), "The username must not be null");

            UserAuthentication userAuthentication = authenticateUser(request.getHeader().getUsername(), request.getHeader().getPassword());
            String authToken = authTokenService != null ? authTokenService.createAuthToken(userAuthentication) : null;

            return setupSuccessResponse(new LoginResponse(authToken), request);
        } catch (Throwable e) {
            return handleError("REST login", e, new LoginResponse(), request);
        }
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        try {
            Validate.notNull(request, "The request must not be null");

            authenticateRequest(request);

            if (request.getHeader().getAuthToken() != null) {
                getSafeAuthTokenService().removeAuthToken(request.getHeader().getAuthToken());
            }

            return setupSuccessResponse(new LogoutResponse(), request);
        } catch (Throwable e) {
            return handleError("REST logout", e, new LogoutResponse(), request);
        }
    }

    @Override
    public GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request) {
        if (request == null) {
            request = new GetKnowledgeBasesRequest();
        }

        try {
            UserContext userContext = authenticateRequest(request);

            return setupSuccessResponse(new GetKnowledgeBasesResponse(getEngine().getKnowledgeBaseManager().getKnowledgeBases().stream()
                    .filter(kb -> securityService.canUseKnowledgeBase(userContext, kb))
                    .filter(kb -> !kb.getName().equals(DefaultKnowledgeBase.NAME)).map(kb -> createRestKnowledgeBase(kb))
                    .collect(Collectors.toList())), request);
        } catch (Throwable e) {
            return handleError("REST getKnowledgeBases", e, new GetKnowledgeBasesResponse(), request);
        }
    }

    @Override
    public GetActionsResponse getActions(GetActionsRequest request) {
        try {
            if (request == null) {
                request = new GetActionsRequest();
            }

            UserContext userContext = authenticateRequest(request);

            boolean actualMetadataRequired = request.getBody().getMetadataRequired() != null ? request.getBody().getMetadataRequired()
                    : RestApiServerConstants.REST_PARAM_ACTIONS_METADATA_REQUIRED_DEFAULT;
            String actionNameRegExp = request.getBody().getName();

            List<ActionAdapter> actions =
                    getEngine().getActions().stream()
                            .filter(action -> actionNameRegExp != null ? action.getMeta().getName().matches(actionNameRegExp) : true)
                            .filter(action -> !action.getKnowledgeBase().getName().equals(DefaultKnowledgeBase.NAME))
                            .filter(action -> actualMetadataRequired
                                    ? action.getMeta().getArgs() != null && action.getMeta().getResult() != null : true)
                            .filter(action -> canCallAction(userContext, action)).collect(Collectors.toList());

            Map<String, DataType<?>> registeredTypes = null;
            if (request.getBody().getRegisteredTypes() != null && request.getBody().getRegisteredTypes()) {
                final Set<String> typeNames = new LinkedHashSet<>();
                actions.stream().forEach(action -> typeNames.addAll(action.getRegisteredTypeNames()));

                registeredTypes = typeNames.stream().collect(SpongeApiUtils.collectorToLinkedMap(registeredTypeName -> registeredTypeName,
                        registeredTypeName -> marshalDataType(getEngine().getType(registeredTypeName))));
            }

            return setupSuccessResponse(new GetActionsResponse(actions.stream().map(action -> createRestActionMeta(action))
                    .sorted(actionsOrderComparator).map(action -> marshalActionMeta(action)).collect(Collectors.toList()), registeredTypes),
                    request);
        } catch (Throwable e) {
            return handleError("REST getActions", e, new GetActionsResponse(), request);
        }
    }

    protected RestActionMeta createRestActionMeta(ActionAdapter actionAdapter) {
        ActionMeta meta = actionAdapter.getMeta();
        return new RestActionMeta(meta.getName(), meta.getLabel(), meta.getDescription(),
                createRestKnowledgeBase(actionAdapter.getKnowledgeBase()), createRestCategory(meta.getCategory()), meta.getFeatures(),
                meta.getArgs(), meta.getResult(), meta.isCallable(), actionAdapter.getQualifiedVersion());

    }

    @Override
    @SuppressWarnings("rawtypes")
    public DataType marshalDataType(DataType type) {
        return (DataType) typeConverter.marshal(new TypeType(), type);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public RestActionMeta marshalActionMeta(RestActionMeta actionMeta) {
        if (actionMeta != null) {
            List<DataType> args = actionMeta.getArgs();
            if (args != null) {
                actionMeta.setArgs(args.stream().map(argType -> marshalDataType(argType)).collect(Collectors.toList()));
            }

            if (actionMeta.getResult() != null) {
                actionMeta.setResult(marshalDataType(actionMeta.getResult()));
            }
        }

        return actionMeta;
    }

    protected ActionAdapter getActionAdapterForRequest(String actionName, ProcessorQualifiedVersion qualifiedVersion,
            UserContext userContext) {
        ActionAdapter actionAdapter = getEngine().getActionManager().getActionAdapter(actionName);

        Validate.notNull(actionAdapter, "The action %s doesn't exist", actionName);
        Validate.isTrue(canCallAction(userContext, actionAdapter), "No privileges to call action %s", actionName);

        if (qualifiedVersion != null && !qualifiedVersion.equals(actionAdapter.getQualifiedVersion())) {
            throw new RestApiInvalidKnowledgeBaseVersionServerException(
                    String.format("The expected action qualified version (%s) differs from the actual (%s)", qualifiedVersion.toString(),
                            actionAdapter.getQualifiedVersion().toString()));
        }

        return actionAdapter;
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request) {
        ActionAdapter actionAdapter = null;

        try {
            Validate.notNull(request, "The request must not be null");
            UserContext userContext = authenticateRequest(request);

            String actionName = request.getBody().getName();
            actionAdapter = getActionAdapterForRequest(actionName, request.getBody().getQualifiedVersion(), userContext);

            List<Object> args = unmarshalActionArgs(actionAdapter, request.getBody().getArgs());

            Object actionResult = getEngine().getOperations().call(actionName, args);

            return setupSuccessResponse(new ActionCallResponse(marshalActionResult(actionAdapter, actionResult)), request);
        } catch (Throwable e) {
            if (actionAdapter != null) {
                return handleError(actionAdapter, e, new ActionCallResponse(), request);
            } else {
                return handleError("REST call", e, new ActionCallResponse(), request);
            }
        }
    }

    protected <T extends SpongeResponse, R extends SpongeRequest> T handleError(ProcessorAdapter<?> processorAdapter, Throwable e,
            T response, R request) {
        if (!(e instanceof ApplicationServerSpongeException)) {
            getEngine().handleError(processorAdapter, e);
        } else {
            logger.debug("REST API application error in "
                    + (processorAdapter != null && processorAdapter.getMeta() != null ? processorAdapter.getMeta().getName() : "unknown"),
                    e);
        }

        return setupErrorResponse(response, request, e);
    }

    protected <T extends SpongeResponse, R extends SpongeRequest> T handleError(String source, Throwable e, T response, R request) {
        if (!(e instanceof ApplicationServerSpongeException)) {
            getEngine().handleError(source, e);
        } else {
            logger.debug("REST API application error in " + source, e);
        }

        return setupErrorResponse(response, request, e);
    }

    protected List<Object> unmarshalActionArgs(ActionAdapter actionAdapter, List<Object> args) {
        return RestApiServerUtils.unmarshalActionCallArgs(typeConverter, actionAdapter, args);
    }

    protected Object marshalActionResult(ActionAdapter actionAdapter, Object result) {
        return RestApiServerUtils.marshalActionCallResult(typeConverter, actionAdapter, result);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SendEventResponse send(SendEventRequest request) {
        try {
            Validate.notNull(request, "The request must not be null");

            UserContext userContext = authenticateRequest(request);

            String eventName = request.getBody().getName();
            Map<String, Object> attributes = request.getBody().getAttributes();

            // Unmarshal attributes if there is an event type registered.
            RecordType eventType = getEngine().getEventTypes().get(eventName);
            if (eventType != null) {
                attributes = (Map<String, Object>) typeConverter.unmarshal(eventType, attributes);
            }

            Event event = sendEvent(eventName, attributes, request.getBody().getLabel(), request.getBody().getDescription(), userContext);

            return setupSuccessResponse(new SendEventResponse(event.getId()), request);
        } catch (Throwable e) {
            return handleError("REST send", e, new SendEventResponse(), request);
        }
    }

    @Override
    public Event sendEvent(String eventName, Map<String, Object> attributes, String label, String description, UserContext userContext) {
        Validate.isTrue(canSendEvent(userContext, eventName), "No privileges to send the '%s' event", eventName);

        EventDefinition definition = getEngine().getOperations().event(eventName);
        if (attributes != null) {
            definition.set(attributes);
        }

        definition.label(label).description(description);

        return definition.send();
    }

    @Override
    public Event sendEvent(String eventName, Map<String, Object> attributes, String label, UserContext userContext) {
        return sendEvent(eventName, attributes, label, null, userContext);
    }

    @Override
    public Event sendEvent(String eventName, Map<String, Object> attributes, UserContext userContext) {
        return sendEvent(eventName, attributes, null, null, userContext);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public IsActionActiveResponse isActionActive(IsActionActiveRequest request) {
        try {
            Validate.notNull(request, "The request must not be null");
            UserContext userContext = authenticateRequest(request);

            List<Boolean> active = new ArrayList<>();

            if (request.getBody().getEntries() != null) {
                request.getBody().getEntries().forEach(entry -> {
                    ActionAdapter actionAdapter = getActionAdapterForRequest(entry.getName(), entry.getQualifiedVersion(), userContext);

                    DataType contextType = entry.getContextType() != null
                            ? (DataType) typeConverter.unmarshal(new TypeType(), entry.getContextType()) : null;
                    Object contextValue = entry.getContextValue() != null && contextType != null
                            ? typeConverter.unmarshal(contextType, entry.getContextValue()) : entry.getContextValue();

                    active.add(getEngine().getOperations().isActionActive(entry.getName(), new IsActionActiveContext(contextValue,
                            contextType, unmarshalActionArgs(actionAdapter, entry.getArgs()), entry.getFeatures())));
                });
            }

            return setupSuccessResponse(new IsActionActiveResponse(active), request);
        } catch (Throwable e) {
            return handleError("REST isActionActive", e, new IsActionActiveResponse(), request);
        }
    }

    @Override
    public ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request) {
        ActionAdapter actionAdapter = null;

        try {
            Validate.notNull(request, "The request must not be null");
            UserContext userContext = authenticateRequest(request);
            actionAdapter = getActionAdapterForRequest(request.getBody().getName(), request.getBody().getQualifiedVersion(), userContext);

            Map<String,
                    ProvidedValue<?>> provided = getEngine().getOperations().provideActionArgs(actionAdapter.getMeta().getName(),
                            new ProvideArgsParameters(request.getBody().getProvide(), request.getBody().getSubmit(),
                                    RestApiServerUtils.unmarshalAuxiliaryActionArgs(typeConverter, actionAdapter,
                                            request.getBody().getCurrent(), request.getBody().getDynamicTypes()),
                                    request.getBody().getDynamicTypes(), request.getBody().getFeatures()));
            RestApiServerUtils.marshalProvidedActionArgValues(typeConverter, actionAdapter, provided, request.getBody().getDynamicTypes());

            return setupSuccessResponse(new ProvideActionArgsResponse(provided), request);
        } catch (Throwable e) {
            if (actionAdapter != null) {
                return handleError(actionAdapter, e, new ProvideActionArgsResponse(), request);
            } else {
                return handleError("REST provideActionArgs", e, new ProvideActionArgsResponse(), request);
            }
        }
    }

    @Override
    public GetEventTypesResponse getEventTypes(GetEventTypesRequest request) {
        try {
            if (request == null) {
                request = new GetEventTypesRequest();
            }

            authenticateRequest(request);

            // Match all events types if the name pattern is null.
            String eventNameRegExp = request.getBody().getName() != null ? request.getBody().getName() : ".*";

            Map<String, RecordType> marshalledEventTypes = getEngine().getEventTypes().entrySet().stream()
                    .filter(entry -> getEngine().getPatternMatcher().matches(eventNameRegExp, entry.getKey())).collect(SpongeApiUtils
                            .collectorToLinkedMap(entry -> entry.getKey(), entry -> (RecordType) marshalDataType(entry.getValue())));

            return setupSuccessResponse(new GetEventTypesResponse(marshalledEventTypes), request);
        } catch (Throwable e) {
            return handleError("REST getEventTypes", e, new GetEventTypesResponse(), request);
        }
    }

    @Override
    public ReloadResponse reload(ReloadRequest request) {
        try {
            if (request == null) {
                request = new ReloadRequest();
            }

            UserContext userContext = authenticateRequest(request);

            Validate.isTrue(userContext.hasRole(settings.getAdminRole()), "No privileges to reload Sponge knowledge bases");

            getEngine().reload();

            return setupSuccessResponse(new ReloadResponse(), request);
        } catch (Throwable e) {
            return handleError("REST reload", e, new ReloadResponse(), request);
        }
    }

    protected RestApiAuthTokenService getSafeAuthTokenService() {
        return Validate.notNull(authTokenService, "Auth token service not configured");
    }

    /**
     * Throws exception if the request can't be successfully authenticated.
     *
     * @param request the request.
     * @return the user context.
     */
    @Override
    public UserContext authenticateRequest(SpongeRequest request) {
        UserAuthentication userAuthentication;
        if (request.getHeader().getAuthToken() != null) {
            Validate.isTrue(request.getHeader().getUsername() == null, "No username is allowed when using a token-based auhentication");
            Validate.isTrue(request.getHeader().getPassword() == null, "No password is allowed when using a token-based auhentication");

            userAuthentication = getSafeAuthTokenService().validateAuthToken(request.getHeader().getAuthToken());
        } else {
            if (request.getHeader().getUsername() == null) {
                if (settings.isAllowAnonymous()) {
                    userAuthentication =
                            securityService.authenticateAnonymous(RestApiServerUtils.createAnonymousUser(settings.getAnonymousRole()));
                } else {
                    throw new SpongeException("Anonymous access is not allowed");
                }
            } else {
                userAuthentication = authenticateUser(request.getHeader().getUsername(), request.getHeader().getPassword());
            }
        }

        // Set the user in the thread local session.
        RestApiSession session = Validate.notNull(getSession(), "The session is not set");
        Validate.isTrue(session instanceof DefaultRestApiSession, "The session class should extend %s", DefaultRestApiSession.class);
        ((DefaultRestApiSession) session).setUserAuthentication(userAuthentication);

        securityService.openSecurityContext(userAuthentication);

        return userAuthentication.getUserContext();
    }

    protected UserAuthentication authenticateUser(String username, String password) throws RestApiInvalidUsernamePasswordServerException {
        return securityService.authenticateUser(username != null ? username.toLowerCase() : null, password);
    }

    protected boolean isActionPublic(ActionAdapter actionAdapter) {
        String isPublicActionActionName = RestApiServerConstants.ACTION_IS_ACTION_PUBLIC;
        Predicate<ActionAdapter> isPublicByAction = action -> getEngine().getOperations().hasAction(isPublicActionActionName)
                ? getEngine().getOperations().call(Boolean.class, isPublicActionActionName, Arrays.asList(action)).booleanValue()
                : RestApiServerConstants.DEFAULT_IS_ACTION_PUBLIC;

        Predicate<ActionAdapter> isPublicBySettings = action -> settings.getPublicActions() != null
                ? settings.getPublicActions().stream()
                        .filter(qn -> action.getKnowledgeBase().getName().matches(qn.getKnowledgeBaseName())
                                && action.getMeta().getName().matches(qn.getName()))
                        .findAny().isPresent()
                : RestApiServerConstants.DEFAULT_IS_ACTION_PUBLIC;

        return isPublicByAction.test(actionAdapter) && isPublicBySettings.test(actionAdapter)
                && !RestApiServerUtils.isActionInternal(actionAdapter.getMeta().getName());
    }

    protected boolean isEventPublic(String eventName) {
        boolean publicBySettings = settings.getPublicEvents() != null
                ? settings.getPublicEvents().stream().filter(name -> eventName.matches(name)).findAny().isPresent()
                : RestApiServerConstants.DEFAULT_IS_EVENT_PUBLIC;

        String isEventPublicActionName = RestApiServerConstants.ACTION_IS_EVENT_PUBLIC;
        boolean publicByAction = getEngine().getOperations().hasAction(isEventPublicActionName)
                ? getEngine().getOperations().call(Boolean.class, isEventPublicActionName, Arrays.asList(eventName)).booleanValue()
                : RestApiServerConstants.DEFAULT_IS_EVENT_PUBLIC;

        return publicBySettings && publicByAction;
    }

    @Override
    public boolean canCallAction(UserContext userContext, ActionAdapter actionAdapter) {
        return isActionPublic(actionAdapter) && securityService.canCallAction(userContext, actionAdapter);
    }

    @Override
    public boolean canSendEvent(UserContext userContext, String eventName) {
        return isEventPublic(eventName) && securityService.canSendEvent(userContext, eventName);
    }

    @Override
    public boolean canSubscribeEvent(UserContext userContext, String eventName) {
        return isEventPublic(eventName) && securityService.canSubscribeEvent(userContext, eventName);
    }

    protected <T extends SpongeResponse, R extends SpongeRequest> T setupResponse(T response, R request) {
        if (request != null && request.getHeader().getId() != null) {
            response.getHeader().setId(request.getHeader().getId());
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
        int kbIndex = SpongeUtils.getKnowledgeBaseIndex(getEngine(), kb);

        return new RestKnowledgeBaseMeta(kb.getName(), kb.getLabel(), kb.getDescription(), kb.getVersion(), kbIndex > -1 ? kbIndex : null);
    }

    protected RestCategoryMeta createRestCategory(String categoryName) {
        if (categoryName == null || !getEngine().hasCategory(categoryName)) {
            return null;
        }

        CategoryMeta category = getEngine().getCategory(categoryName);

        int categoryIndex = SpongeUtils.getCategoryIndex(getEngine(), category);

        return new RestCategoryMeta(category.getName(), category.getLabel(), category.getDescription(), category.getFeatures(),
                categoryIndex > -1 ? categoryIndex : null);
    }

    @Override
    public SpongeResponse createGenericErrorResponse(Throwable e) {
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

    @Override
    public Comparator<RestActionMeta> getActionsOrderComparator() {
        return actionsOrderComparator;
    }

    @Override
    public void setActionsOrderComparator(Comparator<RestActionMeta> actionsOrderComparator) {
        this.actionsOrderComparator = actionsOrderComparator;
    }

    @Override
    public RestApiSession getSession() {
        return session.get();
    }

    @Override
    public void openSession(RestApiSession session) {
        this.session.set(session);
    }

    @Override
    public void closeSession() {
        try {
            securityService.closeSecurityContext();
        } finally {
            session.set(null);
        }
    }

    @Override
    public void setFeature(String name, Object value) {
        features.put(name, value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getFeature(String name) {
        return Validate.notNull((T) features.get(name), "Feature %s not found", name);
    }

    @Override
    public <T> T getFeature(Class<T> cls, String name) {
        return getFeature(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getFeature(String name, T defaultValue) {
        T feature = (T) features.get(name);

        return feature != null ? feature : defaultValue;
    }

    @Override
    public <T> T getFeature(Class<T> cls, String name, T defaultValue) {
        return getFeature(name, defaultValue);
    }
}
