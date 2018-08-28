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

import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.restapi.client.BaseSpongeRestApiClient;
import org.openksavi.sponge.restapi.client.RestApiClientConfiguration;
import org.openksavi.sponge.restapi.client.util.RestApiClientUtils;
import org.openksavi.sponge.restapi.model.request.BaseRequest;
import org.openksavi.sponge.restapi.model.response.BaseResponse;
import org.openksavi.sponge.restapi.util.RestApiUtils;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * A Sponge REST API client that uses OkHttp.
 */
public class OkHttpSpongeRestApiClient extends BaseSpongeRestApiClient {

    public OkHttpSpongeRestApiClient(RestApiClientConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected <T extends BaseRequest, R extends BaseResponse> R doExecute(String operation, T request, Class<R> responseClass) {
        OkHttpClient client = RestApiClientUtils.createOkHttpClient();
        String jsonContectType = "application/json";
        Headers headers = new Headers.Builder().add("Content-Type", jsonContectType).build();

        try {
            String requestBody = getObjectMapper().writeValueAsString(request);
            Response response = client.newCall(new Request.Builder().url(getUrl(operation)).headers(headers)
                    .post(RequestBody.create(MediaType.get(jsonContectType), requestBody)).build()).execute();

            Validate.isTrue(RestApiUtils.isHttpSuccess(response.code()), "Error HTTP status code %s", response.code());

            return response.body() != null ? getObjectMapper().readValue(response.body().string(), responseClass) : null;
        } catch (Throwable e) {
            throw RestApiClientUtils.wrapException(e);
        }
    }
}
