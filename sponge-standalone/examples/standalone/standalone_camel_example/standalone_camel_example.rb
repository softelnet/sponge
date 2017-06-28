# Sponge Knowledge base
# Standalone with Camel example

java_import org.apache.camel.builder.RouteBuilder

class RubyRoute < RouteBuilder
    def configure
        self.from("sponge:spongeEngine").routeId("spongeConsumerCamelRuby") \
                .transform().simple("Ruby route - Received message: ${body}") \
                .to("stream:out")
    end
end

def onStartup
    $camel.context.addRoutes(RubyRoute.new())
end

