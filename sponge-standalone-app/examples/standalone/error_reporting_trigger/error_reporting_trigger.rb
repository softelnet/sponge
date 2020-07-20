# Sponge Knowledge Base
# Error reporting

class HelloWorldTrigger < Trigger
    def onConfigure
        self.withEvent("helloEvent")
    end

    def onRun(event)
        whatIsThis.doSomething()
        puts event.get("say")
    end
end

def onStartup
	$sponge.event("helloEvent").set("say", "Hello World!").send()
end

