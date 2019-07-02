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

import java.util.function.Consumer;

import io.grpc.netty.NettyChannelBuilder;

import org.openksavi.sponge.restapi.client.SpongeRestClient;

/**
 * A default Sponge gRPC API client.
 */
public class DefaultSpongeGrpcClient extends NettySpongeGrpcClient {

    public DefaultSpongeGrpcClient(SpongeRestClient restClient, Consumer<NettyChannelBuilder> channelBuilderConfigurer) {
        super(restClient, channelBuilderConfigurer);
    }

    public DefaultSpongeGrpcClient(SpongeRestClient restClient) {
        super(restClient, null);
    }
}
