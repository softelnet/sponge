/*
 * Copyright 2016-2017 The Sponge authors.
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

import java.util.Map;
import java.util.Objects;

import org.apache.camel.Exchange;

import org.openksavi.sponge.core.event.BaseEvent;
import org.openksavi.sponge.core.util.SpongeUtils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.event.EventClonePolicy;

/**
 * Sponge event containing a Camel message body.
 */
public class SpongeCamelEvent extends BaseEvent {

    private static final long serialVersionUID = -4656303834851564743L;

    public static final String ATTR_ROUTE_ID = "routeId";

    public static final String ATTR_BODY = "body";

    public static final String ATTR_HEADERS = "headers";

    private final String routeId;

    private Object body;

    private Map<String, Object> headers;

    public SpongeCamelEvent(String name, EventClonePolicy clonePolicy, String routeId, Object body, Map<String, Object> headers) {
        super(name, clonePolicy);

        this.routeId = routeId;
        this.body = body;
        this.headers = headers;
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

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, Object> headers) {
        this.headers = headers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T doGet(String name, boolean useDefault, T defaultValue) {
        Object result;
        switch (name) {
        case ATTR_ROUTE_ID:
            result = routeId;
            break;
        case ATTR_BODY:
            result = body;
            break;
        case ATTR_HEADERS:
            result = headers;
            break;
        default:
            return getDefaultAttributeValue(name, useDefault, defaultValue);
        }

        return (T) result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public SpongeCamelEvent set(String name, Object value) {
        switch (name) {
        case ATTR_ROUTE_ID:
            if (!Objects.equals(routeId, value)) {
                throw new IllegalArgumentException("Attribute " + name + " can't be changed");
            }
            break;
        case ATTR_BODY:
            body = value;
            break;
        case ATTR_HEADERS:
            headers = (Map<String, Object>) value;
            break;
        default:
            throw new IllegalArgumentException("Unknown attribute " + name);
        }

        return this;
    }

    @Override
    public boolean has(String name) {
        return ATTR_ROUTE_ID.equals(name) || ATTR_BODY.equals(name) || ATTR_HEADERS.equals(name);
    }

    @Override
    public Map<String, Object> getAll() {
        return SpongeUtils.immutableMapOf(ATTR_ROUTE_ID, routeId, ATTR_BODY, body, ATTR_HEADERS, headers);
    }

    public static SpongeCamelEvent create(Engine engine, String name, Exchange exchange) {
        return new SpongeCamelEvent(name, engine.getConfigurationManager().getEventClonePolicy(),
                exchange.getUnitOfWork().getRouteContext().getRoute().getId(), exchange.getIn().getBody(), exchange.getIn().getHeaders());
    }

    public static SpongeCamelEvent create(Engine engine, Exchange exchange) {
        String exchangRouteId = exchange.getUnitOfWork().getRouteContext().getRoute().getId();
        return new SpongeCamelEvent(exchangRouteId, engine.getConfigurationManager().getEventClonePolicy(), exchangRouteId,
                exchange.getIn().getBody(), exchange.getIn().getHeaders());
    }
}
