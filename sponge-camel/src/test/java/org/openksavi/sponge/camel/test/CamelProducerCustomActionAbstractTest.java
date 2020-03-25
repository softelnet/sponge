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
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.SingleRouteCamelConfiguration;
import org.springframework.context.annotation.Bean;

import org.openksavi.sponge.engine.SpongeEngine;
import org.openksavi.sponge.spring.SpringSpongeEngine;

public abstract class CamelProducerCustomActionAbstractTest {

    @EndpointInject("mock:direct:log")
    protected MockEndpoint logEndpoint;

    @Produce("direct:start")
    protected ProducerTemplate testProducer;

    @Inject
    protected SpongeEngine engine;

    protected static abstract class AbstractTestConfig extends SingleRouteCamelConfiguration {

        @Bean
        public SpongeEngine spongeEngine() {
            return SpringSpongeEngine.builder().knowledgeBase("kb", "examples/camel/camel_producer_custom_action.py").build();
        }
    }

    protected void testRoute() throws InterruptedException {
        CamelTestUtils.setResultWaitTime(60000, logEndpoint);

        logEndpoint.expectedMessageCount(1);
        logEndpoint.expectedBodiesReceived("OK");

        String message = "Send me to the Sponge";
        testProducer.sendBody("direct:start", message);

        await().atMost(60, TimeUnit.SECONDS).until(() -> engine.getOperations().getVariable(String.class, "calledCustomAction") != null);
        TimeUnit.SECONDS.sleep(2);

        assertEquals(message, engine.getOperations().getVariable(String.class, "calledCustomAction"));
        assertFalse(engine.getOperations().getVariable(AtomicBoolean.class, "sentCamelMessage_spongeProducer").get());

        logEndpoint.assertIsSatisfied();

        assertFalse(engine.isError());
    }
}
