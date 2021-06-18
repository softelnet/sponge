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
@ContextConfiguration(classes = { OpenApiContainsGetEndpointsTest.TestConfig.class })
public class OpenApiContainsGetEndpointsTest extends BaseOpenApiTest {

    private static final String SUFFIX = "_GET_VERB";

    @Configuration
    public static class TestConfig extends BaseTestConfig {

        @Override
        protected void setup(RemoteApiSettings settings) {
            settings.setOpenApiDocsForGetVerbOperations(true);
            settings.setOpenApiOperationIdSuffixForGetVerbOperations(SUFFIX);
        }
    }

    @Test
    public void shouldContainGetEndpoints() throws IOException {
        String docs = getOpenApiDocs();

        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_ACTIONS + SUFFIX, true);
        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_CALL + SUFFIX, true);
        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_EVENT_TYPES + SUFFIX, true);
        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_FEATURES + SUFFIX, true);
        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_IS_ACTION_ACTIVE + SUFFIX, true);
        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_KNOWLEDGE_BASES + SUFFIX, true);
        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_LOGIN + SUFFIX, true);
        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_LOGOUT + SUFFIX, true);
        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_PROVIDE_ACTION_ARGS + SUFFIX, true);
        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_SEND + SUFFIX, true);
        RemoteApiTestUtils.assertOpenApiHasOperationId(docs, RemoteApiConstants.OPERATION_ID_VERSION + SUFFIX, true);
    }
}
