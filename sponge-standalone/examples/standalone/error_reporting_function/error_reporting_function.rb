# Sponge Knowledge base
# Error reporting

class HelloWorldTrigger < Trigger
    def onConfigure
        self.event = "helloEvent"
    end

    def onRun(event)
        puts event.get("say")
    end
end

def onStartup
    whatIsThis.doSomething()
	$sponge.event("helloEvent").set("say", "Hello World!").send()
end

