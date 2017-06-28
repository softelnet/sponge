import org.apache.camel.LoggingLevel
import org.apache.camel.builder.RouteBuilder
import org.springframework.context.annotation.*

import org.openksavi.sponge.core.engine.*
import org.openksavi.sponge.engine.Engine
import org.openksavi.sponge.jython.PythonConstants

class TestRouteBuilder extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // @formatter:off
        from("direct:test").routeId("spongeProducer")
                .to("sponge:spongeEngine")
                .log("Sent event: \${body}");

        from("direct:log").routeId("directLog")
                .log("\${body}");

        from("direct:error").routeId("directError")
                .log(LoggingLevel.ERROR, "Received message on direct:error endpoint.");

        from("direct:end").routeId("directEnd")
                .log("Received message on direct:end endpoint: \${body}");

        from("sponge:spongeEngine").routeId("spongeConsumer")
                .log("Received message on direct:consumerEnd endpoint: \${body}");
        // @formatter:on
    }
}

class TestEngine extends DefaultEngine {
    TestEngine() {
        new EngineBuilder(this).knowledgeBase("kb", "examples/camel/camel_multiple_consumer.py").build()
    }
}

beans {
    route(TestRouteBuilder)

    spongeEngine(TestEngine)
}