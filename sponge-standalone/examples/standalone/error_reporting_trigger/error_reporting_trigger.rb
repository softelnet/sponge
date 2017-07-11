# Sponge Knowledge base
# Error reporting

class HelloWorldTrigger < Trigger
    def configure
        self.event = "helloEvent"
    end

    def run(event)
        whatIsThis.doSomething()
        puts event.get("say")
    end
end

def onStartup
	$EPS.event("helloEvent").set("say", "Hello World!").send()
end

