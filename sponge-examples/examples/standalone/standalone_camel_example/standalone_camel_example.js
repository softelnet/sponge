/**
 * Sponge Knowledge base
 * Standalone with Camel example
 */

var RouteBuilder = org.apache.camel.builder.RouteBuilder;

var javaScriptRoute = new RouteBuilder({
    configure: function() {
        var _super_ = Java.super(javaScriptRoute);
        _super_.from("sponge:spongeEngine").routeId("spongeConsumerCamelJavaScript")
                .transform().simple("JavaScript route - Received message: \${body}")
                .to("stream:out");
    }
});

function onStartup() {
    camel.context.addRoutes(javaScriptRoute);
}


