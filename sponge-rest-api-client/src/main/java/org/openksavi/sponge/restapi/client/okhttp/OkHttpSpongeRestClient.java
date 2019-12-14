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

package org.openksavi.sponge.restapi.client.okhttp;

import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Stream;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.BaseSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRequestContext;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.client.util.RestClientUtils;
import org.openksavi.sponge.restapi.model.request.SpongeRequest;
import org.openksavi.sponge.restapi.model.response.SpongeResponse;
import org.openksavi.sponge.restapi.util.RestApiUtils;

/**
 * A Sponge REST API client that uses OkHttp. OkHttpSpongeRestApiClient performs best when you create a single OkHttpSpongeRestApiClient
 * instance and reuse it for all of your REST API calls.
 */
public class OkHttpSpongeRestClient extends BaseSpongeRestClient {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpSpongeRestClient.class);

    /** The OkHttp client. */
    private OkHttpClient okHttpClient;

    /** The lock. */
    private Lock lock = new ReentrantLock(true);

    /** If {@code true}, the underlying OkHttpClient will be closed aggressively. */
    private boolean closeAggressively = false;

    public OkHttpSpongeRestClient(SpongeRestClientConfiguration configuration, OkHttpClient okHttpClient) {
        super(configuration);

        this.okHttpClient = okHttpClient;
    }

    public OkHttpSpongeRestClient(SpongeRestClientConfiguration configuration) {
        this(configuration, new OkHttpClient.Builder().build());
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    protected boolean isCloseAggressively() {
        return closeAggressively;
    }

    protected void setCloseAggressively(boolean closeAggressively) {
        this.closeAggressively = closeAggressively;
    }

    @Override
    @SuppressWarnings("deprecation")
    protected <T extends SpongeRequest, R extends SpongeResponse> R doExecute(String operationType, T request, Class<R> responseClass,
            SpongeRequestContext context) {
        Headers headers = new Headers.Builder().add("Content-Type", RestApiConstants.CONTENT_TYPE_JSON).build();

        try {
            String requestBody = getObjectMapper().writeValueAsString(request);

            if (logger.isDebugEnabled()) {
                logger.debug("REST API {} request: {}", operationType, RestApiUtils.obfuscatePassword(requestBody));
            }

            Stream.concat(Stream.of(context.getOnRequestSerializedListener()), onRequestSerializedListeners.stream())
                    .filter(Objects::nonNull).forEach(listener -> listener.onRequestSerialized(request, requestBody));

            Response httpResponse =
                    okHttpClient
                            .newCall(new Request.Builder().url(getUrl(operationType)).headers(headers)
                                    .post(RequestBody.create(MediaType.get(RestApiConstants.CONTENT_TYPE_JSON), requestBody)).build())
                            .execute();

            ResponseBody httpResponseBody = httpResponse.body();
            String responseBody = httpResponseBody != null ? httpResponseBody.string() : null;
            if (logger.isDebugEnabled()) {
                logger.debug("REST API {} response: {})", operationType, RestApiUtils.obfuscatePassword(responseBody));
            }

            Validate.isTrue(
                    httpResponse.isSuccessful() || httpResponse.code() == RestApiConstants.HTTP_CODE_ERROR && isJson(httpResponseBody),
                    "HTTP error status code is %s", httpResponse.code());

            R response = null;
            try {
                response = getObjectMapper().readValue(responseBody, responseClass);
            } finally {
                final R finalResponse = response;
                Stream.concat(Stream.of(context.getOnResponseDeserializedListener()), onResponseDeserializedListeners.stream())
                        .filter(Objects::nonNull)
                        .forEach(listener -> listener.onResponseDeserialized(request, finalResponse, responseBody));

                httpResponse.close();
            }

            return response;
        } catch (Throwable e) {
            throw RestClientUtils.wrapException(e);
        }
    }

    /**
     * Closes aggressively the underlying OkHttpClient according to the OkHttpClient documentation.
     */
    @Override
    public void close() {
        if (closeAggressively && okHttpClient != null) {
            // Close in a thread-safe manner.
            lock.lock();
            try {
                if (okHttpClient != null) {
                    RestClientUtils.closeOkHttpClient(okHttpClient);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    protected boolean isJson(ResponseBody httpResponseBody) {
        return httpResponseBody != null && httpResponseBody.contentType() != null
                && httpResponseBody.contentType().type().toLowerCase().equals("application")
                && httpResponseBody.contentType().subtype().toLowerCase().equals("json");
    }
}
