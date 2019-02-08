# Sponge Knowledge base
# Hello world

class HelloWorldTrigger < Trigger
    def onConfigure
        self.withEvent("helloEvent")
    end

    def onRun(event)
        puts event.get("say")
    end
end

def onStartup
	$sponge.event("helloEvent").set("say", "Hello World!").send()
end

