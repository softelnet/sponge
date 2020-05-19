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

package org.openksavi.sponge.remoteapi.server.test.remote;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import org.openksavi.sponge.remoteapi.client.BaseSpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientConfiguration;
import org.openksavi.sponge.remoteapi.client.okhttp.OkHttpSpongeClient;
import org.openksavi.sponge.type.RecordType;

@Execution(ExecutionMode.SAME_THREAD)
@net.jcip.annotations.NotThreadSafe
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { EventTypeCacheTest.TestConfig.class })
@DirtiesContext
public class EventTypeCacheTest extends BasicTestTemplate {

    protected BaseSpongeClient createClient(boolean useEventTypeCache) {
        return new OkHttpSpongeClient(SpongeClientConfiguration.builder().url(String.format("http://localhost:%d", port))
                .useEventTypeCache(useEventTypeCache).build());
    }

    @Test
    public void testEventTypeCacheOn() {
        try (BaseSpongeClient client = createClient(true)) {
            String eventTypeName = "notification";
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
        try (BaseSpongeClient client = createClient(false)) {
            String eventTypeName = "notification";
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
        try (BaseSpongeClient client = createClient(true)) {
            String eventTypeName = "notification";

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
        try (BaseSpongeClient client = createClient(true)) {
            String eventTypeName = "notification";

            assertNull(client.getEventType(eventTypeName, false));
            assertNotNull(client.getEventType(eventTypeName));
        }
    }
}
