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

package org.openksavi.sponge.examples.project.camelrssnews;

import java.util.Map;

import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import org.openksavi.sponge.EngineOperations;
import org.openksavi.sponge.camel.CamelUtils;
import org.openksavi.sponge.camel.EngineCamelConfiguration;
import org.openksavi.sponge.engine.Engine;
import org.openksavi.sponge.spring.SpringEngine;

/**
 * Spring configuration that creates the engine and Camel context.
 */
@Configuration
@ComponentScan
public class SpringConfiguration extends EngineCamelConfiguration {

    /** RSS source header name. */
    public static final String HEADER_SOURCE = "source";

    /**
     * The engine is started by Spring at once, in order to load configuration variables (e.g. rssSources) before creating Camel routes.
     *
     * @return the engine.
     */
    @Bean
    public Engine camelRssEngine() {
        // Use EngineBuilder API to create an engine with the configuration file. Also bind Spring and Camel plugins as beans manually.
        return SpringEngine.builder().config(CamelRssConstants.CONFIG_FILE).plugins(springPlugin(), camelPlugin()).build();
    }

    /**
     * Camel routes for reading RSS feeds. Routes could be also defined in XML, Groovy or scripting knowledge bases.
     *
     * @return route builder.
     */
    @Bean
    public RouteBuilder rssInputRoute() {
        return new RouteBuilder() {

            // @formatter:off
            @SuppressWarnings("unchecked")
            @Override
            public void configure() throws Exception {
                EngineOperations operations = camelRssEngine().getOperations();
                Map<String, String> rssSources = operations.getVariable(Map.class, CamelRssConstants.VAR_RSS_SOURCES);

                // Read RSS feeds from all configured sources.
                rssSources.forEach((source, url) ->
                        from("rss:" + url + operations.getVariable(CamelRssConstants.VAR_RSS_ENDPOINT_PARAMETERS, "")).routeId(source)
                            .setHeader(HEADER_SOURCE).constant(source)
                            .to("direct:rss"));

                // Gathers RSS from different sources and sends to Sponge engine as a normalized event.
                from("direct:rss").routeId("rss")
                        .marshal().rss()
                        // Deduplicate by title.
                        .idempotentConsumer(xpath("/rss/channel/item/title/text()"),
                                MemoryIdempotentRepository.memoryIdempotentRepository())
                        // Conversion from RSS XML to Sponge event with attributes.
                        .process((exchange) -> exchange.getIn().setBody(operations.event("news")
                                .set("source", exchange.getIn().getHeader(HEADER_SOURCE))
                                .set("channel", CamelUtils.xpath(exchange, "/rss/channel/title/text()"))
                                .set("title", CamelUtils.xpath(exchange, "/rss/channel/item/title/text()"))
                                .set("link", CamelUtils.xpath(exchange, "/rss/channel/item/link/text()"))
                                .set("description", CamelUtils.xpath(exchange, "/rss/channel/item/description/text()"))
                                .make()))
                        .to("sponge:camelRssEngine");
            }
            // @formatter:on
        };
    }

    /**
     * Camel routes that use the engine as a consumer (directly or indirectly).
     *
     * @return route builder.
     */
    @Bean
    public RouteBuilder consumerRoute() {
        return new RouteBuilder() {

            @Override
            public void configure() throws Exception {
                // @formatter:off
                from("sponge:camelRssEngine").routeId("spongeConsumer")
                        .log("Received Camel message: ${body}");

                from("direct:log").routeId("directLog")
                        .log("${body}");
                // @formatter:on
            }
        };
    }

    /**
     * Camel producer template used by Sponge Camel component.
     *
     * @return producer template.
     * @throws Exception Camel context specific exception.
     */
    @Bean
    public ProducerTemplate spongeProducerTemplate() throws Exception {
        return camelContext().createProducerTemplate();
    }
}
