import org.apache.camel.builder.RouteBuilder;

import javax.inject.Inject

import org.apache.camel.LoggingLevel;

import org.apache.camel.component.mustache.MustacheConstants;

import org.openksavi.sponge.engine.SpongeEngine

class TemplateRoute extends RouteBuilder {

    @Inject
    SpongeEngine engine;

    void configure() {
        // @formatter:off
        from("direct:template")
                .setHeader(MustacheConstants.MUSTACHE_RESOURCE_URI).constant(engine.operations.getVariable("templateUri"))
                .to("mustache:dummy");
        // @formatter:on
    }
}

beans {
    xmlns([context:'http://www.springframework.org/schema/context'])

    templateRoute(TemplateRoute)
}