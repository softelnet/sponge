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
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import org.openksavi.sponge.grpcapi.proto.Event;
import org.openksavi.sponge.grpcapi.proto.RequestHeader;
import org.openksavi.sponge.grpcapi.proto.RequestHeader.Builder;
import org.openksavi.sponge.grpcapi.proto.ResponseHeader;
import org.openksavi.sponge.restapi.client.SpongeClientException;
import org.openksavi.sponge.restapi.client.SpongeRestClient;
import org.openksavi.sponge.restapi.model.RemoteEvent;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.type.converter.TypeConverter;
import org.openksavi.sponge.type.RecordType;

/**
 * A set of gRPC API client utility methods.
 */
public abstract class GrpcClientUtils {

    private GrpcClientUtils() {
        //
    }

    @SuppressWarnings("unchecked")
    public static RemoteEvent createEventFromGrpc(SpongeRestClient restClient, Event grpcEvent) {
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
            Validate.isTrue(!grpcEvent.getAttributes().hasValueAny(), "Any not supported for an event attributes");
            if (StringUtils.isNotEmpty(grpcEvent.getAttributes().getValueJson())) {
                try {
                    TypeConverter typeConverter = restClient.getTypeConverter();

                    Map<String, Object> jsonAttributes = (Map<String, Object>) typeConverter.getObjectMapper()
                            .readValue(grpcEvent.getAttributes().getValueJson(), Map.class);

                    RecordType eventType = restClient.getEventType(event.getName());

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

        return event;
    }

    /**
     * Uses the REST client in order to setup the gRPC request header by reusing the REST API authentication data.
     *
     * @param restClient the REST client.
     * @return the header.
     */
    public static RequestHeader createRequestHeader(SpongeRestClient restClient) {
        // Create a fake request to obtain a header.
        org.openksavi.sponge.restapi.model.request.RequestHeader restHeader = restClient.setupRequest(new GetVersionRequest()).getHeader();

        Builder builder = RequestHeader.newBuilder();
        if (restHeader.getId() != null) {
            builder.setId(restHeader.getId());
        }
        if (restHeader.getUsername() != null) {
            builder.setUsername(restHeader.getUsername());
        }
        if (restHeader.getPassword() != null) {
            builder.setPassword(restHeader.getPassword());
        }
        if (restHeader.getAuthToken() != null) {
            builder.setAuthToken(restHeader.getAuthToken());
        }

        return builder.build();
    }

    public static void handleResponseHeader(SpongeRestClient restClient, String operation, ResponseHeader header) {
        if (header == null) {
            return;
        }

        restClient.handleResponseHeader(operation, StringUtils.isNotEmpty(header.getErrorCode()) ? header.getErrorCode() : null,
                StringUtils.isNotEmpty(header.getErrorMessage()) ? header.getErrorMessage() : null,
                StringUtils.isNotEmpty(header.getDetailedErrorMessage()) ? header.getDetailedErrorMessage() : null);
    }
}
