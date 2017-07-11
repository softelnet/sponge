"""
Sponge Knowledge base
Error reporting
"""

class HelloWorldTrigger(Trigger):
    def configure(self):
        self.event = "helloEvent"
    def run(self, event):
        print event.get("say")

def onStartup():
    whatIsThis.doSomething()
    EPS.event("helloEvent").set("say", "Hello World!").send()
