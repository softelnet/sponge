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

import org.apache.commons.lang3.Validate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import org.openksavi.sponge.restapi.client.BaseSpongeRestApiClient;
import org.openksavi.sponge.restapi.client.RestApiClientConfiguration;
import org.openksavi.sponge.restapi.client.util.RestApiClientUtils;
import org.openksavi.sponge.restapi.model.request.BaseRequest;
import org.openksavi.sponge.restapi.model.response.BaseResponse;

/**
 * A Sponge REST API client that uses Spring (version 5) and OkHttp.
 */
public class SpringSpongeRestApiClient extends BaseSpongeRestApiClient {

    public SpringSpongeRestApiClient(RestApiClientConfiguration configuration) {
        super(configuration);
    }

    @Override
    protected <T extends BaseRequest, R extends BaseResponse> R doExecute(String operation, T request, Class<R> responseClass) {
        ResponseEntity<R> responseEntity =
                getRestTemplate().exchange(getUrl(operation), HttpMethod.POST, new HttpEntity<>(request, createHeaders()), responseClass);
        Validate.isTrue(!responseEntity.getStatusCode().isError(), "Error HTTP status code %s", responseEntity.getStatusCode());

        return responseEntity.getBody();
    }

    protected RestTemplate setupRestTemplate(RestTemplate restTemplate) {
        restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(getObjectMapper())));

        return restTemplate;
    }

    protected RestTemplate getRestTemplate() {
        return setupRestTemplate(getConfiguration().isSsl() ? createHttpsRestTemplate() : new RestTemplate());
    }

    protected RestTemplate createHttpsRestTemplate() {
        try {
            return new RestTemplate(new OkHttp3ClientHttpRequestFactory(RestApiClientUtils.createOkHttpClient()));
        } catch (Exception e) {
            throw RestApiClientUtils.wrapException(e);
        }
    }

    protected HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }
}
