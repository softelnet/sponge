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
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

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
import org.openksavi.sponge.restapi.client.util.RestApiClientUtils;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.RestActionResultMeta;
import org.openksavi.sponge.restapi.model.request.BaseRestRequest;
import org.openksavi.sponge.restapi.model.request.RestActionCallRequest;
import org.openksavi.sponge.restapi.model.request.RestGetActionsRequest;
import org.openksavi.sponge.restapi.model.request.RestGetVersionRequest;
import org.openksavi.sponge.restapi.model.request.RestSendEventRequest;
import org.openksavi.sponge.restapi.model.response.BaseRestResponse;
import org.openksavi.sponge.restapi.model.response.RestActionCallResponse;
import org.openksavi.sponge.restapi.model.response.RestGetActionsResponse;
import org.openksavi.sponge.restapi.model.response.RestGetVersionResponse;
import org.openksavi.sponge.restapi.model.response.RestSendEventResponse;
import org.openksavi.sponge.restapi.util.RestApiUtils;

import okhttp3.OkHttpClient;

/**
 * A default Sponge REST API client.
 */
public class DefaultSpongeRestApiClient implements SpongeRestApiClient {

    private RestApiClientConfiguration configuration;

    private AtomicLong requestId = new AtomicLong(0);

    private ObjectMapper objectMapper = RestApiUtils.createObjectMapper();

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

    protected <T extends BaseRestRequest> T prepareRequest(T request) {
        if (configuration.isUseRequestId()) {
            request.setId(String.valueOf(requestId.incrementAndGet()));
        }

        if (configuration.getAuthToken() != null) {
            if (request.getAuthToken() == null) {
                request.setAuthToken(configuration.getAuthToken());
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

    protected <T extends BaseRestResponse> T retrieveResponse(ResponseEntity<T> response) {
        T result = response.getBody();

        Validate.isTrue(!response.getStatusCode().isError(), "Error HTTP status code %s", response.getStatusCode());
        if (result.getErrorCode() != null || result.getErrorMessage() != null) {
            ResponseErrorSpongeException exception = new ResponseErrorSpongeException(
                    result.getErrorMessage() != null ? result.getErrorMessage() : String.format("Error code: %s", result.getErrorCode()));
            exception.setErrorCode(result.getErrorCode());
            exception.setDetailedErrorMessage(result.getDetailedErrorMessage());

            throw exception;
        }

        return result;
    }

    @Override
    public RestGetVersionResponse getVersion(RestGetVersionRequest request) {
        return retrieveResponse(getRestTemplate().exchange(getUrl() + "version", HttpMethod.POST,
                new HttpEntity<>(prepareRequest(request), createHeaders()), RestGetVersionResponse.class));
    }

    @Override
    public String getVersion() {
        return getVersion(new RestGetVersionRequest()).getVersion();
    }

    @Override
    public RestGetActionsResponse getActions(RestGetActionsRequest request) {
        return retrieveResponse(getRestTemplate().exchange(getUrl() + "actions", HttpMethod.POST,
                new HttpEntity<>(prepareRequest(request), createHeaders()), RestGetActionsResponse.class));
    }

    @Override
    public List<RestActionMeta> getActions(Boolean metadataRequired, String nameRegExp) {
        RestGetActionsRequest request = new RestGetActionsRequest();
        request.setMetadataRequired(metadataRequired);
        request.setNameRegExp(nameRegExp);

        return getActions(request).getActions();
    }

    @Override
    public List<RestActionMeta> getActions() {
        return getActions(new RestGetActionsRequest()).getActions();
    }

    protected ResultMeta<?> retrieveResultMeta(RestActionResultMeta restResultMeta) {
        if (restResultMeta == null) {
            return null;
        }

        return new ResultMeta<>(restResultMeta.getType()).displayName(restResultMeta.getDisplayName())
                .description(restResultMeta.getDescription());
    }

    @Override
    public ResultMeta<?> getActionResultMeta(String actionName) {
        Optional<RestActionMeta> meta = getActions(true, actionName).stream().findFirst();

        return retrieveResultMeta(meta.isPresent() ? meta.get().getResultMeta() : null);
    }

    @Override
    public RestActionCallResponse call(RestActionCallRequest request) {
        return callWithMeta(request, getActionResultMeta(request.getName()));
    }

    @Override
    public Object call(String actionName, Object... args) {
        return call(new RestActionCallRequest(actionName, Arrays.asList((Object[]) args))).getResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T call(Class<T> resultClass, String actionName, Object... args) {
        return (T) call(actionName, (Object[]) args);
    }

    @Override
    public RestActionCallResponse callWithMeta(RestActionCallRequest request, ResultMeta<?> resultMeta) {
        RestActionCallResponse response = retrieveResponse(getRestTemplate().exchange(getUrl() + "call", HttpMethod.POST,
                new HttpEntity<>(prepareRequest(request), createHeaders()), RestActionCallResponse.class));

        if (resultMeta != null) {
            response.setResult(RestApiUtils.unmarshalActionResult(objectMapper, resultMeta, response.getResult()));
        }

        return response;
    }

    @Override
    public Object callWithMeta(ResultMeta<?> resultMeta, String actionName, Object... args) {
        return callWithMeta(new RestActionCallRequest(actionName, Arrays.asList((Object[]) args)), resultMeta).getResult();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T callWithMeta(ResultMeta<?> resultMeta, Class<T> resultClass, String actionName, Object... args) {
        return (T) callWithMeta(resultMeta, actionName, (Object[]) args);
    }

    @Override
    public RestSendEventResponse send(RestSendEventRequest request) {
        return retrieveResponse(getRestTemplate().exchange(getUrl() + "send", HttpMethod.POST,
                new HttpEntity<>(prepareRequest(request), createHeaders()), RestSendEventResponse.class));
    }

    @Override
    public String send(String eventName, Map<String, Object> attributes) {
        return send(new RestSendEventRequest(eventName, attributes)).getEventId();
    }
}
