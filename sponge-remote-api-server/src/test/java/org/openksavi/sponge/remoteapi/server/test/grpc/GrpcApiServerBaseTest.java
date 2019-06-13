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

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiBlockingStub;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiStub;
import org.openksavi.sponge.grpcapi.proto.SubscribeRequest;
import org.openksavi.sponge.grpcapi.proto.SubscribeResponse;
import org.openksavi.sponge.grpcapi.proto.VersionRequest;
import org.openksavi.sponge.grpcapi.proto.VersionResponse;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.restapi.client.BaseSpongeRestClient;
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

    @Deprecated
    private TypeConverter typeConverter = new DefaultTypeConverter(RestApiUtils.createObjectMapper());

    protected abstract BaseSpongeRestClient createRestClient(boolean useEventTypeCache);

    protected int getGrpcPort() {
        return port.intValue() + 1;
    }

    protected ManagedChannel createManagedChannel() {
        return ManagedChannelBuilder.forTarget("dns:///localhost:" + getGrpcPort()).usePlaintext().build();
    }

    @Test
    public void testVersion() {
        ManagedChannel channel = createManagedChannel();
        SpongeGrpcApiBlockingStub stub = SpongeGrpcApiGrpc.newBlockingStub(channel);
        VersionResponse response = stub.getVersion(VersionRequest.newBuilder().build());

        // TODO Dynamic version.
        assertEquals("1.10.0", response.getVersion());

        channel.shutdown();
    }

    @Test
    public void testSubscribe() throws Exception {
        ManagedChannel channel = createManagedChannel();
        SpongeGrpcApiStub stub = SpongeGrpcApiGrpc.newStub(channel);

        int maxEvents = 3;
        final CountDownLatch finishLatch = new CountDownLatch(1);
        final List<SubscribeResponse> responses = Collections.synchronizedList(new ArrayList<>());

        StreamObserver<SubscribeResponse> responseObserver = new StreamObserver<SubscribeResponse>() {

            @Override
            public void onNext(SubscribeResponse response) {
                logger.info("Response event: {}", response.hasEvent() ? response.getEvent().getName() : null);
                responses.add(response);

                if (responses.size() >= maxEvents) {
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

        StreamObserver<SubscribeRequest> requestObserver = stub.subscribe(responseObserver);
        requestObserver.onNext(SubscribeRequest.newBuilder().addAllEventNames(Arrays.asList("notification.*")).build());

        if (!finishLatch.await(20, TimeUnit.SECONDS)) {
            fail("Timeout while waiting for responses.");
        }

        requestObserver.onCompleted();

        assertEquals(maxEvents, responses.size());

        assertEquals("Sponge", typeConverter.getObjectMapper()
                .readValue(responses.get(1).getEvent().getAttributes().getValueJson(), Map.class).get("source"));

        channel.shutdown();
    }
}
