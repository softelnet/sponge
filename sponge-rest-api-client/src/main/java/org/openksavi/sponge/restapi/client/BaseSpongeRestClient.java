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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.function.Supplier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.listener.OnRequestSerializedListener;
import org.openksavi.sponge.restapi.client.listener.OnResponseDeserializedListener;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.RestKnowledgeBaseMeta;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.ActionExecutionRequest;
import org.openksavi.sponge.restapi.model.request.GetActionsRequest;
import org.openksavi.sponge.restapi.model.request.GetEventTypesRequest;
import org.openksavi.sponge.restapi.model.request.GetFeaturesRequest;
import org.openksavi.sponge.restapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.request.LoginRequest;
import org.openksavi.sponge.restapi.model.request.LogoutRequest;
import org.openksavi.sponge.restapi.model.request.ProvideActionArgsRequest;
import org.openksavi.sponge.restapi.model.request.ReloadRequest;
import org.openksavi.sponge.restapi.model.request.RequestHeader;
import org.openksavi.sponge.restapi.model.request.SendEventRequest;
import org.openksavi.sponge.restapi.model.request.SpongeRequest;
import org.openksavi.sponge.restapi.model.request.SubmitActionArgsRequest;
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
import org.openksavi.sponge.restapi.model.response.ResponseHeader;
import org.openksavi.sponge.restapi.model.response.SendEventResponse;
import org.openksavi.sponge.restapi.model.response.SpongeResponse;
import org.openksavi.sponge.restapi.model.response.SubmitActionArgsResponse;
import org.openksavi.sponge.restapi.type.converter.DefaultTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.util.DataTypeUtils;

/**
 * A base Sponge REST API client.
 */
public abstract class BaseSpongeRestClient implements SpongeRestClient {

    protected static final boolean DEFAULT_ALLOW_FETCH_METADATA = true;

    protected static final boolean DEFAULT_ALLOW_FETCH_EVENT_TYPE = true;

    private SpongeRestClientConfiguration configuration;

    private AtomicLong currentRequestId = new AtomicLong(0);

    private AtomicReference<String> currentAuthToken = new AtomicReference<>();

    private TypeConverter typeConverter;

    private Lock lock = new ReentrantLock(true);

    private LoadingCache<String, RestActionMeta> actionMetaCache;

    private LoadingCache<String, RecordType> eventTypeCache;

    private Map<String, Object> featuresCache;

    protected List<OnRequestSerializedListener> onRequestSerializedListeners = new CopyOnWriteArrayList<>();

    protected List<OnResponseDeserializedListener> onResponseDeserializedListeners = new CopyOnWriteArrayList<>();

    public BaseSpongeRestClient(SpongeRestClientConfiguration configuration) {
        setConfiguration(configuration);
    }

    @Override
    public SpongeRestClientConfiguration getConfiguration() {
        return configuration;
    }

    protected void setConfiguration(SpongeRestClientConfiguration configuration) {
        this.configuration = configuration;

        applyConfiguration();

        initActionMetaCache();
        initEventTypeCache();
    }

    private void applyConfiguration() {
        ObjectMapper mapper = RestApiUtils.createObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, configuration.isPrettyPrint());

        typeConverter = new DefaultTypeConverter(mapper);
    }

    @Override
    public TypeConverter getTypeConverter() {
        return typeConverter;
    }

    @Override
    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    @Override
    public void addOnRequestSerializedListener(OnRequestSerializedListener listener) {
        onRequestSerializedListeners.add(listener);
    }

    @Override
    public boolean removeOnRequestSerializedListener(OnRequestSerializedListener listener) {
        return onRequestSerializedListeners.remove(listener);
    }

    @Override
    public void addOnResponseDeserializedListener(OnResponseDeserializedListener listener) {
        onResponseDeserializedListeners.add(listener);
    }

    @Override
    public boolean removeOnResponseDeserializedListener(OnResponseDeserializedListener listener) {
        return onResponseDeserializedListeners.remove(listener);
    }

    private void initActionMetaCache() {
        lock.lock();
        try {
            if (!configuration.isUseActionMetaCache()) {
                actionMetaCache = null;
            } else {
                Caffeine<Object, Object> builder = Caffeine.newBuilder();
                if (configuration.getActionMetaCacheMaxSize() > -1) {
                    builder.maximumSize(configuration.getActionMetaCacheMaxSize());
                }
                if (configuration.getActionMetaCacheExpireSeconds() > -1) {
                    builder.expireAfterWrite(configuration.getActionMetaCacheExpireSeconds(), TimeUnit.SECONDS);
                }

                actionMetaCache = builder.build(actionName -> fetchActionMeta(actionName, null));
            }
        } finally {
            lock.unlock();
        }
    }

    private void initEventTypeCache() {
        lock.lock();
        try {
            if (!configuration.isUseEventTypeCache()) {
                eventTypeCache = null;
            } else {
                Caffeine<Object, Object> builder = Caffeine.newBuilder();
                if (configuration.getEventTypeCacheMaxSize() > -1) {
                    builder.maximumSize(configuration.getEventTypeCacheMaxSize());
                }
                if (configuration.getEventTypeCacheExpireSeconds() > -1) {
                    builder.expireAfterWrite(configuration.getEventTypeCacheExpireSeconds(), TimeUnit.SECONDS);
                }

                eventTypeCache = builder.build(eventTypeName -> fetchEventType(eventTypeName, null));
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clearActionMetaCache() {
        lock.lock();
        try {
            if (actionMetaCache != null) {
                actionMetaCache.invalidateAll();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clearEventTypeCache() {
        lock.lock();
        try {
            if (eventTypeCache != null) {
                eventTypeCache.invalidateAll();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clearFeaturesCache() {
        lock.lock();
        try {
            featuresCache = null;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void clearCache() {
        lock.lock();
        try {
            clearActionMetaCache();
            clearEventTypeCache();
            clearFeaturesCache();
        } finally {
            lock.unlock();
        }
    }

    public LoadingCache<String, RestActionMeta> getActionMetaCache() {
        return actionMetaCache;
    }

    public void setActionMetaCache(LoadingCache<String, RestActionMeta> actionMetaCache) {
        this.actionMetaCache = actionMetaCache;
    }

    public LoadingCache<String, RecordType> getEventTypeCache() {
        return eventTypeCache;
    }

    public void setEventTypeCache(LoadingCache<String, RecordType> eventTypeCache) {
        this.eventTypeCache = eventTypeCache;
    }

    public ObjectMapper getObjectMapper() {
        return typeConverter.getObjectMapper();
    }

    protected final String getUrl(String operation) {
        String baseUrl = configuration.getUrl();

        return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + operation;
    }

    protected abstract <T extends SpongeRequest, R extends SpongeResponse> R doExecute(String operationType, T request,
            Class<R> responseClass, SpongeRequestContext context);

    @Override
    public <T extends SpongeRequest> T setupRequest(T request) {
        // Set empty header if none.
        if (request.getHeader() == null) {
            request.setHeader(new RequestHeader());
        }

        RequestHeader header = request.getHeader();

        if (configuration.isUseRequestId()) {
            header.setId(String.valueOf(currentRequestId.incrementAndGet()));
        }

        // Must be thread-safe.
        String authToken = currentAuthToken.get();
        if (authToken != null) {
            if (header.getAuthToken() == null) {
                header.setAuthToken(authToken);
            }
        } else {
            if (configuration.getUsername() != null && header.getUsername() == null) {
                header.setUsername(configuration.getUsername());
            }

            if (configuration.getPassword() != null && header.getPassword() == null) {
                header.setPassword(configuration.getPassword());
            }
        }

        return request;
    }

    protected <T extends SpongeResponse> T setupResponse(String operation, T response) {
        // Set empty header if none.
        if (response.getHeader() == null) {
            response.setHeader(new ResponseHeader());
        }

        ResponseHeader header = response.getHeader();
        handleResponseHeader(operation, header.getErrorCode(), header.getErrorMessage(), header.getDetailedErrorMessage());

        return response;
    }

    @Override
    public void handleResponseHeader(String operation, String errorCode, String errorMessage, String detailedErrorMessage) {
        if (errorCode != null) {
            if (configuration.isThrowExceptionOnErrorResponse()) {
                String message = errorMessage != null ? errorMessage : String.format("Error code: %s", errorCode);

                ErrorResponseException exception;
                switch (errorCode) {
                case RestApiConstants.ERROR_CODE_INVALID_AUTH_TOKEN:
                    exception = new InvalidAuthTokenException(message);
                    break;
                case RestApiConstants.ERROR_CODE_INVALID_KB_VERSION:
                    exception = new InvalidKnowledgeBaseVersionException(message);
                    break;
                case RestApiConstants.ERROR_CODE_INVALID_USERNAME_PASSWORD:
                    exception = new InvalidUsernamePasswordException(message);
                    break;
                default:
                    exception = new ErrorResponseException(message);
                }

                exception.setErrorCode(errorCode);
                exception.setDetailedErrorMessage(detailedErrorMessage);

                throw exception;
            }
        }
    }

    @Override
    public <T extends SpongeRequest, R extends SpongeResponse> R execute(String operationType, T request, Class<R> responseClass,
            SpongeRequestContext context) {
        if (context == null) {
            context = SpongeRequestContext.builder().build();
        }

        // Set empty header if none.
        if (request.getHeader() == null) {
            request.setHeader(new RequestHeader());
        }

        final SpongeRequestContext finalContext = context;

        return executeWithAuthentication(request, request.getHeader().getUsername(), request.getHeader().getPassword(),
                request.getHeader().getAuthToken(), (req) -> executeDelegate(operationType, req, responseClass, finalContext), () -> {
                    request.getHeader().setAuthToken(null);
                    return request;
                });
    }

    @Override
    public <T extends SpongeRequest, R extends SpongeResponse> R execute(String operationType, T request, Class<R> responseClass) {
        return execute(operationType, request, responseClass, null);
    }

    protected boolean isRequestAnonymous(String requestUsername, String requestPassword) {
        return configuration.getUsername() == null && requestUsername == null && configuration.getPassword() == null
                && requestPassword == null;
    }

    @Override
    public <T, X> X executeWithAuthentication(T request, String requestUsername, String requestPassword, String requestAuthToken,
            Function<T, X> onExecute, Supplier<T> onClearAuthToken) {
        try {
            if (configuration.isAutoUseAuthToken() && currentAuthToken.get() == null && requestAuthToken == null
                    && !isRequestAnonymous(requestUsername, requestPassword)) {
                login();
            }

            return onExecute.apply(request);
        } catch (InvalidAuthTokenException e) {
            // Relogin if set up and necessary.
            if (currentAuthToken.get() != null && configuration.isRelogin()) {
                login();

                // Clear the request auth token and setup a new request.
                T newRequest = onClearAuthToken.get();

                return onExecute.apply(newRequest);
            } else {
                throw e;
            }
        }
    }

    protected <T extends SpongeRequest, R extends SpongeResponse> R executeDelegate(String operationType, T request, Class<R> responseClass,
            SpongeRequestContext context) {
        if (context == null) {
            context = SpongeRequestContext.builder().build();
        }

        return setupResponse(operationType, doExecute(operationType, setupRequest(request), responseClass, context));
    }

    @Override
    public GetVersionResponse getVersion(GetVersionRequest request, SpongeRequestContext context) {
        return execute(RestApiConstants.OPERATION_VERSION, request, GetVersionResponse.class, context);
    }

    @Override
    public GetVersionResponse getVersion(GetVersionRequest request) {
        return getVersion(request, null);
    }

    @Override
    public String getVersion() {
        return getVersion(new GetVersionRequest()).getVersion();
    }

    @Override
    public GetFeaturesResponse getFeatures(GetFeaturesRequest request, SpongeRequestContext context) {
        return execute(RestApiConstants.OPERATION_FEATURES, request, GetFeaturesResponse.class, context);
    }

    @Override
    public GetFeaturesResponse getFeatures(GetFeaturesRequest request) {
        return getFeatures(request, null);
    }

    @Override
    public Map<String, Object> getFeatures() {
        if (featuresCache == null) {
            lock.lock();
            try {
                if (featuresCache == null) {
                    featuresCache = getFeatures(new GetFeaturesRequest()).getFeatures();
                }
            } finally {
                lock.unlock();
            }
        }

        return featuresCache;
    }

    @Override
    public LoginResponse login(LoginRequest request, SpongeRequestContext context) {
        LoginResponse response;
        lock.lock();

        try {
            currentAuthToken.set(null);
            response = executeDelegate(RestApiConstants.OPERATION_LOGIN, request, LoginResponse.class, context);
            currentAuthToken.set(response.getAuthToken());
        } finally {
            lock.unlock();
        }

        return response;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        return login(request, null);
    }

    @Override
    public String login() {
        return login(new LoginRequest(configuration.getUsername(), configuration.getPassword())).getAuthToken();
    }

    @Override
    public LogoutResponse logout(LogoutRequest request, SpongeRequestContext context) {
        LogoutResponse response;
        lock.lock();

        try {
            response = execute(RestApiConstants.OPERATION_LOGOUT, request, LogoutResponse.class, context);
            currentAuthToken.set(null);
        } finally {
            lock.unlock();
        }

        return response;
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        return logout(request, null);
    }

    @Override
    public void logout() {
        logout(new LogoutRequest());
    }

    @Override
    public GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request, SpongeRequestContext context) {
        return execute(RestApiConstants.OPERATION_KNOWLEDGE_BASES, request, GetKnowledgeBasesResponse.class, context);
    }

    @Override
    public GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request) {
        return getKnowledgeBases(request, null);
    }

    @Override
    public List<RestKnowledgeBaseMeta> getKnowledgeBases() {
        return getKnowledgeBases(new GetKnowledgeBasesRequest()).getKnowledgeBases();
    }

    protected GetActionsResponse doGetActions(GetActionsRequest request, boolean populateCache, SpongeRequestContext context) {
        GetActionsResponse response = execute(RestApiConstants.OPERATION_ACTIONS, request, GetActionsResponse.class, context);

        if (response.getActions() != null) {
            response.getActions().forEach(actionMeta -> unmarshalActionMeta(actionMeta));

            // Populate the cache.
            if (populateCache && configuration.isUseActionMetaCache() && actionMetaCache != null) {
                response.getActions().forEach(actionMeta -> actionMetaCache.put(actionMeta.getName(), actionMeta));
            }
        }

        if (response.getTypes() != null) {
            response.getTypes().values().forEach(type -> unmarshalDataType(type));
        }

        return response;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void unmarshalDataType(DataType type) {
        type.setDefaultValue(typeConverter.unmarshal(type, type.getDefaultValue()));
    }

    protected void unmarshalActionMeta(RestActionMeta actionMeta) {
        if (actionMeta != null) {
            if (actionMeta.getArgs() != null) {
                actionMeta.getArgs().forEach(argType -> unmarshalDataType(argType));
            }

            if (actionMeta.getResult() != null) {
                unmarshalDataType(actionMeta.getResult());
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void unmarshalProvidedActionArgValues(RestActionMeta actionMeta, Map<String, ProvidedValue<?>> argValues) {
        if (argValues == null || actionMeta.getArgs() == null) {
            return;
        }

        argValues.forEach((argName, argValue) -> {
            DataType argType = actionMeta.getArg(argName);
            ((ProvidedValue) argValue).setValue(typeConverter.unmarshal(argType, argValue.getValue()));

            if (argValue.getAnnotatedValueSet() != null) {
                argValue.getAnnotatedValueSet().stream().filter(Objects::nonNull)
                        .forEach(annotatedValue -> annotatedValue.setValue(typeConverter.unmarshal(argType, annotatedValue.getValue())));
            }

            if (argValue.getAnnotatedElementValueSet() != null && DataTypeUtils.supportsElementValueSet(argType)) {
                argValue.getAnnotatedElementValueSet().stream().filter(Objects::nonNull).forEach(annotatedValue -> annotatedValue
                        .setValue(typeConverter.unmarshal(((ListType) argType).getElementType(), annotatedValue.getValue())));
            }
        });
    }

    @Override
    public GetActionsResponse getActions(GetActionsRequest request, SpongeRequestContext context) {
        return doGetActions(request, true, context);
    }

    @Override
    public GetActionsResponse getActions(GetActionsRequest request) {
        return getActions(request, null);
    }

    @Override
    public List<RestActionMeta> getActions(String name) {
        return getActions(name, null);
    }

    @Override
    public List<RestActionMeta> getActions(String name, Boolean metadataRequired) {
        GetActionsRequest request = new GetActionsRequest();
        request.setName(name);
        request.setMetadataRequired(metadataRequired);

        return getActions(request).getActions();
    }

    @Override
    public List<RestActionMeta> getActions() {
        return getActions(new GetActionsRequest()).getActions();
    }

    protected RestActionMeta fetchActionMeta(String actionName, SpongeRequestContext context) {
        GetActionsRequest request = new GetActionsRequest();
        request.setName(actionName);
        request.setMetadataRequired(true);

        List<RestActionMeta> actions = doGetActions(request, false, context).getActions();

        return actions != null ? actions.stream().findFirst().orElse(null) : null;
    }

    @Override
    public RestActionMeta getActionMeta(String actionName, boolean allowFetchMetadata, SpongeRequestContext context) {
        if (configuration.isUseActionMetaCache() && actionMetaCache != null) {
            RestActionMeta actionMeta = actionMetaCache.getIfPresent(actionName);
            if (actionMeta != null) {
                return actionMeta;
            }

            return allowFetchMetadata ? actionMetaCache.get(actionName) : null;
        } else {
            return allowFetchMetadata ? fetchActionMeta(actionName, context) : null;
        }
    }

    @Override
    public RestActionMeta getActionMeta(String actionName, boolean allowFetchMetadata) {
        return getActionMeta(actionName, allowFetchMetadata, null);
    }

    @Override
    public RestActionMeta getActionMeta(String actionName) {
        return getActionMeta(actionName, DEFAULT_ALLOW_FETCH_METADATA);
    }

    protected void setupActionExecutionRequest(RestActionMeta actionMeta, ActionExecutionRequest request) {
        // Conditionally set the verification of the processor qualified version on the server side.
        if (configuration.isVerifyProcessorVersion() && actionMeta != null && request.getQualifiedVersion() == null) {
            request.setQualifiedVersion(actionMeta.getQualifiedVersion());
        }

        Validate.isTrue(actionMeta == null || Objects.equals(actionMeta.getName(), request.getName()),
                "Action name '%s' in the metadata doesn't match the action name '%s' in the request",
                actionMeta != null ? actionMeta.getName() : null, request.getName());
    }

    protected ActionCallResponse doCall(RestActionMeta actionMeta, ActionCallRequest request, SpongeRequestContext context) {
        setupActionExecutionRequest(actionMeta, request);

        validateCallArgs(actionMeta, request.getArgs());

        request.setArgs(marshalActionCallArgs(actionMeta, request.getArgs()));

        ActionCallResponse response = execute(RestApiConstants.OPERATION_CALL, request, ActionCallResponse.class, context);

        unmarshalCallResult(actionMeta, response);

        return response;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void validateCallArgs(RestActionMeta actionMeta, List args) {
        if (actionMeta == null || actionMeta.getArgs() == null) {
            return;
        }

        int expectedAllArgCount = actionMeta.getArgs().size();
        int expectedNonOptionalArgCount = (int) actionMeta.getArgs().stream().filter(argType -> !argType.isOptional()).count();
        int actualArgCount = args != null ? args.size() : 0;

        if (expectedNonOptionalArgCount == expectedAllArgCount) {
            Validate.isTrue(expectedAllArgCount == actualArgCount, "Incorrect number of arguments. Expected %d but got %d",
                    expectedAllArgCount, actualArgCount);
        } else {
            Validate.isTrue(expectedNonOptionalArgCount <= actualArgCount && actualArgCount <= expectedAllArgCount,
                    "Incorrect number of arguments. Expected between %d and %d but got %d", expectedNonOptionalArgCount,
                    expectedAllArgCount, actualArgCount);
        }

        // Validate non-nullable arguments.
        for (int i = 0; i < actionMeta.getArgs().size(); i++) {
            DataType argType = actionMeta.getArgs().get(i);
            Validate.isTrue(argType.isOptional() || argType.isNullable() || args.get(i) != null, "Action argument '%s' is not set",
                    argType.getLabel() != null ? argType.getLabel() : argType.getName());
        }
    }

    protected List<Object> marshalActionCallArgs(RestActionMeta actionMeta, List<Object> args) {
        if (args == null || actionMeta == null || actionMeta.getArgs() == null) {
            return args;
        }

        List<Object> result = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            result.add(typeConverter.marshal(actionMeta.getArgs().get(i), args.get(i)));
        }

        return result;
    }

    protected Map<String, Object> marshalAuxiliaryActionArgsCurrent(RestActionMeta actionMeta, Map<String, Object> current) {
        if (current == null || actionMeta == null || actionMeta.getArgs() == null) {
            return current;
        }

        Map<String, Object> marshalled = new LinkedHashMap<>();
        current.forEach((name, value) -> marshalled.put(name, typeConverter.marshal(actionMeta.getArg(name), value)));

        return marshalled;
    }

    protected void unmarshalCallResult(RestActionMeta actionMeta, ActionCallResponse response) {
        if (actionMeta == null || actionMeta.getResult() == null || response.getResult() == null) {
            return;
        }

        response.setResult(typeConverter.unmarshal(actionMeta.getResult(), response.getResult()));
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request, RestActionMeta actionMeta, boolean allowFetchMetadata,
            SpongeRequestContext context) {
        return doCall(actionMeta != null ? actionMeta : getActionMeta(request.getName(), allowFetchMetadata), request, context);
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request, RestActionMeta actionMeta, boolean allowFetchMetadata) {
        return call(request, actionMeta, allowFetchMetadata, null);
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request, RestActionMeta actionMeta) {
        return call(request, actionMeta, true);
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request) {
        return call(request, null);
    }

    @Override
    public Object call(String actionName, List<Object> args) {
        return call(new ActionCallRequest(actionName, args)).getResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T call(Class<T> resultClass, String actionName, List<Object> args) {
        return (T) call(actionName, args);
    }

    @Override
    public Object call(String actionName) {
        return call(actionName, Collections.emptyList());
    }

    @Override
    public <T> T call(Class<T> resultClass, String actionName) {
        return call(resultClass, actionName, Collections.emptyList());
    }

    @Override
    public SendEventResponse send(SendEventRequest request, SpongeRequestContext context) {
        return execute(RestApiConstants.OPERATION_SEND, request, SendEventResponse.class, context);
    }

    @Override
    public SendEventResponse send(SendEventRequest request) {
        return send(request, null);
    }

    @Override
    public String send(String eventName, Map<String, Object> attributes, String label, String description) {
        return send(new SendEventRequest(eventName, attributes, label, description)).getEventId();
    }

    @Override
    public String send(String eventName, Map<String, Object> attributes, String label) {
        return send(eventName, attributes, label, null);
    }

    @Override
    public String send(String eventName, Map<String, Object> attributes) {
        return send(eventName, attributes, null, null);
    }

    @Override
    public ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request, SpongeRequestContext context) {
        RestActionMeta actionMeta = getActionMeta(request.getName());
        setupActionExecutionRequest(actionMeta, request);

        request.setCurrent(marshalAuxiliaryActionArgsCurrent(actionMeta, request.getCurrent()));

        ProvideActionArgsResponse response =
                execute(RestApiConstants.OPERATION_PROVIDE_ACTION_ARGS, request, ProvideActionArgsResponse.class, context);

        if (actionMeta != null) {
            unmarshalProvidedActionArgValues(actionMeta, response.getProvided());
        }

        return response;
    }

    @Override
    public ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request) {
        return provideActionArgs(request, null);
    }

    @Override
    public Map<String, ProvidedValue<?>> provideActionArgs(String actionName, List<String> argNames, Map<String, Object> current) {
        return provideActionArgs(new ProvideActionArgsRequest(actionName, argNames, current)).getProvided();
    }

    @Override
    public Map<String, ProvidedValue<?>> provideActionArgs(String actionName) {
        return provideActionArgs(actionName, null, null);
    }

    @Override
    public SubmitActionArgsResponse submitActionArgs(SubmitActionArgsRequest request, SpongeRequestContext context) {
        RestActionMeta actionMeta = getActionMeta(request.getName());
        setupActionExecutionRequest(actionMeta, request);

        request.setCurrent(marshalAuxiliaryActionArgsCurrent(actionMeta, request.getCurrent()));

        return execute(RestApiConstants.OPERATION_SUBMIT_ACTION_ARGS, request, SubmitActionArgsResponse.class, context);
    }

    @Override
    public SubmitActionArgsResponse submitActionArgs(SubmitActionArgsRequest request) {
        return submitActionArgs(request, null);
    }

    @Override
    public void submitActionArgs(String actionName, List<String> argNames, Map<String, Object> current) {
        submitActionArgs(new SubmitActionArgsRequest(actionName, argNames, current));
    }

    protected GetEventTypesResponse doGetEventTypes(GetEventTypesRequest request, boolean populateCache, SpongeRequestContext context) {
        GetEventTypesResponse response = execute(RestApiConstants.OPERATION_EVENT_TYPES, request, GetEventTypesResponse.class, context);

        if (response != null && response.getEventTypes() != null) {
            response.getEventTypes().values().forEach(this::unmarshalDataType);

            // Populate the cache.
            if (populateCache && configuration.isUseEventTypeCache() && eventTypeCache != null) {
                response.getEventTypes().entrySet().forEach(entry -> eventTypeCache.put(entry.getKey(), entry.getValue()));
            }
        }

        return response;
    }

    @Override
    public GetEventTypesResponse getEventTypes(GetEventTypesRequest request, SpongeRequestContext context) {
        return doGetEventTypes(request, true, context);
    }

    @Override
    public GetEventTypesResponse getEventTypes(GetEventTypesRequest request) {
        return getEventTypes(request, null);
    }

    @Override
    public Map<String, RecordType> getEventTypes(String eventName) {
        return getEventTypes(new GetEventTypesRequest(eventName)).getEventTypes();
    }

    protected RecordType fetchEventType(String eventTypeName, SpongeRequestContext context) {
        return doGetEventTypes(new GetEventTypesRequest(eventTypeName), false, context).getEventTypes().get(eventTypeName);
    }

    @Override
    public RecordType getEventType(String eventTypeName, boolean allowFetchEventType, SpongeRequestContext context) {
        if (configuration.isUseEventTypeCache() && eventTypeCache != null) {
            RecordType eventType = eventTypeCache.getIfPresent(eventTypeName);
            if (eventType != null) {
                return eventType;
            }

            return allowFetchEventType ? eventTypeCache.get(eventTypeName) : null;
        } else {
            return allowFetchEventType ? fetchEventType(eventTypeName, context) : null;
        }
    }

    @Override
    public RecordType getEventType(String eventTypeName, boolean allowFetchEventType) {
        return getEventType(eventTypeName, allowFetchEventType, null);
    }

    @Override
    public RecordType getEventType(String eventTypeName) {
        return getEventType(eventTypeName, DEFAULT_ALLOW_FETCH_EVENT_TYPE);
    }

    @Override
    public ReloadResponse reload(ReloadRequest request, SpongeRequestContext context) {
        return execute(RestApiConstants.OPERATION_RELOAD, request, ReloadResponse.class, context);
    }

    @Override
    public ReloadResponse reload(ReloadRequest request) {
        return reload(request, null);
    }

    @Override
    public void reload() {
        reload(new ReloadRequest());
    }

    @Override
    public void clearSession() {
        lock.lock();

        try {
            currentAuthToken.set(null);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Long getCurrentRequestId() {
        return currentRequestId.get();
    }

    @Override
    public String getCurrentAuthToken() {
        return currentAuthToken.get();
    }
}
