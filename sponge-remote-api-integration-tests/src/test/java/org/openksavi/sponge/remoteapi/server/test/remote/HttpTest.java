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

package org.openksavi.sponge.remoteapi.server.test.remote;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.remoteapi.JsonRpcConstants;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.model.response.ErrorResponse;
import org.openksavi.sponge.remoteapi.util.RemoteApiUtils;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { HttpTest.TestConfig.class })
@DirtiesContext
@SuppressWarnings("deprecation")
public class HttpTest extends BasicTestTemplate {

    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder().callTimeout(Duration.ofMinutes(5)).readTimeout(Duration.ofMinutes(5)).build();
    }

    @Test
    public void testHttpErrorInJsonParser() throws IOException {
        OkHttpClient client = createOkHttpClient();

        String requestBody = "{\"error_property\":\"\"}";
        Response okHttpResponse = client.newCall(new Request.Builder().url(String.format("http://localhost:%d/actions", port))
                .headers(new Headers.Builder().add("Content-Type", RemoteApiConstants.CONTENT_TYPE_JSON).build())
                .post(RequestBody.create(MediaType.get(RemoteApiConstants.CONTENT_TYPE_JSON), requestBody)).build()).execute();
        assertEquals(RemoteApiConstants.HTTP_RESPONSE_CODE_ERROR, okHttpResponse.code());
        ObjectMapper mapper = RemoteApiUtils.createObjectMapper();
        ErrorResponse apiResponse = mapper.readValue(okHttpResponse.body().string(), ErrorResponse.class);
        assertEquals(JsonRpcConstants.ERROR_CODE_INVALID_REQUEST, apiResponse.getError().getCode());
        assertTrue(apiResponse.getError().getMessage().contains("Unrecognized field \"error_property\""));
    }

    @Test
    public void testHttpContentTypeCharset() throws IOException {
        OkHttpClient client = createOkHttpClient();

        Response okHttpResponse = client.newCall(new Request.Builder().url(String.format("http://localhost:%d/actions", port))
                .headers(new Headers.Builder().add("Content-Type", RemoteApiConstants.CONTENT_TYPE_JSON).build())
                .post(RequestBody.create(MediaType.get(RemoteApiConstants.CONTENT_TYPE_JSON), "")).build()).execute();
        assertEquals(200, okHttpResponse.code());
        assertEquals("application/json;charset=utf-8", StringUtils.remove(okHttpResponse.header("Content-Type").trim().toLowerCase(), " "));
    }

    @Test
    public void testOutputStreamResultActionGet() throws IOException {
        OkHttpClient client = createOkHttpClient();

        String params = "{\"name\":\"OutputStreamResultAction\",\"args\":[]}";
        params = new String(Base64.getEncoder().encode(params.getBytes(StandardCharsets.UTF_8)), StandardCharsets.UTF_8);
        params = URLEncoder.encode(params, StandardCharsets.UTF_8.name());

        Response okHttpResponse = client.newCall(new Request.Builder().url(new HttpUrl.Builder().scheme("http").host("localhost").port(port)
                .addPathSegment("call").addQueryParameter("params", params).build()).get().build()).execute();
        assertEquals(200, okHttpResponse.code());
        byte[] responseBody = okHttpResponse.body().bytes();
        String responseString = new String(responseBody);
        assertEquals("text/plain;charset=\"utf-8\"", StringUtils.remove(okHttpResponse.header("Content-Type").trim().toLowerCase(), " "));
        assertEquals("attachment; filename=\"sample+file.txt\"", okHttpResponse.header("Content-Disposition"));
        assertEquals("Sample text file\n", responseString);
    }

    @Test
    public void testOutputStreamResultActionPost() throws IOException {
        OkHttpClient client = createOkHttpClient();

        Response okHttpResponse = client.newCall(new Request.Builder().url(String.format("http://localhost:%d/call", port))
                .headers(new Headers.Builder().add("Content-Type", RemoteApiConstants.CONTENT_TYPE_JSON).build())
                .post(RequestBody.create(MediaType.get(RemoteApiConstants.CONTENT_TYPE_JSON),
                        "{\"params\":{\"name\":\"OutputStreamResultAction\",\"args\":[]}}"))
                .build()).execute();
        assertEquals(200, okHttpResponse.code());
        byte[] responseBody = okHttpResponse.body().bytes();
        String responseString = new String(responseBody);
        assertEquals("text/plain;charset=\"utf-8\"", StringUtils.remove(okHttpResponse.header("Content-Type").trim().toLowerCase(), " "));
        assertEquals("Sample text file\n", responseString);
    }

    @Test
    public void testOutputStreamResultActionPostErrorInJson() throws IOException {
        OkHttpClient client = createOkHttpClient();

        Response okHttpResponse = client.newCall(new Request.Builder().url(String.format("http://localhost:%d/call", port))
                .headers(new Headers.Builder().add("Content-Type", RemoteApiConstants.CONTENT_TYPE_JSON).build()).post(RequestBody
                        .create(MediaType.get(RemoteApiConstants.CONTENT_TYPE_JSON), "{\"name\":\"OutputStreamResultAction\",\"args\":[]}"))
                .build()).execute();
        assertEquals(500, okHttpResponse.code());
    }
}
