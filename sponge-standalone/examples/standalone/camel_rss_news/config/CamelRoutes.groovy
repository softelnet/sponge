import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.idempotent.MemoryIdempotentRepository

import org.openksavi.sponge.engine.SpongeEngine
import org.openksavi.sponge.EngineOperations
import org.openksavi.sponge.camel.CamelUtils

import javax.inject.Inject

import java.util.Map

import org.apache.camel.LoggingLevel;

class RssInputRoute extends RouteBuilder {

    public static final String HEADER_SOURCE = "source";

    @Inject
    private SpongeEngine engine;

    void configure() {
        EngineOperations operations = engine.getOperations();
        Map<String, String> rssSources = operations.getVariable("rssSources");

        // @formatter:off
        // Read RSS feeds from all configured sources.
        rssSources.each { source, url ->
                from("rss:" + url + operations.getVariable("rssEndpointParameters", "")).routeId(source)
                    .setHeader(HEADER_SOURCE).constant(source)
                    .to("direct:rss")};

        // Gathers RSS from different sources and sends to Sponge engine as a normalized event.
        from("direct:rss").routeId("rss")
                .marshal().rss()
                // Deduplicate by title.
                .idempotentConsumer(xpath("/rss/channel/item/title/text()"),
                        MemoryIdempotentRepository.memoryIdempotentRepository())
                // Conversion from RSS XML to Sponge event with attributes.
                .process { it.getIn().setBody(operations.event("news")
                        .set("source", it.getIn().getHeader(HEADER_SOURCE))
                        .set("channel", CamelUtils.xpath(it, "/rss/channel/title/text()"))
                        .set("title", CamelUtils.xpath(it, "/rss/channel/item/title/text()"))
                        .set("link", CamelUtils.xpath(it, "/rss/channel/item/link/text()"))
                        .set("description", CamelUtils.xpath(it, "/rss/channel/item/description/text()"))
                        .make()) }
                .to("sponge:camelRssEngine");
        // @formatter:on
    }
}

class ConsumerRoute extends RouteBuilder {
    void configure() {
        // @formatter:off
        from("sponge:camelRssEngine").routeId("spongeConsumer")
                .transform().simple("Received Camel message: \${body}")
                .to("stream:out");

        from("direct:log").routeId("directLog")
                .transform().simple("Camel: \${body}")
                .to("stream:out");
        // @formatter:on
    }
}

beans {
    xmlns([context:'http://www.springframework.org/schema/context'])

    rssInputRoute(RssInputRoute)

    consumerRoute(ConsumerRoute)
}