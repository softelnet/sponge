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

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.impl.DefaultProducer;

import org.openksavi.sponge.engine.Engine;

/**
 * Sponge Camel producer.
 */
public class SpongeProducer extends DefaultProducer {

    private final Engine engine;

    private final String action;

    public SpongeProducer(Endpoint endpoint, Engine engine, String action) {
        super(endpoint);
        this.engine = engine;
        this.action = action;
    }

    @Override
    public void process(Exchange exchange) throws Exception {
        Message in = exchange.getIn();

        String actionName = in.getHeader(SpongeConstants.SPONGE_ACTION, String.class);
        if (actionName != null) {
            // Remove the header so it won't be propagated.
            in.removeHeader(SpongeConstants.SPONGE_ACTION);
        }

        if (actionName == null) {
            actionName = action != null ? action : CamelProducerAction.NAME;
        }

        Object result = engine.getOperations().call(actionName, exchange);

        exchange.getIn().setBody(result);
    }
}
