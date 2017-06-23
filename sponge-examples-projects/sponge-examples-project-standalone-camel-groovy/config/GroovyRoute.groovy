import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.LoggingLevel;

class GroovyRoute extends RouteBuilder {
    void configure() {
        // @formatter:off
        from("sponge:spongeEngine").routeId("spongeConsumerCamelGroovySpring")
                .transform().simple("Groovy/Spring route - Received message: \${body}")
                .to("stream:out");
        // @formatter:on
    }
}

beans {
    route(GroovyRoute)
}