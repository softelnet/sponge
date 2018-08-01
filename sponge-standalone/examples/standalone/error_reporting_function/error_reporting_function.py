"""
Sponge Knowledge base
Error reporting
"""

class HelloWorldTrigger(Trigger):
    def onConfigure(self):
        self.event = "helloEvent"
    def onRun(self, event):
        print event.get("say")

def onStartup():
    whatIsThis.doSomething()
    sponge.event("helloEvent").set("say", "Hello World!").send()
