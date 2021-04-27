/*
 * Copyright 2016-2021 The Sponge authors.
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

package org.openksavi.sponge.remoteapi.server.camel;

import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.spi.RestConfiguration;

/**
 * A set of Camel support server utility methods.
 */
public abstract class CamelSupportUtils {

    private CamelSupportUtils() {
        //
    }

    /**
     * A copy of Camel's private {@code RestBindingAdvice.setCORSHeaders}.
     *
     * @param corsHeaders the CORS headers.
     * @param exchange the exchange.
     */
    //
    public static void setCorsHeaders(Map<String, String> corsHeaders, Exchange exchange) {
        Message msg = exchange.getMessage();

        // Use default value if none has been configured.
        String allowOrigin = corsHeaders != null ? corsHeaders.get("Access-Control-Allow-Origin") : null;
        if (allowOrigin == null) {
            allowOrigin = RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_ORIGIN;
        }
        String allowMethods = corsHeaders != null ? corsHeaders.get("Access-Control-Allow-Methods") : null;
        if (allowMethods == null) {
            allowMethods = RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_METHODS;
        }
        String allowHeaders = corsHeaders != null ? corsHeaders.get("Access-Control-Allow-Headers") : null;
        if (allowHeaders == null) {
            allowHeaders = RestConfiguration.CORS_ACCESS_CONTROL_ALLOW_HEADERS;
        }
        String maxAge = corsHeaders != null ? corsHeaders.get("Access-Control-Max-Age") : null;
        if (maxAge == null) {
            maxAge = RestConfiguration.CORS_ACCESS_CONTROL_MAX_AGE;
        }
        String allowCredentials = corsHeaders != null ? corsHeaders.get("Access-Control-Allow-Credentials") : null;

        // Restrict the origin if credentials are allowed (https://www.w3.org/TR/cors/ - section 6.1, point 3).
        String origin = exchange.getIn().getHeader("Origin", String.class);
        if ("true".equalsIgnoreCase(allowCredentials) && "*".equals(allowOrigin) && origin != null) {
            allowOrigin = origin;
        }

        msg.setHeader("Access-Control-Allow-Origin", allowOrigin);
        msg.setHeader("Access-Control-Allow-Methods", allowMethods);
        msg.setHeader("Access-Control-Allow-Headers", allowHeaders);
        msg.setHeader("Access-Control-Max-Age", maxAge);
        if (allowCredentials != null) {
            msg.setHeader("Access-Control-Allow-Credentials", allowCredentials);
        }
    }
}
