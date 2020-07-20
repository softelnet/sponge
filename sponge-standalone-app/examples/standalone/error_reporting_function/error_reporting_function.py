"""
Sponge Knowledge Base
Error reporting
"""

class HelloWorldTrigger(Trigger):
    def onConfigure(self):
        self.withEvent("helloEvent")
    def onRun(self, event):
        print event.get("say")

def onStartup():
    whatIsThis.doSomething()
    sponge.event("helloEvent").set("say", "Hello World!").send()
