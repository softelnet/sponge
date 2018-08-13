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

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import org.openksavi.sponge.action.ResultMeta;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.util.RestApiClientUtils;
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

import okhttp3.OkHttpClient;

/**
 * A default Sponge REST API client.
 */
public class DefaultSpongeRestApiClient implements SpongeRestApiClient {

    private RestApiClientConfiguration configuration;

    private AtomicLong requestId = new AtomicLong(0);

    private ObjectMapper objectMapper = RestApiUtils.createObjectMapper();

    private AtomicReference<String> currentAuthToken = new AtomicReference<>();

    private Lock lock = new ReentrantLock(true);

    public DefaultSpongeRestApiClient(RestApiClientConfiguration configuration) {
        this.configuration = configuration;
    }

    @Override
    public RestApiClientConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(RestApiClientConfiguration configuration) {
        this.configuration = configuration;
    }

    protected final String getUrl() {
        return String.format("%s://%s:%d%s/", configuration.isSsl() ? "https" : "http", configuration.getHost(), configuration.getPort(),
                configuration.getBaseUrl());
    }

    protected RestTemplate setupRestTemplate(RestTemplate restTemplate) {
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(objectMapper)));

        return restTemplate;
    }

    protected RestTemplate getRestTemplate() {
        return setupRestTemplate(configuration.isSsl() ? createHttpsRestTemplate() : new RestTemplate());
    }

    protected RestTemplate createHttpsRestTemplate() {
        try {
            final X509TrustManager trustAllCerts = new X509TrustManager() {

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkServerTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                }

                @Override
                public void checkClientTrusted(final X509Certificate[] chain, final String authType) throws CertificateException {
                }
            };

            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, new TrustManager[] { trustAllCerts }, new java.security.SecureRandom());

            return new RestTemplate(new OkHttp3ClientHttpRequestFactory(
                    new OkHttpClient.Builder().sslSocketFactory(sslContext.getSocketFactory(), trustAllCerts)
                            .hostnameVerifier((String hostname, SSLSession session) -> true).build()));
        } catch (Exception e) {
            throw RestApiClientUtils.wrapException(e);
        }
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

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

    protected <T extends BaseResponse> T retrieveResponse(ResponseEntity<T> response) {
        T result = response.getBody();

        Validate.isTrue(!response.getStatusCode().isError(), "Error HTTP status code %s", response.getStatusCode());

        if (result.getErrorCode() != null || result.getErrorMessage() != null) {
            String message =
                    result.getErrorMessage() != null ? result.getErrorMessage() : String.format("Error code: %s", result.getErrorCode());

            ResponseErrorSpongeException exception;
            if (Objects.equals(result.getErrorCode(), RestApiConstants.ERROR_CODE_INVALID_AUTH_TOKEN)) {
                exception = new RestApiInvalidAuthTokenClientException(message);
            } else if (Objects.equals(result.getErrorCode(), RestApiConstants.ERROR_CODE_INCORRECT_KNOWLEDGE_BASE_VERSION)) {
                exception = new RestApiIncorrectKnowledgeBaseVersionClientException(message);
            } else {
                exception = new ResponseErrorSpongeException(message);
            }
            exception.setErrorCode(result.getErrorCode());
            exception.setDetailedErrorMessage(result.getDetailedErrorMessage());

            throw exception;
        }

        return result;
    }

    protected <T extends BaseRequest, R extends BaseResponse> R execute(String operation, T request, Class<R> responseClass) {
        try {
            return doExecute(operation, request, responseClass);
        } catch (RestApiInvalidAuthTokenClientException e) {
            // Relogin if set up and necessary.
            if (currentAuthToken.get() != null && configuration.isRelogin()) {
                login();

                // Clear the request auth token.
                request.setAuthToken(null);

                return doExecute(operation, request, responseClass);
            } else {
                throw e;
            }
        }
    }

    protected <T extends BaseRequest, R extends BaseResponse> R doExecute(String operation, T request, Class<R> responseClass) {
        return retrieveResponse(getRestTemplate().exchange(getUrl() + operation, HttpMethod.POST,
                new HttpEntity<>(prepareRequest(request), createHeaders()), responseClass));
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
        return execute(RestApiConstants.OPERATION_ACTIONS, request, GetActionsResponse.class);
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

    @Override
    public RestActionMeta getActionMeta(String actionName) {
        return getActions(actionName, true).stream().findFirst().orElse(null);
    }

    protected ActionCallResponse doCall(RestActionMeta actionMeta, ActionCallRequest request) {
        if (actionMeta != null && request.getVersion() == null) {
            request.setVersion(actionMeta.getKnowledgeBase().getVersion());
        }

        Validate.isTrue(actionMeta == null || Objects.equals(actionMeta.getName(), request.getName()),
                "Action name '%s' in the metadata doesn't match the action name '%s' in the request", actionMeta.getName(),
                request.getName());

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
