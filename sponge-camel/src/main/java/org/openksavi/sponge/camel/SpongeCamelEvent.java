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

import org.apache.camel.Exchange;

import org.openksavi.sponge.core.event.AttributeMapEvent;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * Sponge event containing a Camel message body.
 */
public class SpongeCamelEvent extends AttributeMapEvent {

    private static final long serialVersionUID = -4656303834851564743L;

    private final String routeId;

    private Object body;

    public SpongeCamelEvent(String name, EventClonePolicy clonePolicy, String routeId, Object body) {
        super(name, clonePolicy);

        this.routeId = routeId;
        this.body = body;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public String getRouteId() {
        return routeId;
    }

    public static SpongeCamelEvent create(Engine engine, String name, Exchange exchange) {
        return new SpongeCamelEvent(name, engine.getConfigurationManager().getEventClonePolicy(),
                exchange.getUnitOfWork().getRouteContext().getRoute().getId(), exchange.getIn().getBody());
    }

    public static SpongeCamelEvent create(Engine engine, Exchange exchange) {
        String exchangRouteId = exchange.getUnitOfWork().getRouteContext().getRoute().getId();
        return new SpongeCamelEvent(exchangRouteId, engine.getConfigurationManager().getEventClonePolicy(), exchangRouteId,
                exchange.getIn().getBody());
    }
}
