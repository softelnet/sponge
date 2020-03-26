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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;

import javax.inject.Inject;
import javax.inject.Named;

import io.grpc.Status;
import io.grpc.Status.Code;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.action.ProvideArgsParameters;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.features.model.ui.IconInfo;
import org.openksavi.sponge.grpcapi.client.ClientSubscription;
import org.openksavi.sponge.grpcapi.client.SpongeGrpcClient;
import org.openksavi.sponge.grpcapi.client.SpongeGrpcClientConfiguration;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.BaseSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.model.RemoteEvent;
import org.openksavi.sponge.type.RecordType;
import org.openksavi.sponge.type.provided.ProvidedValue;
import org.openksavi.sponge.type.value.DynamicValue;

public abstract class GrpcApiServerBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GrpcApiServerBaseTest.class);

    @Inject
    protected SpongeEngine engine;

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

    protected abstract BaseSpongeRestClient createRestClient(boolean useEventTypeCache);

    protected abstract SpongeGrpcClient createGrpcClient(SpongeGrpcClientConfiguration configuration);

    protected final SpongeGrpcClient createGrpcClient() {
        return createGrpcClient(null);
    }

    @Test
    public void testVersion() {
        try (SpongeGrpcClient grpcClient = createGrpcClient()) {
            assertEquals(grpcClient.getRestClient().getVersion(), grpcClient.getVersion());
        }
    }

    private void doTestSubscribe(boolean managed) throws Exception {
        int maxEvents = 3;
        final CountDownLatch finishLatch = new CountDownLatch(1);
        final List<RemoteEvent> events = Collections.synchronizedList(new ArrayList<>());
        ClientSubscription subscription;

        try (SpongeGrpcClient grpcClient = createGrpcClient()) {
            StreamObserver<RemoteEvent> eventObserver = new StreamObserver<RemoteEvent>() {

                @Override
                public void onNext(RemoteEvent event) {
                    if (events.size() >= maxEvents) {
                        finishLatch.countDown();
                    } else {
                        logger.info("Response event: {}", event.getName());
                        events.add(event);
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
            subscription = grpcClient.subscribe(eventNames, true, managed, eventObserver);
            assertEquals(eventNames, subscription.getEventNames());
            assertTrue(subscription.isRegisteredTypeRequired());
            assertTrue(subscription.isSubscribed());

            if (!finishLatch.await(20, TimeUnit.SECONDS)) {
                fail("Timeout while waiting for responses.");
            }

            subscription.close();

            // Terminate the connection.
            grpcClient.close(true);
        }

        assertFalse(subscription.isSubscribed());
        assertEquals(maxEvents, events.size());

        assertEquals("Sponge", events.get(1).getAttributes().get("source"));
    }

    @Test
    public void testSubscribe() throws Exception {
        doTestSubscribe(true);
    }

    @Test
    public void testSubscribeNotManaged() throws Exception {
        doTestSubscribe(false);
    }

    @Test
    public void testRemoteApiFeatures() {
        try (SpongeRestClient client = createRestClient(false)) {
            Map<String, Object> features = client.getFeatures();
            assertTrue((Boolean) features.get(RestApiConstants.REMOTE_API_FEATURE_GRPC_ENABLED));
        }
    }

    @Test
    public void testPortChange() {
        try (SpongeGrpcClient grpcClient = createGrpcClient(SpongeGrpcClientConfiguration.builder().port(9000).build())) {
            StatusRuntimeException e = assertThrows(StatusRuntimeException.class, () -> grpcClient.getVersion());
            assertEquals(Code.UNAVAILABLE, e.getStatus().getCode());
        }
    }

    protected RemoteEvent waitForEvent(SpongeGrpcClient grpcClient, String eventName, Runnable onSend, Predicate<RemoteEvent> predicate) {
        // Subscribe to events of types equal to the one that will be sent.
        final CountDownLatch finishLatch = new CountDownLatch(1);
        AtomicReference<RemoteEvent> receivedEvent = new AtomicReference<>();

        StreamObserver<RemoteEvent> eventObserver = new StreamObserver<RemoteEvent>() {

            @Override
            public void onNext(RemoteEvent event) {
                if (event.getName().equals(eventName) && predicate.test(event)) {
                    receivedEvent.set(event);
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

        ClientSubscription subscription = null;
        try {
            subscription = grpcClient.subscribe(Arrays.asList(eventName), true, eventObserver);

            // Wait for the subscription to prevent race condition errors.
            Thread.sleep(500);

            onSend.run();

            if (!finishLatch.await(20, TimeUnit.SECONDS)) {
                fail("Timeout while waiting for the event.");
            }
        } catch (InterruptedException e) {
            throw SpongeUtils.wrapException(e);
        } finally {
            if (subscription != null) {
                subscription.close();
            }
        }

        return receivedEvent.get();
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSendEventAction() {
        String eventName = "notification";
        String eventLabel = "NOTIFICATION LABEL";
        Map<String, Object> eventAttributes = SpongeUtils.immutableMapOf("source", "SOURCE", "severity", 5, "person",
                SpongeUtils.immutableMapOf("firstName", "James", "surname", "Joyce"));

        try (SpongeGrpcClient grpcClient = createGrpcClient()) {
            RemoteEvent receivedEvent = waitForEvent(grpcClient, eventName, () -> {
                String sendEventActionName = "GrpcApiSendEvent";
                SpongeRestClient restClient = grpcClient.getRestClient();
                Map<String, ProvidedValue<?>> providedArgs =
                        restClient.provideActionArgs(sendEventActionName, new ProvideArgsParameters().withProvide(Arrays.asList("name")));
                assertEquals(1, providedArgs.size());
                assertEquals(1, providedArgs.get("name").getAnnotatedValueSet().size());

                providedArgs = restClient.provideActionArgs(sendEventActionName, new ProvideArgsParameters()
                        .withProvide(Arrays.asList("attributes")).withCurrent(SpongeUtils.immutableMapOf("name", eventName)));
                Map<String, Object> providedAttributes =
                        (Map<String, Object>) ((DynamicValue<?>) providedArgs.get("attributes").getValue()).getValue();
                assertEquals(0, providedAttributes.size());

                RecordType eventType = restClient.getEventType(eventName);
                assertNotNull(eventType);

                // Send a new event by the action.
                restClient.call(sendEventActionName,
                        Arrays.asList(eventName, new DynamicValue<Map<String, Object>>(eventAttributes, eventType), eventLabel, null));

            }, (event) -> event.getAttributes().equals(eventAttributes) && event.getLabel().equals(eventLabel));

            assertNotNull(receivedEvent);
        }
    }

    @Test
    public void testSendEvent() {
        String eventName = "notification";
        String eventLabel = "NOTIFICATION LABEL";
        String eventDescription = "NOTIFICATION DESCRIPTION";
        Map<String, Object> eventAttributes = SpongeUtils.immutableMapOf("source", "SOURCE", "severity", 5, "person",
                SpongeUtils.immutableMapOf("firstName", "James", "surname", "Joyce"));
        Map<String, Object> eventFeatures =
                SpongeUtils.immutableMapOf("icon", new IconInfo("alarm").withColor("FFFFFF"), "extra", "Extra feature");
        Predicate<RemoteEvent> predicate = (event) -> event.getFeatures().get("icon") != null
                && ((IconInfo) event.getFeatures().get("icon")).getName().equals(((IconInfo) eventFeatures.get("icon")).getName())
                && ((IconInfo) event.getFeatures().get("icon")).getColor().equals(((IconInfo) eventFeatures.get("icon")).getColor())
                && event.getFeatures().get("extra") != null && event.getFeatures().get("extra").equals(eventFeatures.get("extra"));

        try (SpongeGrpcClient grpcClient = createGrpcClient()) {
            RemoteEvent receivedEvent = waitForEvent(grpcClient, eventName,
                    () -> grpcClient.getRestClient().send(eventName, eventAttributes, eventLabel, eventDescription, eventFeatures),
                    (event) -> event.getAttributes().equals(eventAttributes) && predicate.test(event) && event.getLabel().equals(eventLabel)
                            && event.getDescription().equals(eventDescription));

            assertNotNull(receivedEvent);
        }
    }
}
