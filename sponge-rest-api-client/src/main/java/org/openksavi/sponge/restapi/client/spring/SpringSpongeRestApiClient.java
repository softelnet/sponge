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

package org.openksavi.sponge.restapi.client.spring;

import java.util.Arrays;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import okhttp3.OkHttpClient;

import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import org.openksavi.sponge.restapi.client.RestApiClientConfiguration;
import org.openksavi.sponge.restapi.client.okhttp.OkHttpSpongeRestApiClient;
import org.openksavi.sponge.restapi.client.util.RestApiClientUtils;
import org.openksavi.sponge.restapi.model.request.BaseRequest;
import org.openksavi.sponge.restapi.model.response.BaseResponse;

/**
 * A Sponge REST API client that uses Spring (version 5) and OkHttp.
 */
public class SpringSpongeRestApiClient extends OkHttpSpongeRestApiClient {

    /** The Spring REST template. */
    private RestTemplate restTemplate;

    /** The lock. */
    private Lock lock = new ReentrantLock(true);

    public SpringSpongeRestApiClient(RestApiClientConfiguration configuration) {
        super(configuration);
    }

    protected RestTemplate getOrCreateRestTemplate() {
        if (restTemplate == null) {
            // Initialize in a thread-safe manner.
            lock.lock();
            try {
                if (restTemplate == null) {
                    OkHttpClient client = RestApiClientUtils.createOkHttpClient();
                    restTemplate = new RestTemplate(new OkHttp3ClientHttpRequestFactory(client));
                    restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(getObjectMapper())));

                    setOkHttpClient(client);
                }
            } finally {
                lock.unlock();
            }
        }

        return restTemplate;
    }

    protected void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    protected <T extends BaseRequest, R extends BaseResponse> R doExecute(String operation, T request, Class<R> responseClass) {
        ResponseEntity<R> responseEntity = getOrCreateRestTemplate().exchange(getUrl(operation), HttpMethod.POST,
                new HttpEntity<>(request, createHeaders()), responseClass);
        Validate.isTrue(!responseEntity.getStatusCode().isError(), "Error HTTP status code %s", responseEntity.getStatusCode());

        return responseEntity.getBody();
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
