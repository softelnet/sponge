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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.BaseSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.client.okhttp.OkHttpSpongeRestClient;
import org.openksavi.sponge.restapi.model.RestActionMeta;

@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ActionMetaCacheTest.TestConfig.class })
@DirtiesContext
public class ActionMetaCacheTest extends BasicTestTemplate {

    protected BaseSpongeRestClient createRestClient(boolean useActionMetaCache) {
        return new OkHttpSpongeRestClient(
                SpongeRestClientConfiguration.builder().url(String.format("http://localhost:%d/%s", port, RestApiConstants.DEFAULT_PATH))
                        .useActionMetaCache(useActionMetaCache).build());
    }

    @Test
    public void testActionCacheOn() {
        try (BaseSpongeRestClient client = createRestClient(true)) {
            String actionName = "UpperCase";

            RestActionMeta actionMeta;

            assertNull(client.getActionMeta(actionName, false));

            actionMeta = client.getActionMeta(actionName);
            assertNotNull(actionMeta);
            assertNotNull(client.getActionMeta(actionName, false));

            assertNotNull(client.getActionMeta(actionName));
            assertTrue(actionMeta == client.getActionMeta(actionName));
            assertNotNull(client.getActionMeta(actionName, false));

            client.clearCache();
            assertNull(client.getActionMeta(actionName, false));

            assertNotNull(client.getActionMeta(actionName));
            assertTrue(actionMeta != client.getActionMeta(actionName));
            assertNotNull(client.getActionMeta(actionName, false));
        }
    }

    @Test
    public void testActionCacheOff() {
        try (BaseSpongeRestClient client = createRestClient(false)) {
            String actionName = "UpperCase";

            RestActionMeta actionMeta;

            actionMeta = client.getActionMeta(actionName);
            assertNotNull(actionMeta);

            assertNotNull(client.getActionMeta(actionName));
            assertTrue(actionMeta != client.getActionMeta(actionName));

            client.clearCache();

            assertNotNull(client.getActionMeta(actionName));
        }
    }

    @Test
    public void testActionCacheOnGetActions() {
        try (BaseSpongeRestClient client = createRestClient(true)) {
            String actionName = "UpperCase";

            assertNull(client.getActionMeta(actionName, false));

            client.getActions();
            assertNotNull(client.getActionMeta(actionName, false));

            assertNotNull(client.getActionMeta(actionName));

            client.clearCache();
            assertNull(client.getActionMeta(actionName, false));

            client.getActions();
            assertNotNull(client.getActionMeta(actionName, false));
        }
    }

    @Test
    public void testFetchActionMeta() {
        try (BaseSpongeRestClient client = createRestClient(true)) {
            String actionName = "UpperCase";

            assertNull(client.getActionMeta(actionName, false));
            assertNotNull(client.getActionMeta(actionName));
        }
    }
}
