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

import java.util.Collections;
import java.util.List;

import io.grpc.stub.StreamObserver;

import org.openksavi.sponge.grpcapi.proto.SubscribeResponse;
import org.openksavi.sponge.restapi.server.security.UserContext;

public class Subscription {

    private long id;

    private List<String> eventNames;

    private StreamObserver<SubscribeResponse> responseObserver;

    private UserContext userContext;

    private boolean active;

    public Subscription(long id, List<String> eventNames, StreamObserver<SubscribeResponse> responseObserver, UserContext userContext,
            boolean active) {
        this.id = id;
        setEventNames(eventNames);
        this.responseObserver = responseObserver;
        this.userContext = userContext;
        this.active = active;
    }

    public Subscription(long id, List<String> eventNames, StreamObserver<SubscribeResponse> responseObserver, UserContext userContext) {
        this(id, eventNames, responseObserver, userContext, true);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getEventNames() {
        return eventNames;
    }

    public void setEventNames(List<String> eventNames) {
        this.eventNames = Collections.unmodifiableList(eventNames);
    }

    public StreamObserver<SubscribeResponse> getResponseObserver() {
        return responseObserver;
    }

    public void setResponseObserver(StreamObserver<SubscribeResponse> responseObserver) {
        this.responseObserver = responseObserver;
    }

    public boolean isActive() {
        return active;
    }

    public UserContext getUserContext() {
        return userContext;
    }

    public void setUserContext(UserContext userContext) {
        this.userContext = userContext;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
