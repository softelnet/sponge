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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.RestActionResultMeta;
import org.openksavi.sponge.restapi.model.RestKnowledgeBaseMeta;
import org.openksavi.sponge.restapi.model.request.ActionCallRequest;
import org.openksavi.sponge.restapi.model.request.BaseRequest;
import org.openksavi.sponge.restapi.model.request.GetActionsRequest;
import org.openksavi.sponge.restapi.model.request.GetKnowledgeBasesRequest;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.request.LoginRequest;
import org.openksavi.sponge.restapi.model.request.LogoutRequest;
import org.openksavi.sponge.restapi.model.request.ReloadRequest;
import org.openksavi.sponge.restapi.model.request.SendEventRequest;
import org.openksavi.sponge.restapi.model.response.ActionCallResponse;
import org.openksavi.sponge.restapi.model.response.BaseResponse;
import org.openksavi.sponge.restapi.model.response.GetActionsResponse;
import org.openksavi.sponge.restapi.model.response.GetKnowledgeBasesResponse;
import org.openksavi.sponge.restapi.model.response.GetVersionResponse;
import org.openksavi.sponge.restapi.model.response.LoginResponse;
import org.openksavi.sponge.restapi.model.response.LogoutResponse;
import org.openksavi.sponge.restapi.model.response.ReloadResponse;
import org.openksavi.sponge.restapi.model.response.SendEventResponse;
import org.openksavi.sponge.restapi.util.RestApiUtils;

/**
 * A base Sponge REST API client.
 */
public abstract class BaseSpongeRestApiClient implements SpongeRestApiClient {

    private RestApiClientConfiguration configuration;

    private AtomicLong requestId = new AtomicLong(0);

    private ObjectMapper objectMapper = RestApiUtils.createObjectMapper();

    private AtomicReference<String> currentAuthToken = new AtomicReference<>();

    private Lock lock = new ReentrantLock(true);

    private LoadingCache<String, RestActionMeta> actionMetaCache;

    public BaseSpongeRestApiClient(RestApiClientConfiguration configuration) {
        setConfiguration(configuration);
    }

    @Override
    public RestApiClientConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RestApiClientConfiguration configuration) {
        this.configuration = configuration;

        initActionMetaCache();
    }

    private void initActionMetaCache() {
        lock.lock();
        try {
            if (configuration != null && !configuration.isUseActionMetaCache()) {
                actionMetaCache = null;
            } else {
                Caffeine<Object, Object> builder = Caffeine.newBuilder();
                if (configuration != null) {
                    if (configuration.getActionMetaCacheMaxSize() > -1) {
                        builder.maximumSize(configuration.getActionMetaCacheMaxSize());
                    }
                    if (configuration.getActionMetaCacheExpireSeconds() > -1) {
                        builder.expireAfterWrite(configuration.getActionMetaCacheExpireSeconds(), TimeUnit.SECONDS);
                    }
                }

                actionMetaCache = builder.build(actionName -> fetchActionMeta(actionName));
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
        return objectMapper;
    }

    protected final String getUrl() {
        return configuration.getUrl();
    }

    protected final String getUrl(String operation) {
        String baseUrl = getUrl();

        return baseUrl + (baseUrl.endsWith("/") ? "" : "/") + operation;
    }

    protected abstract <T extends BaseRequest, R extends BaseResponse> R doExecute(String operation, T request, Class<R> responseClass);

    protected <T extends BaseRequest> T prepareRequest(T request) {
        if (configuration.isUseRequestId()) {
            request.setId(String.valueOf(requestId.incrementAndGet()));
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

    protected <T extends BaseResponse> T prepareResponse(T response) {
        if (response.getErrorCode() != null || response.getErrorMessage() != null) {
            String message = response.getErrorMessage() != null ? response.getErrorMessage()
                    : String.format("Error code: %s", response.getErrorCode());

            ResponseErrorSpongeException exception;
            switch (response.getErrorCode()) {
            case RestApiConstants.ERROR_CODE_INVALID_AUTH_TOKEN:
                exception = new RestApiInvalidAuthTokenClientException(message);
                break;
            case RestApiConstants.ERROR_CODE_INCORRECT_KNOWLEDGE_BASE_VERSION:
                exception = new RestApiIncorrectKnowledgeBaseVersionClientException(message);
                break;
            default:
                exception = new ResponseErrorSpongeException(message);
            }

            exception.setErrorCode(response.getErrorCode());
            exception.setDetailedErrorMessage(response.getDetailedErrorMessage());

            throw exception;
        }

        return response;
    }

    protected <T extends BaseRequest, R extends BaseResponse> R execute(String operation, T request, Class<R> responseClass) {
        try {
            return prepareResponse(doExecute(operation, prepareRequest(request), responseClass));
        } catch (RestApiInvalidAuthTokenClientException e) {
            // Relogin if set up and necessary.
            if (currentAuthToken.get() != null && configuration.isRelogin()) {
                login();

                // Clear the request auth token.
                request.setAuthToken(null);

                return prepareResponse(doExecute(operation, prepareRequest(request), responseClass));
            } else {
                throw e;
            }
        }
    }

    @Override
    public GetVersionResponse getVersion(GetVersionRequest request) {
        return execute(RestApiConstants.OPERATION_VERSION, request, GetVersionResponse.class);
    }

    @Override
    public String getVersion() {
        return getVersion(new GetVersionRequest()).getVersion();
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        LoginResponse response;
        lock.lock();

        try {
            currentAuthToken.set(null);
            response = execute(RestApiConstants.OPERATION_LOGIN, request, LoginResponse.class);
            currentAuthToken.set(response.getAuthToken());
        } finally {
            lock.unlock();
        }

        return response;
    }

    @Override
    public String login() {
        return login(new LoginRequest(configuration.getUsername(), configuration.getPassword())).getAuthToken();
    }

    @Override
    public LogoutResponse logout(LogoutRequest request) {
        LogoutResponse response;
        lock.lock();

        try {
            response = execute(RestApiConstants.OPERATION_LOGOUT, request, LogoutResponse.class);
            currentAuthToken.set(null);
        } finally {
            lock.unlock();
        }

        return response;
    }

    @Override
    public void logout() {
        logout(new LogoutRequest());
    }

    @Override
    public GetKnowledgeBasesResponse getKnowledgeBases(GetKnowledgeBasesRequest request) {
        return execute(RestApiConstants.OPERATION_KNOWLEDGE_BASES, request, GetKnowledgeBasesResponse.class);
    }

    @Override
    public List<RestKnowledgeBaseMeta> getKnowledgeBases() {
        return getKnowledgeBases(new GetKnowledgeBasesRequest()).getKnowledgeBases();
    }

    @Override
    public GetActionsResponse getActions(GetActionsRequest request) {
        return doGetActions(request, true);
    }

    protected GetActionsResponse doGetActions(GetActionsRequest request, boolean populateCache) {
        GetActionsResponse response = execute(RestApiConstants.OPERATION_ACTIONS, request, GetActionsResponse.class);

        // Populate the cache.
        if (populateCache && configuration != null && configuration.isUseActionMetaCache() && actionMetaCache != null) {
            response.getActions().forEach(actionMeta -> actionMetaCache.put(actionMeta.getName(), actionMeta));
        }

        return response;
    }

    @Override
    public List<RestActionMeta> getActions(String nameRegExp) {
        return getActions(nameRegExp, null);
    }

    @Override
    public List<RestActionMeta> getActions(String nameRegExp, Boolean metadataRequired) {
        GetActionsRequest request = new GetActionsRequest();
        request.setMetadataRequired(metadataRequired);
        request.setNameRegExp(nameRegExp);

        return getActions(request).getActions();
    }

    @Override
    public List<RestActionMeta> getActions() {
        return getActions(new GetActionsRequest()).getActions();
    }

    protected ResultMeta<?> convertResultMeta(RestActionResultMeta restResultMeta) {
        if (restResultMeta == null) {
            return null;
        }

        return new ResultMeta<>(restResultMeta.getType()).displayName(restResultMeta.getDisplayName())
                .description(restResultMeta.getDescription());
    }

    protected RestActionMeta fetchActionMeta(String actionName) {
        GetActionsRequest request = new GetActionsRequest();
        request.setMetadataRequired(true);
        request.setNameRegExp(actionName);

        return doGetActions(request, false).getActions().stream().findFirst().orElse(null);
    }

    @Override
    public RestActionMeta getActionMeta(String actionName) {
        if (configuration != null && configuration.isUseActionMetaCache() && actionMetaCache != null) {
            return actionMetaCache.get(actionName);
        } else {
            return fetchActionMeta(actionName);
        }
    }

    protected ActionCallResponse doCall(RestActionMeta actionMeta, ActionCallRequest request) {
        // Conditionally set the verification of the knowledge base version on the server side.
        if (configuration.isVerifyKnowledgeBaseVersion() && actionMeta != null && request.getVersion() == null) {
            request.setVersion(actionMeta.getKnowledgeBase().getVersion());
        }

        Validate.isTrue(actionMeta == null || Objects.equals(actionMeta.getName(), request.getName()),
                "Action name '%s' in the metadata doesn't match the action name '%s' in the request",
                actionMeta != null ? actionMeta.getName() : null, request.getName());

        ActionCallResponse response = execute(RestApiConstants.OPERATION_CALL, request, ActionCallResponse.class);

        if (actionMeta != null && actionMeta.getResultMeta() != null) {
            response.setResult(
                    RestApiUtils.unmarshalActionResult(objectMapper, convertResultMeta(actionMeta.getResultMeta()), response.getResult()));
        }

        return response;
    }

    @Override
    public ActionCallResponse callWithMeta(RestActionMeta actionMeta, ActionCallRequest request) {
        return doCall(actionMeta, request);
    }

    @Override
    public Object callWithMeta(RestActionMeta actionMeta, Object... args) {
        return callWithMeta(actionMeta, new ActionCallRequest(actionMeta.getName(), Arrays.asList((Object[]) args))).getResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T callWithMeta(Class<T> resultClass, RestActionMeta actionMeta, Object... args) {
        return (T) callWithMeta(actionMeta, args);
    }

    @Override
    public ActionCallResponse call(ActionCallRequest request) {
        return callWithMeta(getActionMeta(request.getName()), request);
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
    public Object callWithNoMeta(String actionName, Object... args) {
        return doCall(null, new ActionCallRequest(actionName, Arrays.asList((Object[]) args))).getResult();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T callWithNoMeta(Class<T> resultClass, String actionName, Object... args) {
        return (T) callWithNoMeta(actionName, (Object[]) args);
    }

    @Override
    public SendEventResponse send(SendEventRequest request) {
        return execute(RestApiConstants.OPERATION_SEND, request, SendEventResponse.class);
    }

    @Override
    public String send(String eventName, Map<String, Object> attributes) {
        return send(new SendEventRequest(eventName, attributes)).getEventId();
    }

    @Override
    public ReloadResponse reload(ReloadRequest request) {
        return execute(RestApiConstants.OPERATION_RELOAD, request, ReloadResponse.class);
    }

    @Override
    public void reload() {
        reload(new ReloadRequest());
    }
}
