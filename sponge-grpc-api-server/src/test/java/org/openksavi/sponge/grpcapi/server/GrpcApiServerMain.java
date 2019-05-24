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

import io.grpc.Server;
import io.grpc.ServerBuilder;

import org.openksavi.sponge.core.engine.DefaultSpongeEngine;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.grpcapi.server.kb.GrpcApiServerKb;

public class GrpcApiServerMain {

    public static void main(String... args) throws Exception {
        // TODO Use Java client when ready.
        GrpcApiServiceImpl service = new GrpcApiServiceImpl();
        GrpcApiServerPlugin plugin = new GrpcApiServerPlugin();
        plugin.setService(service);
        SpongeEngine engine = DefaultSpongeEngine.builder().plugin(plugin).knowledgeBase(new GrpcApiServerKb("grpcApiServer"))
                .knowledgeBase(new GrpcApiTestKb("test")).build();
        engine.startup();
        service.setEngine(engine);

        Server server = ServerBuilder.forPort(8889).addService(service).build();

        server.start();

        server.awaitTermination();
        engine.shutdown();
    }
}
