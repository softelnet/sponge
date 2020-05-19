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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.InactiveActionException;
import org.openksavi.sponge.action.ProvideArgsParameters;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.client.listener.OnRequestSerializedListener;
import org.openksavi.sponge.remoteapi.client.listener.OnResponseDeserializedListener;
import org.openksavi.sponge.remoteapi.feature.converter.DefaultFeatureConverter;
import org.openksavi.sponge.remoteapi.feature.converter.FeatureConverter;
import org.openksavi.sponge.remoteapi.feature.converter.FeaturesUtils;
import org.openksavi.sponge.remoteapi.model.RemoteActionMeta;
import org.openksavi.sponge.remoteapi.model.RemoteEvent;
import org.openksavi.sponge.remoteapi.model.RemoteKnowledgeBaseMeta;
import org.openksavi.sponge.remoteapi.model.request.ActionCallRequest;
import org.openksavi.sponge.remoteapi.model.request.ActionExecutionInfo;
import org.openksavi.sponge.remoteapi.model.request.GetActionsRequest;
import org.openksavi.sponge.remoteapi.model.request.GetActionsRequest.GetActionsRequestBody;
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
import org.openksavi.sponge.remoteapi.model.request.RequestHeader;
import org.openksavi.sponge.remoteapi.model.request.SendEventRequest;
import org.openksavi.sponge.remoteapi.model.request.SendEventRequest.SendEventRequestBody;
import org.openksavi.sponge.remoteapi.model.request.SpongeRequest;
import org.openksavi.sponge.remoteapi.model.response.ActionCallResponse;
import org.openksavi.sponge.remoteapi.model.response.BodySpongeResponse;
import org.openksavi.sponge.remoteapi.model.response.GetActionsResponse;
import org.openksavi.sponge.remoteapi.model.response.GetActionsResponse.GetActionsResponseBody;
import org.openksavi.sponge.remoteapi.model.response.GetEventTypesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetFeaturesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetKnowledgeBasesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetVersionResponse;
import org.openksavi.sponge.remoteapi.model.response.IsActionActiveResponse;
import org.openksavi.sponge.remoteapi.model.response.LoginResponse;
import org.openksavi.sponge.remoteapi.model.response.LogoutResponse;
import org.openksavi.sponge.remoteapi.model.response.ProvideActionArgsResponse;
import org.openksavi.sponge.remoteapi.model.response.ReloadResponse;
import org.openksavi.sponge.remoteapi.model.response.ResponseHeader;
import org.openksavi.sponge.remoteapi.model.response.SendEventResponse;
import org.openksavi.sponge.remoteapi.model.response.SpongeResponse;
import org.openksavi.sponge.remoteapi.type.converter.BaseTypeConverter;
import org.openksavi.sponge.remoteapi.type.converter.DefaultTypeConverter;
import org.openksavi.sponge.remoteapi.type.converter.TypeConverter;
import org.openksavi.sponge.remoteapi.type.converter.unit.ObjectTypeUnitConverter;
import org.openksavi.sponge.remoteapi.util.RemoteApiUtils;
import org.openksavi.sponge.type.DataType;
import org.openksavi.sponge.type.DataTypeKind;
import org.openksavi.sponge.type.ListType;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.TypeType;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.AnnotatedValue;
import org.openksavi.sponge.util.DataTypeUtils;

/**
 * A base Sponge Remote API client.
 */
public abstract class BaseSpongeClient implements SpongeClient {

    protected static final boolean DEFAULT_ALLOW_FETCH_METADATA = true;

    protected static final boolean DEFAULT_ALLOW_FETCH_EVENT_TYPE = true;

    private SpongeClientConfiguration configuration;

    private AtomicLong currentRequestId = new AtomicLong(0);

    private AtomicReference<String> currentAuthToken = new AtomicReference<>();

    private TypeConverter typeConverter;

    private FeatureConverter featureConverter;

    private Lock lock = new ReentrantLock(true);

    private LoadingCache<String, RemoteActionMeta> actionMetaCache;

    private LoadingCache<String, RecordType> eventTypeCache;

    private Map<String, Object> featuresCache;

    protected List<OnRequestSerializedListener> onRequestSerializedListeners = new CopyOnWriteArrayList<>();

    protected List<OnResponseDeserializedListener> onResponseDeserializedListeners = new CopyOnWriteArrayList<>();

    public BaseSpongeClient(SpongeClientConfiguration configuration) {
        setConfiguration(configuration);
    }

    @Override
    public SpongeClientConfiguration getConfiguration() {
        return configuration;
    }

    protected void setConfiguration(SpongeClientConfiguration configuration) {
        this.configuration = configuration;

        applyConfiguration();

        initActionMetaCache();
        initEventTypeCache();
    }

    private void applyConfiguration() {
        ObjectMapper mapper = RemoteApiUtils.createObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, configuration.isPrettyPrint());
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        typeConverter = new DefaultTypeConverter(mapper);
        initObjectTypeMarshalers(typeConverter);

        featureConverter = new DefaultFeatureConverter(mapper);

        typeConverter.setFeatureConverter(featureConverter);
    }

    protected void initObjectTypeMarshalers(TypeConverter typeConverter) {
        ObjectTypeUnitConverter objectConverter =
                (ObjectTypeUnitConverter) ((BaseTypeConverter) typeConverter).getInternalUnitConverter(DataTypeKind.OBJECT);

        if (objectConverter == null) {
            return;
        }

        // Add RemoteEvent marshaler and unmarshaler.
        objectConverter.addMarshaler(RemoteApiConstants.REMOTE_EVENT_OBJECT_TYPE_CLASS_NAME, (TypeConverter converter,
                Object value) -> RemoteApiUtils.marshalRemoteEvent((RemoteEvent) value, converter, eventName -> getEventType(eventName)));
        objectConverter.addUnmarshaler(RemoteApiConstants.REMOTE_EVENT_OBJECT_TYPE_CLASS_NAME, (TypeConverter converter,
                Object value) -> RemoteApiUtils.unmarshalRemoteEvent(value, converter, eventName -> getEventType(eventName)));
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
    public FeatureConverter getFeatureConverter() {
        return featureConverter;
    }

    @Override
    public void setFeatureConverter(FeatureConverter featureConverter) {
        this.featureConverter = featureConverter;
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

    public LoadingCache<String, RemoteActionMeta> getActionMetaCache() {
        return actionMetaCache;
    }

    public void setActionMetaCache(LoadingCache<String, RemoteActionMeta> actionMetaCache) {
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

        if (configuration.getFeatures() != null && header.getFeatures() == null) {
            header.setFeatures(configuration.getFeatures());
        }

        return request;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected <T extends SpongeResponse> T setupResponse(String operation, T response) {
        // Set empty header if none.
        if (response.getHeader() == null) {
            response.setHeader(new ResponseHeader());
        }

        // Set empty body if none.
        if (response instanceof BodySpongeResponse) {
            BodySpongeResponse bodyResponse = (BodySpongeResponse) response;
            if (bodyResponse.getBody() == null) {
                bodyResponse.setBody(bodyResponse.createBody());
            }
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

                RuntimeException exception;
                switch (errorCode) {
                case RemoteApiConstants.ERROR_CODE_INVALID_AUTH_TOKEN:
                    exception = new InvalidAuthTokenException(message);
                    break;
                case RemoteApiConstants.ERROR_CODE_INVALID_KB_VERSION:
                    exception = new InvalidKnowledgeBaseVersionException(message);
                    break;
                case RemoteApiConstants.ERROR_CODE_INVALID_USERNAME_PASSWORD:
                    exception = new InvalidUsernamePasswordException(message);
                    break;
                case RemoteApiConstants.ERROR_CODE_INACTIVE_ACTION:
                    exception = new InactiveActionException(message);
                    break;
                default:
                    exception = new ErrorResponseException(message);
                }

                if (exception instanceof ErrorResponseException) {
                    ((ErrorResponseException) exception).setErrorCode(errorCode);
                    ((ErrorResponseException) exception).setDetailedErrorMessage(detailedErrorMessage);
                }

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
        return execute(RemoteApiConstants.OPERATION_VERSION, request, GetVersionResponse.class, context);
    }

    @Override
    public GetVersionResponse getVersion(GetVersionRequest request) {
        return getVersion(request, null);
    }

    @Override
    public String getVersion() {
        return getVersion(new GetVersionRequest()).getBody().getVersion();
    }

    @Override
    public GetFeaturesResponse getFeatures(GetFeaturesRequest request, SpongeRequestContext context) {
        return execute(RemoteApiConstants.OPERATION_FEATURES, request, GetFeaturesResponse.class, context);
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
                    featuresCache = getFeatures(new GetFeaturesRequest()).getBody().getFeatures();
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
            response = executeDelegate(RemoteApiConstants.OPERATION_LOGIN, request, LoginResponse.class, context);
            currentAuthToken.set(response.getBody().getAuthToken());
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
        return login(new LoginRequest(configuration.getUsername(), configuration.getPassword())).getBody().getAuthToken();
    }

    @Override
    public LogoutResponse logout(LogoutRequest request, SpongeRequestContext context) {
        LogoutResponse response;
        lock.lock();

        try {
            response = execute(RemoteApiConstants.OPERATION_LOGOUT, request, LogoutResponse.class, context);
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
        return execute(RemoteApiConstants.OPERATION_KNOWLEDGE_BASES, request, GetKnowledgeBasesResponse.class, context);
    }

    @Override
    public GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request) {
        return getKnowledgeBases(request, null);
    }

    @Override
    public List<RemoteKnowledgeBaseMeta> getKnowledgeBases() {
        return getKnowledgeBases(new GetKnowledgeBasesRequest()).getBody().getKnowledgeBases();
    }

    protected GetActionsResponse doGetActions(GetActionsRequest request, boolean populateCache, SpongeRequestContext context) {
        GetActionsResponse response = execute(RemoteApiConstants.OPERATION_ACTIONS, request, GetActionsResponse.class, context);
        GetActionsResponseBody responseBody = response.getBody();

        if (responseBody.getActions() != null) {
            responseBody.getActions().forEach(actionMeta -> unmarshalActionMeta(actionMeta));

            // Populate the cache.
            if (populateCache && configuration.isUseActionMetaCache() && actionMetaCache != null) {
                responseBody.getActions().forEach(actionMeta -> actionMetaCache.put(actionMeta.getName(), actionMeta));
            }
        }

        if (responseBody.getTypes() != null) {
            responseBody.getTypes().values().forEach(type -> unmarshalDataType(type));
        }

        return response;
    }

    @SuppressWarnings({ "rawtypes" })
    protected DataType marshalDataType(DataType type) {
        return (DataType) typeConverter.marshal(new TypeType(), type);
    }

    @SuppressWarnings({ "rawtypes" })
    protected DataType unmarshalDataType(DataType type) {
        return (DataType) typeConverter.unmarshal(new TypeType(), type);
    }

    @SuppressWarnings("rawtypes")
    protected void unmarshalActionMeta(RemoteActionMeta actionMeta) {
        if (actionMeta != null) {
            List<DataType> args = actionMeta.getArgs();
            if (args != null) {
                for (int i = 0; i < args.size(); i++) {
                    args.set(i, unmarshalDataType(args.get(i)));
                }
            }

            if (actionMeta.getResult() != null) {
                actionMeta.setResult(unmarshalDataType(actionMeta.getResult()));
            }

            actionMeta.setFeatures(FeaturesUtils.unmarshal(featureConverter, actionMeta.getFeatures()));
            if (actionMeta.getCategory() != null) {
                actionMeta.getCategory().setFeatures(FeaturesUtils.unmarshal(featureConverter, actionMeta.getCategory().getFeatures()));
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void unmarshalProvidedActionArgValues(RemoteActionMeta actionMeta, Map<String, ProvidedValue<?>> argValues) {
        if (argValues == null || actionMeta.getArgs() == null) {
            return;
        }

        argValues.forEach((argName, argValue) -> {
            DataType argType = actionMeta.getArg(argName);
            ((ProvidedValue) argValue).setValue(typeConverter.unmarshal(argType, argValue.getValue()));

            if (argValue.getAnnotatedValueSet() != null) {
                argValue.getAnnotatedValueSet().stream().filter(Objects::nonNull)
                        .forEach(annotatedValue -> ((AnnotatedValue) annotatedValue)
                                .setValue(typeConverter.unmarshal(argType, annotatedValue.getValue())));
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
    public List<RemoteActionMeta> getActions(String name) {
        return getActions(name, null);
    }

    @Override
    public List<RemoteActionMeta> getActions(String name, Boolean metadataRequired) {
        GetActionsRequestBody body = new GetActionsRequestBody();
        body.setName(name);
        body.setMetadataRequired(metadataRequired);

        return getActions(new GetActionsRequest(body)).getBody().getActions();
    }

    @Override
    public List<RemoteActionMeta> getActions() {
        return getActions(new GetActionsRequest()).getBody().getActions();
    }

    protected RemoteActionMeta fetchActionMeta(String actionName, SpongeRequestContext context) {
        GetActionsRequestBody body = new GetActionsRequestBody();
        body.setName(actionName);
        body.setMetadataRequired(true);

        List<RemoteActionMeta> actions = doGetActions(new GetActionsRequest(body), false, context).getBody().getActions();

        return actions != null ? actions.stream().findFirst().orElse(null) : null;
    }

    @Override
    public RemoteActionMeta getActionMeta(String actionName, boolean allowFetchMetadata, SpongeRequestContext context) {
        if (configuration.isUseActionMetaCache() && actionMetaCache != null) {
            RemoteActionMeta actionMeta = actionMetaCache.getIfPresent(actionName);
            if (actionMeta != null) {
                return actionMeta;
            }

            return allowFetchMetadata ? actionMetaCache.get(actionName) : null;
        } else {
            return allowFetchMetadata ? fetchActionMeta(actionName, context) : null;
        }
    }

    @Override
    public RemoteActionMeta getActionMeta(String actionName, boolean allowFetchMetadata) {
        return getActionMeta(actionName, allowFetchMetadata, null);
    }

    @Override
    public RemoteActionMeta getActionMeta(String actionName) {
        return getActionMeta(actionName, DEFAULT_ALLOW_FETCH_METADATA);
    }

    protected void setupActionExecutionInfo(RemoteActionMeta actionMeta, ActionExecutionInfo info) {
        // Conditionally set the verification of the processor qualified version on the server side.
        if (configuration.isVerifyProcessorVersion() && actionMeta != null && info.getQualifiedVersion() == null) {
            info.setQualifiedVersion(actionMeta.getQualifiedVersion());
        }

        Validate.isTrue(actionMeta == null || Objects.equals(actionMeta.getName(), info.getName()),
                "Action name '%s' in the metadata doesn't match the action name '%s' in the request",
                actionMeta != null ? actionMeta.getName() : null, info.getName());
    }

    protected ActionCallResponse doCall(RemoteActionMeta actionMeta, ActionCallRequest request, SpongeRequestContext context) {
        setupActionExecutionInfo(actionMeta, request.getBody());

        validateCallArgs(actionMeta, request.getBody().getArgs());

        request.getBody().setArgs(marshalActionCallArgs(actionMeta, request.getBody().getArgs()));

        ActionCallResponse response = execute(RemoteApiConstants.OPERATION_CALL, request, ActionCallResponse.class, context);

        unmarshalCallResult(actionMeta, response);

        return response;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public void validateCallArgs(RemoteActionMeta actionMeta, List args) {
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

    protected List<Object> marshalActionCallArgs(RemoteActionMeta actionMeta, List<Object> args) {
        if (args == null || actionMeta == null || actionMeta.getArgs() == null) {
            return args;
        }

        List<Object> result = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            result.add(typeConverter.marshal(actionMeta.getArgs().get(i), args.get(i)));
        }

        return result;
    }

    @SuppressWarnings("rawtypes")
    protected Map<String, Object> marshalAuxiliaryActionArgsCurrent(RemoteActionMeta actionMeta, Map<String, Object> current,
            Map<String, DataType> dynamicTypes) {
        Map<String, Object> marshalled = new LinkedHashMap<>();

        if (current != null) {
            if (actionMeta == null || actionMeta.getArgs() == null) {
                // Not marshalled.
                marshalled.putAll(current);
            } else {
                current.forEach((name, value) -> marshalled.put(name, typeConverter.marshal(
                        dynamicTypes != null && dynamicTypes.containsKey(name) ? dynamicTypes.get(name) : actionMeta.getArg(name), value)));
            }
        }

        return marshalled;
    }

    protected void unmarshalCallResult(RemoteActionMeta actionMeta, ActionCallResponse response) {
        if (actionMeta == null || actionMeta.getResult() == null || response.getBody().getResult() == null) {
            return;
        }

        response.getBody().setResult(typeConverter.unmarshal(actionMeta.getResult(), response.getBody().getResult()));
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request, RemoteActionMeta actionMeta, boolean allowFetchMetadata,
            SpongeRequestContext context) {
        return doCall(actionMeta != null ? actionMeta : getActionMeta(request.getBody().getName(), allowFetchMetadata), request, context);
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request, RemoteActionMeta actionMeta, boolean allowFetchMetadata) {
        return call(request, actionMeta, allowFetchMetadata, null);
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request, RemoteActionMeta actionMeta) {
        return call(request, actionMeta, true);
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request) {
        return call(request, null);
    }

    @Override
    public Object call(String actionName, List<Object> args) {
        return call(new ActionCallRequest(actionName, args)).getBody().getResult();
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
        SendEventRequestBody body = request.getBody();

        // Use a temporary RemoteEvent to marshal attributes and features.
        RemoteEvent event = new RemoteEvent(null, body.getName(), null, 0, body.getLabel(), body.getDescription(), body.getAttributes(),
                body.getFeatures());
        event = RemoteApiUtils.marshalRemoteEvent(event, typeConverter, eventName -> getEventType(eventName));

        // Set marshalled fields.
        body.setAttributes(event.getAttributes());
        body.setFeatures(event.getFeatures());

        return execute(RemoteApiConstants.OPERATION_SEND, request, SendEventResponse.class, context);
    }

    @Override
    public SendEventResponse send(SendEventRequest request) {
        return send(request, null);
    }

    @Override
    public String send(String eventName, Map<String, Object> attributes, String label, String description, Map<String, Object> features) {
        return send(new SendEventRequest(eventName, attributes, label, description, features)).getBody().getEventId();
    }

    @Override
    public String send(String eventName, Map<String, Object> attributes, String label, String description) {
        return send(eventName, attributes, label, description, null);
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
    public IsActionActiveResponse isActionActive(IsActionActiveRequest request, SpongeRequestContext context) {
        if (request.getBody().getEntries() != null) {
            request.getBody().getEntries().forEach(entry -> {
                RemoteActionMeta actionMeta = getActionMeta(entry.getName());
                setupActionExecutionInfo(actionMeta, entry);

                if (entry.getContextType() != null) {
                    entry.setContextType(marshalDataType(entry.getContextType()));
                }

                if (entry.getContextValue() != null && entry.getContextType() != null) {
                    entry.setContextValue(typeConverter.marshal(entry.getContextType(), entry.getContextValue()));
                }

                if (entry.getArgs() != null) {
                    entry.setArgs(marshalActionCallArgs(actionMeta, entry.getArgs()));
                }

                entry.setFeatures(FeaturesUtils.marshal(featureConverter, entry.getFeatures()));
            });
        }

        return execute(RemoteApiConstants.OPERATION_IS_ACTION_ACTIVE, request, IsActionActiveResponse.class, context);
    }

    @Override
    public IsActionActiveResponse isActionActive(IsActionActiveRequest request) {
        return isActionActive(request, null);
    }

    @Override
    public List<Boolean> isActionActive(List<IsActionActiveEntry> entries) {
        boolean areActivatableActions = false;
        for (IsActionActiveEntry entry : entries) {
            RemoteActionMeta actionMeta = getActionMeta(entry.getName(), false);
            if (actionMeta != null ? actionMeta.isActivatable() : true) {
                areActivatableActions = true;
                break;
            }
        }

        // No need to connect to the server.
        if (!areActivatableActions) {
            return Stream.generate(() -> Boolean.TRUE).limit(entries.size()).collect(Collectors.toList());
        }

        // Clone all entries in order to modify their copies later.
        entries = entries.stream().map((entry) -> entry != null ? entry.clone() : null).collect(Collectors.toList());

        return isActionActive(new IsActionActiveRequest(entries)).getBody().getActive();
    }

    @Override
    public ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request, SpongeRequestContext context) {
        RemoteActionMeta actionMeta = getActionMeta(request.getBody().getName());

        setupActionExecutionInfo(actionMeta, request.getBody());

        request.getBody().setCurrent(
                marshalAuxiliaryActionArgsCurrent(actionMeta, request.getBody().getCurrent(), request.getBody().getDynamicTypes()));

        // Clone and marshal all argument features.
        request.getBody().setArgFeatures(marshalProvideArgsFeaturesMap(request.getBody().getArgFeatures()));

        ProvideActionArgsResponse response =
                execute(RemoteApiConstants.OPERATION_PROVIDE_ACTION_ARGS, request, ProvideActionArgsResponse.class, context);

        if (actionMeta != null) {
            unmarshalProvidedActionArgValues(actionMeta, response.getBody().getProvided());
        }

        return response;
    }

    protected Map<String, Map<String, Object>> marshalProvideArgsFeaturesMap(Map<String, Map<String, Object>> featuresMap) {
        if (featuresMap == null) {
            return null;
        }

        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : featuresMap.entrySet()) {
            result.put(entry.getKey(), FeaturesUtils.marshal(featureConverter, entry.getValue()));
        }

        return result;
    }

    @Override
    public ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request) {
        return provideActionArgs(request, null);
    }

    @Override
    public Map<String, ProvidedValue<?>> provideActionArgs(String actionName, ProvideArgsParameters parameters) {
        return provideActionArgs(new ProvideActionArgsRequest(actionName, parameters.getProvide(), parameters.getSubmit(),
                parameters.getCurrent(), parameters.getDynamicTypes(), parameters.getArgFeatures())).getBody().getProvided();
    }

    protected GetEventTypesResponse doGetEventTypes(GetEventTypesRequest request, boolean populateCache, SpongeRequestContext context) {
        GetEventTypesResponse response = execute(RemoteApiConstants.OPERATION_EVENT_TYPES, request, GetEventTypesResponse.class, context);

        if (response != null && response.getBody().getEventTypes() != null) {
            response.getBody().getEventTypes().entrySet()
                    .forEach(entry -> entry.setValue((RecordType) unmarshalDataType(entry.getValue())));

            // Populate the cache.
            if (populateCache && configuration.isUseEventTypeCache() && eventTypeCache != null) {
                response.getBody().getEventTypes().entrySet().forEach(entry -> eventTypeCache.put(entry.getKey(), entry.getValue()));
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
        return getEventTypes(new GetEventTypesRequest(eventName)).getBody().getEventTypes();
    }

    protected RecordType fetchEventType(String eventTypeName, SpongeRequestContext context) {
        return doGetEventTypes(new GetEventTypesRequest(eventTypeName), false, context).getBody().getEventTypes().get(eventTypeName);
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
        return execute(RemoteApiConstants.OPERATION_RELOAD, request, ReloadResponse.class, context);
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
