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

import java.io.Closeable;
import java.util.List;

import io.grpc.stub.StreamObserver;

import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiBlockingStub;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiStub;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.model.RemoteEvent;

/**
 * A Sponge gRPC API client.
 */
public interface SpongeGrpcClient extends Closeable {

    SpongeGrpcClientConfiguration getConfiguration();

    String getVersion();

    ClientSubscription subscribe(List<String> eventNames, boolean registeredTypeRequired, boolean managed,
            StreamObserver<RemoteEvent> eventStreamObserver);

    ClientSubscription subscribe(List<String> eventNames, boolean registeredTypeRequired, StreamObserver<RemoteEvent> eventStreamObserver);

    ClientSubscription subscribe(List<String> eventNames, StreamObserver<RemoteEvent> eventStreamObserver);

    SpongeGrpcApiBlockingStub getServiceBlockingStub();

    SpongeGrpcApiStub getServiceAsyncStub();

    SpongeRestClient getRestClient();

    long getKeepAliveTime();

    void setKeepAliveTime(long keepAliveTime);

    long getKeepAliveTimeout();

    void setKeepAliveTimeout(long keepAliveTimeout);

    long getChannelShutdownTimeout();

    void setChannelShutdownTimeout(long channelShutdownTimeout);

    void close(boolean terminate);

    @Override
    void close();
}
