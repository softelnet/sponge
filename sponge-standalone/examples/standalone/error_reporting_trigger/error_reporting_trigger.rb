# Sponge Knowledge base
# Error reporting

class HelloWorldTrigger < Trigger
    def onConfigure
        self.event = "helloEvent"
    end

    def onRun(event)
        whatIsThis.doSomething()
        puts event.get("say")
    end
end

def onStartup
	$EPS.event("helloEvent").set("say", "Hello World!").send()
end

