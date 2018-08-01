"""
Sponge Knowledge base
Error reporting
"""

class HelloWorldTrigger(Trigger):
    def onConfigure(self):
        self.event = "helloEvent"
    def onRun(self, event):
        whatIsThis.doSomething()
        print event.get("say")

def onStartup():
    sponge.event("helloEvent").set("say", "Hello World!").send()
