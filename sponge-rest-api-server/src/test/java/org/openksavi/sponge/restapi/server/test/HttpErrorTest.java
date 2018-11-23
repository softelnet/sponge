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

package org.openksavi.sponge.restapi.server.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.util.RestClientUtils;
import org.openksavi.sponge.restapi.model.response.SpongeResponse;
import org.openksavi.sponge.restapi.util.RestApiUtils;

@net.jcip.annotations.NotThreadSafe
@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { HttpErrorTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class HttpErrorTest extends BasicTestTemplate {

    @Test
    public void testHttpErrorInJsonParser() throws IOException {
        OkHttpClient client = RestClientUtils.createOkHttpClient();

        String requestBody = "{\"error_property\":\"\"}";
        Response okHttpResponse = client
                .newCall(new Request.Builder().url(String.format("http://localhost:%d/%s/actions", port, RestApiConstants.DEFAULT_PATH))
                        .headers(new Headers.Builder().add("Content-Type", RestApiConstants.APPLICATION_JSON_VALUE).build())
                        .post(RequestBody.create(MediaType.get(RestApiConstants.APPLICATION_JSON_VALUE), requestBody)).build())
                .execute();
        assertEquals(200, okHttpResponse.code());
        ObjectMapper mapper = RestApiUtils.createObjectMapper();
        SpongeResponse apiResponse = mapper.readValue(okHttpResponse.body().string(), SpongeResponse.class);
        assertEquals(RestApiConstants.DEFAULT_ERROR_CODE, apiResponse.getErrorCode());
        assertTrue(apiResponse.getErrorMessage().contains("Unrecognized field \"error_property\""));
    }
}
