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

/**
 * A Sponge gRPC API client configuration.
 */
public class SpongeGrpcClientConfiguration {

    /** The service port. */
    private Integer port;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    /**
     * A Sponge gRPC API configuration builder.
     *
     * @return the builder.
     */
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private SpongeGrpcClientConfiguration configuration = new SpongeGrpcClientConfiguration();

        /**
         * Builds the configuration.
         *
         * @return the configuration.
         */
        public SpongeGrpcClientConfiguration build() {
            return configuration;
        }

        /**
         * Sets the service port.
         *
         * @param port the service port.
         * @return the builder.
         */
        public Builder port(Integer port) {
            configuration.setPort(port);
            return this;
        }
    }
}
