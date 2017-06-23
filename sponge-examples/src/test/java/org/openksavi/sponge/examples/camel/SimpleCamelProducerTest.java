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

package org.openksavi.sponge.examples.camel;

import static org.awaitility.Awaitility.await;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import org.openksavi.sponge.camel.EngineCamelConfiguration;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.spring.SpringEngine;

public class SimpleCamelProducerTest {

    @Configuration
    public static class ExampleConfiguration extends EngineCamelConfiguration {

        @Bean
        public Engine spongeEngine() {
            // Use EngineBuilder API to create an engine. Also bind Spring and Camel plugins as beans manually.
            return SpringEngine.builder().knowledgeBase("camelkb", "examples/camel/camel_producer.py")
                    .plugins(springPlugin(), camelPlugin()).build();
        }

        @Bean
        public RouteBuilder exampleRoute() {
            return new RouteBuilder() {

                @Override
                public void configure() {
                    from("direct:start").routeId("spongeProducer").to("sponge:spongeEngine");
                }
            };
        }
    }

    @Test
    public void testCamelProducer() throws Exception {
        // Starting Spring context.
        try (GenericApplicationContext context = new AnnotationConfigApplicationContext(ExampleConfiguration.class)) {
            context.start();

            // Sending Camel message.
            CamelContext camel = context.getBean(CamelContext.class);
            ProducerTemplate producerTemplate = camel.createProducerTemplate();
            producerTemplate.sendBody("direct:start", "Send me to the Sponge");

            // Waiting for the engine to process an event.
            Engine engine = context.getBean(Engine.class);
            await().atMost(10, TimeUnit.SECONDS)
                    .until(() -> engine.getOperations().getVariable(AtomicBoolean.class, "sentCamelMessage").get());
            context.stop();
        }
    }
}
