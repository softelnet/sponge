/*
 * Copyright 2016-2017 Softelnet.
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

package org.openksavi.sponge.camel;

import org.apache.camel.Endpoint;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.RouteDefinition;

/**
 * Introduces {@code fromS} methods that delegate to the corresponding {@code from} methods in order to avoid using 'from'
 * since it could be a reserved keyword in scripting languages (e.g. in Python).
 */
public abstract class ScriptRouteBuilder extends RouteBuilder {

    public RouteDefinition fromS(String uri) {
        return from(uri);
    }

    public RouteDefinition fromS(Endpoint endpoint) {
        return from(endpoint);
    }

    public RouteDefinition fromS(String... uris) {
        return from(uris);
    }

    public RouteDefinition fromS(Endpoint... endpoints) {
        return from(endpoints);
    }
}
