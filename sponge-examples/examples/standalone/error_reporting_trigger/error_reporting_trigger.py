"""
Sponge Knowledge base
Error reporting
"""

class HelloWorldTrigger(Trigger):
    def configure(self):
        self.eventName = "helloEvent"
    def run(self, event):
        whatIsThis.doSomething()
        print event.get("say")

def onStartup():
    EPS.event("helloEvent").set("say", "Hello World!").send()
