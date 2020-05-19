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

import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiImplBase;
import org.openksavi.sponge.grpcapi.proto.SubscribeRequest;
import org.openksavi.sponge.grpcapi.proto.SubscribeResponse;
import org.openksavi.sponge.grpcapi.proto.VersionRequest;
import org.openksavi.sponge.grpcapi.proto.VersionResponse;
import org.openksavi.sponge.grpcapi.server.util.GrpcApiServerUtils;
import org.openksavi.sponge.remoteapi.model.request.GetVersionRequest;
import org.openksavi.sponge.remoteapi.model.response.GetVersionResponse;
import org.openksavi.sponge.remoteapi.server.RemoteApiService;
import org.openksavi.sponge.remoteapi.server.security.UserContext;

public class DefaultGrpcApiService extends SpongeGrpcApiImplBase {

    private static final Logger logger = LoggerFactory.getLogger(DefaultGrpcApiService.class);

    private SpongeEngine engine;

    private ServerSubscriptionManager subscriptionManager;

    private RemoteApiService remoteApiService;

    public DefaultGrpcApiService() {
    }

    public SpongeEngine getEngine() {
        return engine;
    }

    public void setEngine(SpongeEngine engine) {
        this.engine = engine;
    }

    public RemoteApiService getRemoteApiService() {
        return remoteApiService;
    }

    public void setRemoteApiService(RemoteApiService remoteApiService) {
        this.remoteApiService = remoteApiService;
    }

    public ServerSubscriptionManager getSubscriptionManager() {
        return subscriptionManager;
    }

    public void setSubscriptionManager(ServerSubscriptionManager subscriptionManager) {
        this.subscriptionManager = subscriptionManager;
    }

    @Override
    public void getVersion(VersionRequest request, StreamObserver<VersionResponse> responseObserver) {
        try {
            GetVersionRequest remoteApiRequest = GrpcApiServerUtils.createRemoteApiRequest(remoteApiService.getTypeConverter(), request);

            // Open a new session. The user will be set later in the service.
            remoteApiService.openSession(createSession());
            GetVersionResponse remoteApiResponse = remoteApiService.getVersion(remoteApiRequest);

            VersionResponse response = GrpcApiServerUtils.createResponse(remoteApiService.getTypeConverter(), remoteApiResponse);

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } catch (Throwable e) {
            // The internal error.
            logger.error("getVersion internal error", e);
            responseObserver.onError(GrpcApiServerUtils.createInternalException(e));
        } finally {
            // Close the session.
            remoteApiService.closeSession();
        }
    }

    protected void doSubscribe(long subscriptionId, SubscribeRequest request, StreamObserver<SubscribeResponse> responseObserver) {
        try {
            // Open a new session. The user will be set later in the Remote API service.
            remoteApiService.openSession(createSession());

            // Check user credentials.
            UserContext userContext = authenticateRequest(request);

            logger.debug("New subscription {}", subscriptionId);
            subscriptionManager.putSubscription(new ServerSubscription(subscriptionId, request.getEventNamesList(),
                    request.getRegisteredTypeRequired(), responseObserver, userContext,
                    request.hasHeader() && !StringUtils.isEmpty(request.getHeader().getId()) ? request.getHeader().getId() : null));
        } finally {
            // Close the session.
            remoteApiService.closeSession();
        }
    }

    @Override
    public void subscribe(SubscribeRequest request, StreamObserver<SubscribeResponse> responseObserver) {
        doSubscribe(subscriptionManager.createNewSubscriptionId(), request, responseObserver);
    }

    @Override
    public StreamObserver<SubscribeRequest> subscribeManaged(StreamObserver<SubscribeResponse> responseObserver) {
        return new StreamObserver<SubscribeRequest>() {

            private long subscriptionId = subscriptionManager.createNewSubscriptionId();

            @Override
            public void onNext(SubscribeRequest request) {
                ServerSubscription previousSubscription = subscriptionManager.getSubscription(subscriptionId);

                // Handle keep alive requests.
                boolean isKeepAlive =
                        previousSubscription != null && request.getEventNamesList().equals(previousSubscription.getEventNames());

                // The first request in the client stream creates a new subscription.
                if (previousSubscription == null && !isKeepAlive) {
                    doSubscribe(subscriptionId, request, responseObserver);
                }

                if (isKeepAlive) {
                    logger.debug("Keep alive for id {}", subscriptionId);
                }
            }

            @Override
            public void onError(Throwable e) {
                if (e instanceof StatusRuntimeException) {
                    io.grpc.Status status = ((StatusRuntimeException) e).getStatus();
                    if (status != null && status.getCode() == io.grpc.Status.Code.CANCELLED) {
                        // Cancelled by the caller.
                        subscriptionManager.removeSubscription(subscriptionId);

                        return;
                    }
                }

                logger.error("subscribe() request stream error", e);
            }

            @Override
            public synchronized void onCompleted() {
                ServerSubscription subscription = subscriptionManager.removeSubscription(subscriptionId);
                if (subscription != null) {
                    responseObserver.onCompleted();
                }
            }
        };

        // The event stream to the client will be provided by the pushEvent method called by the correlator.
    }

    public void pushEvent(org.openksavi.sponge.event.Event event) {
        subscriptionManager.pushEvent(event);
    }

    protected GrpcApiSession createSession() {
        return new GrpcApiSession(null);
    }

    protected UserContext authenticateRequest(SubscribeRequest request) {
        return remoteApiService
                .authenticateRequest(GrpcApiServerUtils.createFakeRemoteApiRequest(remoteApiService.getTypeConverter(), request));
    }
}
