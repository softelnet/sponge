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

package org.openksavi.sponge.remoteapi.server.test.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.RegExUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.restapi.client.BaseSpongeRestClient;
import org.openksavi.sponge.restapi.client.DefaultSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRequestContext;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ClientListenerTest.TestConfig.class })
@DirtiesContext
public class ClientListenerTest extends BasicTestTemplate {

    protected BaseSpongeRestClient createRestClient() {
        return new DefaultSpongeRestClient(SpongeRestClientConfiguration.builder().url(String.format("http://localhost:%d", port)).build());
    }

    private String normalizeJson(String json) {
        return RegExUtils.removeAll(json, "\\s");
    }

    @Test
    public void testGlobalListeners() {
        final List<String> requestStringList = new ArrayList<>();
        final List<String> responseStringList = new ArrayList<>();

        try (BaseSpongeRestClient client = createRestClient()) {
            client.addOnRequestSerializedListener((request, requestString) -> requestStringList.add(requestString));
            client.addOnResponseDeserializedListener((request, response, responseString) -> responseStringList.add(responseString));

            client.getVersion();
            String version = client.getVersion();
            client.getVersion();

            assertEquals(engine.getVersion(), version);
            assertEquals(3, requestStringList.size());
            assertEquals(3, responseStringList.size());
            assertEquals("{\"header\":{\"id\":null,\"username\":null,\"password\":null,\"authToken\":null,\"features\":null}}",
                    normalizeJson(requestStringList.get(0)));
            assertTrue(normalizeJson(responseStringList.get(0)).matches(
                    "\\{\"header\":\\{\"id\":null,\"errorCode\":null,\"errorMessage\":null,\"detailedErrorMessage\":null,\"requestTime\":\".*\",\"responseTime\":\".*\"\\},\"body\":\\{\"version\":\""
                            + engine.getVersion() + "\"\\}\\}"));
        }
    }

    @Test
    public void testOneRequestListeners() {
        final AtomicReference<String> requestStringHolder = new AtomicReference<>();
        final AtomicReference<String> responseStringHolder = new AtomicReference<>();

        try (BaseSpongeRestClient client = createRestClient()) {
            client.getVersion();

            SpongeRequestContext context = SpongeRequestContext.builder()
                    .onRequestSerializedListener((request, requestString) -> requestStringHolder.set(requestString))
                    .onResponseDeserializedListener((request, response, responseString) -> responseStringHolder.set(responseString))
                    .build();
            String version = client.getVersion(new GetVersionRequest(), context).getBody().getVersion();

            assertEquals(engine.getVersion(), version);

            client.getVersion();

            assertEquals("{\"header\":{\"id\":null,\"username\":null,\"password\":null,\"authToken\":null,\"features\":null}}",
                    normalizeJson(requestStringHolder.get()));
            assertTrue(normalizeJson(responseStringHolder.get()).matches(
                    "\\{\"header\":\\{\"id\":null,\"errorCode\":null,\"errorMessage\":null,\"detailedErrorMessage\":null,\"requestTime\":\".*\",\"responseTime\":\".*\"\\},\"body\":\\{\"version\":\""
                            + engine.getVersion() + "\"\\}\\}"));
        }
    }
}
