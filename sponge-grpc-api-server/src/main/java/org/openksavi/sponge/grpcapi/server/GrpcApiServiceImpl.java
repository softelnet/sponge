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

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.protobuf.Any;
import com.google.protobuf.Timestamp;
import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;
import io.grpc.stub.StreamObserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.proto.Event;
import org.openksavi.sponge.grpcapi.proto.ObjectValue;
import org.openksavi.sponge.grpcapi.proto.ResponseHeader;
import org.openksavi.sponge.grpcapi.proto.SpongeGrpcApiGrpc.SpongeGrpcApiImplBase;
import org.openksavi.sponge.grpcapi.proto.SubscribeRequest;
import org.openksavi.sponge.grpcapi.proto.SubscribeResponse;
import org.openksavi.sponge.grpcapi.proto.VersionRequest;
import org.openksavi.sponge.grpcapi.proto.VersionResponse;
import org.openksavi.sponge.restapi.type.converter.DefaultTypeConverter;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.restapi.util.RestApiUtils;

// TODO Authorization like in the REST API service (but handle username or authToken that can be changes).
public class GrpcApiServiceImpl extends SpongeGrpcApiImplBase {

    private static final Logger logger = LoggerFactory.getLogger(GrpcApiServiceImpl.class);

    private SpongeEngine engine;

    private AtomicLong currentSubscriptionId = new AtomicLong(0);

    private Map<Long, Subscription> subscriptions = new ConcurrentHashMap<>();

    // TODO Delegate to BaseRemoteApiService
    // private DefaultRestApiService targetApiService;

    // TODO Use targetApiService.
    private TypeConverter typeConverter = new DefaultTypeConverter(RestApiUtils.createObjectMapper());

    public GrpcApiServiceImpl() {
    }

    public SpongeEngine getEngine() {
        return engine;
    }

    public void setEngine(SpongeEngine engine) {
        this.engine = engine;
    }

    // public DefaultRestApiService getTargetApiService() {
    // return targetApiService;
    // }
    //
    // public void setTargetApiService(DefaultRestApiService targetApiService) {
    // this.targetApiService = targetApiService;
    // }

    @Override
    public void getVersion(VersionRequest request, StreamObserver<VersionResponse> responseObserver) {
        try {
            // TODO Delegate to BaseRemoteApiService.

            VersionResponse.Builder responseBuilder = VersionResponse.newBuilder().setVersion(engine.getVersion());
            if (request.getHeader() != null && request.getHeader().getId() != null) {
                responseBuilder.setHeader(ResponseHeader.newBuilder().setId(request.getHeader().getId()));
            }

            responseObserver.onNext(responseBuilder.build());
            responseObserver.onCompleted();
        } catch (Throwable e) {
            // TODO Error handling compose from BaseRemoteApiService.
            VersionResponse response = VersionResponse.newBuilder()
                    .setHeader(ResponseHeader.newBuilder().setId(request.getHeader() != null ? request.getHeader().getId() : null)
                            .setErrorCode("?").setErrorMessage("?").setDetailedErrorMessage("?").build())
                    .build();

            Status status = Status.newBuilder().setCode(Code.INTERNAL.getNumber()).setMessage(response.getHeader().getErrorMessage())
                    .addDetails(Any.pack(response)).build();
            responseObserver.onError(StatusProto.toStatusRuntimeException(status));
        }
    }

    @Override
    public StreamObserver<SubscribeRequest> subscribe(StreamObserver<SubscribeResponse> responseObserver) {
        return new StreamObserver<SubscribeRequest>() {

            private long subscriptionId = currentSubscriptionId.incrementAndGet();

            @Override
            public void onNext(SubscribeRequest request) {
                // TODO Use concurrent map?
                Subscription previousSubscription = subscriptions.get(subscriptionId);

                // Handle keep alive requests.
                boolean isKeepAlive =
                        previousSubscription != null && request.getEventNamesList().equals(previousSubscription.getEventNames());
                if (previousSubscription == null && !isKeepAlive) {
                    logger.debug("New subscription {}", subscriptionId);
                    subscriptions.put(subscriptionId, new Subscription(subscriptionId, request.getEventNamesList(),
                            responseObserver/*
                                             * , request.getHeader() != null ? request.getHeader().getId() : null
                                             */));
                }

                if (isKeepAlive) {
                    logger.debug("Keep alive for id {}", subscriptionId);
                }
                // if (previousSubscription == null) {
                // // Push the initial response with no event.
                // responseObserver.onNext(SubscribeResponse.newBuilder().setSubscriptionId(subscriptionId).build());
                // }
            }

            @Override
            public void onError(Throwable e) {
                logger.error("subscribe() request stream error", e);
            }

            @Override
            public synchronized void onCompleted() {
                Subscription subscription = subscriptions.remove(subscriptionId);
                if (subscription != null) {
                    responseObserver.onCompleted();
                }
            }

        };

        // List<String> eventNames = request.getEventNamesList();
        //
        // long subscriptionId = currentSubscriptionId.incrementAndGet();
        // subscriptions.put(subscriptionId, new Subscription(subscriptionId, eventNames, responseObserver,
        // request.getHeader() != null ? request.getHeader().getId() : null));

        // The stream will be provides by the pushEvent method called by the correlator.
    }

    public void pushEvent(org.openksavi.sponge.event.Event event) {
        subscriptions.values().forEach(subscription -> {
            if (subscription.isActive() && subscription.getEventNames().stream()
                    .anyMatch(eventNamePattern -> engine.getPatternMatcher().matches(eventNamePattern, event.getName()))) {
                // TODO Request header, id.
                // TODO Error handling - server side log.
                try {
                    synchronized (subscription.getResponseObserver()) {
                        subscription.getResponseObserver().onNext(createSubscribeResponse(subscription, event));
                    }
                } catch (StatusRuntimeException e) {
                    if (!e.getStatus().isOk()) {// Objects.equals(e.getStatus().getCode(), io.grpc.Status.Code.CANCELLED)) {
                        logger.debug("Setting subscription {} as inactive because the status code is {}", subscription.getId(),
                                e.getStatus().getCode());
                        subscription.setActive(false);
                    } else {
                        logger.error("pushEvent() StatusRuntimeException", e);
                    }
                } catch (Throwable e) {
                    logger.error("pushEvent() error", e);
                }
            }
        });

        // Cleanup inactive subscriptions.
        // TODO Move cleanup somewhere else.
        List<Long> inactiveSubscriptionIds = subscriptions.values().stream().filter(subscription -> !subscription.isActive())
                .map(subscription -> subscription.getId()).collect(Collectors.toList());
        inactiveSubscriptionIds.forEach(subscriptions::remove);
    }

    // TODO MapStruct.
    protected SubscribeResponse createSubscribeResponse(Subscription subscription, org.openksavi.sponge.event.Event event) {
        return SubscribeResponse.newBuilder().setSubscriptionId(subscription.getId()).setEvent(createEvent(event)).build();
    }

    // TODO MapStruct.
    protected Event createEvent(org.openksavi.sponge.event.Event event) {
        Event.Builder eventBuilder = Event.newBuilder().setId(event.getId()).setName(event.getName()).setPriority(event.getPriority())
                .setTime(Timestamp.newBuilder().setSeconds(event.getTime().getEpochSecond()).setNanos(event.getTime().getNano()));

        Map<String, Object> attributes = event.getAll();

        if (attributes != null) {
            ObjectValue.Builder attributesValueBuilder = ObjectValue.newBuilder();
            try {
                attributesValueBuilder.setValueJson(typeConverter.getObjectMapper().writeValueAsString(attributes));
            } catch (JsonProcessingException e) {
                throw SpongeUtils.wrapException(e);
            }
            eventBuilder.setAttributes(attributesValueBuilder.build());
        }

        // event.getAll().forEach((name, value) -> {
        // try {
        // });

        return eventBuilder.build();
    }

    // TODO Could be unnecessary. Unsubscribing can be done by changing: rpc Subscribe (stream SubscribeRequest) returns (stream
    // SubscribeResponse) {}
    // and pass SubscribeRequest.cancelled = true at a specific time.
    // @Override
    // public void unsubscribe(UnsubscribeRequest request, StreamObserver<UnsubscribeResponse> responseObserver) {
    // // TODO Request header, id, user.
    // // TODO Prevent unsubscribe of a subscription initiated by other party.
    // // Subscription subscription = subscriptions.remove(request.getSubscriptionId());
    // //
    // // if (subscription != null) {
    // // subscription.getResponseObserver().onCompleted();
    // // }
    // }
}
