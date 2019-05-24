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

package org.openksavi.sponge.grpcapi.server;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiBlockingStub;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiStub;
import org.openksavi.sponge.grpcapi.proto.SubscribeRequest;
import org.openksavi.sponge.grpcapi.proto.SubscribeResponse;
import org.openksavi.sponge.grpcapi.proto.VersionRequest;
import org.openksavi.sponge.grpcapi.proto.VersionResponse;
import org.openksavi.sponge.restapi.type.converter.DefaultTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;

public class GrpcApiServerTest {

    private static final Logger logger = LoggerFactory.getLogger(GrpcApiServerTest.class);

    private TypeConverter typeConverter = new DefaultTypeConverter(RestApiUtils.createObjectMapper());

    // TODO @Test
    public void testGrpc() {
        // TODO usePlaintext is just to avoid using TLS!
        ManagedChannel channel = ManagedChannelBuilder.forTarget("dns:///localhost:8889").usePlaintext().build();
        // ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext().build();
        SpongeGrpcApiBlockingStub stub = SpongeGrpcApiGrpc.newBlockingStub(channel);
        VersionResponse response = stub.getVersion(VersionRequest.newBuilder().build());

        // TODO Dynamic version.
        assertEquals("1.10.0", response.getVersion());

        channel.shutdown();
    }

    // TODO @Test
    public void testGrpcSubscribe() throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forTarget("dns:///localhost:8889").usePlaintext().build();
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
        requestObserver.onNext(SubscribeRequest.newBuilder().addAllEventNames(Arrays.asList("e.*")).build());

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
