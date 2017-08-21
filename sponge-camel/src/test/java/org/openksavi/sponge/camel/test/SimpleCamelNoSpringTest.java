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

package org.openksavi.sponge.camel.test;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;
import org.junit.Test;

import org.openksavi.sponge.core.engine.DefaultEngine;
import org.openksavi.sponge.engine.Engine;

public class SimpleCamelNoSpringTest {

    @Test
    public void testCamelProducer() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        Engine engine = DefaultEngine.builder().knowledgeBase("camelkb", "examples/camel/camel_producer.py").build();
        registry.put("spongeEngine", engine);
        CamelContext camel = new DefaultCamelContext(registry);

        camel.addRoutes(new RouteBuilder() {

            @Override
            public void configure() {
                // @formatter:off
                from("direct:start").routeId("spongeProducer")
                    .to("sponge:spongeEngine");
                // @formatter:on
            }
        });

        camel.start();

        try {
            ProducerTemplate producerTemplate = camel.createProducerTemplate();
            producerTemplate.sendBody("direct:start", "Send me to the Sponge");

            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "sentCamelMessage").get());
        } finally {
            camel.stop();
        }
    }

    @Test
    public void testCamelConsumer() throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        Engine engine = DefaultEngine.builder().knowledgeBase("camelkb", "examples/camel/camel_consumer.py").build();
        registry.put("spongeEngine", engine);
        CamelContext camel = new DefaultCamelContext(registry);

        camel.addRoutes(new RouteBuilder() {

            @Override
            public void configure() {
                // @formatter:off
                from("sponge:spongeEngine").routeId("spongeConsumer")
                    .log("${body}")
                    .process(exchange -> engine.getOperations().getVariable(AtomicBoolean.class, "receivedCamelMessage").set(true))
                    .to("stream:out");
                // @formatter:on
            }
        });

        camel.start();

        try {
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "receivedCamelMessage").get());
            assertFalse(engine.isError());
        } finally {
            camel.stop();
        }
    }
}
