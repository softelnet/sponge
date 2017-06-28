# Sponge Knowledge base
# Hello world

class HelloWorldTrigger < Trigger
    def configure
        self.eventName = "helloEvent"
    end

    def run(event)
        puts event.get("say")
    end
end

def onStartup
	$EPS.event("helloEvent").set("say", "Hello World!").send()
end

