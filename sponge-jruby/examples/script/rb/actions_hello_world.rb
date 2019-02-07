# Sponge Knowledge base
# Hello World action

class HelloWorldAction < Action
    def onConfigure
        self.label = "Hello world"
        self.description = "Returns a greeting text."
        self.argsMeta = [ArgMeta.new("name", StringType.new()).withLabel("Your name").withDescription("Type your name.")]
        self.resultMeta = ResultMeta.new(StringType.new()).withLabel("Greeting").withDescription("The greeting text.")
    end
    def onCall(name)
        return "Hello World! Hello %s!" % [name]
    end
end

def onStartup
    $sponge.logger.info("{}", $sponge.call("HelloWorldAction", ["Sponge user"]))
end
