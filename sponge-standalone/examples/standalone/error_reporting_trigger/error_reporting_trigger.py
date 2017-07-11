"""
Sponge Knowledge base
Error reporting
"""

class HelloWorldTrigger(Trigger):
    def configure(self):
        self.event = "helloEvent"
    def run(self, event):
        whatIsThis.doSomething()
        print event.get("say")

def onStartup():
    EPS.event("helloEvent").set("say", "Hello World!").send()
