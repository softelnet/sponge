# Sponge Knowledge base
# Hello World action

class HelloWorldAction < Action
    def onConfigure
        self.label = "Hello world"
        self.description = "Returns a greeting text."
        self.argsMeta = [ArgMeta.new("name", StringType.new()).label("Your name").description("Type your name.")]
        self.resultMeta = ResultMeta.new(StringType.new()).label("Greeting").description("The greeting text.")
    end
    def onCall(name)
        return "Hello World! Hello %s!" % [name]
    end
end

def onStartup
    $sponge.logger.info("{}", $sponge.call("HelloWorldAction", ["Sponge user"]))
end
