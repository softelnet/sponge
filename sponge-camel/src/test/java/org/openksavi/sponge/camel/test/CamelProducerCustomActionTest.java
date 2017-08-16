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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.apache.camel.test.spring.CamelSpringDelegatingTestContextLoader;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.spring.SpringEngine;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { CamelProducerCustomActionTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
@DirtiesContext
public class CamelProducerCustomActionTest {

    @EndpointInject(uri = "mock:direct:log")
    protected MockEndpoint logEndpoint;

    @Produce(uri = "direct:start")
    protected ProducerTemplate testProducer;

    @Inject
    protected Engine engine;

    @Configuration
    public static class TestConfig extends SingleRouteCamelConfiguration {

        @Bean
        public Engine spongeEngine() {
            return SpringEngine.builder().knowledgeBase("kb", "examples/camel/camel_producer_custom_action.py").build();
        }

        @Bean
        @Override
        public RouteBuilder route() {
            return new RouteBuilder() {

                @Override
                public void configure() throws Exception {
                    // @formatter:off
                    from("direct:start").routeId("spongeProducer")
                            .to("sponge:spongeEngine?action=CustomAction&managed=false")
                            .log("Action result as a body: ${body}")
                            .to("direct:log");

                    from("direct:log").routeId("directLog")
                            .log("Action result as a body: ${body}");
                    // @formatter:on
                }
            };
        }
    }

    @Test
    public void testRoute() throws InterruptedException {
        logEndpoint.expectedMessageCount(1);
        logEndpoint.expectedBodiesReceived("OK");

        String message = "Send me to the Sponge";
        testProducer.sendBody("direct:start", message);

        await().atMost(10, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(String.class, "calledCustomAction") != null);
        TimeUnit.SECONDS.sleep(1);

        assertEquals(message, engine.getOperations().getVariable(String.class, "calledCustomAction"));
        assertFalse(engine.getOperations().getVariable(AtomicBoolean.class, "sentCamelMessage_spongeProducer").get());

        logEndpoint.assertIsSatisfied();
    }
}
