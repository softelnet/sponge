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

import org.openksavi.sponge.config.Configuration;
import org.openksavi.sponge.java.JPlugin;

/**
 * Sponge gRPC API server plugin.
 */
public class GrpcApiServerPlugin extends JPlugin {

    public static final String NAME = "grpcApiServer";

    private GrpcApiServiceImpl service;

    public GrpcApiServerPlugin() {
        setName(NAME);
    }

    public GrpcApiServerPlugin(String name) {
        super(name);
    }

    @Override
    public void onConfigure(Configuration configuration) {
        // TODO
    }

    public GrpcApiServiceImpl getService() {
        return service;
    }

    public void setService(GrpcApiServiceImpl service) {
        this.service = service;
    }

    // public void start() {
    // getSponge().enableJava(GrpcApiSubscribeCorrelator.class);
    // }
    //
    // public void stop() {
    // getSponge().disableJava(GrpcApiSubscribeCorrelator.class);
    // }
}
