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

package org.openksavi.sponge.grpcapi.client.util;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.rpc.Status;

import io.grpc.protobuf.StatusProto;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.grpcapi.proto.Event;
import org.openksavi.sponge.grpcapi.proto.ObjectValue;
import org.openksavi.sponge.grpcapi.proto.RequestHeader;
import org.openksavi.sponge.grpcapi.proto.RequestHeader.Builder;
import org.openksavi.sponge.remoteapi.RemoteApiConstants;
import org.openksavi.sponge.remoteapi.client.SpongeClient;
import org.openksavi.sponge.remoteapi.client.SpongeClientException;
import org.openksavi.sponge.remoteapi.feature.converter.FeaturesUtils;
import org.openksavi.sponge.remoteapi.model.RemoteEvent;
import org.openksavi.sponge.remoteapi.model.request.GetVersionRequest;
import org.openksavi.sponge.remoteapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.RecordType;

/**
 * A set of gRPC API client utility methods.
 */
public abstract class GrpcClientUtils {

    private GrpcClientUtils() {
        //
    }

    @SuppressWarnings("unchecked")
    public static RemoteEvent createEventFromGrpc(SpongeClient spongeClient, Event grpcEvent) {
        RemoteEvent event = new RemoteEvent();

        if (StringUtils.isNotEmpty(grpcEvent.getId())) {
            event.setId(grpcEvent.getId());
        }

        if (StringUtils.isNotEmpty(grpcEvent.getName())) {
            event.setName(grpcEvent.getName());
        }

        event.setPriority(grpcEvent.getPriority());

        if (grpcEvent.hasTime()) {
            event.setTime(Instant.ofEpochSecond(grpcEvent.getTime().getSeconds(), grpcEvent.getTime().getNanos()));
        }

        if (StringUtils.isNotEmpty(grpcEvent.getLabel())) {
            event.setLabel(grpcEvent.getLabel());
        }

        if (StringUtils.isNotEmpty(grpcEvent.getDescription())) {
            event.setDescription(grpcEvent.getDescription());
        }

        if (grpcEvent.hasAttributes()) {
            Validate.isTrue(!grpcEvent.getAttributes().hasValueAny(), "Any not supported for event attributes");
            if (StringUtils.isNotEmpty(grpcEvent.getAttributes().getValueJson())) {
                try {
                    TypeConverter typeConverter = spongeClient.getTypeConverter();

                    Map<String, Object> jsonAttributes = (Map<String, Object>) typeConverter.getObjectMapper()
                            .readValue(grpcEvent.getAttributes().getValueJson(), Map.class);

                    RecordType eventType = spongeClient.getEventType(event.getName());

                    // Unmarshal event attributes only if the event type is registered.
                    if (eventType != null) {
                        for (Map.Entry<String, Object> entry : jsonAttributes.entrySet()) {

                            event.getAttributes().put(entry.getKey(),
                                    typeConverter.unmarshal(eventType.getFieldType(entry.getKey()), entry.getValue()));
                        }
                    }
                } catch (IOException e) {
                    throw new SpongeClientException(e);
                }
            }
        }

        if (grpcEvent.hasFeatures()) {
            Validate.isTrue(!grpcEvent.getFeatures().hasValueAny(), "Any not supported for event features");
            if (StringUtils.isNotEmpty(grpcEvent.getFeatures().getValueJson())) {
                try {
                    TypeConverter typeConverter = spongeClient.getTypeConverter();

                    Map<String, Object> jsonFeatures = (Map<String, Object>) typeConverter.getObjectMapper()
                            .readValue(grpcEvent.getFeatures().getValueJson(), Map.class);

                    event.setFeatures(FeaturesUtils.unmarshal(typeConverter.getFeatureConverter(), jsonFeatures));
                } catch (IOException e) {
                    throw new SpongeClientException(e);
                }
            }
        }

        return event;
    }

    /**
     * Uses the Remote API client in order to setup the gRPC request header by reusing the Remote API authentication data.
     *
     * @param spongeClient the Remote API client.
     * @return the header.
     */
    public static RequestHeader createRequestHeader(SpongeClient spongeClient) {
        // Create a fake request to obtain a header.
        org.openksavi.sponge.remoteapi.model.request.RequestHeader header =
                spongeClient.setupRequest(new GetVersionRequest()).getParams().getHeader();

        if (header == null) {
            return null;
        }

        Builder builder = RequestHeader.newBuilder();
        if (header.getUsername() != null) {
            builder.setUsername(header.getUsername());
        }
        if (header.getPassword() != null) {
            builder.setPassword(header.getPassword());
        }
        if (header.getAuthToken() != null) {
            builder.setAuthToken(header.getAuthToken());
        }
        if (header.getFeatures() != null) {
            try {
                builder.setFeatures(ObjectValue.newBuilder()
                        .setValueJson(spongeClient.getTypeConverter().getObjectMapper().writeValueAsString(header.getFeatures())).build());
            } catch (JsonProcessingException e) {
                throw new SpongeClientException(e);
            }
        }

        return builder.build();
    }

    public static boolean isPredefinedGrpcStatusCode(int code) {
        return Arrays.stream(io.grpc.Status.Code.values()).map(s -> s.value()).anyMatch(c -> c == code);
    }

    public static void handleError(SpongeClient spongeClient, String operation, Exception e) {
        Status status = StatusProto.fromThrowable(e);

        org.openksavi.sponge.remoteapi.model.response.ResponseError responseError;

        if (status != null) {
            if (isPredefinedGrpcStatusCode(status.getCode()) && e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }

            responseError = new org.openksavi.sponge.remoteapi.model.response.ResponseError(status.getCode(), status.getMessage());
        }

        responseError =
                new org.openksavi.sponge.remoteapi.model.response.ResponseError(RemoteApiConstants.ERROR_CODE_GENERIC, e.getMessage());

        spongeClient.handleErrorResponse(operation, responseError);
    }
}
