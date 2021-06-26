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

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.spring.javaconfig.CamelConfiguration;
import org.apache.camel.test.spring.CamelSpringRunner;
import org.apache.camel.test.spring.MockEndpoints;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import org.openksavi.sponge.engine.SpongeEngine;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { CamelGroovyConfigurationTest.TestConfig.class })
@MockEndpoints
@DirtiesContext
public class CamelGroovyConfigurationTest {

    @EndpointInject("mock:direct:end")
    protected MockEndpoint endEndpoint;

    @EndpointInject("mock:direct:error")
    protected MockEndpoint errorEndpoint;

    @EndpointInject("mock:direct:log")
    protected MockEndpoint logEndpoint;

    @Produce("direct:test")
    protected ProducerTemplate testProducer;

    @Inject
    protected SpongeEngine engine;

    @Configuration
    @ImportResource("CamelTestTestConfiguration.groovy")
    public static class TestConfig extends CamelConfiguration {

        @Bean
        public ProducerTemplate spongeProducerTemplate() throws Exception {
            return camelContext().createProducerTemplate();
        }
    }

    @Test
    public void testRoute() throws InterruptedException {
        CamelTestUtils.setResultWaitTime(60000, endEndpoint, errorEndpoint, logEndpoint);

        endEndpoint.expectedMessageCount(1);
        errorEndpoint.expectedMessageCount(0);
        logEndpoint.expectedMessageCount(1);

        testProducer.sendBody("<name>test</name>");

        endEndpoint.assertIsSatisfied();
        errorEndpoint.assertIsSatisfied();
        logEndpoint.assertIsSatisfied();

        TimeUnit.SECONDS.sleep(2);

        assertEquals(null, engine.getError());
    }
}
