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

import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiBlockingStub;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiStub;
import org.openksavi.sponge.grpcapi.proto.SubscribeRequest;
import org.openksavi.sponge.grpcapi.proto.SubscribeResponse;
import org.openksavi.sponge.grpcapi.proto.VersionRequest;
import org.openksavi.sponge.grpcapi.proto.VersionResponse;
import org.openksavi.sponge.grpcapi.server.GrpcApiServerPlugin;
import org.openksavi.sponge.remoteapi.server.test.PortTestConfig;
import org.openksavi.sponge.restapi.RestApiConstants;
import org.openksavi.sponge.restapi.client.BaseSpongeRestClient;
import org.openksavi.sponge.restapi.client.SpongeRestClientConfiguration;
import org.openksavi.sponge.restapi.client.okhttp.OkHttpSpongeRestClient;
import org.openksavi.sponge.restapi.server.RestApiServerPlugin;
import org.openksavi.sponge.restapi.type.converter.DefaultTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;
import org.openksavi.sponge.spring.SpringSpongeEngine;

@net.jcip.annotations.NotThreadSafe
@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { GrpcApiServerTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@DirtiesContext
public class GrpcApiServerTest {

    private static final Logger logger = LoggerFactory.getLogger(GrpcApiServerTest.class);

    @Inject
    protected SpongeEngine engine;

    @Inject
    @Named(PortTestConfig.PORT_BEAN_NAME)
    protected Integer port;

    @Deprecated
    private TypeConverter typeConverter = new DefaultTypeConverter(RestApiUtils.createObjectMapper());

    @Configuration
    public static class TestConfig extends PortTestConfig {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().plugins(camelPlugin(), spongeRestApiPlugin(), spongeGrpcApiPlugin())
                    .config("examples/remote-api-server/remote_api.xml").build();
        }

        @Bean
        public RestApiServerPlugin spongeRestApiPlugin() {
            RestApiServerPlugin plugin = new RestApiServerPlugin();
            plugin.getSettings().setPort(spongeRestApiPort());
            plugin.getSettings().setPrettyPrint(true);

            return plugin;
        }

        @Bean
        public GrpcApiServerPlugin spongeGrpcApiPlugin() {
            return new GrpcApiServerPlugin();
        }
    }

    protected BaseSpongeRestClient createRestClient(boolean useEventTypeCache) {
        return new OkHttpSpongeRestClient(SpongeRestClientConfiguration.builder()
                .url(String.format("http://localhost:%d/%s", port, RestApiConstants.DEFAULT_PATH)).build());
    }

    protected int getGrpcPort() {
        return port.intValue() + 1;
    }

    protected ManagedChannel createManagedChannel() {
        return ManagedChannelBuilder.forTarget("dns:///localhost:" + getGrpcPort()).usePlaintext().build();
    }

    @Test
    public void testVersion() {
        // TODO usePlaintext is just to avoid using TLS!
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
