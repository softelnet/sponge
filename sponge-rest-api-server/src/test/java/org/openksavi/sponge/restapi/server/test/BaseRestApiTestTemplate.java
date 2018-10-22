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

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import javax.inject.Named;

import org.junit.Test;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.restapi.client.RestApiIncorrectKnowledgeBaseVersionClientException;
import org.openksavi.sponge.restapi.client.SpongeRestApiClient;
import org.openksavi.sponge.restapi.model.RestActionMeta;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.response.GetVersionResponse;
import org.openksavi.sponge.type.StringType;
import org.openksavi.sponge.type.TypeKind;

public abstract class BaseRestApiTestTemplate {

    @Inject
    protected SpongeEngine engine;

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

    protected abstract SpongeRestApiClient createRestApiClient();

    @Test
    public void testVersion() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            assertEquals(engine.getVersion(), client.getVersion());
        }
    }

    @Test
    public void testVersionWithId() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            client.getConfiguration().setUseRequestId(true);

            GetVersionRequest request = new GetVersionRequest();
            GetVersionResponse response = client.getVersion(request);

            assertEquals(null, response.getErrorCode());
            assertEquals(null, response.getErrorMessage());
            assertEquals(null, response.getDetailedErrorMessage());
            assertEquals(engine.getVersion(), response.getVersion());
            assertEquals("1", response.getId());
            assertEquals(response.getId(), request.getId());
        }
    }

    @Test
    public void testActions() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            assertEquals(3, client.getActions().size());
        }
    }

    @Test
    public void testActionsParamArgMetadataRequiredTrue() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            assertEquals(3, client.getActions(null, true).size());
        }
    }

    @Test
    public void testActionsParamArgMetadataRequiredFalse() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            List<RestActionMeta> actions = client.getActions(null, false);

            assertEquals(4, actions.size());
            RestActionMeta meta = actions.stream().filter(action -> action.getName().equals("UpperCase")).findFirst().get();
            assertEquals(TypeKind.STRING, meta.getArgsMeta().get(0).getType().getKind());
            assertTrue(meta.getArgsMeta().get(0).getType() instanceof StringType);
        }
    }

    @Test
    public void testActionsNameRegExp() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            String nameRegExp = ".*Case";
            List<RestActionMeta> actions = client.getActions(nameRegExp);

            assertEquals(2, actions.size());
            assertTrue(actions.stream().allMatch(action -> action.getName().matches(nameRegExp)));
        }
    }

    @Test
    public void testActionsNameExact() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            String nameRegExp = "UpperCase";
            List<RestActionMeta> actions = client.getActions(nameRegExp);

            assertEquals(1, actions.size());
            assertEquals(actions.get(0).getName(), nameRegExp);
        }
    }

    @Test
    public void testCall() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            String arg1 = "test1";

            Object result = client.call("UpperCase", arg1);

            assertTrue(result instanceof String);
            assertEquals(arg1.toUpperCase(), result);

            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "actionCalled").get());
            assertFalse(engine.isError());
        }
    }

    @Test
    public void testCallWithExpectedKnowledgeBaseVersion() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            String arg1 = "test1";

            Object result = client.callWithMeta(client.getActions("UpperCase").get(0), arg1);

            assertTrue(result instanceof String);
            assertEquals(arg1.toUpperCase(), result);

            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "actionCalled").get());
            assertFalse(engine.isError());
        }
    }

    @Test(expected = RestApiIncorrectKnowledgeBaseVersionClientException.class)
    public void testCallWithWrongExpectedKnowledgeBaseVersion() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            String arg1 = "test1";

            RestActionMeta actionMeta = client.getActions("UpperCase").get(0);
            actionMeta.getKnowledgeBase().setVersion(2);

            try {
                Object result = client.callWithMeta(actionMeta, arg1);
                assertNull(result);
            } finally {
                engine.clearError();
            }
        }
    }

    @Test
    public void testSend() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            assertNotNull(client.send("alarm", SpongeUtils.immutableMapOf("attr1", "Test")));

            await().atMost(30, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "eventSent").get());
            assertFalse(engine.isError());
        }
    }

    @Test
    public void testKnowledgeBases() {
        try (SpongeRestApiClient client = createRestApiClient()) {
            assertEquals(1, client.getKnowledgeBases().size());
        }
    }
}
