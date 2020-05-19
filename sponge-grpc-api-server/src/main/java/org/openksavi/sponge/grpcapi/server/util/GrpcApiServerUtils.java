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

package org.openksavi.sponge.grpcapi.server.util;

import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;

import org.apache.commons.lang3.StringUtils;

import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.grpcapi.proto.ObjectValue;
import org.openksavi.sponge.grpcapi.proto.RequestHeader;
import org.openksavi.sponge.grpcapi.proto.ResponseHeader;
import org.openksavi.sponge.grpcapi.proto.SubscribeRequest;
import org.openksavi.sponge.grpcapi.proto.VersionRequest;
import org.openksavi.sponge.grpcapi.proto.VersionResponse;
import org.openksavi.sponge.remoteapi.model.request.GetVersionRequest;
import org.openksavi.sponge.remoteapi.model.request.SpongeRequest;
import org.openksavi.sponge.remoteapi.model.response.GetVersionResponse;
import org.openksavi.sponge.remoteapi.type.converter.TypeConverter;

/**
 * A set of common gRPC API utility methods.
 */
public abstract class GrpcApiServerUtils {

    private GrpcApiServerUtils() {
        //
    }

    @SuppressWarnings("unchecked")
    public static <T extends SpongeRequest> T setupRemoteApiRequestHeader(TypeConverter typeConverter, T remoteApiRequest, RequestHeader header) {
        if (header != null) {
            if (!StringUtils.isEmpty(header.getId())) {
                remoteApiRequest.getHeader().setId(header.getId());
            }
            if (!StringUtils.isEmpty(header.getUsername())) {
                remoteApiRequest.getHeader().setUsername(header.getUsername());
            }
            if (!StringUtils.isEmpty(header.getPassword())) {
                remoteApiRequest.getHeader().setPassword(header.getPassword());
            }
            if (!StringUtils.isEmpty(header.getAuthToken())) {
                remoteApiRequest.getHeader().setAuthToken(header.getAuthToken());
            }
            if (header.hasFeatures()) {
                try {
                    Map<String, Object> jsonFeatures =
                            (Map<String, Object>) typeConverter.getObjectMapper().readValue(header.getFeatures().getValueJson(), Map.class);
                    remoteApiRequest.getHeader().setFeatures(jsonFeatures);
                } catch (JsonProcessingException e) {
                    throw SpongeUtils.wrapException(e);
                }
            }
        }

        return remoteApiRequest;
    }

    public static GetVersionRequest createRemoteApiRequest(TypeConverter typeConverter, VersionRequest request) {
        return setupRemoteApiRequestHeader(typeConverter, new GetVersionRequest(), request.hasHeader() ? request.getHeader() : null);
    }

    public static SpongeRequest createFakeRemoteApiRequest(TypeConverter typeConverter, SubscribeRequest request) {
        // Use a fake request.
        return setupRemoteApiRequestHeader(typeConverter, new GetVersionRequest(), request.hasHeader() ? request.getHeader() : null);
    }

    public static ResponseHeader createResponseHeader(TypeConverter typeConverter,
            org.openksavi.sponge.remoteapi.model.response.ResponseHeader remoteApiHeader) {
        ResponseHeader.Builder headerBuilder = ResponseHeader.newBuilder();
        if (remoteApiHeader.getId() != null) {
            headerBuilder.setId(remoteApiHeader.getId());
        }
        if (remoteApiHeader.getErrorCode() != null) {
            headerBuilder.setErrorCode(remoteApiHeader.getErrorCode());
        }
        if (remoteApiHeader.getErrorMessage() != null) {
            headerBuilder.setErrorMessage(remoteApiHeader.getErrorMessage());
        }
        if (remoteApiHeader.getDetailedErrorMessage() != null) {
            headerBuilder.setDetailedErrorMessage(remoteApiHeader.getDetailedErrorMessage());
        }
        if (remoteApiHeader.getFeatures() != null) {
            try {
                headerBuilder.setFeatures(ObjectValue.newBuilder()
                        .setValueJson(typeConverter.getObjectMapper().writeValueAsString(remoteApiHeader.getFeatures())).build());
            } catch (JsonProcessingException e) {
                throw SpongeUtils.wrapException(e);
            }
        }

        return headerBuilder.build();
    }

    public static VersionResponse createResponse(TypeConverter typeConverter, GetVersionResponse remoteApiResponse) {
        VersionResponse.Builder builder =
                VersionResponse.newBuilder().setHeader(createResponseHeader(typeConverter, remoteApiResponse.getHeader()));

        if (remoteApiResponse.getBody().getVersion() != null) {
            builder.setVersion(remoteApiResponse.getBody().getVersion());
        }

        return builder.build();
    }

    public static StatusRuntimeException createInternalException(Throwable e) {
        return StatusProto.toStatusRuntimeException(Status.newBuilder().setCode(Code.INTERNAL.getNumber())
                .setMessage(e.getMessage() != null ? e.getMessage() : e.toString()).build());
    }
}
