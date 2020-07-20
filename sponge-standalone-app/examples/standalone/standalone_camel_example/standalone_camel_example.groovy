/**
 * Sponge Knowledge Base
 * Standalone with Camel example
 */

import org.apache.camel.builder.RouteBuilder

class GroovyRoute extends RouteBuilder {
    void configure() {
        from("sponge:spongeEngine").routeId("spongeConsumerCamelGroovy")
            .transform().simple("Groovy route - Received message: \${body}")
            .to("stream:out")
    }
}

void onStartup() {
    camel.context.addRoutes(new GroovyRoute())
}