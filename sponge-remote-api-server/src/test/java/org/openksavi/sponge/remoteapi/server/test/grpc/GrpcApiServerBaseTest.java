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

package org.openksavi.sponge.remoteapi.server.test.grpc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.client.ClientSubscription;
import org.openksavi.sponge.grpcapi.client.SpongeGrpcClient;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.BaseSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.client.model.RemoteEvent;
import org.openksavi.sponge.restapi.type.converter.DefaultTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;

public abstract class GrpcApiServerBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GrpcApiServerBaseTest.class);

    @Inject
    protected SpongeEngine engine;

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

    protected abstract BaseSpongeRestClient createRestClient(boolean useEventTypeCache);

    protected abstract SpongeGrpcClient createGrpcClient();

    @Test
    public void testVersion() {
        try (SpongeGrpcClient grpcClient = createGrpcClient()) {
            assertEquals(grpcClient.getRestClient().getVersion(), grpcClient.getVersion());
        }
    }

    @Test
    public void testSubscribe() throws Exception {
        int maxEvents = 3;
        final CountDownLatch finishLatch = new CountDownLatch(1);
        final List<RemoteEvent> events = Collections.synchronizedList(new ArrayList<>());
        ClientSubscription subscription;

        try (SpongeGrpcClient grpcClient = createGrpcClient()) {
            StreamObserver<RemoteEvent> eventObserver = new StreamObserver<RemoteEvent>() {

                @Override
                public void onNext(RemoteEvent event) {
                    logger.info("Response event: {}", event.getName());
                    events.add(event);

                    if (events.size() >= maxEvents) {
                        finishLatch.countDown();
                    }
                }

                @Override
                public void onError(Throwable t) {
                    logger.warn("Error: {}", Status.fromThrowable(t));
                    finishLatch.countDown();
                }

                @Override
                public void onCompleted() {
                    finishLatch.countDown();
                }
            };

            List<String> eventNames = Arrays.asList("notification.*");
            subscription = grpcClient.subscribe(eventNames, true, eventObserver);
            assertEquals(eventNames, subscription.getEventNames());
            assertTrue(subscription.isRegisteredTypeRequired());
            assertTrue(subscription.isSubscribed());

            if (!finishLatch.await(20, TimeUnit.SECONDS)) {
                fail("Timeout while waiting for responses.");
            }

            subscription.close();
        }

        assertFalse(subscription.isSubscribed());
        assertEquals(maxEvents, events.size());

        assertEquals("Sponge", events.get(1).getAttributes().get("source"));
    }

    @Test
    public void testRemoteApiFeatures() {
        try (SpongeRestClient client = createRestClient(false)) {
            Map<String, Object> features = client.getFeatures();
            assertEquals(1, features.size());
            assertTrue((Boolean) features.get(RestApiConstants.REMOTE_API_FEATURE_GRPC_ENABLED));
        }
    }
}
