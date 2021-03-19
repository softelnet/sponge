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

import java.io.IOException;
import java.time.Duration;
import java.util.function.Consumer;

import com.fasterxml.jackson.databind.ObjectMapper;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.model.response.ActionCallResponse;
import org.openksavi.sponge.remoteapi.util.RemoteApiUtils;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { FileUploadTest.TestConfig.class })
@DirtiesContext
@SuppressWarnings("deprecation")
public class FileUploadTest extends BasicTestTemplate {

    private OkHttpClient createOkHttpClient() {
        return new OkHttpClient.Builder().callTimeout(Duration.ofMinutes(5)).readTimeout(Duration.ofMinutes(5)).build();
    }

    private void testTemplate(Consumer<MultipartBody.Builder> builderConsumer, String responseText) throws IOException {
        OkHttpClient client = createOkHttpClient();

        String json =
                "{\"jsonrpc\":\"2.0\",\"method\":\"call\",\"params\":{\"name\":\"InputStreamArgAction\",\"args\":[\"VALUE\"]},\"id\":1}";

        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("jsonrpc", json);

        builderConsumer.accept(builder);

        RequestBody requestBody = builder.build();

        Request request = new Request.Builder().url(String.format("http://localhost:%d/call", port)).post(requestBody).build();

        Response okHttpResponse = client.newCall(request).execute();
        assertEquals(RemoteApiConstants.HTTP_RESPONSE_CODE_OK, okHttpResponse.code());
        ObjectMapper mapper = RemoteApiUtils.createObjectMapper();
        ActionCallResponse apiResponse = mapper.readValue(okHttpResponse.body().string(), ActionCallResponse.class);

        assertEquals(responseText, apiResponse.getResult().getValue());
    }

    @Test
    public void testFileUpload() throws IOException {
        testTemplate(builder -> {
            builder.addFormDataPart("fileStream", "file11.txt",
                    RequestBody.create(MediaType.parse("application/octet-stream"), "File 11 contents."));
            builder.addFormDataPart("fileStream2", "file21.txt",
                    RequestBody.create(MediaType.parse("application/octet-stream"), "File 21 contents."));
            builder.addFormDataPart("fileStream2", "file22.txt",
                    RequestBody.create(MediaType.parse("application/octet-stream"), "File 22 contents."));
        }, "Uploaded file11.txt file21.txt file22.txt");
    }

    @Test
    public void testFileUploadNoMultiFiles() throws IOException {
        testTemplate(builder -> {
            builder.addFormDataPart("fileStream", "file11.txt",
                    RequestBody.create(MediaType.parse("application/octet-stream"), "File 11 contents."));
        }, "Uploaded file11.txt");
    }

    @Test
    public void testFileUploadNoFirstFile() throws IOException {
        testTemplate(builder -> {
            builder.addFormDataPart("fileStream2", "file21.txt",
                    RequestBody.create(MediaType.parse("application/octet-stream"), "File 21 contents."));
            builder.addFormDataPart("fileStream2", "file22.txt",
                    RequestBody.create(MediaType.parse("application/octet-stream"), "File 22 contents."));
        }, "Uploaded file21.txt file22.txt");
    }

    @Test
    public void testFileUploadNoFiles() throws IOException {
        testTemplate(builder -> {
        }, "Uploaded");
    }
}
