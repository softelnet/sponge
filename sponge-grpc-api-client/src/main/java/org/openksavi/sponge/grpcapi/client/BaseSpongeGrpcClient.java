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

package org.openksavi.sponge.grpcapi.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.grpcapi.client.util.GrpcClientUtils;
import org.openksavi.sponge.grpcapi.proto.RequestHeader;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiBlockingStub;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiStub;
import org.openksavi.sponge.grpcapi.proto.VersionRequest;
import org.openksavi.sponge.grpcapi.proto.VersionRequest.Builder;
import org.openksavi.sponge.grpcapi.proto.VersionResponse;
import org.openksavi.sponge.remoteapi.client.SpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientException;
import org.openksavi.sponge.remoteapi.model.RemoteEvent;

/**
 * A base Sponge gRPC API client.
 */
public abstract class BaseSpongeGrpcClient<T extends ManagedChannelBuilder<?>> implements SpongeGrpcClient {

    private static final Logger logger = LoggerFactory.getLogger(BaseSpongeGrpcClient.class);

    private SpongeClient spongeClient;

    private SpongeGrpcClientConfiguration configuration;

    private Consumer<T> channelBuilderConfigurer;

    private ManagedChannel channel;

    private SpongeGrpcApiStub serviceAsyncStub;

    private SpongeGrpcApiBlockingStub serviceBlockingStub;

    private long keepAliveTime = 15 * 60;

    private long keepAliveTimeout = 5 * 60;

    private long channelShutdownTimeout = -1;

    private Lock lock = new ReentrantLock(true);

    protected BaseSpongeGrpcClient(SpongeClient spongeClient, SpongeGrpcClientConfiguration configuration,
            Consumer<T> channelBuilderConfigurer) {
        this.spongeClient = spongeClient;
        this.configuration = configuration;
        this.channelBuilderConfigurer = channelBuilderConfigurer;

        open();
    }

    protected BaseSpongeGrpcClient(SpongeClient spongeClient, SpongeGrpcClientConfiguration configuration) {
        this(spongeClient, configuration, null);
    }

    protected BaseSpongeGrpcClient(SpongeClient spongeClient, Consumer<T> channelBuilderConfigurer) {
        this(spongeClient, null, channelBuilderConfigurer);
    }

    protected BaseSpongeGrpcClient(SpongeClient spongeClient) {
        this(spongeClient, null, null);
    }

    @Override
    public SpongeClient getSpongeClient() {
        return spongeClient;
    }

    @Override
    public SpongeGrpcClientConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public SpongeGrpcApiStub getServiceAsyncStub() {
        return serviceAsyncStub;
    }

    @Override
    public SpongeGrpcApiBlockingStub getServiceBlockingStub() {
        return serviceBlockingStub;
    }

    @Override
    public long getKeepAliveTime() {
        return keepAliveTime;
    }

    @Override
    public void setKeepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
    }

    @Override
    public long getKeepAliveTimeout() {
        return keepAliveTimeout;
    }

    @Override
    public void setKeepAliveTimeout(long keepAliveTimeout) {
        this.keepAliveTimeout = keepAliveTimeout;
    }

    @Override
    public long getChannelShutdownTimeout() {
        return channelShutdownTimeout;
    }

    @Override
    public void setChannelShutdownTimeout(long channelShutdownTimeout) {
        this.channelShutdownTimeout = channelShutdownTimeout;
    }

    protected abstract T createChannelBuilder(String host, int port);

    protected void open() {
        if (channel != null) {
            return;
        }

        lock.lock();
        try {
            if (channel != null) {
                return;
            }

            URI remoteUri = new URI(spongeClient.getConfiguration().getUrl());

            String host = remoteUri.getHost();

            Integer port = configuration != null ? configuration.getPort() : null;
            if (port == null) {
                // If the port is not configured explicitly, use the Sponge gRPC API service port convention: Remote API port + 1.
                int remotePort = remoteUri.getPort() > -1 ? remoteUri.getPort() : (spongeClient.getConfiguration().isSsl() ? 443 : 80);
                port = remotePort + 1;
            }

            logger.info("Creating a new client to the Sponge gRPC API service on {}:{}", host, port);

            T channelBuilder = createChannelBuilder(host, port);

            // If the Remote API service is not HTTPS, use insecure gRPC.
            if (!spongeClient.getConfiguration().isSsl()) {
                channelBuilder.usePlaintext();
            }

            if (keepAliveTime >= 0) {
                channelBuilder.keepAliveTime(keepAliveTime, TimeUnit.SECONDS);
            }

            if (keepAliveTimeout >= 0) {
                channelBuilder.keepAliveTimeout(keepAliveTimeout, TimeUnit.SECONDS);
            }

            if (channelBuilderConfigurer != null) {
                channelBuilderConfigurer.accept(channelBuilder);
            }

            channel = channelBuilder.build();

            serviceAsyncStub = SpongeGrpcApiGrpc.newStub(channel);
            serviceBlockingStub = SpongeGrpcApiGrpc.newBlockingStub(channel);
        } catch (URISyntaxException e) {
            throw new SpongeClientException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void close() {
        close(false);
    }

    @Override
    public void close(boolean terminate) {
        if (channel == null) {
            return;
        }

        lock.lock();
        try {
            if (channel == null) {
                return;
            }

            if (terminate) {
                channel.shutdownNow();
            } else {
                channel.shutdown();
            }

            if (channelShutdownTimeout > 0) {
                channel.awaitTermination(channelShutdownTimeout, TimeUnit.SECONDS);
            }

            channel = null;
        } catch (InterruptedException e) {
            throw new SpongeClientException(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String getVersion() {
        Builder versionBuilder = VersionRequest.newBuilder();

        RequestHeader header = GrpcClientUtils.createRequestHeader(spongeClient);
        if (header != null) {
            versionBuilder.setHeader(header);
        }

        VersionRequest request = versionBuilder.build();

        VersionResponse response = spongeClient.executeWithAuthentication(request, request.getHeader().getUsername(),
                request.getHeader().getPassword(), request.getHeader().getAuthToken(), (VersionRequest req) -> {
                    try {
                        return serviceBlockingStub.getVersion(req);
                    } catch (Exception e) {
                        GrpcClientUtils.handleError(spongeClient, "getVersion", e);

                        throw e;
                    }
                }, () -> {
                    RequestHeader newHeader = RequestHeader.newBuilder(request.getHeader()).setAuthToken(null).build();
                    return VersionRequest.newBuilder(request).setHeader(newHeader).build();
                });

        return StringUtils.isNotEmpty(response.getVersion()) ? response.getVersion() : null;
    }

    @Override
    public ClientSubscription subscribe(List<String> eventNames, boolean registeredTypeRequired, boolean managed,
            StreamObserver<RemoteEvent> eventStreamObserver) {
        ClientSubscription subscription = new ClientSubscription(this, eventNames, registeredTypeRequired, managed, eventStreamObserver);
        subscription.open();

        return subscription;
    }

    @Override
    public ClientSubscription subscribe(List<String> eventNames, boolean registeredTypeRequired,
            StreamObserver<RemoteEvent> eventStreamObserver) {
        return subscribe(eventNames, registeredTypeRequired, true, eventStreamObserver);
    }

    @Override
    public ClientSubscription subscribe(List<String> eventNames, StreamObserver<RemoteEvent> eventStreamObserver) {
        return subscribe(eventNames, false, true, eventStreamObserver);
    }
}
