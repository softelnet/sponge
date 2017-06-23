# Sponge Knowledge base
# Error reporting

class HelloWorldTrigger < Trigger
    def configure
        self.eventName = "helloEvent"
    end

    def run(event)
        puts event.get("say")
    end
end

def onStartup
    whatIsThis.doSomething()
	$EPS.event("helloEvent").set("say", "Hello World!").send()
end

