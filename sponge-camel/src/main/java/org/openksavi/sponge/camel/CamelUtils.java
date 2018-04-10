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

import org.apache.camel.Exchange;
import org.apache.camel.builder.xml.XPathBuilder;

import org.openksavi.sponge.SpongeException;
import org.openksavi.sponge.action.Action;
import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.event.Event;
import org.openksavi.sponge.event.EventDefinition;

/**
 * Camel integration utility methods.
 */
public abstract class CamelUtils {

    public static void assertCamelProducerActionArgs(Action action, Object... args) {
        if (args == null || args.length != 1 || !(args[0] instanceof Exchange)) {
            throw new IllegalArgumentException(action.getName() + " requires exactly one argument of class " + Exchange.class);
        }
    }

    public static Event getEvent(Exchange exchange) {
        if (exchange.getIn() != null) {
            Object body = exchange.getIn().getBody();
            if (body != null) {
                if (body instanceof Event) {
                    return (Event) body;
                } else if (body instanceof EventDefinition) {
                    return ((EventDefinition) body).make();
                }
            }
        }

        return null;
    }

    public static Event getOrCreateInputEvent(SpongeEngine engine, Exchange exchange) {
        Event event = getEvent(exchange);

        if (event == null) {
            event = SpongeCamelEvent.create(engine, exchange);
        }

        return event;
    }

    public static String xpath(Exchange exchange, String path) {
        return XPathBuilder.xpath(path).stringResult().evaluate(exchange, String.class);
    }

    public static CamelPlugin getPlugin(SpongeEngine engine) {
        CamelPlugin plugin = engine.getPluginManager().getPlugin(CamelPlugin.NAME, CamelPlugin.class);
        if (plugin == null) {
            throw new SpongeException("Camel plugin not found");
        }

        return plugin;
    }
}
