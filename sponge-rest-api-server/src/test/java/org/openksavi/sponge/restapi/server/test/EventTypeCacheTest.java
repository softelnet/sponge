/*
 * Copyright 2016-2019 The Sponge authors.
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.BaseSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.client.okhttp.OkHttpSpongeRestClient;
import org.openksavi.sponge.type.RecordType;

@net.jcip.annotations.NotThreadSafe
@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { EventTypeCacheTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class EventTypeCacheTest extends BasicTestTemplate {

    protected BaseSpongeRestClient createRestClient(boolean useEventTypeCache) {
        return new OkHttpSpongeRestClient(
                SpongeRestClientConfiguration.builder().url(String.format("http://localhost:%d/%s", port, RestApiConstants.DEFAULT_PATH))
                        .useEventTypeCache(useEventTypeCache).build());
    }

    @Test
    public void testEventTypeCacheOn() {
        try (BaseSpongeRestClient client = createRestClient(true)) {
            String eventTypeName = "alarm";
            RecordType eventType;

            assertNull(client.getEventType(eventTypeName, false));

            eventType = client.getEventType(eventTypeName);
            assertNotNull(eventType);
            assertNotNull(client.getEventType(eventTypeName, false));

            assertNotNull(client.getEventType(eventTypeName));
            assertTrue(eventType == client.getEventType(eventTypeName));
            assertNotNull(client.getEventType(eventTypeName, false));

            client.clearCache();
            assertNull(client.getEventType(eventTypeName, false));

            assertNotNull(client.getEventType(eventTypeName));
            assertTrue(eventType != client.getEventType(eventTypeName));
            assertNotNull(client.getEventType(eventTypeName, false));
        }
    }

    @Test
    public void testEventTypeCacheOff() {
        try (BaseSpongeRestClient client = createRestClient(false)) {
            String eventTypeName = "alarm";
            RecordType eventType;

            eventType = client.getEventType(eventTypeName);
            assertNotNull(eventType);

            assertNotNull(client.getEventType(eventTypeName));
            assertTrue(eventType != client.getEventType(eventTypeName));

            client.clearCache();

            assertNotNull(client.getEventType(eventTypeName));
        }
    }

    @Test
    public void testEventTypeCacheOnGetEventTypes() {
        try (BaseSpongeRestClient client = createRestClient(true)) {
            String eventTypeName = "alarm";

            assertNull(client.getEventType(eventTypeName, false));

            client.getEventTypes();
            assertNotNull(client.getEventType(eventTypeName, false));

            assertNotNull(client.getEventType(eventTypeName));

            client.clearCache();
            assertNull(client.getEventType(eventTypeName, false));

            client.getEventTypes();
            assertNotNull(client.getEventType(eventTypeName, false));
        }
    }

    @Test
    public void testFetchEventType() {
        try (BaseSpongeRestClient client = createRestClient(true)) {
            String eventTypeName = "alarm";

            assertNull(client.getEventType(eventTypeName, false));
            assertNotNull(client.getEventType(eventTypeName));
        }
    }
}
