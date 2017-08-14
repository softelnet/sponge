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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
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

import org.openksavi.sponge.camel.CamelUtils;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.spring.SpringEngine;

@RunWith(CamelSpringRunner.class)
@ContextConfiguration(classes = { CamelRssTest.TestConfig.class }, loader = CamelSpringDelegatingTestContextLoader.class)
@MockEndpoints
@DirtiesContext
public class CamelRssTest {

    @Inject
    protected Engine engine;

    @Configuration
    public static class TestConfig extends SingleRouteCamelConfiguration {

        @Bean
        public Engine spongeEngine() {
            return SpringEngine.builder().knowledgeBase("kb", "examples/camel/camel_rss.py").build();
        }

        @Bean
        public ProducerTemplate spongeProducerTemplate() throws Exception {
            return camelContext().createProducerTemplate();
        }

        @Bean
        @Override
        public RouteBuilder route() {
            return new RouteBuilder() {

                @Override
                public void configure() throws Exception {
                    // @formatter:off
                    // RSS from CNN, body set to the title, deduplicated by body (title), put into Sponge as a camel event
                    // (containing exchange).
                    from("rss:http://rss.cnn.com/rss/edition.rss?sortEntries=false&consumer.delay=1000").routeId("rss")
                            .marshal().rss()
                            .setBody(xpath("/rss/channel/item/title/text()"))
                            .idempotentConsumer(body(), MemoryIdempotentRepository.memoryIdempotentRepository())
                            .transform(body().prepend("CNN news: "))
                            .to("sponge:spongeEngine");

                    // RSS from BBC, deduplicated by title, put into Sponge as an event with attributes containing decomposed feed.
                    from("rss:http://feeds.bbci.co.uk/news/world/rss.xml?consumer.delay=1000").routeId("rssDecomposed")
                            .marshal().rss()
                            .idempotentConsumer(xpath("/rss/channel/item/title/text()"),
                                    MemoryIdempotentRepository.memoryIdempotentRepository())
                            // Conversion from RSS XML to Sponge event with attributes.
                            .process((exchange) -> {
                                exchange.getIn().setBody(spongeEngine().getOperations().makeEvent("rssDecomposed")
                                    .set("source", "BBC")
                                    .set("title", CamelUtils.xpath(exchange, "/rss/channel/item/title/text()"))
                                    .set("link", CamelUtils.xpath(exchange, "/rss/channel/item/link/text()"))
                                    .set("description", CamelUtils.xpath(exchange, "/rss/channel/item/description/text()")));
                            })
                            .to("sponge:spongeEngine");

                    from("sponge:spongeEngine").routeId("spongeConsumer")
                            .process(exchange -> spongeEngine().getOperations().getVariable(AtomicInteger.class, "receivedCamelMessages")
                                    .incrementAndGet())
                            .log("${body}");

                    from("direct:log").routeId("directLog")
                            .process(exchange -> spongeEngine().getOperations().getVariable(AtomicInteger.class, "receivedCamelMessages")
                                    .incrementAndGet())
                            .log("${body}");
                    // @formatter:on
                }
            };
        }
    }

    @Test
    public void testRoute() throws InterruptedException {
        await().atMost(10, TimeUnit.SECONDS)
                .until(() -> engine.getOperations().getVariable(AtomicInteger.class, "receivedCamelMessages").intValue() >= 4);
        TimeUnit.SECONDS.sleep(1);
    }
}
