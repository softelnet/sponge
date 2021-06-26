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
import org.openksavi.sponge.remoteapi.model.response.ErrorResponse;
import org.openksavi.sponge.remoteapi.model.response.GetVersionResponse;
import org.openksavi.sponge.remoteapi.server.RemoteApiService;
import org.openksavi.sponge.remoteapi.type.converter.TypeConverter;

/**
 * A set of common gRPC API utility methods.
 */
@SuppressWarnings("rawtypes")
public abstract class GrpcApiServerUtils {

    private GrpcApiServerUtils() {
        //
    }

    @SuppressWarnings("unchecked")
    public static <T extends SpongeRequest> T setupRemoteApiRequestHeader(TypeConverter typeConverter, T remoteApiRequest,
            RequestHeader header) {

        if (remoteApiRequest.getHeader() == null) {
            remoteApiRequest.setHeader(new org.openksavi.sponge.remoteapi.model.request.RequestHeader());
        }

        if (header != null) {
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

    public static ResponseHeader createResponseHeader(RemoteApiService remoteApiService, TypeConverter typeConverter,
            org.openksavi.sponge.remoteapi.model.response.SpongeResponse remoteApiResponse) {
        ResponseHeader.Builder headerBuilder = ResponseHeader.newBuilder();
        if (remoteApiResponse.getError() != null) {
            throw createStatusException(remoteApiService, remoteApiResponse.getError());
        }

        if (remoteApiResponse.getResult() != null && remoteApiResponse.getResult().getHeader() != null
                && remoteApiResponse.getResult().getHeader().getFeatures() != null) {
            try {
                headerBuilder.setFeatures(ObjectValue.newBuilder()
                        .setValueJson(
                                typeConverter.getObjectMapper().writeValueAsString(remoteApiResponse.getResult().getHeader().getFeatures()))
                        .build());
            } catch (JsonProcessingException e) {
                throw SpongeUtils.wrapException(e);
            }
        }

        return headerBuilder.build();
    }

    public static VersionResponse createResponse(RemoteApiService remoteApiService, GetVersionResponse remoteApiResponse) {
        VersionResponse.Builder builder = VersionResponse.newBuilder()
                .setHeader(createResponseHeader(remoteApiService, remoteApiService.getTypeConverter(), remoteApiResponse));

        if (remoteApiResponse.getResult().getValue() != null) {
            builder.setVersion(remoteApiResponse.getResult().getValue());
        }

        return builder.build();
    }

    public static StatusRuntimeException createStatusException(RemoteApiService remoteApiService, Throwable e) {
        ErrorResponse remoteApiErrorResponse =
                remoteApiService.getErrorHandlerFactory().createErrorHandler(remoteApiService.getSettings(), e).createErrorResponse();

        return createStatusException(remoteApiService, remoteApiErrorResponse.getError());
    }

    public static StatusRuntimeException createStatusException(RemoteApiService remoteApiService,
            org.openksavi.sponge.remoteapi.model.response.ResponseError responseError) {
        return StatusProto.toStatusRuntimeException(
                Status.newBuilder().setCode(responseError.getCode()).setMessage(responseError.getMessage()).build());
    }

    public static int calculateDefaultPortByRemoteApi(int remoteApiPort) {
        return remoteApiPort + 1;
    }
}
