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
import java.util.Arrays;
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
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ArgValue;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.listener.OnRequestSerializedListener;
import org.openksavi.sponge.restapi.client.listener.OnResponseDeserializedListener;
import org.openksavi.sponge.restapi.model.RestActionArgMeta;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.RestKnowledgeBaseMeta;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.ActionExecutionRequest;
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
import org.openksavi.sponge.restapi.type.converter.DefaultTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.type.DataType;

/**
 * A base Sponge REST API client.
 */
public abstract class BaseSpongeRestClient implements SpongeRestClient {

    protected static final boolean DEFAULT_ALLOW_FETCH_METADATA = true;

    private SpongeRestClientConfiguration configuration;

    private AtomicLong currentRequestId = new AtomicLong(0);

    private AtomicReference<String> currentAuthToken = new AtomicReference<>();

    private TypeConverter typeConverter;

    private Lock lock = new ReentrantLock(true);

    private LoadingCache<String, RestActionMeta> actionMetaCache;

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
    }

    private void applyConfiguration() {
        ObjectMapper mapper = RestApiUtils.createObjectMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, configuration.isPrettyPrint());

        typeConverter = new DefaultTypeConverter(mapper);
    }

    public TypeConverter getTypeConverter() {
        return typeConverter;
    }

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

    @Override
    public void clearCache() {
        lock.lock();
        try {
            if (actionMetaCache != null) {
                actionMetaCache.invalidateAll();
            }
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

    public ObjectMapper getObjectMapper() {
        return typeConverter.getObjectMapper();
    }

    protected final String getUrl(String operation) {
        String baseUrl = configuration.getUrl();

        return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + operation;
    }

    protected abstract <T extends SpongeRequest, R extends SpongeResponse> R doExecute(String operation, T request, Class<R> responseClass,
            SpongeRequestContext context);

    protected <T extends SpongeRequest> T setupRequest(T request) {
        if (configuration.isUseRequestId()) {
            request.setId(String.valueOf(currentRequestId.incrementAndGet()));
        }

        // Must be thread-safe.
        String authToken = currentAuthToken.get();
        if (authToken != null) {
            if (request.getAuthToken() == null) {
                request.setAuthToken(authToken);
            }
        } else {
            if (configuration.getUsername() != null && request.getUsername() == null) {
                request.setUsername(configuration.getUsername());
            }

            if (configuration.getPassword() != null && request.getPassword() == null) {
                request.setPassword(configuration.getPassword());
            }
        }

        return request;
    }

    protected <T extends SpongeResponse> T setupResponse(T response) {
        if (response.getErrorCode() != null) {
            if (configuration.isThrowExceptionOnErrorResponse()) {
                String message = response.getErrorMessage() != null ? response.getErrorMessage()
                        : String.format("Error code: %s", response.getErrorCode());

                ErrorResponseException exception;
                switch (response.getErrorCode()) {
                case RestApiConstants.ERROR_CODE_INVALID_AUTH_TOKEN:
                    exception = new InvalidAuthTokenException(message);
                    break;
                case RestApiConstants.ERROR_CODE_INCORRECT_KNOWLEDGE_BASE_VERSION:
                    exception = new IncorrectKnowledgeBaseVersionException(message);
                    break;
                default:
                    exception = new ErrorResponseException(message);
                }

                exception.setErrorCode(response.getErrorCode());
                exception.setDetailedErrorMessage(response.getDetailedErrorMessage());

                throw exception;
            }
        }

        return response;
    }

    protected <T extends SpongeRequest, R extends SpongeResponse> R execute(String operation, T request, Class<R> responseClass,
            SpongeRequestContext context) {
        if (context == null) {
            context = SpongeRequestContext.builder().build();
        }

        try {
            return setupResponse(doExecute(operation, setupRequest(request), responseClass, context));
        } catch (InvalidAuthTokenException e) {
            // Relogin if set up and necessary.
            if (currentAuthToken.get() != null && configuration.isRelogin()) {
                login();

                // Clear the request auth token.
                request.setAuthToken(null);

                return setupResponse(doExecute(operation, setupRequest(request), responseClass, context));
            } else {
                throw e;
            }
        }
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
    public LoginResponse login(LoginRequest request, SpongeRequestContext context) {
        LoginResponse response;
        lock.lock();

        try {
            currentAuthToken.set(null);
            response = execute(RestApiConstants.OPERATION_LOGIN, request, LoginResponse.class, context);
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

        return response;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void unmarshalActionMeta(RestActionMeta actionMeta) {
        if (actionMeta != null) {
            if (actionMeta.getArgsMeta() != null) {
                actionMeta.getArgsMeta().forEach(argMeta -> {
                    DataType type = argMeta.getType();
                    type.setDefaultValue(typeConverter.unmarshal(type, argMeta.getType().getDefaultValue()));
                });
            }

            if (actionMeta.getResultMeta() != null) {
                DataType type = actionMeta.getResultMeta().getType();
                type.setDefaultValue(typeConverter.unmarshal(type, type.getDefaultValue()));
            }
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void unmarshalProvidedActionArgValues(RestActionMeta actionMeta, Map<String, ArgValue<?>> argValues) {
        if (argValues == null || actionMeta.getArgsMeta() == null) {
            return;
        }

        argValues.forEach((argName, argValue) -> {
            RestActionArgMeta argMeta = actionMeta.getArgMeta(argName);
            ((ArgValue) argValue).setValue(typeConverter.unmarshal(argMeta.getType(), argValue.getValue()));

            if (argValue.getValueSet() != null) {
                ((ArgValue) argValue).setValueSet(argValue.getValueSet().stream()
                        .map(value -> typeConverter.unmarshal(argMeta.getType(), value)).collect(Collectors.toList()));
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
        // Conditionally set the verification of the knowledge base version on the server side.
        if (configuration.isVerifyKnowledgeBaseVersion() && actionMeta != null && request.getVersion() == null) {
            request.setVersion(actionMeta.getKnowledgeBase().getVersion());
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

    @SuppressWarnings("rawtypes")
    protected void validateCallArgs(RestActionMeta actionMeta, List args) {
        // Validate arguments.
        if (actionMeta == null || actionMeta.getArgsMeta() == null) {
            return;
        }

        int expectedAllArgCount = actionMeta.getArgsMeta().size();
        int expectedNonOptionalArgCount = (int) actionMeta.getArgsMeta().stream().filter(argMeta -> !argMeta.isOptional()).count();
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
        for (int i = 0; i < actionMeta.getArgsMeta().size(); i++) {
            RestActionArgMeta meta = actionMeta.getArgsMeta().get(i);
            Validate.isTrue(meta.isOptional() || meta.getType().isNullable() || args.get(i) != null, "Action argument '%s' is not set",
                    meta.getDisplayName() != null ? meta.getDisplayName() : meta.getName());
        }
    }

    protected List<Object> marshalActionCallArgs(RestActionMeta actionMeta, List<Object> args) {
        if (args == null || actionMeta == null || actionMeta.getArgsMeta() == null) {
            return args;
        }

        List<Object> result = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            result.add(typeConverter.marshal(actionMeta.getArgsMeta().get(i).getType(), args.get(i)));
        }

        return result;
    }

    protected Map<String, Object> marshalProvideActionArgsCurrent(RestActionMeta actionMeta, Map<String, Object> current) {
        if (current == null || actionMeta == null || actionMeta.getArgsMeta() == null) {
            return current;
        }

        Map<String, Object> marshalled = new LinkedHashMap<>();
        current.forEach((name, value) -> marshalled.put(name, typeConverter.marshal(actionMeta.getArgMeta(name).getType(), value)));

        return marshalled;
    }

    protected void unmarshalCallResult(RestActionMeta actionMeta, ActionCallResponse response) {
        if (actionMeta == null || actionMeta.getResultMeta() == null || response.getResult() == null) {
            return;
        }

        response.setResult(typeConverter.unmarshal(actionMeta.getResultMeta().getType(), response.getResult()));
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
    public Object call(String actionName, Object... args) {
        return call(new ActionCallRequest(actionName, Arrays.asList((Object[]) args))).getResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T call(Class<T> resultClass, String actionName, Object... args) {
        return (T) call(actionName, (Object[]) args);
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
    public String send(String eventName, Map<String, Object> attributes) {
        return send(new SendEventRequest(eventName, attributes)).getEventId();
    }

    @Override
    public ProvideActionArgsResponse provideActionArgs(ProvideActionArgsRequest request, SpongeRequestContext context) {
        RestActionMeta actionMeta = getActionMeta(request.getName());
        setupActionExecutionRequest(actionMeta, request);

        request.setCurrent(marshalProvideActionArgsCurrent(actionMeta, request.getCurrent()));

        ProvideActionArgsResponse response =
                execute(RestApiConstants.OPERATION_ACTION_ARGS, request, ProvideActionArgsResponse.class, context);

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
    public Map<String, ArgValue<?>> provideActionArgs(String actionName, List<String> argNames, Map<String, Object> current) {
        return provideActionArgs(new ProvideActionArgsRequest(actionName, argNames, current)).getProvided();
    }

    @Override
    public Map<String, ArgValue<?>> provideActionArgs(String actionName) {
        return provideActionArgs(actionName, null, null);
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
}
