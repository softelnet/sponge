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

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.BaseSpongeRestApiClient;
import org.openksavi.sponge.restapi.client.RestApiClientConfiguration;
import org.openksavi.sponge.restapi.client.util.RestApiClientUtils;
import org.openksavi.sponge.restapi.model.request.BaseRequest;
import org.openksavi.sponge.restapi.model.response.BaseResponse;
import org.openksavi.sponge.restapi.util.RestApiUtils;

/**
 * A Sponge REST API client that uses OkHttp. OkHttpSpongeRestApiClient performs best when you create a single OkHttpSpongeRestApiClient
 * instance and reuse it for all of your REST API calls.
 */
public class OkHttpSpongeRestApiClient extends BaseSpongeRestApiClient {

    private static final Logger logger = LoggerFactory.getLogger(OkHttpSpongeRestApiClient.class);

    /** The OkHttp client. */
    private OkHttpClient okHttpClient;

    /** The lock. */
    private Lock lock = new ReentrantLock(true);

    /** If {@code true}, the underlying OkHttpClient will be closed aggressively. */
    private boolean closeAggressively = false;

    public OkHttpSpongeRestApiClient(RestApiClientConfiguration configuration) {
        super(configuration);
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    protected OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    protected OkHttpClient getOrCreateOkHttpClient() {
        if (okHttpClient == null) {
            // Initialize in a thread-safe manner.
            lock.lock();
            try {
                if (okHttpClient == null) {
                    okHttpClient = RestApiClientUtils.createOkHttpClient();
                }
            } finally {
                lock.unlock();
            }
        }

        return okHttpClient;
    }

    protected boolean isCloseAggressively() {
        return closeAggressively;
    }

    protected void setCloseAggressively(boolean closeAggressively) {
        this.closeAggressively = closeAggressively;
    }

    @Override
    protected <T extends BaseRequest, R extends BaseResponse> R doExecute(String operation, T request, Class<R> responseClass) {
        Headers headers = new Headers.Builder().add("Content-Type", RestApiConstants.APPLICATION_JSON_VALUE).build();

        try {
            String requestBody = getObjectMapper().writeValueAsString(request);

            if (logger.isDebugEnabled()) {
                logger.debug("REST API {} request: {}", operation, RestApiClientUtils.obfuscatePassword(requestBody));
            }

            Response response = getOrCreateOkHttpClient()
                    .newCall(new Request.Builder().url(getUrl(operation)).headers(headers)
                            .post(RequestBody.create(MediaType.get(RestApiConstants.APPLICATION_JSON_VALUE), requestBody)).build())
                    .execute();

            Validate.isTrue(RestApiUtils.isHttpSuccess(response.code()), "HTTP status code is %s", response.code());

            String responseBody = response.body().string();
            if (logger.isDebugEnabled()) {
                logger.debug("REST API {} response: {})", operation, RestApiClientUtils.obfuscatePassword(responseBody));
            }

            return response.body() != null ? getObjectMapper().readValue(responseBody, responseClass) : null;
        } catch (Throwable e) {
            throw RestApiClientUtils.wrapException(e);
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
                    RestApiClientUtils.closeOkHttpClient(okHttpClient);
                }
            } finally {
                lock.unlock();
            }
        }
    }
}
