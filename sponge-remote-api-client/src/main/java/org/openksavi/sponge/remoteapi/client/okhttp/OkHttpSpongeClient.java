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

package org.openksavi.sponge.remoteapi.client.okhttp;

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

import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.client.BaseSpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientConfiguration;
import org.openksavi.sponge.remoteapi.client.SpongeRequestContext;
import org.openksavi.sponge.remoteapi.client.util.SpongeClientUtils;
import org.openksavi.sponge.remoteapi.model.request.SpongeRequest;
import org.openksavi.sponge.remoteapi.model.response.SpongeResponse;
import org.openksavi.sponge.remoteapi.util.RemoteApiUtils;

/**
 * A Sponge Remote API client that uses OkHttp. OkHttpSpongeClient performs best when you create a single OkHttpSpongeClient instance and
 * reuse it for all of your Remote API calls.
 */
@SuppressWarnings("rawtypes")
public class OkHttpSpongeClient extends BaseSpongeClient {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpSpongeClient.class);

    /** The OkHttp client. */
    private OkHttpClient okHttpClient;

    /** The lock. */
    private Lock lock = new ReentrantLock(true);

    /** If {@code true}, the underlying OkHttpClient will be closed aggressively. */
    private boolean closeAggressively = false;

    public OkHttpSpongeClient(SpongeClientConfiguration configuration, OkHttpClient okHttpClient) {
        super(configuration);

        this.okHttpClient = okHttpClient;
    }

    public OkHttpSpongeClient(SpongeClientConfiguration configuration) {
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
    protected <T extends SpongeRequest, R extends SpongeResponse> R doExecute(T request, Class<R> responseClass,
            SpongeRequestContext context) {
        Headers headers = new Headers.Builder().add("Content-Type", RemoteApiConstants.CONTENT_TYPE_JSON).build();

        try {
            String requestBody = getObjectMapper().writeValueAsString(request);

            if (logger.isDebugEnabled()) {
                logger.debug("Remote API {} request: {}", request.getMethod(), RemoteApiUtils.obfuscatePassword(requestBody));
            }

            Stream.concat(Stream.of(context.getOnRequestSerializedListener()), onRequestSerializedListeners.stream())
                    .filter(Objects::nonNull).forEach(listener -> listener.onRequestSerialized(request, requestBody));

            Response httpResponse =
                    okHttpClient
                            .newCall(new Request.Builder().url(getUrl()).headers(headers)
                                    .post(RequestBody.create(MediaType.get(RemoteApiConstants.CONTENT_TYPE_JSON), requestBody)).build())
                            .execute();

            ResponseBody httpResponseBody = httpResponse.body();
            String responseBody = httpResponseBody != null ? httpResponseBody.string() : null;
            if (logger.isDebugEnabled()) {
                logger.debug("Remote API {} response: {})", request.getMethod(), RemoteApiUtils.obfuscatePassword(responseBody));
            }

            Validate.isTrue(
                    httpResponse.isSuccessful()
                            || httpResponse.code() == RemoteApiConstants.HTTP_RESPONSE_CODE_ERROR && isJson(httpResponseBody),
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
            throw SpongeClientUtils.wrapException(e);
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
                    SpongeClientUtils.closeOkHttpClient(okHttpClient);
                }
            } finally {
                lock.unlock();
            }
        }
    }

    protected boolean isJson(ResponseBody httpResponseBody) {
        if (httpResponseBody == null || httpResponseBody.contentType() == null) {
            return false;
        }

        MediaType mediaType = httpResponseBody.contentType();

        if (mediaType == null || mediaType.type() == null || mediaType.subtype() == null) {
            return false;
        }

        return mediaType.type().toLowerCase().equals("application") && mediaType.subtype().toLowerCase().equals("json");
    }
}
