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

import com.google.rpc.Code;
import com.google.rpc.Status;

import io.grpc.StatusRuntimeException;
import io.grpc.protobuf.StatusProto;

import org.apache.commons.lang3.StringUtils;

import org.openksavi.sponge.grpcapi.proto.RequestHeader;
import org.openksavi.sponge.grpcapi.proto.ResponseHeader;
import org.openksavi.sponge.grpcapi.proto.SubscribeRequest;
import org.openksavi.sponge.grpcapi.proto.VersionRequest;
import org.openksavi.sponge.grpcapi.proto.VersionResponse;
import org.openksavi.sponge.restapi.model.request.GetVersionRequest;
import org.openksavi.sponge.restapi.model.request.SpongeRequest;
import org.openksavi.sponge.restapi.model.response.GetVersionResponse;

/**
 * A set of common gRPC API utility methods.
 */
public abstract class GrpcApiServerUtils {

    private GrpcApiServerUtils() {
        //
    }

    public static <T extends SpongeRequest> T setupRestRequestHeader(T restRequest, RequestHeader header) {
        if (header != null) {
            if (!StringUtils.isEmpty(header.getId())) {
                restRequest.getHeader().setId(header.getId());
            }
            if (!StringUtils.isEmpty(header.getUsername())) {
                restRequest.getHeader().setUsername(header.getUsername());
            }
            if (!StringUtils.isEmpty(header.getPassword())) {
                restRequest.getHeader().setPassword(header.getPassword());
            }
            if (!StringUtils.isEmpty(header.getAuthToken())) {
                restRequest.getHeader().setAuthToken(header.getAuthToken());
            }
        }

        return restRequest;
    }

    public static GetVersionRequest createRestRequest(VersionRequest request) {
        return setupRestRequestHeader(new GetVersionRequest(), request.hasHeader() ? request.getHeader() : null);
    }

    public static SpongeRequest createRestRequest(SubscribeRequest request) {
        return setupRestRequestHeader(new SpongeRequest(), request.hasHeader() ? request.getHeader() : null);
    }

    public static ResponseHeader createResponseHeader(org.openksavi.sponge.restapi.model.response.ResponseHeader restHeader) {
        ResponseHeader.Builder headerBuilder = ResponseHeader.newBuilder();
        if (restHeader.getId() != null) {
            headerBuilder.setId(restHeader.getId());
        }
        if (restHeader.getErrorCode() != null) {
            headerBuilder.setErrorCode(restHeader.getErrorCode());
        }
        if (restHeader.getErrorMessage() != null) {
            headerBuilder.setErrorMessage(restHeader.getErrorMessage());
        }
        if (restHeader.getDetailedErrorMessage() != null) {
            headerBuilder.setDetailedErrorMessage(restHeader.getDetailedErrorMessage());
        }

        return headerBuilder.build();
    }

    public static VersionResponse createResponse(GetVersionResponse restResponse) {
        VersionResponse.Builder builder = VersionResponse.newBuilder().setHeader(createResponseHeader(restResponse.getHeader()));

        if (restResponse.getVersion() != null) {
            builder.setVersion(restResponse.getVersion());
        }

        return builder.build();
    }

    public static StatusRuntimeException createInternalException(Throwable e) {
        return StatusProto.toStatusRuntimeException(Status.newBuilder().setCode(Code.INTERNAL.getNumber())
                .setMessage(e.getMessage() != null ? e.getMessage() : e.toString()).build());
    }
}
