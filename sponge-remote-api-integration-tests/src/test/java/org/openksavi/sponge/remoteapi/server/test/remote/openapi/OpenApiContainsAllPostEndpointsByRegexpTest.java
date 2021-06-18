/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.server.test.remote.openapi;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.server.RemoteApiSettings;
import org.openksavi.sponge.remoteapi.server.test.RemoteApiTestUtils;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ContextConfiguration(classes = { OpenApiContainsAllPostEndpointsByRegexpTest.TestConfig.class })
public class OpenApiContainsAllPostEndpointsByRegexpTest extends BaseOpenApiTest {

    @Configuration
    public static class TestConfig extends BaseTestConfig {

        @Override
        protected void setup(RemoteApiSettings settings) {
            settings.setOpenApiDocsForEndpoints(".*");
        }
    }

    @Test
    public void shouldContainAllPostEndpointsByRegexp() throws IOException {
        String docs = getOpenApiDocs();

        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_ACTIONS, true);
        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_CALL, true);
        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_EVENT_TYPES, true);
        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_FEATURES, true);
        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_IS_ACTION_ACTIVE, true);
        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_KNOWLEDGE_BASES, true);
        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_LOGIN, true);
        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_LOGOUT, true);
        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_PROVIDE_ACTION_ARGS, true);
        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_SEND, true);
        RemoteApiTestUtils.assertOpenApiHasEndpoint(docs, RemoteApiConstants.METHOD_VERSION, true);
    }
}
