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

import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import io.grpc.stub.StreamObserver;

import org.openksavi.sponge.grpcapi.client.util.GrpcClientUtils;
import org.openksavi.sponge.grpcapi.proto.SubscribeRequest;
import org.openksavi.sponge.grpcapi.proto.SubscribeResponse;
import org.openksavi.sponge.restapi.model.RemoteEvent;

/**
 * A client side event subscription.
 */
public class ClientSubscription {

    private long id;

    private SpongeGrpcClient grpcClient;

    private List<String> eventNames;

    private boolean registeredTypeRequired;

    private boolean subscribed = false;

    private StreamObserver<RemoteEvent> eventStreamObserver;

    private Lock lock = new ReentrantLock(true);

    private StreamObserver<SubscribeRequest> requestObserver;

    public ClientSubscription(SpongeGrpcClient grpcClient, List<String> eventNames, boolean registeredTypeRequired,
            StreamObserver<RemoteEvent> eventStreamObserver) {
        this.grpcClient = grpcClient;
        this.eventNames = eventNames;
        this.registeredTypeRequired = registeredTypeRequired;
        this.eventStreamObserver = eventStreamObserver;
    }

    public void open() {
        if (subscribed) {
            return;
        }

        lock.lock();
        try {
            if (subscribed) {
                return;
            }

            StreamObserver<SubscribeResponse> responseObserver = new StreamObserver<SubscribeResponse>() {

                @Override
                public void onNext(SubscribeResponse response) {
                    // Set the subscription id from the server.
                    if (response.getSubscriptionId() > 0) {
                        id = response.getSubscriptionId();
                    }

                    if (subscribed) {
                        eventStreamObserver.onNext(GrpcClientUtils.createEventFromGrpc(grpcClient.getRestClient(), response.getEvent()));
                    }
                }

                @Override
                public void onError(Throwable t) {
                    subscribed = false;
                    eventStreamObserver.onError(t);
                }

                @Override
                public void onCompleted() {
                    subscribed = false;
                    eventStreamObserver.onCompleted();
                }
            };

            requestObserver = grpcClient.getServiceAsyncStub().subscribeManaged(responseObserver);
            requestObserver.onNext(createAndSetupSubscribeRequest());

            subscribed = true;
        } finally {
            lock.unlock();
        }
    }

    protected SubscribeRequest createAndSetupSubscribeRequest() {
        if (grpcClient.getRestClient().getConfiguration().isAutoUseAuthToken()) {
            // Invoke the synchronous gRPC API operation to ensure the current authToken renewal. The auth token is shared
            // by both the REST and gRPC connection. Here the `getVersion` operation is used.
            grpcClient.getVersion();
        }

        return SubscribeRequest.newBuilder().setHeader(GrpcClientUtils.createRequestHeader(grpcClient.getRestClient()))
                .addAllEventNames(eventNames).setRegisteredTypeRequired(registeredTypeRequired).build();
    }

    public void close() {
        lock.lock();
        try {
            if (requestObserver != null) {
                requestObserver.onCompleted();
            }

            subscribed = false;
        } finally {
            lock.unlock();
        }
    }

    public long getId() {
        return id;
    }

    public List<String> getEventNames() {
        return eventNames;
    }

    public boolean isRegisteredTypeRequired() {
        return registeredTypeRequired;
    }

    public boolean isSubscribed() {
        return subscribed;
    }

    public StreamObserver<RemoteEvent> getEventStreamObserver() {
        return eventStreamObserver;
    }
}
