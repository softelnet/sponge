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
import org.openksavi.sponge.remoteapi.model.request.ActionCallRequest.ActionCallParams;
import org.openksavi.sponge.remoteapi.model.request.ActionExecutionInfo;
import org.openksavi.sponge.remoteapi.model.request.GetActionsRequest;
import org.openksavi.sponge.remoteapi.model.request.GetActionsRequest.GetActionsParams;
import org.openksavi.sponge.remoteapi.model.request.GetEventTypesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetEventTypesRequest.GetEventTypesParams;
import org.openksavi.sponge.remoteapi.model.request.GetFeaturesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.remoteapi.model.request.GetVersionRequest;
import org.openksavi.sponge.remoteapi.model.request.IsActionActiveRequest;
import org.openksavi.sponge.remoteapi.model.request.IsActionActiveRequest.IsActionActiveEntry;
import org.openksavi.sponge.remoteapi.model.request.IsActionActiveRequest.IsActionActiveParams;
import org.openksavi.sponge.remoteapi.model.request.LoginRequest;
import org.openksavi.sponge.remoteapi.model.request.LogoutRequest;
import org.openksavi.sponge.remoteapi.model.request.ProvideActionArgsRequest;
import org.openksavi.sponge.remoteapi.model.request.ProvideActionArgsRequest.ProvideActionArgsParams;
import org.openksavi.sponge.remoteapi.model.request.ReloadRequest;
import org.openksavi.sponge.remoteapi.model.request.RequestHeader;
import org.openksavi.sponge.remoteapi.model.request.SendEventRequest;
import org.openksavi.sponge.remoteapi.model.request.SendEventRequest.SendEventParams;
import org.openksavi.sponge.remoteapi.model.request.SpongeRequest;
import org.openksavi.sponge.remoteapi.model.response.ActionCallResponse;
import org.openksavi.sponge.remoteapi.model.response.GetActionsResponse;
import org.openksavi.sponge.remoteapi.model.response.GetActionsResponse.GetActionsResult;
import org.openksavi.sponge.remoteapi.model.response.GetEventTypesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetFeaturesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetKnowledgeBasesResponse;
import org.openksavi.sponge.remoteapi.model.response.GetVersionResponse;
import org.openksavi.sponge.remoteapi.model.response.IsActionActiveResponse;
import org.openksavi.sponge.remoteapi.model.response.LoginResponse;
import org.openksavi.sponge.remoteapi.model.response.LogoutResponse;
import org.openksavi.sponge.remoteapi.model.response.ProvideActionArgsResponse;
import org.openksavi.sponge.remoteapi.model.response.ReloadResponse;
import org.openksavi.sponge.remoteapi.model.response.ResponseError;
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
import org.openksavi.sponge.util.SpongeApiUtils;

/**
 * A base Sponge Remote API client.
 */
@SuppressWarnings("rawtypes")
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

    protected final String getUrl() {
        String baseUrl = configuration.getUrl();

        return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + RemoteApiConstants.ENDPOINT_JSONRPC;
    }

    protected abstract <T extends SpongeRequest, R extends SpongeResponse> R doExecute(T request, Class<R> responseClass,
            SpongeRequestContext context);

    @Override
    public <T extends SpongeRequest> T setupRequest(T request) {
        if (request.getId() == null) {
            request.setId(String.valueOf(currentRequestId.incrementAndGet()));
        }

        // Set empty header if none.
        if (request.getHeader() == null) {
            request.setHeader(new RequestHeader());
        }

        RequestHeader header = request.getHeader();

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

        // Clear header if it is empty.
        if (header.getUsername() == null && header.getPassword() == null && header.getAuthToken() == null
                && (header.getFeatures() == null || header.getFeatures().isEmpty())) {
            request.setHeader(null);
        }

        return request;
    }

    protected <T extends SpongeResponse> T setupResponse(String method, T response) {
        // Set empty header if none.
        if (response.getResult() != null && response.getResult().getHeader() == null) {
            response.getResult().setHeader(new ResponseHeader());
        }

        if (response.getError() != null) {
            handleErrorResponse(method, response.getError());
        }

        return response;
    }

    @Override
    public void handleErrorResponse(String method, ResponseError error) {
        if (error.getCode() != null) {
            if (configuration.isThrowExceptionOnErrorResponse()) {
                String message = error.getMessage() != null ? error.getMessage() : String.format("Error code: %s", error.getCode());

                RuntimeException exception;
                switch (error.getCode()) {
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
                    ((ErrorResponseException) exception).setErrorCode(error.getCode());
                    if (error.getData() != null) {
                        ((ErrorResponseException) exception).setErrorData(error.getData());
                    }
                }

                throw exception;
            }
        }
    }

    @Override
    public <T extends SpongeRequest, R extends SpongeResponse> R execute(T request, Class<R> responseClass, SpongeRequestContext context) {
        if (context == null) {
            context = SpongeRequestContext.builder().build();
        }

        // Set empty header if none.
        if (request.getHeader() == null) {
            request.setHeader(new RequestHeader());
        }

        RequestHeader header = request.getHeader();

        final SpongeRequestContext finalContext = context;

        return executeWithAuthentication(request, header.getUsername(), header.getPassword(), header.getAuthToken(),
                (req) -> executeDelegate(req, responseClass, finalContext), () -> {
                    header.setAuthToken(null);
                    return request;
                });
    }

    @Override
    public <T extends SpongeRequest, R extends SpongeResponse> R execute(T request, Class<R> responseClass) {
        return execute(request, responseClass, null);
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

    protected <T extends SpongeRequest, R extends SpongeResponse> R executeDelegate(T request, Class<R> responseClass,
            SpongeRequestContext context) {
        if (context == null) {
            context = SpongeRequestContext.builder().build();
        }

        return setupResponse(request.getMethod(), doExecute(setupRequest(request), responseClass, context));
    }

    @Override
    public GetVersionResponse getVersion(GetVersionRequest request, SpongeRequestContext context) {
        return execute(request, GetVersionResponse.class, context);
    }

    @Override
    public GetVersionResponse getVersion(GetVersionRequest request) {
        return getVersion(request, null);
    }

    @Override
    public String getVersion() {
        return getVersion(new GetVersionRequest()).getResult().getValue();
    }

    @Override
    public GetFeaturesResponse getFeatures(GetFeaturesRequest request, SpongeRequestContext context) {
        return execute(request, GetFeaturesResponse.class, context);
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
                    featuresCache = getFeatures(new GetFeaturesRequest()).getResult().getValue();
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
            response = executeDelegate(request, LoginResponse.class, context);
            currentAuthToken.set(response.getResult().getValue().getAuthToken());
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
        RequestHeader header = new RequestHeader();
        header.setUsername(configuration.getUsername());
        header.setPassword(configuration.getPassword());

        LoginRequest request = new LoginRequest();
        request.getParams().setHeader(header);

        return login(request).getResult().getValue().getAuthToken();
    }

    @Override
    public LogoutResponse logout(LogoutRequest request, SpongeRequestContext context) {
        LogoutResponse response;
        lock.lock();

        try {
            response = execute(request, LogoutResponse.class, context);
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
        return execute(request, GetKnowledgeBasesResponse.class, context);
    }

    @Override
    public GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request) {
        return getKnowledgeBases(request, null);
    }

    @Override
    public List<RemoteKnowledgeBaseMeta> getKnowledgeBases() {
        return getKnowledgeBases(new GetKnowledgeBasesRequest()).getResult().getValue();
    }

    protected GetActionsResponse doGetActions(GetActionsRequest request, boolean populateCache, SpongeRequestContext context) {
        GetActionsResponse response = execute(request, GetActionsResponse.class, context);
        GetActionsResult result = response.getResult();

        if (result.getValue().getActions() != null) {
            result.getValue().getActions().forEach(actionMeta -> unmarshalActionMeta(actionMeta));

            // Populate the cache.
            if (populateCache && configuration.isUseActionMetaCache() && actionMetaCache != null) {
                result.getValue().getActions().forEach(actionMeta -> actionMetaCache.put(actionMeta.getName(), actionMeta));
            }
        }

        if (result.getValue().getTypes() != null) {
            result.getValue().getTypes().values().forEach(type -> unmarshalDataType(type));
        }

        return response;
    }

    protected DataType marshalDataType(DataType type) {
        return (DataType) typeConverter.marshal(new TypeType(), type);
    }

    protected DataType unmarshalDataType(DataType type) {
        return (DataType) typeConverter.unmarshal(new TypeType(), type);
    }

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

    @SuppressWarnings({ "unchecked" })
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
        GetActionsParams params = new GetActionsParams();
        params.setName(name);
        params.setMetadataRequired(metadataRequired);

        return getActions(new GetActionsRequest(params)).getResult().getValue().getActions();
    }

    @Override
    public List<RemoteActionMeta> getActions() {
        return getActions(new GetActionsRequest()).getResult().getValue().getActions();
    }

    protected RemoteActionMeta fetchActionMeta(String actionName, SpongeRequestContext context) {
        GetActionsParams params = new GetActionsParams();
        params.setName(actionName);
        params.setMetadataRequired(true);

        List<RemoteActionMeta> actions = doGetActions(new GetActionsRequest(params), false, context).getResult().getValue().getActions();

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

    @SuppressWarnings("unchecked")
    protected ActionCallResponse doCall(RemoteActionMeta actionMeta, ActionCallRequest request, SpongeRequestContext context) {
        setupActionExecutionInfo(actionMeta, request.getParams());

        Object args = request.getParams().getArgs();

        if (args instanceof List || args == null) {
            validateCallArgs(actionMeta, (List) args);
            request.getParams().setArgs(marshalActionCallArgs(actionMeta, (List) args));
        } else if (args instanceof Map) {
            validateCallArgs(actionMeta, (Map<String, ?>) args);
            request.getParams().setArgs(marshalActionCallArgs(actionMeta, (Map<String, ?>) args));
        } else {
            throw new IllegalArgumentException("Action args should be an instance of a List or a Map");
        }

        ActionCallResponse response = execute(request, ActionCallResponse.class, context);

        unmarshalCallResult(actionMeta, response);

        return response;
    }

    @Override
    public void validateCallArgs(RemoteActionMeta actionMeta, List args) {
        if (actionMeta == null || actionMeta.getArgs() == null) {
            return;
        }

        SpongeApiUtils.validateActionCallArgs(actionMeta.getArgs(), args);
    }

    @Override
    public void validateCallArgs(RemoteActionMeta actionMeta, Map<String, ?> args) {
        if (actionMeta == null || actionMeta.getArgs() == null) {
            return;
        }

        args.forEach((name, value) -> {
            SpongeApiUtils.validateActionCallArg(actionMeta.getArgs().get(RemoteApiUtils.getActionArgIndex(actionMeta, name)), value);
        });
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

    protected Map<String, ?> marshalActionCallArgs(RemoteActionMeta actionMeta, Map<String, ?> args) {
        if (args == null || actionMeta == null || actionMeta.getArgs() == null) {
            return args;
        }

        Map<String, Object> result = new LinkedHashMap<>(args.size());
        args.forEach((name, value) -> result.put(name, typeConverter.marshal(actionMeta.getArg(name), value)));

        return result;
    }

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
        if (actionMeta == null || actionMeta.getResult() == null || response.getResult() == null) {
            return;
        }

        response.getResult().setValue(typeConverter.unmarshal(actionMeta.getResult(), response.getResult().getValue()));
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request, RemoteActionMeta actionMeta, boolean allowFetchMetadata,
            SpongeRequestContext context) {
        return doCall(actionMeta != null ? actionMeta : getActionMeta(request.getParams().getName(), allowFetchMetadata), request, context);
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
        return call(new ActionCallRequest(new ActionCallParams(actionName, args, null))).getResult().getValue();
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
    public Object call(String actionName, Map<String, ?> args) {
        return call(new ActionCallRequest(new ActionCallParams(actionName, args, null))).getResult().getValue();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T call(Class<T> resultClass, String actionName, Map<String, ?> args) {
        return (T) call(actionName, args);
    }

    @Override
    public SendEventResponse send(SendEventRequest request, SpongeRequestContext context) {
        SendEventParams params = request.getParams();

        // Use a temporary RemoteEvent to marshal attributes and features.
        RemoteEvent event = new RemoteEvent(null, params.getName(), null, 0, params.getLabel(), params.getDescription(),
                params.getAttributes(), params.getFeatures());
        event = RemoteApiUtils.marshalRemoteEvent(event, typeConverter, eventName -> getEventType(eventName));

        // Set marshalled fields.
        params.setAttributes(event.getAttributes());
        params.setFeatures(event.getFeatures());

        return execute(request, SendEventResponse.class, context);
    }

    @Override
    public SendEventResponse send(SendEventRequest request) {
        return send(request, null);
    }

    @Override
    public String send(String eventName, Map<String, Object> attributes, String label, String description, Map<String, Object> features) {
        return send(new SendEventRequest(new SendEventParams(eventName, attributes, label, description, features))).getResult().getValue();
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
        if (request.getParams().getEntries() != null) {
            request.getParams().getEntries().forEach(entry -> {
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

        return execute(request, IsActionActiveResponse.class, context);
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

        return isActionActive(new IsActionActiveRequest(new IsActionActiveParams(entries))).getResult().getValue();
    }

    @Override
    public ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request, SpongeRequestContext context) {
        RemoteActionMeta actionMeta = getActionMeta(request.getParams().getName());

        setupActionExecutionInfo(actionMeta, request.getParams());

        request.getParams().setCurrent(
                marshalAuxiliaryActionArgsCurrent(actionMeta, request.getParams().getCurrent(), request.getParams().getDynamicTypes()));

        // Clone and marshal all argument features.
        request.getParams().setArgFeatures(marshalProvideArgsFeaturesMap(request.getParams().getArgFeatures()));

        ProvideActionArgsResponse response = execute(request, ProvideActionArgsResponse.class, context);

        if (actionMeta != null) {
            unmarshalProvidedActionArgValues(actionMeta, response.getResult().getValue());
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
        return provideActionArgs(new ProvideActionArgsRequest(new ProvideActionArgsParams(actionName, parameters.getProvide(),
                parameters.getSubmit(), parameters.getCurrent(), parameters.getDynamicTypes(), parameters.getArgFeatures()))).getResult()
                        .getValue();
    }

    protected GetEventTypesResponse doGetEventTypes(GetEventTypesRequest request, boolean populateCache, SpongeRequestContext context) {
        GetEventTypesResponse response = execute(request, GetEventTypesResponse.class, context);

        Map<String, RecordType> eventTypes = response.getResult().getValue();
        if (eventTypes != null) {
            eventTypes.entrySet().forEach(entry -> entry.setValue((RecordType) unmarshalDataType(entry.getValue())));

            // Populate the cache.
            if (populateCache && configuration.isUseEventTypeCache() && eventTypeCache != null) {
                eventTypes.entrySet().forEach(entry -> eventTypeCache.put(entry.getKey(), entry.getValue()));
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
        return getEventTypes(new GetEventTypesRequest(new GetEventTypesParams(eventName))).getResult().getValue();
    }

    protected RecordType fetchEventType(String eventTypeName, SpongeRequestContext context) {
        return doGetEventTypes(new GetEventTypesRequest(new GetEventTypesParams(eventTypeName)), false, context).getResult().getValue()
                .get(eventTypeName);
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
        return execute(request, ReloadResponse.class, context);
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
